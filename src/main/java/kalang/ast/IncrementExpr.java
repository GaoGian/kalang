
package kalang.ast;
import java.io.*;
import java.nio.*;
import java.net.*;
import java.util.*;
import kalang.core.Type;
/**
 *
 * @author Kason Yang <i@kasonyang.com>
 */
public class IncrementExpr extends ExprNode{
    
    protected AssignableExpr expr;
    
    protected boolean isDesc = false;
    
    protected boolean isPrefix = false;


    public IncrementExpr(AssignableExpr expr,boolean isDesc,boolean isPrefix) {
        this.expr = expr;
        this.isDesc = isDesc;
        this.isPrefix = isPrefix;
        addChild(expr);
    }

    public IncrementExpr(AssignableExpr expr) {
        this.expr = expr;
    }

    @Override
    public Type getType() {
        return getType(getExpr());
    }

    /**
     * @return the expr
     */
    public AssignableExpr getExpr() {
        return expr;
    }

    /**
     * @return the isDesc
     */
    public boolean isIsDesc() {
        return isDesc;
    }

    /**
     * @param isDesc the isDesc to set
     */
    public void setIsDesc(boolean isDesc) {
        this.isDesc = isDesc;
    }

    /**
     * @return the isPrefix
     */
    public boolean isIsPrefix() {
        return isPrefix;
    }

    /**
     * @param isPrefix the isPrefix to set
     */
    public void setIsPrefix(boolean isPrefix) {
        this.isPrefix = isPrefix;
    }

}
