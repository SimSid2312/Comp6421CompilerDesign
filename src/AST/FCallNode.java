package AST;

import java.util.List;

import Visitors.Visitor;

public class FCallNode extends Node{

	public FCallNode(){
		super("");
	}


	public FCallNode(Node idNode, Node aParamsNode){
		super("");
		this.addChild(idNode);
		this.addChild(aParamsNode);
	}
	
	public FCallNode(List<Node> listOfDimNodes){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(listOfDimNodes);
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
