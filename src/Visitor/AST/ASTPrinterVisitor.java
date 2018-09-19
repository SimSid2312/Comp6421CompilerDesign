package Visitor.AST;
import Visitors.*;

import java.io.File;
import java.io.PrintWriter;

import AST.*;
public class ASTPrinterVisitor extends Visitor{

	public String m_outputfilename = new String(); 
	public String m_outputstring   = new String();
	
	
	public ASTPrinterVisitor() {
	}
	
	public ASTPrinterVisitor(String p_filename) {
		this.m_outputfilename = p_filename; 
	}
	
	public void printLine(Node p_node) {
	   	for (int i = 0; i < Node.m_nodelevel; i++ )
	   		m_outputstring += "  ";
    	
    	String toprint = String.format("%-75s" , p_node.getClass().getName()); 
    	for (int i = 0; i < Node.m_nodelevel; i++ )
    		toprint = toprint.substring(0, toprint.length() - 2);
    	toprint += String.format("%-25s" , (p_node.getData() == null || p_node.getData().isEmpty())         ? " | " : " | " + p_node.getData());    	
    	toprint += String.format("%-20s" , (p_node.getType() == null || p_node.getType().isEmpty())         ? " | " : " | " + p_node.getType());
        toprint += (String.format("%-20s" , (p_node.m_subtreeString == null || p_node.m_subtreeString.isEmpty()) ? " | " : " | " + (p_node.m_subtreeString.replaceAll("\\n+",""))));
    	
        m_outputstring += toprint + "\n";
    	
    	Node.m_nodelevel++;
//    	List<Node> children = p_node.getChildren();
//		for (int i = 0; i < children.size(); i++ ){
//			children.get(i).printSubtree();
//		}
		Node.m_nodelevel--;	
	}
	
	
	public void visit(ProgNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		m_outputstring += "=====================================================================\n";
		m_outputstring += "Node type                 | data      | type      | subtreestring\n";
		m_outputstring += "=====================================================================\n";
    	this.printLine(p_node);
    	Node.m_nodelevel++;
    	for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
    	m_outputstring += "=====================================================================\n"; 
		if (!this.m_outputfilename.isEmpty()) {
			File file = new File(this.m_outputfilename);
			try (PrintWriter out = new PrintWriter(file)){ 
			    out.println(this.m_outputstring);
			}
			catch(Exception e){
				e.printStackTrace();}
		}
	}
	
	public void visit(AdditionOP p_node)
	{
		// propagate accepting the same visitor to all the children
				// this effectively achieves Depth-First AST Traversal
		    	this.printLine(p_node);
		    	Node.m_nodelevel++;
				for (Node child : p_node.getChildren())
					child.accept(this);
		    	Node.m_nodelevel--;
	}
	
	public void visit(AddOpNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(ArithExpr p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(DotOpNode p_node){
		
		// propagate accepting the same visitor to all the children
				// this effectively achieves Depth-First AST Traversal
		    	this.printLine(p_node);
		    	Node.m_nodelevel++;
				for (Node child : p_node.getChildren())
					child.accept(this);
		    	Node.m_nodelevel--;
	};
	

	public void visit(AssignOPNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(AssignStatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(ClassListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(ClassNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(DataMemberNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(DimListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(DivisionOP p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(DivOPNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(DotOp p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(Expr p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FactorArithExprNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FactorNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FactorNotNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FactorNumNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FactorSignNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FCallNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(ForStatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(fParamNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(funcDeclNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FuncDefListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FuncDefNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			{
			child.accept(this);}
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FuncDefStatBlock p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(FunScope p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(GetStatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(IdNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(IfNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(IndiceListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(indiceNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(InherListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(MemberListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(MultiplicationOP p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(MultOpNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(notNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(NumNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(ParamListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	
	public void visit(PutSatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	

	public void visit(RelationOP p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	

	public void visit(RelExpr p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(ReturnStatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(signNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(StatBlockNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(StatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(TermNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(TypeNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	public void visit(varDeclListNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(VarDeclNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
	
	public void visit(VarNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	this.printLine(p_node);
    	Node.m_nodelevel++;
		for (Node child : p_node.getChildren())
			child.accept(this);
    	Node.m_nodelevel--;
		
	}
}
