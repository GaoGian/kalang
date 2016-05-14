package kalang.ast;
import java.util.*;
import kalang.core.*;
public class TryStmt extends Statement{
    
    public Statement execStmt;
    
    public CatchBlock[] catchStmts;
    
    public Statement finallyStmt;
    
    
    public TryStmt(Statement execStmt,CatchBlock[] catchStmts,Statement finallyStmt){
            this.execStmt = execStmt;
            this.catchStmts = catchStmts;
            this.finallyStmt = finallyStmt;
            addChild(execStmt);
            addChildren(catchStmts);
            addChild(finallyStmt);
    }

    public TryStmt(Statement tryExecStmt, List<CatchBlock> catchStatements, Statement finallyStmt) {
        this(tryExecStmt, catchStatements.toArray(new CatchBlock[0]), finallyStmt);
    }

    public Statement getExecStmt() {
        return execStmt;
    }

    public CatchBlock[] getCatchStmts() {
        return catchStmts;
    }

    public Statement getFinallyStmt() {
        return finallyStmt;
    }
    
}