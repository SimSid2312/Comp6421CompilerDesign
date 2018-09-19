package AST;

import java.util.List;

import Visitors.Visitor;

public class PutSatNode extends Node{

	public PutSatNode(){
		super("");
	}


	public PutSatNode(Node idNode, Node aParamsNode){
		super("");
		this.addChild(idNode);
		this.addChild(aParamsNode);
	}
	
	public PutSatNode(List<Node> listOfDimNodes){
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
