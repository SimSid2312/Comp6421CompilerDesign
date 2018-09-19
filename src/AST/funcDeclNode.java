package AST;

import java.util.List;

import Visitors.Visitor;

public class funcDeclNode extends Node{

	
	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor.   
	 */
	
	public funcDeclNode(){
		super("");
	}
	
	/*public funcDeclNode(Node parent){
		super("", parent);
	}*/
	
	public funcDeclNode(Node child)
	{
		super("");
		this.addChild(child);
	}
	public funcDeclNode(Node type, Node id, List<Node> fparamNodeList){
		super(""); 
		this.addChild(type);
		this.addChild(id);
		List<Node> fparamNodeList_New=this.reverseChildList(fparamNodeList);
		//this.addChild(dimList);	
		for (Node child : fparamNodeList_New)
			this.addChild(child);
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
