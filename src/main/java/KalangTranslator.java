import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kalang.antlr.KalangParser.ArgumentDeclContext;
import kalang.antlr.KalangParser.ArgumentDeclListContext;
import kalang.antlr.KalangParser.ArgumentsContext;
import kalang.antlr.KalangParser.BreakStatContext;
import kalang.antlr.KalangParser.ClassBodyContext;
import kalang.antlr.KalangParser.CompiliantUnitContext;
import kalang.antlr.KalangParser.ContinueStatContext;
import kalang.antlr.KalangParser.DoWhileStatContext;
import kalang.antlr.KalangParser.ExprAssignContext;
import kalang.antlr.KalangParser.ExprGetArrayElementContext;
import kalang.antlr.KalangParser.ExprGetFieldContext;
import kalang.antlr.KalangParser.ExprInvocationContext;
import kalang.antlr.KalangParser.ExprLogicCmpContext;
import kalang.antlr.KalangParser.ExprLogicContext;
import kalang.antlr.KalangParser.ExprMemberInvocationContext;
import kalang.antlr.KalangParser.ExprMidOpContext;
import kalang.antlr.KalangParser.ExprNotOpContext;
import kalang.antlr.KalangParser.ExprPrimayContext;
import kalang.antlr.KalangParser.ExprSelfOpContext;
import kalang.antlr.KalangParser.ExprSelfOpPreContext;
import kalang.antlr.KalangParser.ExprStatContext;
import kalang.antlr.KalangParser.ExpressionContext;
import kalang.antlr.KalangParser.ExpressionsContext;
import kalang.antlr.KalangParser.FieldDeclContext;
import kalang.antlr.KalangParser.FieldDeclListContext;
import kalang.antlr.KalangParser.ForInitContext;
import kalang.antlr.KalangParser.ForStatContext;
import kalang.antlr.KalangParser.ForUpdateContext;
import kalang.antlr.KalangParser.GetterContext;
import kalang.antlr.KalangParser.IfStatContext;
import kalang.antlr.KalangParser.IfStatSuffixContext;
import kalang.antlr.KalangParser.ImportDeclContext;
import kalang.antlr.KalangParser.ImportDeclListContext;
import kalang.antlr.KalangParser.ImportPathContext;
import kalang.antlr.KalangParser.LiteralContext;
import kalang.antlr.KalangParser.MethodDeclContext;
import kalang.antlr.KalangParser.MethodDeclListContext;
import kalang.antlr.KalangParser.ModifierContext;
import kalang.antlr.KalangParser.NewExprContext;
import kalang.antlr.KalangParser.PrimaryIdentifierContext;
import kalang.antlr.KalangParser.PrimaryLiteralContext;
import kalang.antlr.KalangParser.PrimayParenContext;
import kalang.antlr.KalangParser.QualifiedNameContext;
import kalang.antlr.KalangParser.ReturnStatContext;
import kalang.antlr.KalangParser.SetterContext;
import kalang.antlr.KalangParser.StartContext;
import kalang.antlr.KalangParser.StatContext;
import kalang.antlr.KalangParser.StatListContext;
import kalang.antlr.KalangParser.TypeContext;
import kalang.antlr.KalangParser.VarDeclContext;
import kalang.antlr.KalangParser.VarDeclStatContext;
import kalang.antlr.KalangParser.VarInitContext;
import kalang.antlr.KalangParser.WhileStatContext;
import kalang.antlr.KalangVisitor;
import kalang.core.VarObject;
import kalang.core.VarTable;
import jast.ast.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;


public class KalangTranslator extends AbstractParseTreeVisitor<Object> implements KalangVisitor<Object> {

	//short name to full name
	private Map<String,String> fullNames = new HashMap();
	private List<String> importPaths = new LinkedList();
	//VarTable vtb = new VarTable();
	Stack<VarTable> vtbs = new Stack();
	HashMap<VarObject,VarDeclStmt> varDeclStmts = new HashMap();
	List<String> fields = new LinkedList();
	List<String> parameters;
	
