package AST;
import java.util.List;

import Visitors.Visitor;

public class ClassListNode extends Node {
	
	public ClassListNode(){
		super("");
	}
	
	public ClassListNode(Node parent){
		super("", parent);
	}
	
	public ClassListNode(List<Node> listOfClassNodes){
		super("");
		List<Node> ClassList_New=this.reverseChildList(listOfClassNodes);
		for (Node child : ClassList_New)
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