package AST;

import Visitors.Visitor;

public class fParamNode extends Node {

	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor.   
	 */
	
	public fParamNode(){
		super("");
	}
	
	public fParamNode(Node parent){
		super("", parent);
	}
	
	public fParamNode(Node type, Node id, Node fparamList){
		super(""); 
		this.addChild(type);
		this.addChild(id);
		this.addChild(fparamList);		
	}
	
	/**
	 * Every node should have an accept method, which 
	 * should call accept on its children to propagate
	 * the action of the visitor on its children. 
	 */
	/*public void accept(Visitor visitor) {
		for (Node child : this.getChildren() )
			child.accept(visitor);
		visitor.visit(this);
	}*/
	public void accept(Visitor p_visitor) {
		p_visitor.visit(this);
	}
}
