/*

*/
package kalang.ast;
import java.util.*;
import javax.annotation.Nullable;
import kalang.core.*;
public class LoopStmt extends Statement{
    
    public Statement[] initStmts;
    public Statement loopBody;
    public ExprNode preConditionExpr;
    public ExprNode postConditionExpr;
    
    public LoopStmt(@Nullable Statement[] initStmts,@Nullable Statement loopBody,@Nullable ExprNode preConditionExpr,@Nullable ExprNode postConditionExpr){
            this.initStmts = initStmts == null ? new Statement[0] : initStmts;
            this.loopBody = loopBody == null ? new BlockStmt() : loopBody;
            this.preConditionExpr = preConditionExpr;
            this.postConditionExpr = postConditionExpr;
            addChildren(initStmts);
            addChild(loopBody);
            if(preConditionExpr!=null){
                addChild(preConditionExpr);
            }
            if(postConditionExpr!=null){
                addChild(postConditionExpr);
            }
    }

    public Statement[] getInitStmts() {
        return initStmts;
    }

    public Statement getLoopBody() {
        return loopBody;
    }

    @Nullable
    public ExprNode getPreConditionExpr() {
        return preConditionExpr;
    }

    @Nullable
    public ExprNode getPostConditionExpr() {
        return postConditionExpr;
    }
    
    
}