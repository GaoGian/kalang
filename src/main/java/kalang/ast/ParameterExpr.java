package kalang.ast;
import java.util.*;
import kalang.core.*;
public class ParameterExpr extends ExprNode{
    
    protected VarObject parameter;
    
    public ParameterExpr(VarObject parameter){
            this.parameter = parameter;
    }

    @Override
    public Type getType() {
        return getParameter().type;
    }

    /**
     * @return the parameter
     */
    public VarObject getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(VarObject parameter) {
        Objects.requireNonNull(parameter);
        this.parameter = parameter;
    }
}