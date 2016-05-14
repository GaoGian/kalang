/*

*/
package kalang.ast;
import java.util.*;
import javax.annotation.Nullable;
import kalang.core.*;
import kalang.util.AstUtil;
public class ClassNode extends AstNode implements Annotationable{
    
    public int modifier;
    
    public String name;
    
    public ClassNode superClassNode;
    
    public final List<FieldNode> fields = new ArrayList<>();
    
    protected List<MethodNode> methods;
    
    public List<ClassNode> interfaces;
    
    public boolean isInterface;
    
    public boolean isArray;
    
    public final List<AnnotationNode> annotations = new LinkedList<>();
    
    public ClassNode(Integer modifier,String name,@Nullable ClassNode superClassNode,@Nullable List<ClassNode> interfaces,boolean isInterface,boolean isArray){
            methods = new LinkedList();
            if(interfaces == null) interfaces = new LinkedList();
            this.modifier = modifier;
            this.name = name;
            this.superClassNode = superClassNode;
            this.interfaces = interfaces;
            this.isInterface = isInterface;
            this.isArray = isArray;
    }
    

    public FieldNode createField(){
        FieldNode fieldNode = FieldNode.create(this);
        fields.add(fieldNode);
        addChild(fieldNode);
        return fieldNode;
    }
    
    public MethodNode createMethodNode(int modifier,Type type,String name,@Nullable ParameterNode[] parameters,@Nullable Type[] exceptionTypes){
        MethodNode md = new MethodNode(modifier, type, name,parameters,exceptionTypes);
        methods.add(md);
        addChild(md);
        return md;
    }
    
    public MethodNode[] getDeclaredMethodNodes(){
        return methods.toArray(new MethodNode[0]);
    }
    
    public MethodNode[] getMethods(){
        Map<String,MethodNode> mds = new HashMap<>();
        if(superClassNode!=null){
            MethodNode[] parentMds = superClassNode.getMethods();
            for(MethodNode m:parentMds){
                String descriptor = AstUtil.getMethodDescriptor(m);
                mds.put(descriptor, m);
            }
        }
        MethodNode[] decMds = getDeclaredMethodNodes();
        for(MethodNode m:decMds){
            String descriptor = AstUtil.getMethodDescriptor(m);
            mds.put(descriptor, m);
        }
        return mds.values().toArray(new MethodNode[0]);        
    }
    
    public boolean isSubclassOf(ClassNode clazz){
        if(superClassNode!=null){
            if(superClassNode.equals(clazz)) return true;
            if(superClassNode.isSubclassOf(clazz)) return true;
        }
        if(interfaces!=null){
            for(ClassNode itf:interfaces){
                if(itf.equals(clazz)) return true;
                if(itf.isSubclassOf(clazz)) return true;
            }
        }
        return false;
    }

    @Override
    public AnnotationNode[] getAnnotations() {
        return annotations.toArray(new AnnotationNode[0]);
    }

    @Nullable
    public ClassNode getSuperClassNode() {
        return superClassNode;
    }

    public void setSuperClassNode(@Nullable ClassNode superClassNode) {
        this.superClassNode = superClassNode;
    }
    
}