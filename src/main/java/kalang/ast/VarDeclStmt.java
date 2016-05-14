package kalang.ast;
import java.util.*;
import kalang.core.*;
public class VarDeclStmt extends Statement{
    
    public LocalVarNode[] vars;
    
    public VarDeclStmt(LocalVarNode var){
        this(new LocalVarNode[]{var});
    }
    
    public VarDeclStmt(LocalVarNode... var){
            this.vars = var;
            addChildren(vars);
    }

    public VarDeclStmt(List<LocalVarNode> vars) {
        this(vars.toArray(new LocalVarNode[vars.size()]));
    }
    
    
}