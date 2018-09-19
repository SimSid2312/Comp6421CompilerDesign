package AST;
import java.util.ArrayList;
import java.util.List;

import SymbolTable.*;
import Visitors.Visitor;

public abstract class Node {
	
    private List<Node> m_children    = new ArrayList<Node>();
    private Node m_parent            = null;
    public String m_data            = null;
    public static int m_nodelevel   = 0;

    // The following data members have been added
    // during the implementation of the visitors
    // These could be added using a decorator pattern
    // triggered by a visitor
    
    // introduced by type checking visitor
    public String m_type            = null;
    public String m_linenum         = null;
    public String m_fparamnode_visited=null;
    public String m_operator_type=null;
 // introduced by the construct assignment and expression string visitor
    public String m_subtreeString   = null;
    
 // introduced by symbol table creation visitor
    public SymTab m_symtab           = null;
    public SymTabEntry m_symtabentry = null;
    
    private String node_type =null;
    private String class_name=null;
    private String scope_Spec=null;
    private String var_type=null;
    private String to_checkClass=null;
    // introduced by code generation visitors
    public  String      m_localRegister      = new String(); 
    public  String      m_leftChildRegister  = new String(); 
    public  String      m_rightChildRegister = new String(); 
    public  String      m_moonVarName        = new String();
    public  List<String> my_indiceList_moonVar=new ArrayList<String>();
     public Node() {
    	
    }
    
    public Node(String p_data) {
        this.setData(p_data);
    }
   
    public void set_scope_spec(String scope)
    {
    	scope_Spec=scope;
    }
    public void set_indiceList_moonVar(List<String> mylist)
    {
    	my_indiceList_moonVar.addAll(mylist);
    }
    public List<String> get_indiceList_moonvar()
    {
    	return my_indiceList_moonVar;
    }

    public String get_scope_spec()
    {
    	return scope_Spec;
    }
    public void set_var_type(String input){
          var_type=input;
    }
    
    public String get_var_type()
    {
    	return var_type;
    }
    
 
 
    public Node(String p_data, Node p_parent) {
        this.setData(p_data);
        this.setParent(p_parent);
        p_parent.addChild(this);
    }
    
       
    public Node(String p_data, String p_type) {
        this.setData(p_data);
        this.setType(p_type);
    }    
    
    public void setclass_name()
    {
    	this.class_name="className";
    }
    public String getclass_name()
    {
    	return class_name;
    }
    
    public void set_OP(String op)
    {
    	m_operator_type=op;
    }
    
    public String get_OP()
    {
    	return m_operator_type;
    }
    
    public List<Node> reverseChildList(List<Node> paramlist)
    {
    	List reverselist=new ArrayList<>();
    	for (int i=paramlist.size()-1;i>=0;i--)
    	{
    		reverselist.add(paramlist.get(i));
    	}
    	return reverselist;
    }
    public void setNodeType(String nodetype)
    {
    	this.node_type=nodetype;
    }
    
    public String getNodeType()
    {
    	return node_type;
    }
    public List<Node> getChildren() {
        return m_children;
    }

    public void setParent(Node parent) {
        this.m_parent = parent;
    }

    public Node getParent() {
        return m_parent;
    }

    public void addChild(Node p_child) {
    	p_child.setParent(this);
        this.m_children.add(p_child);
    }

    public String getData() {
        return this.m_data;
    }

    public void setData(String p_data) {
        this.m_data = p_data;
    }

    public String getType() {
        return this.m_type;
    }

     public void setType(String p_type) {
       this.m_type = p_type;
    }
     
     public void setLine(String line)
     {
    	m_linenum=line;
     }
     
     public String getLine()
     {
    	 return m_linenum;
     }
     
     public void set_fparamnode_visited(String input)
     {
     	m_fparamnode_visited=input;
     }
     
     public String get_fparamnode_visited()
     {
     	return m_fparamnode_visited;
     }

    public String getSubtreeString() {
        return this.m_subtreeString;
    }

    public void setSubtreeString(String p_data) {
        this.m_subtreeString = p_data;
    }

    public boolean isRoot() {
        return (this.m_parent == null);
    }

    public boolean isLeaf() {
        if(this.m_children.size() == 0) 
            return true;
        else 
            return false;
    }

    public void removeParent() {
        this.m_parent = null;
    }

    public void print(){
    	System.out.println("===================================================================================");
    	System.out.println("Node type                 | data  | type      | subtreestring | symtabentry");
    	System.out.println("===================================================================================");
    	this.printSubtree();
    	System.out.println("===================================================================================");

    }

    public void printSubtree(){
    	for (int i = 0; i < Node.m_nodelevel; i++ )
    		System.out.print("  ");
    	
    	String toprint = String.format("%-75s" , this.getClass().getName()); 
    	for (int i = 0; i < Node.m_nodelevel; i++ )
    		toprint = toprint.substring(0, toprint.length() - 2);
    	toprint += String.format("%-20s" , (this.getData() == null || this.getData().isEmpty())         ? " | " : " | " + this.getData());    	
    	toprint += String.format("%-20s" , (this.getType() == null || this.getType().isEmpty())         ? " | " : " | " + this.getType());
        toprint += (String.format("%-16s" , (this.m_subtreeString == null || this.m_subtreeString.isEmpty()) ? " | " : " | " + (this.m_subtreeString.replaceAll("\\n+",""))));
    	
    	System.out.println(toprint);
    	
    	Node.m_nodelevel++;
    	List<Node> children = this.getChildren();
		for (int i = 0; i < children.size(); i++ ){
			children.get(i).printSubtree();
		}
		Node.m_nodelevel--;
    }
   

    /**
     * Every class that will be visited needs an accept method, which 
     * then calls the specific visit method in the visitor, achieving
     * double dispatch. 
     */    
    public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}