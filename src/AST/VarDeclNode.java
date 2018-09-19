package AST;
import java.util.List;

import Visitors.Visitor;

public class VarDeclNode extends Node {
	
	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor.   
	 */
	
	public VarDeclNode(){
		super("");
	}
	
	public VarDeclNode(Node parent){
		super("", parent);
	}
	
	public VarDeclNode(Node type, Node id, Node dimList){
		super(""); 
		this.addChild(type);
		this.addChild(id);
		this.addChild(dimList);		
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