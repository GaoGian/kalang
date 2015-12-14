/*
Don't modify!This file is generated automately.
*/
package jast.ast;
import java.util.*;
public class TryStmt extends Statement{
    
    public Statement execStmt;
    
    public List<CatchStmt> catchStmts;
    
    public Statement finallyStmt;
    
    
    public TryStmt(){
        
            if(catchStmts == null) catchStmts = new LinkedList();
        
    }
    
    
    public TryStmt(Statement execStmt,List<CatchStmt> catchStmts,Statement finallyStmt){
        
            if(catchStmts == null) catchStmts = new LinkedList();
        
        
            this.execStmt = execStmt;
        
            this.catchStmts = catchStmts;
        
            this.finallyStmt = finallyStmt;
        
    }
    
    
    public static TryStmt create(){
        TryStmt node = new TryStmt();
        
        node.catchStmts = new LinkedList();
        
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
        
        addChild(ls,execStmt);
        
        addChild(ls,catchStmts);
        
        addChild(ls,finallyStmt);
        
        return ls;
    }
    
    public String toString(){
        String str = "TryStmt{\r\n";
        
        str += "  execStmt:" + execStmt.toString()+"\r\n";
        
        str += "  catchStmts:" + catchStmts.toString()+"\r\n";
        
        str += "  finallyStmt:" + finallyStmt.toString()+"\r\n";
        
        return str+"}";
    }
}