/*

*/
package kalang.ast;
import java.util.*;
import kalang.core.*;
public class ExprStmt extends Statement{
    
    protected ExprNode expr;
    
    public ExprStmt(ExprNode expr){
            this.expr = expr;
            addChild(expr);
    }
    
    /**
     * @return the expr
     */
    public ExprNode getExpr() {
        return expr;
    }

}