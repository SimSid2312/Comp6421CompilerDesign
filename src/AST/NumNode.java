package AST;
import Visitors.Visitor;

public class NumNode extends Node {
	
	public NumNode(String data){
		super(data);
	}
	
	public NumNode(String data,String type){
		super(data,type);
	}
	public NumNode(String data, Node parent){
		super(data, parent);
	}
	
	/**
     * Every class that will be visited needs an accept method, which 
     * then calls the specific visit method in the visitor, achieving
     * double dispatch. 
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
