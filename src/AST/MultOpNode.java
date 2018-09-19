package AST;
import java.util.List;

import Visitors.Visitor;

public class MultOpNode extends Node {
	
	public MultOpNode(String data){
		super(data);
	}
	
	public MultOpNode(String data, Node parent){
		super(data, parent);
	}
	
	public MultOpNode(String data, Node leftChild, Node rightChild){
		super(data); 
		this.addChild(leftChild);
		this.addChild(rightChild);
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
