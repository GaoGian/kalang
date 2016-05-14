package kalang.ast;

import java.util.*;
import kalang.core.*;

public class CatchBlock extends AstNode {

    public LocalVarNode catchVar;

    public Statement execStmt;

    public CatchBlock(LocalVarNode catchVar, Statement execStmt) {
        this.catchVar = catchVar;
        this.execStmt = execStmt;
        addChild(catchVar);
        addChild(execStmt);
    }

}
