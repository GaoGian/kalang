package kalang.ast;
import java.util.*;
import kalang.core.*;
public class ThrowStmt extends Statement{
    
    public ExprNode expr;
    
    public ThrowStmt(ExprNode expr){
            this.expr = expr;
            addChild(expr);
    }

}