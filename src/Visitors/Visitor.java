package Visitors;
import AST.*;

/**
 * Visitor superclass. Can be either an interface or an abstract class. 
 * Needs to have one visit method for each of the visit methods 
 * implemented by any of its subclasses. 
 * 
 * This forces all its subclasses
 * to implement all of them, even if they are not concerned with 
 * processing of this particular subtype, creating visit methods
 * with an empty function body. 
 * 
 * In order to avoid having empty visit functions, one can create 
 * the Visitor class as a normal class with all visitor methods 
 * with empty function bodies.  
 * 
 * These empty methods are executed in case a specific visitor does 
 * not implement a visit method for a specific kind of visitable 
 * object on which accept may be called, but no action is required
 * for this specific visitor.  
 */
 
 public class Visitor {
	 
	 public void visit(ClassListNode node)   {};
	 public void visit(ClassNode node)       {};
	 public void visit(InherListNode node) {};
	 public void visit (MemberListNode node) {};
	 public void visit(DimListNode node)     {};
	 public void visit(FuncDefListNode node) {};
	 public void visit(FuncDefNode node)     {};
	 public void visit(IdNode node)          {};
	 public void visit(Node node)            {};
     public void visit(ProgNode node)        {};
	 public void visit(ForStatNode node)  {};
	 public void visit (StatNode node) {};
	 public void visit(StatBlockNode node)   {};
	 public void visit(TypeNode node)        {};
	 public void visit(VarDeclNode node)     {};
	 public void visit(fParamNode node) {};
	 public void visit(funcDeclNode node) {};
	 public void visit(FuncDefStatBlock node) {};
	 public void visit(FunScope p_node){};
	 public void visit(AdditionOP p_node) {};
	 public void visit(AssignOPNode p_node) {};
	 public void visit(DivisionOP p_node) {};
	 public void visit(DivOPNode p_node) {};
	 public void visit(DotOp p_node) {};
     public void visit(FactorNotNode p_node) {};
     public void visit(FactorSignNode p_node) {};
	 public void visit(FCallNode p_node) {};
	 public void visit(GetStatNode p_node) {};
	 public void visit(IfNode p_node) {};
	 public void visit(IndiceListNode p_node) {};
	 public void visit(MultiplicationOP p_node) {};
	 public void visit(notNode p_node) {};
	 public void visit(ParamListNode p_node) {};
	 public void visit(PutSatNode p_node) {};
	 public void visit(RelationOP p_node){};
	 public void visit(RelExpr p_node) {};
	 public void visit(ReturnStatNode p_node) {};
	 public void visit(signNode p_node) {};
	 public void visit(varDeclListNode p_node){};
	 public void visit(DotOpNode p_node){};
	 
	 //Typechecking 
	 public void visit(Expr node){};
	 public void visit(FactorNode node) {};
	 public void visit (FactorNumNode node) {};
	 public void visit(AddOpNode node)       {};
	 public void visit(AssignStatNode node)  {};
	 public void visit(NumNode numNode)      {};
	 public void visit(MultOpNode node)      {};
	 public void visit(TermNode node) {};
	 public void visit(ArithExpr node) {};
	 public void visit(FactorArithExprNode node) {};
	 public void visit(VarNode node){};
	 public void visit(indiceNode node) {};
	 public void visit(DataMemberNode node){};
	


	 
}