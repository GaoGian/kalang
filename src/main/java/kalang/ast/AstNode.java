package kalang.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import kalang.compiler.OffsetRange;
import kalang.core.*;

/**
 * The base class of any ast node
 * 
 * @author Kason Yang <i@kasonyang.com>
 */
public abstract class AstNode {
    
    private final List<AstNode> children = new LinkedList<>();
    
    @Nullable
    protected AstNode parent;
    
    @Nonnull
    public OffsetRange offset = OffsetRange.NONE;
    
    protected final void addChild(AstNode c){
        Objects.requireNonNull(c);
        children.add(c);
        c.parent = this;
    }
    
    protected final void addChildren(AstNode... children){
        for(AstNode c:children) addChild(c);
    }
    
    protected void clearChild(){
        for(AstNode c:children){
            removeChild(c);
        }
    }
    
    protected void removeChild(AstNode c){
        children.remove(c);
        c.parent = null;
    }
    
    @Nullable
    public AstNode getParent() {
        return parent;
    }
    
    public AstNode[] getChildren(){
        return children.toArray(new AstNode[children.size()]);
    }
        
}
