package kalang.ast;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.annotation.Nullable;
import kalang.core.*;
public class MethodNode extends AstNode implements Annotationable{
    
    public int modifier;
    
    public Type type;
    
    public String name;
    
    public ParameterNode[] parameters;
    
    public final List<AnnotationNode> annotations = new LinkedList<>();
    
    @Nullable
    public BlockStmt body = null;
    
    public final List<Type> exceptionTypes = new LinkedList<>();

    public MethodNode(int modifier, Type type, String name){
        this(modifier, type, name, null, null);
    }
    
    public MethodNode(int modifier, Type type, String name,@Nullable ParameterNode[] parameters,@Nullable Type[] exceptionTypes) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
        this.parameters = parameters == null? new ParameterNode[0] : parameters;
        if(exceptionTypes!=null){
            this.exceptionTypes.addAll(Arrays.asList(exceptionTypes));
        }
        addChildren(parameters);
        if(!Modifier.isAbstract(modifier)){
            body = new BlockStmt();
            addChild(body);
        }
    }

    @Override
    public AnnotationNode[] getAnnotations() {
        return annotations.toArray(new AnnotationNode[0]);
    }

    public int getModifier() {
        return modifier;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ParameterNode[] getParameters() {
        return parameters;
    }

    public Type[] getExceptionTypes() {
        return exceptionTypes.toArray(new Type[exceptionTypes.size()]);
    }

    @Nullable
    public BlockStmt getBody() {
        return body;
    }
    
    @Deprecated
    public ClassNode getClassNode(){
        return (ClassNode) getParent();
    }
    
}