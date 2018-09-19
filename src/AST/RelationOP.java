package AST;

import Visitors.Visitor;

public class RelationOP extends Node{

	public RelationOP(String data){
		super(data);
	}
	
	public RelationOP(String data, Node parent){
		super(data, parent);
	}
	
	public RelationOP(String data, String type){
		super(data, type);
	}
	
	public RelationOP(Node childNode){
		super("");
		this.addChild(childNode);
	}
	
	/**
	 * Every class that may be visited by any visitor needs
	 * to implement the accept method, that calls the appropriate
	 * visit method in the passed visitor, achieving double
	 * dispatch. 
	 * 
	 */
	/*public void accept(Visitor visitor) {
		visitor.visit(this);
	}*/
	/*public void accept(Visitor visitor) {
		for (Node child : this.getChildren())
			child.accept(visitor);
		visitor.visit(this);
	}*/
	public void accept(Visitor p_visitor) {
		p_visitor.visit(this);
	}
}
