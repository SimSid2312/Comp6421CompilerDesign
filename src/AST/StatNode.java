package AST;

import java.util.List;

import Visitors.Visitor;

public class StatNode extends Node{

	public StatNode(){
		super("");
	}
	
	/*public StatNode(Node parent){
		super("", parent);
	}*/
	
	public StatNode(Node child){
		super("");
		this.addChild(child);
	}
	
	/**
	 * If a visitable class contains members that also may need 
	 * to be visited, it should make these members to accept
	 * the visitor before itself being visited. 
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
