package AST;

import Visitors.Visitor;

public class FactorNumNode extends Node{
	
	public FactorNumNode(String data){
		super(data);
	}
	
	public FactorNumNode(String data, Node parent){
		super(data, parent);
	}
	
	public FactorNumNode(String data, String type){
		super(data, type);
	}
	
	public FactorNumNode(Node childNode){
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
