package kalang.ast;
import java.util.*;
import kalang.core.*;
public class ReturnStmt extends Statement{
    
    public ExprNode expr = null;
    
    public ReturnStmt(){
        
    }
    
    public ReturnStmt(ExprNode expr){
            this.expr = expr;
            addChild(expr);
    }

}