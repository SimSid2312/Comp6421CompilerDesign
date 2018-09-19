package AST;
import java.util.List;

import Visitors.Visitor;

public class FuncDefListNode extends Node {
	
	public FuncDefListNode(){
		super("");
	}
	
	public FuncDefListNode(Node parent){
		super("", parent);
	}
	
	public FuncDefListNode(List<Node> listOfFuncDefNodes){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(listOfFuncDefNodes);
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