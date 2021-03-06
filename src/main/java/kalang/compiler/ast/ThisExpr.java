package kalang.compiler.ast;

import kalang.compiler.core.ObjectType;
import kalang.compiler.core.Type;
import kalang.compiler.core.Types;
public class ThisExpr extends ExprNode{
    
    private final ObjectType classType;
    
    public ThisExpr(ObjectType classType){
            this.classType = classType;
    }
    
    public ThisExpr(ClassNode classNode){
        this(Types.getClassType(classNode));
    }

    @Override
    public Type getType() {
        return classType;
    }
    
}