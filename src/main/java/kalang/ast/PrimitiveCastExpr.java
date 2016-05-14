package kalang.ast;
import java.util.*;
import kalang.core.*;
public class PrimitiveCastExpr extends ExprNode{
    
    protected PrimitiveType fromType;
    
    protected PrimitiveType toType;
    
    protected ExprNode expr;
    
    
    public PrimitiveCastExpr(){
        
    }
    
    
    public PrimitiveCastExpr(PrimitiveType fromType,PrimitiveType toType,ExprNode expr){
            this.fromType = fromType;
            this.toType = toType;
            this.expr = expr;
            addChild(expr);
    }
        

    @Override
    public Type getType() {
        return getToType();
    }

    /**
     * @return the fromType
     */
    public PrimitiveType getFromType() {
        return fromType;
    }

    /**
     * @param fromType the fromType to set
     */
    public void setFromType(PrimitiveType fromType) {
        Objects.requireNonNull(fromType);
        this.fromType = fromType;
    }

    /**
     * @return the toType
     */
    public PrimitiveType getToType() {
        return toType;
    }

    /**
     * @param toType the toType to set
     */
    public void setToType(PrimitiveType toType) {
        Objects.requireNonNull(toType);
        this.toType = toType;
    }

    /**
     * @return the expr
     */
    public ExprNode getExpr() {
        return expr;
    }

}