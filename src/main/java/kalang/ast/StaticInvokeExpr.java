
package kalang.ast;
import java.io.*;
import java.nio.*;
import java.net.*;
import java.util.*;
import kalang.AmbiguousMethodException;
import kalang.MethodNotFoundException;
import kalang.util.AstUtil;
/**
 *
 * @author Kason Yang <i@kasonyang.com>
 */
public class StaticInvokeExpr extends InvocationExpr{

    private ClassReference invokeClass;
    
    public static StaticInvokeExpr create(ClassReference clazz, String methodName, ExprNode[] args) throws MethodNotFoundException, AmbiguousMethodException {
        MethodSelection ms = applyMethod(clazz.getReferencedClassNode() , methodName, args);
        MethodNode md = ms.selectedMethod;
        if(!AstUtil.isStatic(md.getModifier())){
            throw new MethodNotFoundException(methodName + " is not static");
        }
        return new StaticInvokeExpr(clazz, md , ms.appliedArguments);
    }

    public StaticInvokeExpr(ClassReference invokeClass, MethodNode method, ExprNode[] args) {
        super(method, args);
        if(!AstUtil.isStatic(method.getModifier())){
            throw new IllegalArgumentException("static method is required");
        }
        this.invokeClass = invokeClass;
        addChild(invokeClass);
        addChildren(args);
    }

    public ClassReference getInvokeClass() {
        return invokeClass;
    }
    
}
