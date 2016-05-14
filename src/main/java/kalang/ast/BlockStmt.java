/*

*/
package kalang.ast;
import java.util.*;
import kalang.core.*;
public class BlockStmt extends Statement{
    
    public final List<Statement> statements = new LinkedList<>();
    
    public BlockStmt(){
        
    }
    
    public void addStatement(Statement stmt){
        addChild(stmt);
        statements.add(stmt);
    }
    
}