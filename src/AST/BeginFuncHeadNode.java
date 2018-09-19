package AST;

import Visitors.Visitor;

public class BeginFuncHeadNode extends Node{

	public BeginFuncHeadNode(String data){
		super(data);
	}
	
	public BeginFuncHeadNode(String data, Node parent){
		super(data, parent);
	}
	
	public BeginFuncHeadNode(String data, String type){
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
