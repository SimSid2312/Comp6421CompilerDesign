package AST;

import java.util.List;

import Visitors.Visitor;

public class FuncDefStatBlock extends Node{

	public FuncDefStatBlock(String data) {
		super(data);
	}
	
	public FuncDefStatBlock(Node parent){
		super("", parent);
	}
	
	public FuncDefStatBlock(List<Node> list_node){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(list_node);
        for (Node child : Member_NodeList_New)
            this.addChild(child);
	}
	
	public Node getNode()
	{
		return this;
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
