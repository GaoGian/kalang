/*
Don't modify!This file is generated automately.
*/
package jast.ast;
import java.util.*;
public class VarDeclStmt extends Statement{
    
    public VarObject var;
    
    
    
    public VarDeclStmt(VarObject var=null){
        
        
            this.var = var;
        
    }
    
    
    public static VarDeclStmt create(){
        VarDeclStmt node = new VarDeclStmt();
        
        return node;
    }
    
    private void addChild(List<AstNode> list,List nodes){
        if(nodes!=null) list.addAll(nodes);
    }
    
    private void addChild(List<AstNode> list,AstNode node){
        if(node!=null) list.add(node);
    }
    
    public List<AstNode> getChildren(){
        List<AstNode> ls = new LinkedList();
        
        return ls;
    }
    
    public String toString(){
        String str = "VarDeclStmt{\r\n";
        
        str += "  var:" + var.toString()+"\r\n";
        
        return str+"}";
    }
}