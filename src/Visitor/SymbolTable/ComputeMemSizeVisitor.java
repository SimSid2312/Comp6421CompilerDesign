package Visitor.SymbolTable;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AST.*;
import SymbolTable.*;
import Visitors.Visitor;

public class ComputeMemSizeVisitor extends Visitor{

	public String  m_outputfilename = new String(); 

	public ComputeMemSizeVisitor() {
	}
	
	public ComputeMemSizeVisitor(String p_filename) {
		this.m_outputfilename = p_filename; 
	}
	
	public int sizeOfEntry(Node p_node) {
		int size = 0;
		
		if (p_node.m_symtabentry!=null)
		{		
			System.out.println(p_node.m_type+p_node.getNodeType());
			if(p_node.m_symtabentry.m_type.equals("int"))
			   size = 4;
		    else if(p_node.m_symtabentry.m_type.equals("float"))
			   size = 8;
		    else if (!p_node.m_symtabentry.m_type.contains("error")) // a class variable
		    {
		    	//System.out.println(p_node.get_var_type());
		    	//System.out.println(p_node.m_symtabentry.m_subtable);
		    	//System.out.println(p_node.m_symtabentry.m_subtable.m_size);
		    	size=0-p_node.m_symtabentry.m_subtable.m_size;
		    	System.out.println(size);
		    	
		    }
		// if it is an array, multiply by all dimension sizes
			
			if (!p_node.getNodeType().equals("funcDecl"))
		  {
			  VarEntry ve = (VarEntry) p_node.m_symtabentry; 
			   if(!(ve.m_dims==null))
			       for(Integer dim : ve.m_dims)
				      size *= dim;	
		
	      }
		
		    else if (p_node.getNodeType().equals("funcDecl"))
		    {
		    	FuncDeclEntry fe = (FuncDeclEntry) p_node.m_symtabentry; 
		    	if(!(fe.p_fdeclparam==null))
		    	{
		    		for (String match:fe.p_fdeclparam)
		    		{
		    			for (SymTabEntry child:p_node.getParent().m_symtab.m_symlist)
		    		   {
		    			   if (child.m_kind.equals("fParam") && child.m_name.equals(match.split(" ")[1]))
		    			   {
		    				   size*=child.m_size;
		    			   }
		    		   }
		    		}
		    	}
		    }
		    /*else if (p_node.getNodeType().equals("FcallNode"))
		    {
		    	
		       
		    }*/
		
	   }
	
		System.out.println("return size:"+size);
		return size;
	}
	

	public int sizeOfTypeNode(Node p_node) {
		int size = 0;
		if (p_node.m_symtabentry!=null)
		{
			if(p_node.m_type.equals("int"))
			size = 4;
		    else if(p_node.m_type.equals("float"))
			size = 8;
		}
		return size;
	}
	
	public void visit(ProgNode p_node){
		System.out.print("=================Computing Mem sizes=====================");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println();
		System.out.println("Visiting ProgNode");
		for (Node child : p_node.getChildren() )
			child.accept(this);
		if (!this.m_outputfilename.isEmpty()) {
			File file = new File(this.m_outputfilename);
			try (PrintWriter out = new PrintWriter(file)){ 
			    out.println(p_node.m_symtab);
			}
			catch(Exception e){
				e.printStackTrace();}
		}
	}
	
	public void visit (FuncDefStatBlock p_node) // this block comes after program and in the function Definition
	{
				
		if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("Prog")) // Condition that checks this is Program Block
		{
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			System.out.println("Visiting Program");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			// compute total size and offsets along the way
			// this should be node on all nodes that represent
			// a scope and contain their own table
			for (SymTabEntry entry : p_node.m_symtab.m_symlist){
				entry.m_offset     = p_node.m_symtab.m_size - entry.m_size;
				p_node.m_symtab.m_size -= entry.m_size;
			}
		}
		
