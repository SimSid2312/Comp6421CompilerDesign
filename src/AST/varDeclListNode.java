package AST;

import java.util.List;

import Visitors.Visitor;

public class varDeclListNode extends Node{

	public varDeclListNode(String data){
		super(data);
	}
	
	public varDeclListNode(Node parent){
		super("", parent);
	}
	
	public varDeclListNode(List<Node> listOfVarDecl){
		super("");
		for (Node child : listOfVarDecl)
			this.addChild(child);
	}
	
	/**
	 * If a visitable class contains members that also may need 
	 * to be visited, it should make these members to accept
	 * the visitor before itself being visited. 
	 */
	/*public void accept(Visitor visitor) {
		for (Node child : this.getChildren())
			child.accept(visitor);
		visitor.visit(this);
	}*/
	public void accept(Visitor p_visitor) {
		p_visitor.visit(this);
	}
}
