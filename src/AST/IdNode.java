package AST;
import Visitors.Visitor;

public class IdNode extends Node {
	
	public IdNode(String data){
		super(data);
	}
	
	public IdNode(String data, Node parent){
		super(data, parent);
	}
	
	public IdNode(String data, String type){
		super(data, type);
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