		else if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("funcDef"))//FuncDefNode StateBlock
		{
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			System.out.println("Visiting funcDef's FuncDefStatBlock");
			for (Node child : p_node.getChildren() )
				child.accept(this);			
		}	
	}
	
	public void visit(ClassListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting ClassListNode");
		for (Node child : p_node.getChildren())
			child.accept(this);
	};
	
	public void visit(ClassNode p_node){
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting ClassNode");
		for (Node child : p_node.getChildren() )
			child.accept(this);
		// compute total size and offsets along the way		
		// this should be node on all nodes that represent
		// a scope and contain their own table
		for (SymTabEntry entry : p_node.m_symtab.m_symlist){
			entry.m_offset = p_node.m_symtab.m_size - entry.m_size;
			p_node.m_symtab.m_size -= entry.m_size;
		}
	};
	
	public void visit (MemberListNode p_node)
	{	
		//System.out.println("visiting MemberListNode");
     	// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting MemberListNode");
		for (Node child : p_node.getChildren()) {
			child.accept(this);		
		}	
	}
	
	public void visit (fParamNode p_node)
	{	
		//System.out.println("visiting MemberListNode");
     	// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if (p_node.m_symtabentry!=null)
		{	System.out.println("Visiting fParamNode");
		for (Node child : p_node.getChildren()) {
			child.accept(this);	
	      p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
		   }	
		}
	}
	
	public void visit(VarDeclNode p_node){
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if (p_node.m_symtabentry!=null)
		{System.out.println("Visiting vardecl  "+p_node.getChildren().get(1).getData());
		for (Node child : p_node.getChildren() )
			child.accept(this);
		// determine the size for basic variables
		
		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
		}
	}
	
	public void visit(funcDeclNode p_node){
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() )
			child.accept(this);
		// determine the size for basic variables
		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
	}
	
	public void visit(InherListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren())
			child.accept(this);
		
		 //System.out.println(p_node.m_data);
	     HashMap<String,SymTab> tempSymTab=new HashMap<String,SymTab>();
	     tempSymTab=p_node.m_symtabentry.m_inherlist;
	     //sp_node.m_symtabentry.m_size=0;
	     Set<String> str=tempSymTab.keySet();
	     for (String str1:str)
	     {
	    	p_node.m_symtabentry.m_size=p_node.m_symtabentry.m_size - tempSymTab.get(str1).m_size;
	     }
	    
	}

	public void visit(DimListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren())
			child.accept(this);
	}
	
	public void visit(FuncDefListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		 
		System.out.println("Visiting FuncDefListNode");
		for (Node child : p_node.getChildren())
			child.accept(this);
    
	};
	

	public void visit(IdNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting IdNode");
		for (Node child : p_node.getChildren())
			child.accept(this);
	};
	
	public void visit(TypeNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting TypeNode");
		for (Node child : p_node.getChildren() )
			child.accept(this);
	 };
	 
	 public void visit(NumNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
		 System.out.println("Visiting NumNode");
			for (Node child : p_node.getChildren())
				child.accept(this);
			p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
		};

		
	public void visit(StatBlockNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			System.out.println("Visiting StatBlockNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
    }
		
    public void visit(StatNode p_node)
	{
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
	    System.out.println("Visiting StatNode");
		for (Node child : p_node.getChildren() )
			child.accept(this);
	}
    //------------------------------------------------------------------------->
    public void visit(RelExpr p_node)
    {
    	// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	System.out.println("Visiting RelExpr");
		for (Node child : p_node.getChildren())
			child.accept(this);
		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
    }
    
    public void visit(GetStatNode p_node)
	 {
		// propagate accepting the same visitor to all the children
	  	    // this effectively achieves Depth-First AST Traversal
	       System.out.println("Visiting GetStatNode");
	  	    for (Node child : p_node.getChildren())
	  			 child.accept(this);
	 }
    public void visit(MultOpNode p_node) {
  		// propagate accepting the same visitor to all the children
  		// this effectively achieves Depth-First AST Traversal
   		System.out.println("Visiting MultOpNode");
  		for (Node child : p_node.getChildren())
  			child.accept(this);
  		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
  	};
  	
  	public void visit(AddOpNode p_node) {
  		// propagate accepting the same visitor to all the children
  		// this effectively achieves Depth-First AST Traversal
  		System.out.println("Visiting AddOpNode");
  		for (Node child : p_node.getChildren())
  			child.accept(this);
  		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
  	};

     public void visit(AssignStatNode p_node) {
	 // propagate accepting the same visitor to all the children
	 // this effectively achieves Depth-First AST Traversal
     System.out.println("Visiting AssignStatNode");
	  for (Node child : p_node.getChildren())
			child.accept(this);
     };
   //----------------------------------------------------------------------------> 
    public void visit(VarNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	 // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting VarNode");
   	  for (Node child : p_node.getChildren())
   			child.accept(this);
    }
    
    public void visit(DataMemberNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting DataMemberNode");
   	  for (Node child : p_node.getChildren())
   			child.accept(this);
    }
    
    public void visit(ParamListNode p_node) 
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting ParamListNode");
   	  for (Node child : p_node.getChildren())
   			child.accept(this);
    	
    }
    
    public void visit(IndiceListNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting IndiceListNode");
   	  for (Node child : p_node.getChildren())
   			child.accept(this);
    }
    
    public void visit(FactorNode p_node)
    {
    	System.out.println("Visiting FactorNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.accept(this);
		}
    	
    }
 	
    public void visit(Expr p_node) {
		System.out.println("Visiting Expr");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.accept(this);
		}
	}
    
    public void visit(ArithExpr p_node)
    {
    	System.out.println("Visiting ArithExpr");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.accept(this);
		}
    }
  
    public void visit(FCallNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
       	System.out.println("Visiting FCallNode");
		for (Node child : p_node.getChildren() )
			child.accept(this);
		System.out.println(p_node.getNodeType());
		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
	}
    
    public void visit(FuncDefNode p_node){
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	    System.out.println("-----------------");
    		System.out.println("Visiting FuncDefNode");
		    for (Node child : p_node.getChildren() )
			     child.accept(this);
		     // compute total size and offsets along the way
		    // this should be node on all nodes that represent
		   // a scope and contain their own table
	 	  // stack frame contains the return value at the bottom of the stack
		   if (p_node.m_symtab!=null)
		  {
			  p_node.m_symtab.m_size = -(this.sizeOfTypeNode(p_node.getChildren().get(0)));
		      //then is the return addess is stored on the stack frame
		      p_node.m_symtab.m_size -= 4;
		      for (SymTabEntry entry : p_node.m_symtab.m_symlist){
			       entry.m_offset = p_node.m_symtab.m_size - entry.m_size; 
			       p_node.m_symtab.m_size -= entry.m_size;
		   }
		  }
		   System.out.println("-------------");
	}

    public void visit(PutSatNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting PutSatNode");
   	    for (Node child : p_node.getChildren())
   			 child.accept(this);
    }

    public void visit(FactorNumNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting FactorNumNode");
   	    for (Node child : p_node.getChildren())
   			 child.accept(this);
    }

    public void visit(ReturnStatNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
   	    // this effectively achieves Depth-First AST Traversal
        System.out.println("Visiting ReturnStatNode");
   	    for (Node child : p_node.getChildren())
   			 child.accept(this);
    }
    
    public void visit(ForStatNode p_node){
       	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting ForStatNode");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
  	  if (p_node.m_symtabentry!=null)
  		p_node.m_symtabentry.m_size = this.sizeOfEntry(p_node);
    }

    public void visit(IfNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting IfNode");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
    }

    public void visit(indiceNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting indiceNode");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
    }
    
    public void visit(TermNode p_node)
    {
    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting TermNode");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
    	
    }
 
    public void visit(RelationOP p_node)
    {
    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting RelationOP");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
    }
    
    public void visit(DotOp p_node) {
   	 
    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting DotOp");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
	 }
	 
	 public void visit(DotOpNode p_node){
		

    	 // propagate accepting the same visitor to all the children
  	    // this effectively achieves Depth-First AST Traversal
       System.out.println("Visiting DotOpNode");
  	    for (Node child : p_node.getChildren())
  			 child.accept(this);
	 }
	 
	 

}
