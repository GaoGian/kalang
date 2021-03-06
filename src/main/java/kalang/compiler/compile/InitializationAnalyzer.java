package kalang.compiler.compile;

import kalang.compiler.ast.*;
import kalang.compiler.core.Type;
import kalang.compiler.core.VarTable;
import kalang.compiler.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The initialization analyzer class.
 *
 * @author Kason Yang
 */
public class InitializationAnalyzer extends AstVisitor<Object> {

    private AstLoader astLoader;

    private MethodNode method;

    private CompilationUnit source;

    private VarTable<LocalVarNode, Void> assignedVars = new VarTable<>();

    private DiagnosisReporter diagnosisReporter;
    
    private boolean returned = false;

    public InitializationAnalyzer(CompilationUnit source, AstLoader astLoader) {
        this.astLoader = astLoader;
        this.source = source;
        
    }

    public void check(ClassNode clz,MethodNode method) {
        this.diagnosisReporter = new DiagnosisReporter(source);
        this.visit(method);
    }

    @Override
    public Object visit(AstNode node) {
        if (node == null) {
            return null;
        }
        if (node instanceof VarExpr) {
            if (!assignedVars.exist(((VarExpr) node).getVar(), true)) {
                this.diagnosisReporter.report(Diagnosis.Kind.ERROR
                        , ((VarExpr) node).getVar().getName() + " is uninitialized!", ((VarExpr) node).offset);
            }
        }
        return super.visit(node);
    }

    @Override
    public Object visitAssignExpr(AssignExpr node) {
        AssignableExpr to = node.getTo();
        if (to instanceof VarExpr) {
            assignedVars.put(((VarExpr) to).getVar(), null);
        }
        return super.visitAssignExpr(node);
    }

    @Override
    public Type visitTryStmt(TryStmt node) {
        List<VarTable<LocalVarNode, Void>> assignedList = new ArrayList(node.getCatchStmts().size() + 1);
        enterNewFrame();
        if (!visitAndCheckReturned(node.getExecStmt())){
            assignedList.add(assignedVars);
        }
        exitFrame();
        for (CatchBlock cs : node.getCatchStmts()) {
            enterNewFrame();
            if (!visitAndCheckReturned(cs)){
                assignedList.add(assignedVars);
            }
            exitFrame();
        }
        addIntersectedAssignedVar(assignedList);
        Statement finallyStmt = node.getFinallyStmt();
        if (finallyStmt != null) {
            visit(finallyStmt);
        }
        return null;
    }

    @Override
    public Type visitIfStmt(IfStmt node) {
        List<VarTable<LocalVarNode, Void>> assignedVarsList = new ArrayList(2);
        enterNewFrame();
        if (!visitAndCheckReturned(node.getTrueBody())){
            assignedVarsList.add(assignedVars);
        }
        exitFrame();
        enterNewFrame();
        if (!visitAndCheckReturned(node.getFalseBody())){
            assignedVarsList.add(assignedVars);
        }
        exitFrame();        
        addIntersectedAssignedVar(assignedVarsList);
        return null;
    }

    @Override
    public Type visitLoopStmt(LoopStmt node) {
        visit(node.getPreConditionExpr());
        enterNewFrame();
        visit(node.getLoopBody());
        exitFrame();
        enterNewFrame();
        visit(node.getUpdateStmt());
        exitFrame();
        visit(node.getPostConditionExpr());
        return null;
    }

    @Override
    public Object visitMethodNode(MethodNode node) {
        method = node;
        return super.visitMethodNode(node);
    }

    protected void enterNewFrame() {
        assignedVars = new VarTable<>(assignedVars);
    }

    protected void exitFrame() {
        assignedVars = assignedVars.getParent();
    }

    protected void addIntersectedAssignedVar(List<VarTable<LocalVarNode, Void>> assignedVarsList) {
        if (assignedVarsList.isEmpty()){
            return;
        }
        if (assignedVarsList.size()==1){
            for(Map.Entry<LocalVarNode, Void> s:assignedVarsList.get(0).vars().entrySet()){
                assignedVars.put(s.getKey(), null);
            }
            return;
        }
        Set<LocalVarNode>[] assigned = new Set[assignedVarsList.size()];
        for (int i = 0; i < assigned.length; i++) {
            assigned[i] = assignedVarsList.get(i).keySet();
        }
        Set<LocalVarNode> sets = CollectionsUtil.getIntersection(assigned);
        for (LocalVarNode s : sets) {
            assignedVars.put(s, null);
        }
    }

    @Override
    public Object visitCatchBlock(CatchBlock node) {
        assignedVars.put(node.catchVar, null);
        return super.visitCatchBlock(node);
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) {
        returned = true;
        return super.visitReturnStmt(node);
    }

    @Override
    public Object visitThrowStmt(ThrowStmt node) {
        returned = true;
        return super.visitThrowStmt(node);
    }
    
    private boolean visitAndCheckReturned(AstNode node){
        boolean oldReturned = returned;
        returned = false;
        visit(node);
        boolean ret = returned;
        returned = oldReturned;
        return ret;
    }
    
}
