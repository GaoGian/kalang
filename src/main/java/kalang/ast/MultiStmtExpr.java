/*

*/
package kalang.ast;
import java.util.*;
import kalang.core.*;
public class MultiStmtExpr extends ExprNode{
    
    public Statement[] stmts;
    
    public ExprNode reference;
    
    public MultiStmtExpr(Statement[] stmts,ExprNode reference){
            this.stmts = stmts;
            this.reference = reference;
            addChildren(stmts);
    }

    public MultiStmtExpr(List<Statement> stmts, VarExpr reference) {
        this(stmts.toArray(new Statement[stmts.size()]), reference);
    }

    @Override
    public Type getType() {
        return getType(reference);
    }

    public Statement[] getStmts() {
        return stmts;
    }

    public ExprNode getReference() {
        return reference;
    }
    
}