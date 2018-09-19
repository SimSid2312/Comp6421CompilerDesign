package AST;

import java.util.List;

import Visitors.Visitor;

public class FactorNotNode extends Node{

	public FactorNotNode(String data){
		super(data);
	}
	
	public FactorNotNode(String data, Node parent){
		super(data, parent);
	}
	
	public FactorNotNode(String data, String type){
		super(data, type);
	}
	
	public FactorNotNode(Node childNode){
		super("");
		this.addChild(childNode);
	}
	
	public FactorNotNode(List<Node> list_node){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(list_node);
        for (Node child : Member_NodeList_New)
            this.addChild(child);
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
