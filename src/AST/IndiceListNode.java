package AST;

import java.util.List;

import Visitors.Visitor;

public class IndiceListNode extends Node{

	public IndiceListNode(){
		super("");
	}
	
	public IndiceListNode(Node parent){
		super("", parent);
	}
	
	public IndiceListNode(List<Node> listOfIndice){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(listOfIndice);
		for (Node child : Member_NodeList_New)
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
