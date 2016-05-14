/*

*/
package kalang.ast;
import java.util.*;
import javax.annotation.Nonnull;
import kalang.core.*;
/**
 * BinaryExpr presents a binary operation, such as add,sub,multiply and so on
 * @author Kason Yang <i@kasonyang.com>
 */
public abstract class BinaryExpr extends ExprNode{
    
    public static final String
            //Math
            OP_ADD = "+",
            OP_SUB = "-",
            OP_MUL = "*",
            OP_DIV = "/",
            OP_REM = "%",
            //Bitwise
            OP_AND = "&",
            OP_OR = "|",
            OP_XOR = "^",
            OP_SHIFT_LEFT = "<<",
            OP_SHIFT_RIGHT = ">>",
            //Compare
            OP_LE = "<=",
            OP_GE = ">=",
            OP_LT = "<",
            OP_GT = ">",
            OP_EQ = "==",
            OP_NE = "!=",
            //Logic
            OP_LOGIC_AND = "&&",
            OP_LOGIC_OR = "||"
            ;
            
    
    @Nonnull
    protected ExprNode expr1;
    
    @Nonnull
    protected ExprNode expr2;
    
    @Nonnull
    protected String operation;
    
    public BinaryExpr(@Nonnull ExprNode expr1,@Nonnull ExprNode expr2,@Nonnull String operation){
            this.expr1 = expr1;
            this.expr2 = expr2;
            this.operation = operation;
            addChild(expr1);
            addChild(expr2);
    }

    /**
     * @return the expr1
     */
    public ExprNode getExpr1() {
        return expr1;
    }


    /**
     * @return the expr2
     */
    public ExprNode getExpr2() {
        return expr2;
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

    public void setExpr1(ExprNode expr) {
        expr1 = expr;
        clearChild();
        addChild(expr1);
        addChild(expr2);
    }

    public void setExpr2(ExprNode expr) {
        expr2 = expr;
        clearChild();
        addChild(expr1);
        addChild(expr2);
    }

}