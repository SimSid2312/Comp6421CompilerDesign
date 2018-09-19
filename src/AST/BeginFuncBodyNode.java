package AST;

import Visitors.Visitor;

public class BeginFuncBodyNode extends Node {

	public BeginFuncBodyNode(String data){
		super(data);
	}
	
	public BeginFuncBodyNode(String data, Node parent){
		super(data, parent);
	}
	
	public BeginFuncBodyNode(String data, String type){
		super(data, type);
	}
	
	/**
	 * Every class that may be visited by any visitor needs
	 * to implement the accept method, that calls the appropriate
	 * visit method in the passed visitor, achieving double
	 * dispatch. 
	 * 
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
}
