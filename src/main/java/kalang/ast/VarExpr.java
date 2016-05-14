package kalang.ast;
import java.util.*;
import kalang.core.*;
public class VarExpr extends AssignableExpr{
    
    protected VarObject var;
    
    
    public VarExpr(VarObject var){
            this.var = var;
            
    }
    

    @Override
    public Type getType() {
        return getVar().getType();
    }

    /**
     * @return the var
     */
    public VarObject getVar() {
        return var;
    }

    /**
     * @param var the var to set
     */
    public void setVar(VarObject var) {
        Objects.requireNonNull(var);
        this.var = var;
    }
}