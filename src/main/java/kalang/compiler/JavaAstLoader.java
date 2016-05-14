package kalang.compiler;

import kalang.AstNotFoundException;
import kalang.ast.ClassNode;
import kalang.ast.VarObject;
import kalang.ast.MethodNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import kalang.ast.FieldNode;
import kalang.ast.ParameterNode;
import kalang.core.Type;
import kalang.core.Types;
import kalang.exception.Exceptions;
import kalang.util.AstUtil;

/**
 * The class loads ast from java class
 * 
 * @author Kason Yang <i@kasonyang.com>
 */
public class JavaAstLoader extends AstLoader {

    static String ROOT_CLASS = "java.lang.Object";
    
    private ClassLoader javaClassLoader;
    
    private Map<String,ClassNode> loadedClasses  =new HashMap<>();
    
    private static String getMethodDescriptor(Executable m){
        Class<?>[] pts = m.getParameterTypes();
        String[] types = new String[pts.length];
        for(int i=0;i<types.length;i++){
            types[i] = pts[i].getName();
        }
        String returnType = "V";
        if(m instanceof Method){
            returnType = ((Method)m).getReturnType().getName();
        }
        return AstUtil.getMethodDescriptor(m.getName(),returnType, types);
    }

    /**
     * build ast from java class
     * @param clz the java class
     * @return the ast built from java class
     * @throws AstNotFoundException 
     */
    @Nonnull
    public ClassNode buildFromClass(@Nonnull Class clz) throws AstNotFoundException {
        String  name = clz.getName();
        Class superClass = clz.getSuperclass();
        ClassNode cn = new ClassNode(clz.getModifiers(), name, null, null, clz.isInterface(), clz.isArray());
        loadedClasses.put(clz.getName(), cn);
        ClassNode parent;
        if (superClass != null) {
            parent = findAst(superClass.getName());
        } else if (!name.equals(ROOT_CLASS)) {
            parent = findAst(ROOT_CLASS);
        }else{
            parent = null;
        }
        cn.setSuperClassNode(parent);
        Class[] clzInterfaces = clz.getInterfaces();
        if(clzInterfaces != null){
            for(Class itf:clzInterfaces){
                cn.interfaces.add(findAst(itf.getName()));
            }
        }
        List<Executable> methods = new LinkedList();
        methods.addAll(Arrays.asList(clz.getDeclaredMethods()));
        methods.addAll(Arrays.asList(clz.getDeclaredConstructors()));
        Class[] itfs = clz.getInterfaces();
        //TODO should default method of  interface becomes a declared method
        //MethodNode[] mds = methods.toArray(new MethodNode[0]);
        List<String> declaredMethods = new LinkedList<>();
        for(Executable m:methods){
            declaredMethods.add(getMethodDescriptor(m));
        }
        if(itfs!=null){
            for(Class i:itfs){
                for(Method m:i.getMethods()){
                    if(
                            m.isDefault() 
                            && !declaredMethods.contains(getMethodDescriptor(m))){
                        methods.add(m);
                    }
                }
            }
        }
        for (Executable m : methods) {
            buildMethodNode(cn, m);
        }
        for (Field f : clz.getFields()) {
            FieldNode fn = cn.createField();
            fn.name = f.getName();
            fn.type =getType(f.getType());
            fn.modifier = f.getModifiers();
        }
        return cn;
    }

    public JavaAstLoader(@Nonnull ClassLoader javaClassLoader) {
        this.javaClassLoader = javaClassLoader;
    }

    public JavaAstLoader() {
        javaClassLoader = this.getClass().getClassLoader();
    }
    
    private MethodNode buildMethodNode(ClassNode clazz,Executable m) throws AstNotFoundException{
        Parameter[] parameters = m.getParameters();
        ParameterNode[] parameterNodes = new ParameterNode[parameters.length];
        for (int i=0;i<parameters.length;i++) {
            Parameter p = parameters[i];
            ParameterNode pn = new ParameterNode(p.getModifiers(),getType(p.getType()),p.getName());
            parameterNodes[i] = pn;
        }
        Type type;
        String methodName;
        if (m instanceof Method) {
            type =getType(((Method) m).getReturnType());
            methodName = m.getName();
        } else if (m instanceof Constructor) {
            methodName = "<init>";
            type = Types.VOID_TYPE;// getType(clz);
        }else{
            throw Exceptions.unsupportedTypeException(m);
        }
        Type[] exceptionTypes = getTypes(m.getParameterTypes());
        MethodNode methodNode = clazz.createMethodNode(m.getModifiers(), type, methodName, parameterNodes, exceptionTypes);
        return methodNode;
    }
    

    @Override
    protected ClassNode findAst(String className) throws AstNotFoundException {
        if(className==null){
            System.err.println("warning:trying to null class");
            throw new AstNotFoundException("null");
        }
        ClassNode ast = loadedClasses.get(className);
        if(ast!=null){
            return ast;
        }
        try {
            return super.findAst(className);
        } catch (AstNotFoundException e) {
            try {
                Class clz = javaClassLoader.loadClass(className);
                ast = buildFromClass(clz);
                return ast;
            } catch (ClassNotFoundException ex) {
                throw e;
            }
        }
    }

    private Type getType(Class<?> type) throws AstNotFoundException {
        if(type.isPrimitive()){
            return Types.getPrimitiveType(type.getTypeName());
        }else if(type.isArray()){
            return Types.getArrayType(getType(type.getComponentType()));
        }else{
            return Types.getClassType(findAst(type.getName()));
        }
    }

    private Type[] getTypes(Class<?>[] parameterTypes) throws AstNotFoundException {
        Type[] types = new Type[parameterTypes.length];
        for(int i=0;i<parameterTypes.length;i++){
            types[i] = getType(parameterTypes[i]);
        }
        return types;
    }

}
