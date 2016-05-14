/*

*/
package kalang.ast;
import java.util.*;
import javax.annotation.Nonnull;
import kalang.core.*;
public class AssignExpr extends ExprNode{
    
    @Nonnull
    protected AssignableExpr to;
    
    @Nonnull
    protected ExprNode from;
    
    public AssignExpr(@Nonnull AssignableExpr to,@Nonnull ExprNode from){
        addChild(to);
        addChild(from);    
        this.to = to;
        this.from = from;    
    }

    @Override
    public Type getType() {
        return getType(getTo());
    }

    /**
     * @return the to
     */
    public AssignableExpr getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(AssignableExpr to) {
        Objects.requireNonNull(to);
        removeChild(this.to);
        addChild(to);
        this.to = to;
    }

    /**
     * @return the from
     */
    public ExprNode getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(ExprNode from) {
        Objects.requireNonNull(from);
        removeChild(this.from);
        addChild(from);
        this.from = from;
    }
    
}