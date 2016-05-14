package kalang.ast;
import java.util.*;
import kalang.core.*;
public abstract class VarObject extends AstNode{
    
    public int modifier;
    
    public Type type;
    
    public String name;
    
    public ExprNode initExpr;
    
    
    public VarObject(){
        
    }
    
    
    public VarObject(int modifier,Type type,String name,ExprNode initExpr){
            this.modifier = modifier;
            this.type = type;
            this.name = name;
            this.initExpr = initExpr;
            addChild(initExpr);
    }
    
    public Type getType(){
        if(type!=null) return type;
        if(initExpr!=null){
            return initExpr.getType();
        }
        return Types.ROOT_TYPE;
    }
}