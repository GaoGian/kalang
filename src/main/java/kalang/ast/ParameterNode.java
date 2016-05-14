
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
public class ParameterNode extends VarObject{

    public ParameterNode(int modifier, Type type, String name) {
        super(modifier, type, name, null);
    }

    
    
    public ParameterNode(Type type, String name) {
        super(0,type, name, null);
    }
    
}
