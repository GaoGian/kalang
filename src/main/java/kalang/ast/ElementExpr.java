package kalang.ast;
import java.util.*;
import kalang.core.*;
/**
 * The ElementExpr presents a element of a array,for example <code>args[i]</code>
 * 
 * @author Kason Yang <i@kasonyang.com>
 */
public class ElementExpr extends AssignableExpr{
    
    protected ExprNode arrayExpr;
    
    protected ExprNode index;
    
    public ElementExpr(ExprNode target,ExprNode key){
            this.arrayExpr = target;
            this.index = key;
            addChild(arrayExpr);
            addChild(index);
    }
    

    @Override
    public Type getType() {
        Type arrayType = getType(getArrayExpr());
        if(arrayType==null) return Types.VOID_TYPE;
        if(!(arrayType instanceof ArrayType)){
            throw new UnknownError("ArrayType is required!");
        }
        return ((ArrayType)arrayType).getComponentType();
    }

    /**
     * @return the arrayExpr
     */
    public ExprNode getArrayExpr() {
        return arrayExpr;
    }

    /**
     * @return the index
     */
    public ExprNode getIndex() {
        return index;
    }
    
}