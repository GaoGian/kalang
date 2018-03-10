package kalang.compiler;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import kalang.antlr.KalangParser;
import kalang.antlr.KalangParserBaseVisitor;
import kalang.ast.ClassNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author Kason Yang
 */
public class ClassNodeBuilder extends KalangParserBaseVisitor<Object> {

    private ClassNode topClass;

    private ClassNode thisClazz;
    
    private Map<ClassNode,ParserRuleContext> defContext = new HashMap();
    
    private boolean isScript = false;

    AstBuilder astBuilder;
    private final CompilationUnit compilationUnit;
    private DiagnosisReporter diagnosisReporter;

    public ClassNodeBuilder(CompilationUnit compilationUnit, AstBuilder astBuilder) {
        this.compilationUnit = compilationUnit;
        this.astBuilder = astBuilder;
    }
    
    public ClassNode build(KalangParser.CompilationUnitContext ctx){
        this.visitCompilationUnit(ctx);
        return topClass;
    }

    @Override
    public Object visitScriptDef(KalangParser.ScriptDefContext ctx) {
        this.isScript = true;
        //FIXME fix fileName
        //thisClazz.fileName = this.compilationUnit.getSource().getFileName();
        String className = astBuilder.getClassName();
        int modifier = Modifier.PUBLIC;
        topClass = thisClazz = new ClassNode(className, modifier);
        this.defContext.put(topClass, ctx);
        super.visitScriptDef(ctx);
        return null;
    }    

    @Override
    public Object visitClassDef(KalangParser.ClassDefContext ctx) {
        ClassNode oldClass = thisClazz;
        Token nameIdentifier = ctx.name;
        int modifier = astBuilder.parseModifier(ctx.varModifier());
        Token classKind = ctx.classKind;
        boolean isInterface = false;
        if (classKind != null) {
            if (classKind.getText().equals("interface")) {
                modifier |= Modifier.ABSTRACT | Modifier.INTERFACE;
                isInterface = true;
            }
        }
        String classDefName;
        if (oldClass != null) {
            if (nameIdentifier == null) {
                diagnosisReporter.report(Diagnosis.Kind.ERROR,"Identifier excepted", ctx);
                return null;
            }
            classDefName = oldClass.name + "$" + nameIdentifier.getText();
        } else {
            classDefName = astBuilder.getClassName();
        }
        ClassNode theClass = thisClazz = new ClassNode(classDefName, modifier);
        astBuilder.thisClazz = thisClazz;
        this.defContext.put(theClass, ctx);
        if (oldClass == null) {
            this.topClass = theClass;
        } else {
            oldClass.classes.add(thisClazz);
            thisClazz.enclosingClass = oldClass;
        }
        //FIXME fix file name
        //thisClazz.fileName = this.compilationUnit.getSource().getFileName();
        visit(ctx.classBody());
        astBuilder.mapAst(thisClazz, ctx);
        thisClazz = oldClass;
        return null;
    }

    private boolean isNonStaticInnerClass(ClassNode clazz) {
        return clazz.enclosingClass != null && !Modifier.isStatic(clazz.modifier);
    }

    private boolean isDeclaringNonStaticInnerClass() {
        return isNonStaticInnerClass(thisClazz);
    }
    
    public ClassNode getClassNode(){
        return topClass;
    }
    
    public ParserRuleContext getClassNodeDefContext(ClassNode classNode){
        return this.defContext.get(classNode);
    }

    public boolean isScript() {
        return isScript;
    }
    
    public void setDiagnosisHandler(DiagnosisHandler diagnosisHandler){
        this.diagnosisReporter = new DiagnosisReporter(
                this.compilationUnit.getCompileContext()
                , diagnosisHandler, this.compilationUnit.getSource());
    }
    
}