	private Map<AstNode,ParseTree> a2p = new HashMap();
	
	private VarTable getVarTable(){
		return vtbs.peek();
	}
	
	public Map<AstNode,ParseTree> getParseTreeMap(){
		return this.a2p;
	}
	
	private void pushVarTable(){
		VarTable vtb;
		if(vtbs.size()>0){
			vtb = new VarTable(getVarTable());
		}else{
			vtb = new VarTable();
		}
		
		vtbs.push(vtb);
	}
	
	private void popVarTable(){
		vtbs.pop();
	}
	
	private VarDeclStmt doVisitVarDeclAndInit(VarDeclContext vd,VarInitContext vi){
		VarDeclStmt vds = this.visitVarDecl(vd);
		if(vi!=null){
			AstNode val = visitVarInit(vi);
			vds.initExpr = (ExprNode) (val);
		}
		a2p.put(vds, vd);
		return vds;
	}
	
	@Override
	public ClassNode visitStart(StartContext ctx) {
		return visitCompiliantUnit(ctx.compiliantUnit());
	}

	@Override
	public ClassNode visitCompiliantUnit(CompiliantUnitContext ctx) {
		//List<ImportNode> imports = this.visitImportDeclList(ctx.importDeclList());
		
		ClassNode cls = visitClassBody(ctx.classBody());
		//cls.imports = imports;
		cls.name=(ctx.Identifier(0).getText());
		if(ctx.modifier()!=null){
			cls.modifier=visitModifier(ctx.modifier());
		}
		
		if(ctx.Identifier().size()>1){
			cls.parentName=(ctx.Identifier(1).getText());
		}
		return cls;
	}

	@Override
	public ClassNode visitClassBody(ClassBodyContext ctx) {
		this.pushVarTable();
		ClassNode cls = new ClassNode();
		cls.fields = this.visitFieldDeclList(ctx.fieldDeclList());
		cls.methods = this.visitMethodDeclList(ctx.methodDeclList());
		a2p.put(cls,ctx);
		return cls;
	}

	@Override
	public List visitFieldDeclList(FieldDeclListContext ctx) {
		List list = new LinkedList();
		for(FieldDeclContext fd:ctx.fieldDecl()){
			list.add(this.visitFieldDecl(fd));
		}
		return list;
	}

	@Override
	public FieldNode visitFieldDecl(FieldDeclContext ctx) {
		String type = ctx.type()==null?"Object":ctx.type().getText();
		FieldNode fo = new FieldNode();
		fo.name=(ctx.Identifier().getText());
		fo.type=(type);
		if(ctx.varInit()!=null){
			fo.initExpr =  (visitVarInit(ctx.varInit()));
		}
		if(ctx.modifier()!=null){
			fo.modifier = visitModifier(ctx.modifier());
		}
		fields.add(fo.name);
		//TODO visit setter and getter
		return fo;
	}

