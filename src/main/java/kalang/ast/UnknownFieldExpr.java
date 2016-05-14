
package kalang.ast;
import java.io.*;
import java.nio.*;
import java.net.*;
import java.util.*;
import kalang.core.Type;
import kalang.core.Types;
/**
 *
 * @author Kason Yang <i@kasonyang.com>
 */
public class UnknownFieldExpr extends AssignableExpr{

    private AstNode target;
    
    private String fieldName;
    
    private ClassNode specialClass;

    public UnknownFieldExpr(AstNode target,ClassNode specialClass, String fieldName) {
        this.target = target;
        this.fieldName = fieldName;
        this.specialClass = specialClass;
        addChild(target);
    }

    public AstNode getTarget() {
        return target;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public Type getType() {
        return Types.ROOT_TYPE;
    }

    public ClassNode getSpecialClass() {
        return specialClass;
    }
    

}
