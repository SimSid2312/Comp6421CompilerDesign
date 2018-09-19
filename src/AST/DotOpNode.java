package AST;

import Visitors.Visitor;

public class DotOpNode extends Node{

	public DotOpNode(String data){
		super(data);
	}
	
	public DotOpNode(String data, Node parent){
		super(data, parent);
	}
	
	public DotOpNode(String data, Node leftChild, Node rightChild){
		super(data); 
		this.addChild(leftChild);
		this.addChild(rightChild);
	}
	
	public void accept(Visitor p_visitor) {
		p_visitor.visit(this);
	}
}
