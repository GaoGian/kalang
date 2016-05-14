package kalang.ast;
import java.util.*;
import kalang.core.*;
public class UnaryExpr extends ExprNode{
    
    public final static String
            //OPERATION_INC = "++",
            //OPERATION_DEC = "--",
            OPERATION_NEG = "-",
            OPERATION_POS = "+",
            OPERATION_LOGIC_NOT = "!",
            OPERATION_NOT = "~" ;
    
    protected ExprNode expr;
    
    protected String operation;
    
    public UnaryExpr(ExprNode expr,String operation){
            this.expr = expr;
            this.operation = operation;
            addChild(expr);
    }

    @Override
    public Type getType() {
        switch(getOperation()){
            case "!":return Types.BOOLEAN_TYPE;
            default:return getType(getExpr());
        }
    }

    /**
     * @return the expr
     */
    public ExprNode getExpr() {
        return expr;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        Objects.requireNonNull(operation);
        this.operation = operation;
    }
    
}