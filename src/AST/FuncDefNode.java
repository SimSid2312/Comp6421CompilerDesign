package AST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import LexParser.AST_T;
import SymbolTable.SymTabEntry;
import Visitors.Visitor;

public class FuncDefNode extends Node {
		
	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor. 
	 * In the project, this record would be added to a symbol 
	 * table.  
	 */

	public FuncDefNode(){
		super("");
	}
	
	public FuncDefNode(Node parent){
		super("", parent);
	}
	
	public FuncDefNode(Node type, Node id, Node paramList, Node statBlock){
		super("");
		this.addChild(type);
		this.addChild(id);
		this.addChild(paramList);		
		this.addChild(statBlock);
	}
	
	public FuncDefNode(List<Node> list_node){
		super("");
		List<Node> Member_NodeList_New=this.reverseChildList(list_node);
        for (Node child : Member_NodeList_New)
            this.addChild(child);
	}
	
		
	
	/**
	 * Every node should have an accept method, which 
	 * should call accept on its children to propagate
	 * the action of the visitor on its children. 
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