	@Override
	public AstNode visitSetter(SetterContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AstNode visitGetter(GetterContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List visitMethodDeclList(MethodDeclListContext ctx) {
		LinkedList list = new LinkedList();
		for(MethodDeclContext md:ctx.methodDecl()){
			list.add(visitMethodDecl(md));
		}
		return list;
	}

	@Override
	public MethodNode visitMethodDecl(MethodDeclContext ctx) {
		this.pushVarTable();
		this.parameters = new LinkedList();
		String name = ctx.Identifier().getText();
		String type = ctx.type()==null ? "Object" :ctx.type().getText();
		int mdf = 0;
		if(ctx.modifier()!=null){
			mdf = visitModifier(ctx.modifier());
		}
		boolean isStatic = false;
		if(ctx.STATIC()!=null){
			isStatic = true;
		}
		MethodNode method = new MethodNode(mdf,type,name,isStatic);
		BlockStmt body = new BlockStmt();
		method.body = body;
		if(ctx.argumentDeclList()!=null){
			method.parameters = visitArgumentDeclList(ctx.argumentDeclList());
		}
		body.statements = visitStatList(ctx.statList());
		this.popVarTable();
		this.parameters = null;
		a2p.put(method,ctx);
		return method;
	}

	@Override
	public AstNode visitType(TypeContext ctx) {
		//do nothing
		return null;
	}

	@Override
	public List visitArgumentDeclList(ArgumentDeclListContext ctx) {
		List list = new LinkedList();
		for(ArgumentDeclContext ad:ctx.argumentDecl()){
			list.add(visitArgumentDecl(ad));
		}
		return list;
	}

	@Override
	public ParameterNode visitArgumentDecl(ArgumentDeclContext ctx) {
		String name = ctx.Identifier().getText();
		String type = "Object";
		if(ctx.type()!=null){
			type = ctx.type().getText();
		}
		/*VarObject vo = new VarObject();
		this.getVarTable().put(name, vo);
		this.vars.add(vo);
		vo.setName(name);
		vo.setType(type);
		vo.setId(vars.indexOf(vo));*/
		
		ParameterNode pn = new ParameterNode();
		pn.name = name;
		pn.type = type;
		this.parameters.add(name);
		a2p.put(pn,ctx);
		return pn;
	}

	@Override
	public List visitStatList(StatListContext ctx) {
		List list = new LinkedList();
		for(StatContext s:ctx.stat()){
			list.add(visitStat(s));
		}
		return list;
	}

	
	@Override
	public AstNode visitIfStat(IfStatContext ctx) {
		IfStmt ifStmt = new IfStmt();
		BlockStmt trueBody = new BlockStmt();
		BlockStmt falseBody = new BlockStmt();
		ifStmt.trueBody = trueBody;
		ifStmt.falseBody = falseBody;
		ExprNode expr = visitExpression(ctx.expression());
		ifStmt.conditionExpr = expr;
		trueBody.statements = visitStatList(ctx.statList());
		falseBody.statements = visitIfStatSuffix(ctx.ifStatSuffix());
		a2p.put(ifStmt,ctx);
		return ifStmt;
	}

	private ExprNode visitExpression(ExpressionContext expression) {
		return (ExprNode) visit(expression);
	}

	@Override
	public List visitIfStatSuffix(IfStatSuffixContext ctx) {
		LinkedList list = new LinkedList();
		list.add(visitStatList(ctx.statList()));
		return list;
	}

	@Override
	public Statement visitStat(StatContext ctx) {
		return (Statement) visit(ctx.getChild(0));
	}

	/*private void visitAll(ParserRuleContext ctx) {
		for(ParseTree c:ctx.children){
			visit(c);
		}
	}*/

	@Override
	public ReturnStmt visitReturnStat(ReturnStatContext ctx) {
		ExprNode expr = visitExpression(ctx.expression());
		ReturnStmt rs = new ReturnStmt();
		rs.expr = expr;
		a2p.put(rs,ctx);
		return rs;
	}

	@Override
	public AstNode visitVarDeclStat(VarDeclStatContext ctx) {
		return this.doVisitVarDeclAndInit(ctx.varDecl(), ctx.varInit());		
	}

	@Override
	public VarDeclStmt visitVarDecl(VarDeclContext ctx) {
		String name = ctx.Identifier().getText();
		String type = "Object";
		if(ctx.type()!=null){
			type = ctx.type().getText();
		}
		boolean isReadOnly = ctx.getChild(0).getText() == "val";
		//TODO readonly
		VarTable vtb = this.getVarTable();
		if(this.getNodeByName(name)!=null){
			reportError("defined duplicatedly:" + name,ctx.Identifier());
		}
		VarDeclStmt vds = new VarDeclStmt();
		VarObject vo = new VarObject();
		vo.setName(name);
		vo.setType(type);
		//vars.add(vo);
		this.varDeclStmts.put(vo, vds);
		vtb.put(name, vo);
		//Integer vid = vars.indexOf(vo);
		/*
		NameExpr ve = new NameExpr();
		ve.name = name;*/
		
		vds.varName = name;
		vds.type = type;
		//vds
		//vds.varId = vid;
		a2p.put(vds,ctx);
		return vds;
	}

	private void reportError(String string, ParseTree tree) {
		throw new ParseError(string,tree);
	}

	@Override
	public ExprNode visitVarInit(VarInitContext ctx) {
		ExprNode vo = visitExpression(ctx.expression());
		return vo;
	}

	@Override
	public AstNode visitBreakStat(BreakStatContext ctx) {
		BreakStmt bs = new BreakStmt();
		a2p.put(bs,ctx);
		return bs;
	}

	@Override
	public AstNode visitContinueStat(ContinueStatContext ctx) {
		ContinueStmt cs = new ContinueStmt();
		a2p.put(cs,ctx);
		return cs;
	}

	@Override
	public AstNode visitWhileStat(WhileStatContext ctx) {
		//WhileStmt ws = new WhileStmt();
		LoopStmt ws = new LoopStmt();
		BlockStmt body = new BlockStmt();
		ws.loopBody = body;
		AstNode expr = visitExpression(ctx.expression());
		ws.preConditionExpr = (ExprNode) expr;
		body.statements = visitStatList(ctx.statList());
		a2p.put(ws,ctx);
		return ws;
	}

	@Override
	public AstNode visitDoWhileStat(DoWhileStatContext ctx) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoopStmt visitForStat(ForStatContext ctx) {
		this.pushVarTable();
		LoopStmt ls = new LoopStmt();
		BlockStmt body = new BlockStmt();
		ls.loopBody = body;
		ls.initStmts = visitForInit(ctx.forInit());
		AstNode texpr = visitExpression(ctx.expression());
		ls.preConditionExpr = (ExprNode) texpr;
		body.statements = visitStatList(ctx.statList());
		body.statements.addAll(visitForUpdate(ctx.forUpdate()));
		this.popVarTable();
		a2p.put(ls,ctx);
		return ls;
	}

	@Override
	public List visitForInit(ForInitContext ctx) {
		List list = new LinkedList();
		VarDeclStmt vs = this.doVisitVarDeclAndInit(ctx.varDecl(), ctx.varInit());
		list.add(vs);
		return list;
	}

	@Override
	public List visitForUpdate(ForUpdateContext ctx) {
		return visitExpressions(ctx.expressions());
	}

	@Override
	public List visitExpressions(ExpressionsContext ctx) {
		List list = new LinkedList();
		for(ExpressionContext e:ctx.expression()){
			AstNode expr = visitExpression(e);
			list.add(new ExprStmt((ExprNode) expr));
		}
		return list;
	}

	@Override
	public ExprStmt visitExprStat(ExprStatContext ctx) {
		AstNode expr = visitExpression(ctx.expression());
		ExprStmt es = new ExprStmt();
		es.expr = (ExprNode) expr;
		a2p.put(es,ctx);
		return es;
	}

	@Override
	public AstNode visitExprPrimay(ExprPrimayContext ctx) {
		return (AstNode) visit(ctx.primary());
	}

	@Override
	public InvocationExpr visitExprMemberInvocation(ExprMemberInvocationContext ctx) {
		InvocationExpr ie = this.getInvocationExpr(
				null
				, ctx.Identifier().getText()
				,ctx.arguments());
		a2p.put(ie,ctx);
		return ie;
	}

	@Override
	public AssignExpr visitExprAssign(ExprAssignContext ctx) {
		AstNode to = visitExpression(ctx.expression(0));
		AstNode from = visitExpression(ctx.expression(1));
		AssignExpr aexpr = new AssignExpr();
		aexpr.from = (ExprNode) from;
		aexpr.to = (ExprNode) to;
		a2p.put(aexpr, ctx);
		return aexpr;
	}

	@Override
	public AstNode visitExprMidOp(ExprMidOpContext ctx) {
		String op = ctx.getChild(1).getText();
		BinaryExpr be = new BinaryExpr();
		be.expr1 = (ExprNode) visitExpression(ctx.expression(0));
		be.expr2 = (ExprNode) visitExpression(ctx.expression(1));
		be.operation = op;
		a2p.put(be, ctx);
		return be;
	}
	
	private InvocationExpr getInvocationExpr(AstNode expr,String methodName,ArgumentsContext arguments){
		InvocationExpr is = new InvocationExpr();
		is.methodName =methodName;
		is.target = (ExprNode) expr;
		is.arguments = visitArguments(arguments);
		return is;
	}

	@Override
	public AstNode visitExprInvocation(ExprInvocationContext ctx) {
		InvocationExpr ei = this.getInvocationExpr(
				visitExpression(ctx.expression())
				, ctx.Identifier().getText()
				, ctx.arguments());
		a2p.put(ei, ctx);
		return ei;
	}

	@Override
	public AstNode visitExprNotOp(ExprNotOpContext ctx) {
		UnaryExpr ue = new UnaryExpr();
		ue.expr = (ExprNode) visitExpression(ctx.expression());
		ue.preOperation = ctx.getChild(0).getText();
		a2p.put(ue, ctx);
		return ue;
	}

	@Override
	public AstNode visitExprGetField(ExprGetFieldContext ctx) {
		AstNode expr = visitExpression(ctx.expression());
		String name = ctx.Identifier().getText();
		FieldExpr fe = new FieldExpr();
		fe.target = (ExprNode) expr;
		fe.fieldName = name;
		a2p.put(fe, ctx);
		return fe;
	}

	@Override
	public AstNode visitExprLogicCmp(ExprLogicCmpContext ctx) {
		BinaryExpr be = new BinaryExpr();
		be.expr1 = (ExprNode) visitExpression(ctx.expression(0));
		be.expr2 = (ExprNode) visitExpression(ctx.expression(1));
		be.operation = ctx.getChild(1).getText();
		a2p.put(be, ctx);
		return be;
	}

	@Override
	public AstNode visitExprSelfOp(ExprSelfOpContext ctx) {
		UnaryExpr ue = new UnaryExpr();
		ue.postOperation = ctx.getChild(1).getText();
		ue.expr = (ExprNode) visitExpression(ctx.expression());
		a2p.put(ue, ctx);
		return ue;
	}

	@Override
	public AstNode visitExprLogic(ExprLogicContext ctx) {
		BinaryExpr be = new BinaryExpr();
		be.expr1 = (ExprNode) visitExpression(ctx.expression(0));
		be.expr2 = (ExprNode) visitExpression(ctx.expression(1));
		be.operation = ctx.getChild(1).getText();
		a2p.put(be, ctx);
		return be;
	}

	@Override
	public UnaryExpr visitExprSelfOpPre(ExprSelfOpPreContext ctx) {
		String op = ctx.getChild(0).getText();
		UnaryExpr ue = new UnaryExpr();
		ue.expr = (ExprNode) visitExpression(ctx.expression());
		ue.preOperation = op;
		a2p.put(ue, ctx);
		return ue;
	}

	@Override
	public ElementExpr visitExprGetArrayElement(ExprGetArrayElementContext ctx) {
		ElementExpr ee = new ElementExpr();
		ee.target = (ExprNode) visitExpression(ctx.expression(0));
		ee.key = (ExprNode) visitExpression(ctx.expression(1));
		a2p.put(ee, ctx);
		return ee;
	}

	@Override
	public ExprNode visitPrimayParen(PrimayParenContext ctx) {
		return visitExpression(ctx.expression());
	}

	@Override
	public ConstExpr visitPrimaryLiteral(PrimaryLiteralContext ctx) {
		return visitLiteral(ctx.literal());
	}

	@Override
	public AstNode visitPrimaryIdentifier(PrimaryIdentifierContext ctx) {
		String name = ctx.Identifier().getText();
		return getNodeByName(name);
	}

	private AstNode getNodeByName(String name) {
		VarTable vtb = this.getVarTable();
		if(vtb.exist(name)){
			VarExpr ve = new VarExpr();
			VarObject vo = vtb.get(name);
			//Integer vid = vars.indexOf(vo);
			//ve.id = vid;
			ve.declStmt = this.varDeclStmts.get(vo);
			return ve;
		}else if(fields.contains(name)){
			FieldExpr fe = new FieldExpr();
			fe.fieldName = name;
			return fe;
		}else if(fullNames.containsKey(name)){
			return new ClassExpr(fullNames.get(name));
		}else if(parameters.contains(name)){
			return new ParameterExpr(name);
		}else{
			//TODO find path class
		}
		return null;
	}

	@Override
	public ConstExpr visitLiteral(LiteralContext ctx) {
		ConstExpr ce = new ConstExpr();
		String t = ctx.getText();
		//TODO parse value
		ce.value = t;
		if(ctx.IntegerLiteral()!=null){
			ce.type = "int";
		}else if(ctx.FloatingPointLiteral()!=null){
			ce.type = "float";
		}else if(ctx.BooleanLiteral()!=null){
			ce.type = "boolean";
		}else if(ctx.CharacterLiteral()!=null){
			ce.type = "char";
		}else if(ctx.StringLiteral()!=null){
			ce.type = "String";
		}else{
			ce.type = "null";
		}
		a2p.put(ce, ctx);
		return ce;
	}

	@Override
	public List<ExprNode> visitArguments(ArgumentsContext ctx) {
		LinkedList<ExprNode> list = new LinkedList();
		for(ExpressionContext e:ctx.expression()){
			ExprNode expr = visitExpression(e);
			list.add(expr);
		}
		return list;
	}
	
	private List doVisitAll(List list){
		List retList = new LinkedList();
		for(Object i:list){
			retList.add(visit((ParseTree) i));
		}
		return retList;
	}

	@Override
	public Object visitImportDeclList(ImportDeclListContext ctx) {
		doVisitAll(ctx.importDecl());
		return null;
	}

	@Override
	public Object visitImportDecl(ImportDeclContext ctx) {
		String name = ctx.importPath().getText();
		if(name.endsWith("*")){
			this.importPaths.add(name);
		}else{
			String[] namePs = name.split("\\.");
			this.fullNames.put(namePs[namePs.length-1], name);
		}
		return null;
	}

	@Override
	public Object visitQualifiedName(QualifiedNameContext ctx) {
		//do nothing
		return null;
	}

	@Override
	public Object visitImportPath(ImportPathContext ctx) {
		//do nothing
		return null;
	}

	@Override
	public Integer visitModifier(ModifierContext ctx) {
		int m = 0;
		for(ParseTree n:ctx.children){
			String text = n.getText();
			switch(text){
			case "public":
				m += Modifier.PUBLIC;break;
			case "private":
				m += Modifier.PRIVATE;break;
			case "protected":
				m += Modifier.PROTECTED;break;
				default:
					System.err.println("Unknown modifier" + text);
			}
		}
		return m;
	}

	@Override
	public NewExpr visitNewExpr(NewExprContext ctx) {
		NewExpr newExpr = new NewExpr();
		newExpr.type = ctx.Identifier().getText();
		newExpr.arguments = this.visitArguments(ctx.arguments());
		a2p.put(newExpr, ctx);
		return newExpr;
	}

}