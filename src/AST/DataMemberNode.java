package AST;

import java.util.List;

import Visitors.Visitor;

public class DataMemberNode extends Node{

	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor.   
	 */
	
	public DataMemberNode(){
		super("");
	}
	
	public DataMemberNode(Node parent){
		super("", parent);
	}
	
	public DataMemberNode(List<Node> listOfDimNodes){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(listOfDimNodes);
		for (Node child : Member_NodeList_New)
			this.addChild(child);
	}
	
	/**
	 * Every node should have an accept method, which 
	 * should call accept on its children to propagate
	 * the action of the visitor on its children. 
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
