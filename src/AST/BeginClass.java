package AST;

import Visitors.Visitor;

public class BeginClass extends Node{

	public BeginClass(String data){
		super(data);
	}
	
	public BeginClass(String data, Node parent){
		super(data, parent);
	}
	
	public BeginClass(String data, String type){
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
