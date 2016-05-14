/*

*/
package kalang.ast;
import java.util.*;
import kalang.core.*;
public class IfStmt extends Statement{
    
    protected ExprNode conditionExpr;
    
    protected Statement trueBody;
    
    protected Statement falseBody;
    
    public IfStmt(ExprNode conditionExpr,Statement trueBody,Statement falseBody){
            this.conditionExpr = conditionExpr;
            this.trueBody = trueBody;
            this.falseBody = falseBody;
            addChildren(conditionExpr,trueBody,falseBody);
    }

    /**
     * @return the conditionExpr
     */
    public ExprNode getConditionExpr() {
        return conditionExpr;
    }

    /**
     * @return the trueBody
     */
    public Statement getTrueBody() {
        return trueBody;
    }

    /**
     * @return the falseBody
     */
    public Statement getFalseBody() {
        return falseBody;
    }

}