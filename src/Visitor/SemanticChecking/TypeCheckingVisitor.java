package Visitor.SemanticChecking;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import AST.*;
import LexParser.AST_T;
import LexParser.ParseT;
import SymbolTable.*;
import Visitor.SymbolTable.SymTabCreationVisitor;
import Visitors.Visitor;
/**
 * Visitor to compute the type of subexpressions and assignment statements. 
 * 
 * This applies only to nodes that are part of expressions and assignment statements i.e.
 * AddOpNode, MultOpNode, and AssignStatNode. 
 * 
 */

public class TypeCheckingVisitor extends Visitor {

	public String m_outputfilename = new String();
	public String m_errors         = new String();
    
	public TypeCheckingVisitor() {
	}
	
	public TypeCheckingVisitor(String p_filename) {
		this.m_outputfilename = p_filename; 
	}
	
	public void visit(AssignStatNode p_node){ 
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if(p_node.m_symtab!=null)
		{
			System.out.println("Visiting AssignStatNode");
		    for (Node child : p_node.getChildren() )
		  {
			 child.m_symtab = p_node.m_symtab;
			 child.accept(this);
		  }
		
		    System.out.println( p_node.getChildren().get(0).getNodeType());
		
		 String leftOperandType  = p_node.getChildren().get(0).getType();
		 String rightOperandType = p_node.getChildren().get(1).getType();
				
		if (rightOperandType!=null && rightOperandType.equals("integer"))
			rightOperandType="int";
	
	      if (leftOperandType!=null && leftOperandType.equals("integer"))
		   leftOperandType="int";
			
        System.out.println(p_node.getChildren().get(0).getData());
        
		if(leftOperandType.equals(rightOperandType))
			p_node.setType(leftOperandType);
		else{
			p_node.setType("typeerror");
			String line="";
			if(p_node.getChildren().get(0).getChildren().get(0).getNodeType().equals("DotOPNode"))
			{
				line=p_node.getChildren().get(0).getLine();
			}
			else 
			{
				line=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getLine();
			}
			 
			this.m_errors += "AssignStatNode type error: " 
					+ p_node.getChildren().get(0).getData()
					+ "(" + p_node.getChildren().get(0).getType() + ")"
					+  " and "
					+ p_node.getChildren().get(1).getData()
					+ "(" + p_node.getChildren().get(1).getType() + ")"
					+ " at line num:"+ line
					+ "\n";
		 }
	   
	   }
		
	}
	
	
	public void visit(DotOpNode p_node){
		
		if(p_node.m_symtab!=null)
		{ 
			System.out.println("Visiting DotOpNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren() )
			{	 
				 child.m_symtab = p_node.m_symtab;
				 child.accept(this);
				 
			}	 
			
			System.out.println(p_node.m_symtab);
			//System.out.println("here"+p_node.getChildren().get(1).getData());
			String leftchildData=(p_node.getChildren().get(0).getData());
			String rightchildData=(p_node.getChildren().get(1).getData());
			System.out.println("leftchildData"+leftchildData);
			System.out.println("rightchildData"+rightchildData+"---->"+rightchildData.split(":")[0]);
			System.out.println();
			Boolean found=false;
			String object_dim="";
			System.out.println(p_node.m_symtab);
			if (p_node.getChildren().get(0).m_type.contains("error"))
			{
				if (p_node.getChildren().get(0).m_type.equals("UndefinedClasserror"))
				{
					if((p_node.getChildren().get(1).m_symtabentry!=null && p_node.getChildren().get(1).getNodeType().equals("FcallNode")))
					{
						
					p_node.getChildren().get(1).m_symtabentry.m_type="UndefinedClassMembererror";
					p_node.setType("UndefinedClassMembererror");
					System.out.println(p_node.getChildren().get(1).m_symtabentry);
					this.m_errors += "UndefinedClassMembererror: " 
							+ rightchildData
							+ "in class:"
							+  p_node.getChildren().get(1).getChildren().get(0).getData()
							+ " at line num:"+p_node.getLine() 
							+ "\n";
					found = true;
					}
					else 
					found=false;
				}
			}
			else
			{for (SymTabEntry str:p_node.m_symtab.m_symlist)
			{
				
				if (// this ensure the dimension of the object must be present while using it					
					str.m_name.split(":")[0].equals(leftchildData.split(":")[0]) 
					&& str.m_type.equals(p_node.get_var_type().split(":")[1])
					&& str.m_name.split(":")[1].replaceAll("//s+", "").split(",").length==leftchildData.split(":")[1].replaceAll("//s+", "").split(",").length
					&& !str.m_name.equals("litval")) // here we match both name and the class
				{
					System.out.println("inside");
					object_dim="match";
					String leftchildType=str.m_type;
					System.out.println("leftchildType"+leftchildType);
					System.out.println(leftchildData.split(":")[1]);
					System.out.println(str.m_name.split(":")[1]);
					//System.out.println(str.m_subtable);
					for (SymTabEntry classentries:str.m_subtable.m_symlist)
					{
						//System.out.println(classentries.m_name);
						if(classentries.m_kind.equals("var") 
						   && classentries.m_name.split(":")[0].equals(rightchildData.split(":")[0]))
						   
						{
							System.out.println("classentries.m_kind:"+classentries.m_kind);
							System.out.println("here i am "+classentries.m_name);
							System.out.println("rightchildData"+rightchildData);
							String[] classmem=classentries.m_name.split(":")[1].trim().split(",");
							String[]  nodemem=rightchildData.split(":")[1].trim().split(",");
							
							if (nodemem[0].equals(""))
							{
								//System.out.println(true);
								nodemem[0]="";
							}
							if (classmem[0].equals("[]"))
							{
								//System.out.println(true);
								classmem[0]="";
							}
							System.out.println("here:"+classmem[0]+"--->here:"+nodemem[0]);
							System.out.println(classmem.length==nodemem.length);
							//found matching member
							if (classmem.length==nodemem.length && nodemem.length>1 && !classmem[0].equals("") && !nodemem[0].equals("")) // dimensions matchinfor array
							{
							p_node.getChildren().get(1).getChildren().get(0).setType(classentries.m_type);
							p_node.setType(classentries.m_type);
							p_node.setData(leftchildData+"."+rightchildData.replace(",", " "));
							found=true;
							System.out.println(p_node.getData());
							System.out.println(p_node.getType());
							System.out.println("lengh class:"+ classentries.m_name.split(":")[1].split(",").length);
							System.out.println("lenght node:"+ rightchildData.split(":")[1].split(",").length);
							}
							else if (nodemem.length==1 && classmem.length==nodemem.length && classmem[0].equals("") && nodemem[0].equals(""))
							{
								p_node.getChildren().get(1).getChildren().get(0).setType(classentries.m_type);
								p_node.setType(classentries.m_type);
								p_node.setData(leftchildData+"."+rightchildData.replace(",", " "));
								found=true;
								System.out.println(p_node.getData());
								System.out.println(p_node.getType());
								System.out.println("lengh class:"+ classentries.m_name.split(":")[1].split(",").length);
								System.out.println("lenght node:"+ rightchildData.split(":")[1].split(",").length);
							}
							
							System.out.println("found:"+found);	
							
								
						}
						else if (classentries.m_kind.equals("func")
								&& classentries.m_name.split(":")[0].equals(rightchildData.split(":")[0]))
						{
							System.out.println("classentries.m_kind:"+classentries.m_kind);
							p_node.getChildren().get(1).getChildren().get(0).setType(classentries.m_type);
							//----------> Matching the Function dimension----------------------------------->
							List<String>class_param_type=new ArrayList<String>();
							List<String> class_param_dim=new ArrayList<String>();
						    List<String> node_param_type=new ArrayList<String>();
						    List<String> node_param_dim =new ArrayList<String>();
							for (int i=0;i<classentries.p_fdeclparam.size();i++)
							{								
								class_param_type.add(classentries.p_fdeclparam.get(i).split(" ")[0]);
								class_param_dim.add(classentries.p_fdeclparam.get(i).split(" ")[2]);
								
							}							
							System.out.println(p_node.getChildren().get(1).getChildren().get(1).getNodeType());
							System.out.println("here data:"+p_node.getChildren().get(1).getChildren().get(1).getData());
							System.out.println("here type"+p_node.getChildren().get(1).getChildren().get(1).getType());
							
							if (p_node.getChildren().get(1).getChildren().get(1).getType().equals("integer"))
								p_node.getChildren().get(1).getChildren().get(1).setType("int");//="int";
							node_param_dim.addAll(Arrays.asList(p_node.getChildren().get(1).getChildren().get(1).getData().split("/,")));
				node_param_type.addAll(Arrays.asList(p_node.getChildren().get(1).getChildren().get(1).getType().split("/,")));
							
							
						
							System.out.println(class_param_type);
							System.out.println(class_param_dim);
						    System.out.println(node_param_type);
							System.out.println(node_param_dim);
							Boolean type_match=false;
							int dim_match=0;
							
							
							//if (node_param_type.size()==1 && node_param_t)
							
							if (node_param_type.size()==1 & node_param_type.get(0).equals(""))
							{
								System.out.println("inner");
								node_param_type.clear();
								node_param_dim.clear();
							}
							
							System.out.println(class_param_type.size());
							System.out.println(node_param_type.size());
							System.out.println(class_param_dim.size());
							System.out.println(node_param_dim.size());
							
							if (class_param_type.size()==node_param_type.size() && class_param_dim.size()==node_param_type.size())
							{
								if (class_param_type.size()==0)
									type_match=true;
								else
								{
									for (int i=0;i<class_param_type.size();i++)
								{									
									if (class_param_type.get(i).equals(node_param_type.get(i)) 
										|| (node_param_type.get(i).equals("integer") && class_param_type.get(i).equals("int"))  )
										type_match=true;
									
								}
								
								for (int i=0;i<node_param_dim.size();i++)
								{
									int a=node_param_dim.get(i).trim().split(",").length;
									int b=class_param_dim.get(i).trim().split(",").length;
								     if (a==b)
								    	 dim_match++;
								}
								
							    }
							}
							//--------------------> Logic to match dim and type Ends here------------------------->
							if (dim_match==node_param_dim.size() && type_match==true )
							{found=true;
							p_node.setType(classentries.m_type);
							p_node.setData(leftchildData.split(":")[0]+"."+rightchildData+"()");
							//System.out.println(p_node.getData());
							//System.out.println(p_node.getType());
							}
							System.out.println(found+" "+type_match+" "+dim_match+" "+node_param_dim.size());
						}				
								
					}
				}
				
			  }
			}
			for (Node i:p_node.getChildren())
			{
				if (i.getNodeType().equals("FcallNode") && found!=false)
				{
					//System.out.println("here------------------>");
					i.m_symtabentry.m_type=p_node.getType();
					i.setType(p_node.getType());
					p_node.setData(i.getData());
				}
			}
			
			
			p_node.m_moonVarName=p_node.getData();
			if (found==false && !object_dim.equals(""))// Error throw
			{
				p_node.setType("UndefinedClassMembererror");
				for (Node i:p_node.getChildren())
				{
					if (i.getNodeType().equals("FcallNode"))
					{
						i.m_symtabentry.m_type=p_node.getType();
						i.setType(p_node.getType());
						p_node.setData(i.getData());
					}
				}
				if(p_node.m_symtabentry!=null)
					p_node.m_symtabentry.m_type="UndefinedClassMembererror";
			
				this.m_errors += "UndefinedClassMembererror: " 
						+ rightchildData
						+ "in class:"
						+  p_node.getChildren().get(1).getChildren().get(0).getData()
						+ " at line num:"+p_node.getLine() 
						+ "\n";
			}
			else if (object_dim.equals("") && found==false)
			{
				p_node.setType("UndefinedIdentifiererror");
				for (Node i:p_node.getChildren())
				{
					if (i.getNodeType().equals("FcallNode"))
					{
						i.m_symtabentry.m_type=p_node.getType();
						i.setType(p_node.getType());
						p_node.setData(i.getData());
					}
				}
				if(p_node.m_symtabentry!=null)
					p_node.m_symtabentry.m_type="UndefinedIdentifiererror";
			
				this.m_errors += "UndefinedIdentifiererror: " 
						+  p_node.getChildren().get(0).getData()						
						+ " at line num:"+p_node.getLine() 
						+ "\n";
			}
			
		}
	System.out.println(p_node.m_symtab);
		
	 }
	
	
	public void visit(RelExpr p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting RelExpr");
			String op="";
		    List<String> child_type= new ArrayList<String>();
		    for (Node child : p_node.getChildren() )
		   {
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
			  
			  if (child.getNodeType().equals("RelationOPeratorNode"))
			  {
				child_type.add(child.getData());
				op=child.getData();
			  }
			  else	
			  child_type.add(child.getType());
			
		   }
		
		//System.out.println(child_type);
		 for (int i=0;i<child_type.size();i++)
	    {
		   if (child_type.get(i).equals("integer"))
		   {
			 child_type.set(i, "int");
		   }
	    }
	 
		// System.out.println("updated to.. "+ child_type);
	    if (child_type.get(0).equals(child_type.get(2)))
	    {
	    	p_node.setType(child_type.get(0));
	    	 p_node.setData(p_node.getChildren().get(0).getData());
	    	 p_node.m_symtabentry.m_type=child_type.get(0);
	    	 System.out.println(p_node.m_moonVarName);
	    	 System.out.println("RelExpr: "+op+" "+p_node.m_moonVarName);
	    }
		
	    else
		{
			p_node.setType("typeerror");
			this.m_errors += "RelExpr type error: " 
					+ p_node.get_OP()+" "
					+ p_node.getChildren().get(0).getData()
					+ "(" + p_node.getChildren().get(0).getType() + ")"
					+  " and "
					+ p_node.getChildren().get(2).getData()
					+ "(" + p_node.getChildren().get(2).getType() + ")"
					+ " at line number:"+ p_node.getLine()
					+ "\n";
			 p_node.m_symtabentry.m_type="typeerror";
		}		
		
	   }
	}

	public void visit(AddOpNode p_node){ 
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		
		if(p_node.m_symtab!=null)
		{
			System.out.println("Visiting AddOpNode");
		    for (Node child : p_node.getChildren() )
		   {
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
		   }
			
		
		  String leftOperandType  = p_node.getChildren().get(0).getType();
		  String rightOperandType = p_node.getChildren().get(1).getType();
		
		
		  if (rightOperandType!=null && rightOperandType.equals("integer"))
			  rightOperandType="int";
	
	     if (leftOperandType!=null && leftOperandType.equals("integer"))
		     leftOperandType="int";
		
		  if( leftOperandType.equals(rightOperandType))
		  {
			  p_node.setType(leftOperandType);
			 // p_node.setData(p_data);
			  if (p_node.m_symtabentry!=null)
			  {
				  p_node.m_symtabentry.m_type=leftOperandType; //Updating the symboltable entry
			  }
			  p_node.setData(p_node.getChildren().get(0).getData()+p_node.get_OP()+p_node.getChildren().get(1).getData());
			  System.out.println("AddOpNode: "+p_node.getData()+" "+p_node.m_moonVarName);
			  System.out.println(p_node.getChildren().get(0).m_moonVarName);
			  System.out.println(p_node.getChildren().get(1).m_moonVarName);
		  }
		  else{
			 
			  
			 p_node.setType("typeerror");
			 this.m_errors += "AddOpNode type error:  " 
					+ p_node.get_OP()
					+ " (" + p_node.getChildren().get(0).getType() + ")"
					+  " and "
					+ p_node.getChildren().get(1).getData()
					+ " (" + p_node.getChildren().get(1).getType() + ")"
					+ " at line number:"+ p_node.getLine()
					+ "\n";
			  if (p_node.m_symtabentry!=null)
			 p_node.m_symtabentry.m_type="typeerror";
		   }
		
		}
	}
	
	public void visit(MultOpNode p_node){ 
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if(p_node.m_symtab!=null)
		{System.out.println("Visiting MultOpNode");
		for (Node child : p_node.getChildren() )
		{
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
			
		
		String leftOperandType  = p_node.getChildren().get(0).getType();
		String rightOperandType = p_node.getChildren().get(1).getType();
		
		if (rightOperandType!=null && rightOperandType.equals("integer"))
				rightOperandType="int";
		
		if (leftOperandType!=null && leftOperandType.equals("integer"))
			leftOperandType="int";
				
		if(leftOperandType.equals(rightOperandType))
			{
			  p_node.setType(leftOperandType);
			  if (p_node.m_symtabentry!=null)
			  { p_node.m_symtabentry.m_type=leftOperandType;}
			  
			  System.out.println("MultOpNode: "+p_node.getData()+" "+p_node.m_moonVarName);
			  p_node.setData(p_node.getChildren().get(0).getData()+p_node.get_OP()+p_node.getChildren().get(1).getData());
			 
			}
		else{
			p_node.setType("typeerror");
			this.m_errors += "MultOpNode type error: " 
					+ p_node.get_OP()
					+ "(" + p_node.getChildren().get(0).getType() + ")"
					+  " and "
					+ p_node.getChildren().get(1).getData()
					+ "(" + p_node.getChildren().get(1).getType() + ")"
					+ " at line number:"+ p_node.getLine()
					+ "\n";
			  if (p_node.m_symtabentry!=null)
			p_node.m_symtabentry.m_type="typeerror";
		    }
		   
	   }
	}
	
	public void visit(ProgNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting ProgNode");
		for (Node child : p_node.getChildren())
		{
			//child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		 	try 
			{ 
				ParseT.Semantic_err.write(this.m_errors);
				ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
				ParseT.Semantic_err.newLine();
				
							
			}
			catch(Exception e){
				e.printStackTrace();}
		 	
		 	
		
	}
	
	public void visit(ClassListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting ClassListNode");
		for (Node child : p_node.getChildren())
			{//child.m_symtab = p_node.m_symtab;
			child.accept(this);}
	}
	

	public void visit(ClassNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if(p_node.m_symtab!=null) // The class will only be visited if its m_symtab not null
		{System.out.println("Visiting ClassNode:"+p_node.getChildren().get(0).getData()+p_node.getChildren().get(0).getLine());
		for (Node child : p_node.getChildren())
		{ child.m_symtab = p_node.m_symtab;
		  child.accept(this);
		  
		 }
		System.out.println("-------------");
		}
	}
	
	public void visit (MemberListNode p_node)
	{	
		System.out.println("visiting MemberListNode");
     	// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(FuncDefListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting FuncDefListNode");
		for (Node child : p_node.getChildren())
			child.accept(this);
	};
	
	public void visit(FuncDefNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if(p_node.m_symtab!=null)  // The function definition will only be visited if its m_symtab not null
		{
			System.out.println("Visiting FuncDefNode:"+p_node.getChildren().get(1).getData()+p_node.getChildren().get(0).getLine());
			for (Node child : p_node.getChildren())
		    {child.accept(this);
	        }	
			if (p_node.getChildren().get(2).getData().equals("::"))
			{
				p_node.setData(p_node.getChildren().get(3).getData());
				p_node.setType(p_node.getChildren().get(0).getType());
			}
			else 
		     {p_node.setData(p_node.getChildren().get(1).getData());
		      p_node.setType(p_node.getChildren().get(1).getType());
		     }
		     System.out.println("-------------");
		}
	}
		
    public void visit (FuncDefStatBlock p_node) // this block comes after program
   {		
	  if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("Prog")) // Condition that checks this is Program Block
	  {
		System.out.println("Visiting FuncDefStatBlock (ProgramBlockNode)");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() ) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
		}
	  }
	
	 else if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("funcDef"))//FuncDefNode
	 {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting FuncDefStatBlock");
					for (Node child : p_node.getChildren() ) {
							child.m_symtab = p_node.m_symtab;
							child.accept(this);
					}
	 }
	
	
    }

   public void visit(InherListNode p_node){
	
	  System.out.println("Visiting InherListNode");
	// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	 for (Node child : p_node.getChildren() ) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	
   }

   public void visit(VarDeclNode p_node) {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	if (p_node.m_symtab!=null)
	{
		System.out.println("Visiting VarDeclNode:"+p_node.getChildren().get(1).getData()+p_node.getChildren().get(1).getLine());
	    for (Node child : p_node.getChildren() )
	    {
	    	
	    	child.accept(this);
	    	
	    }
	    
	}
  }

   public void visit(TypeNode p_node) {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	 if (p_node.m_symtab!=null)
	 {
		System.out.println("Visiting TypeNode");
	    for (Node child : p_node.getChildren())
		    child.accept(this);
	    p_node.m_type = p_node.getData();
	    //System.out.println(p_node.m_type);
	 }
   }

   public void visit(IdNode p_node) {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	if (p_node.m_symtab!=null)
	{
		System.out.println("Visiting IdNode:"+p_node.getData());
	    for (Node child : p_node.getChildren())
		  child.accept(this);
	}
  }


  public void visit(DimListNode p_node) {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	if (p_node.m_symtab!=null)
	{
		System.out.println("Visiting DimListNode");
	    for (Node child : p_node.getChildren() )
		   child.accept(this);
     }
   }

   public void visit(NumNode p_node) {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	if (p_node.m_symtab!=null)
	{
		System.out.println("Visiting NumNode");
	    for (Node child : p_node.getChildren())
		     child.accept(this);
	}
  }

   public void visit(StatBlockNode p_node)
  {
	// propagate accepting the same visitor to all the children
	// this effectively achieves Depth-First AST Traversal
	if (p_node.m_symtab!=null)
	{
		System.out.println("Visiting StatBlockNode");
		int count=0;
	    for (Node child : p_node.getChildren() )
	    {
		   child.m_symtab = p_node.m_symtab;
		   child.accept(this);
		   count++;
	    }
          //System.out.println("here"+count);
	    if (count>0)
		{p_node.m_type = p_node.getChildren().get(0).m_type;
	    p_node.setData(p_node.getChildren().get(0).getData());}
     }
   }
   
	public void visit(StatNode p_node)
	{
		if (p_node.m_symtab!=null)
		{	
			System.out.println("Visiting StatNode");
		    // propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
		    for (Node child : p_node.getChildren() ) {
				  child.m_symtab = p_node.m_symtab;
				  child.accept(this);
			}
		
		}
 }
	
    public void visit(VarNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting VarNode");
			String data_value="";
		    for (Node child : p_node.getChildren() )
		   {
		    	System.out.println(child.getNodeType());
			   child.m_symtab = p_node.m_symtab;
			   child.accept(this);
			   System.out.println(child.getNodeType());
			   if (child.getNodeType().equals("DotOP"))
			   {
				   data_value+=child.getData();
				   
			   }
			   else if (child.getNodeType().equals("DotOPNode"))
			   {
				   System.out.println("simii "+p_node.getChildren().get(0).getData());
				   data_value+=(p_node.getChildren().get(0).getData().replaceAll(":","")).replaceAll("\\s+", "");
				   p_node.setType(p_node.getChildren().get(0).getType());
				   p_node.setLine(child.getLine());
				  
			   }
			   
			   else 
				{				
					data_value+=(child.getData().replaceAll(":","")).replaceAll("\\s+", "");
					System.out.println(data_value);
					
				}
			   
			   
		   }
		
		  
			if(p_node.getChildren().get(0).getNodeType().equals("DotOpNode"))
			{
				p_node.setData(p_node.getChildren().get(0).getData());
				 p_node.m_moonVarName=(p_node.getData().replaceAll(":","")).replaceAll("\\s+", "");
			}
			else
		    {
				p_node.setData(p_node.getChildren().get(0).getData());
				p_node.setType(p_node.getChildren().get(0).getType());
				p_node.m_moonVarName=(p_node.getData().replaceAll(":","")).replaceAll("\\s+", "");
				
		    }
			System.out.println("in varNode"+p_node.getData());
	    }
	}
	

	public void visit(FactorNumNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting FactorNumNode");
		    for (Node child : p_node.getChildren())
			   child.accept(this);
		   String childOperandType=p_node.getChildren().get(0).getType();
		   p_node.setType(childOperandType);
		   p_node.setData(p_node.getChildren().get(0).getData());
	    }
	}
	

	public void visit(FactorNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting FactorNode");
		    for (Node child : p_node.getChildren() )
			 {
			   child.m_symtab = p_node.m_symtab;
			   child.accept(this);
			}
		   String childOperandType=p_node.getChildren().get(0).getType();
		   p_node.setType(childOperandType);
		   p_node.setData((p_node.getChildren().get(0).getData().replaceAll(":","")).replaceAll("\\s+", ""));
	   }
	}
	
	public void visit(TermNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting TermNode");
		    for (Node child : p_node.getChildren() )
			{
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
			}
		 String childOperandType=p_node.getChildren().get(0).getType();
		 p_node.setType(childOperandType);
		 p_node.setData(p_node.getChildren().get(0).getData());
		}
	}
	

	public void visit(ArithExpr p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting ArithExpr");
		    for (Node child : p_node.getChildren() )
			{
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
			}
		String childOperandType=p_node.getChildren().get(0).getType();
		p_node.setType(childOperandType);
		p_node.setData(p_node.getChildren().get(0).getData());
	   }
	}
	

	public void visit(Expr p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting Expr");
		    for (Node child : p_node.getChildren() )
			{
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
			}
		String childOperandType=p_node.getChildren().get(0).getType();
		p_node.setType(childOperandType);
		p_node.setData(p_node.getChildren().get(0).getData());
		
	   }
	}
	
	public void visit(DataMemberNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting DataMemberNode");
		    String data_value = "";
		    String DMemName="";
		    for (Node child : p_node.getChildren() )
			{
			  child.m_symtab = p_node.m_symtab;
			   child.accept(this);
			   if (child.getNodeType().equals("id"))
				   DMemName=child.getData()+":";
			   
			   else if (child.getType()!=null && child.getNodeType().equals("indexListNode") && child.getType().equals("IllegalExpressionTypeerror"))
			   {
				   data_value+="IllegalExpressionTypeerror";
			   }
			   else 
			   {
				   data_value+=child.getData()+" ";
			   }
			}
		
		     {
		    	data_value=DMemName+data_value;
		    	//System.out.println(data_value);
		      }
		    
		 System.out.println("to find:   "+data_value+"  from");
		 System.out.println(p_node.m_symtab.m_name);
        
         //-> A check if the variable is a member for class
		 String res=p_node.m_symtab.lookup_VarDecl_Type(data_value,p_node.m_symtab.m_name);
		  System.out.println("RESULT:"+res);
        System.out.println(p_node.getParent().getNodeType());
       
        if (!p_node.getParent().getNodeType().equals("DotOPNode") && (res.equals("")))
		 {
			  p_node.setData(data_value);
			  //p_node.m_symtabentry.m_type="UndefinedIdentifererror";
			  p_node.setType("UndefinedIdentifererror");
			this.m_errors += "Undefined Identifer : "
			                     + data_value
			                     +" at line number:"
			                     + p_node.getChildren().get(0).getLine()
			                     +"\n";
		 }
		 else if (!p_node.getParent().getNodeType().equals("DotOPNode") && res.contains("Error:ambiguity"))
		  {
			  p_node.setData(data_value);
			  //p_node.m_symtabentry.m_type="UndefinedIdentifererror";
			  p_node.setType("UndefinedIdentifererror");
			  this.m_errors += res +" at line number:"
				                     + p_node.getChildren().get(0).getLine()
				                     +"\n";
		  }
		  else
			  {
			     p_node.setType(res);
			     p_node.setData(data_value);
			     
			  }	
		  
	  }
		//System.out.println(p_node.m_symtab);
	}
	
	public void visit (indiceNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting indiceNode");
		    for (Node child : p_node.getChildren() )
		   {
			   child.m_symtab = p_node.m_symtab;
			   child.accept(this);
		   }
			
		  
		  String childOperandType=p_node.getChildren().get(0).getType();
		  p_node.setType(childOperandType);
		  p_node.setData(p_node.getChildren().get(0).getData());
	  }
	}
	
	public void visit (IndiceListNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting IndiceListNode");
		    List<String> indice_type=new ArrayList<String>();
		    List<String> init_indices_value=new ArrayList<String>();
		    List<String>  indices_value=new ArrayList<String>();
		    for (Node child : p_node.getChildren() )
			{
			   child.m_symtab = p_node.m_symtab;
			   child.accept(this);
			   String childOperandType=child.getType();
			   indice_type.add(childOperandType);
			   init_indices_value.add(child.getData());
			}
		    
		    //System.out.println("init_indices_value"+init_indices_value);
		    //System.out.println("indice_type"+indice_type);
		    for (int i=0;i<init_indices_value.size();i++)
		    {
		    	String ch=init_indices_value.get(i).replaceAll(":","");
		    	indices_value.add(ch);		    	
		    }
       
	    String found="";

	    //String dim=indices_value.toString().replace(", ", ",");
	    //System.out.println(dim);
		if (indice_type.size()>0)
		{
			//System.out.println("Sim pol"+p_node.getChildren().get(0).getType());
			p_node.setType(p_node.getChildren().get(0).getType());
			p_node.setData(indices_value.toString());
			for (int i=0;i<indice_type.size();i++)
			{
				if (!indice_type.get(i).equals("int") && !indice_type.get(i).equals("integer"))
					found="error";
			}
			
		}
		//System.out.println("founddddddddddd"+found+"   "+indice_type);
		if (found!="")//errornous indice under this inde
		{
			p_node.setType("IllegalExpressionTypeerror");
			this.m_errors += "IllegalExpressionTypeerror: Not an Integer"
		                     +" at line number:"
		                     + p_node.getChildren().get(0).getLine()
		                     +"\n";
			//p_node.m_symtabentry.m_type="IndicelistNodeIllegalExpressionTypeerror";
		}
		
		}
	}
	
	public void visit(AdditionOP p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting AdditionOP");
		   for (Node child : p_node.getChildren() )
		  {		
			child.accept(this);
		  }	
	    }
	}
	
	public void visit (signNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			
			System.out.println("Visiting signNode");
		    for (Node child : p_node.getChildren() )
		   {
		      
			  child.accept(this);
		   }
		}
	}
	
	public void visit(FactorSignNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting FactorSignNode");
		     for (Node child : p_node.getChildren() )
		   {	
		      child.m_symtab = p_node.m_symtab;	 
			  child.accept(this);
			  if(child.getNodeType().equalsIgnoreCase("FactorNode"))
			 {
				 
				p_node.setType(child.getType());
			 }
		   }
		}
	}
	
	public void visit(notNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting notNode");
	 	    for (Node child : p_node.getChildren() )
		   {
			   child.accept(this);
		   }		
		}
	}
	
	public void visit(FactorNotNode p_node)
	{
		if (p_node.m_symtab!=null)
		{	
			System.out.println("Visiting FactorNotNode");
		    for (Node child : p_node.getChildren() )
		   {
			 child.m_symtab = p_node.m_symtab;
			 child.accept(this);
			 if(child.getNodeType().equalsIgnoreCase("FactorNode"))
			 {
				p_node.setType(child.getType());
			 }
		   }
		}
		
	}
	
	public void visit(FactorArithExprNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting FactorArithExprNode");
		    for (Node child : p_node.getChildren() )
		   {
			  child.m_symtab = p_node.m_symtab;
			  child.accept(this);
		   }
			
		   String childOperandType=p_node.getChildren().get(0).getType();
	 	   p_node.setType(childOperandType);
	 	  p_node.setData(p_node.getChildren().get(0).getData());
	   }
	}
	
	public void visit(RelationOP p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting RelationOP");
		    for (Node child : p_node.getChildren() )
		   {
			//child.m_symtab = p_node.m_symtab;
			child.accept(this);
		    }
		}
	}
	
	public void visit(DotOp p_node) {
		 
		 System.out.println("Visiting DotOp");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				
				child.accept(this);
			}
			
	 }
	
	public void visit(ParamListNode p_node) {
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		if (p_node.m_symtab!=null)
		{
			String childList="";
			String childtype="";
			System.out.println("Visiting ParamListNode");
		    for (Node child : p_node.getChildren() )
		   {
			 child.m_symtab = p_node.m_symtab;
			 child.accept(this);
			 childList+=child.getData().trim()+"/,";
			 childtype+=child.getType()+"/,";
			 
		   }
		   
		    if (childList.equals("/,") && childtype.equals("/,"))
		    {   
		    	childList="/,";
		    	childtype="/,";
		    }
		    
		    	
		    p_node.setData(childList);
		    p_node.setType(childtype);
		   
		}
	}
		
	public void visit(ForStatNode p_node)
	{
		if (p_node.m_symtab!=null)
		{
			System.out.println("Visiting ForStatNode");
	 	    // propagate accepting the same visitor to all the children
		   // this effectively achieves Depth-First AST Traversal
		  for (Node child : p_node.getChildren() ) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
		}
		
	}
		
	public void visit(GetStatNode p_node)
	{
		if (p_node.m_symtab!=null)
		{// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting GetStatNode");
		for (Node child : p_node.getChildren() )
		{
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
			
		p_node.setType(p_node.getChildren().get(0).getType());
	    }
	}
	
	public void visit(PutSatNode p_node)
	{
		if (p_node.m_symtab!=null)
		{// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting PutSatNode");
		for (Node child : p_node.getChildren() )
		{
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
			
		p_node.setType(p_node.getChildren().get(0).getType());
	    }
	}
		
	public void visit(ReturnStatNode p_node)
	{
		if (p_node.m_symtab!=null)
		{// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting ReturnStatNode");
		for (Node child : p_node.getChildren() )
		{
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
			
		}
		
		p_node.setType(p_node.getChildren().get(0).getType());
		System.out.println(p_node.getChildren().get(0).getType());
		if (p_node.getChildren().get(0).getType().equals("integer"))
			p_node.setType("int");
		   if (p_node.getParent().getParent().getParent().getNodeType().equals("funcDef") && !p_node.getParent().getParent().getParent().m_symtabentry.m_type.equals(p_node.getType()))
		  {
			   String name="",line="";
			   if (p_node.getParent().getParent().getParent().getChildren().get(2).equals("::"))
			   {  name = p_node.getParent().getParent().getParent().getChildren().get(1).getData()+
						   p_node.getParent().getParent().getParent().getChildren().get(2).getData()+
						   p_node.getParent().getParent().getParent().getChildren().get(3).getData();
			      line = p_node.getParent().getParent().getParent().getChildren().get(3).getLine();
			   }
			   else if (!p_node.getParent().getParent().getParent().getChildren().get(2).equals("::"))
			   {
				   name=p_node.getParent().getParent().getParent().getChildren().get(1).getData();
				   line=p_node.getParent().getParent().getParent().getChildren().get(1).getLine();
			   }
			    p_node.setType("typeerror");
				this.m_errors += "ReturnStatNode type error: "
						+" for the function:"+ name+ "at line number: "+ line +"\n";
						
		   }
	    }
	}
	
	public void visit(FCallNode p_node)
	{
		if (p_node.m_symtab!=null)
		{// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting FCallNode");
		for (Node child : p_node.getChildren() )
		   {
			     child.m_symtab = p_node.m_symtab;
				 child.accept(this);
			}
		 p_node.setData(p_node.getChildren().get(0).getData());
		 p_node.setLine(p_node.getChildren().get(0).getLine());
		  
		//System.out.println(p_node.m_symtab);
		//System.out.println(p_node.getNodeType());
		String parameter="";
		String fname="";
		
		for (Node child:p_node.getChildren())
		{
			 if (child.getNodeType().equals("id"))
		  		   fname=child.getData();
			 
		   if (child.getNodeType().equals("ParamListNode"))
		   {
			   String param[]=child.getData().replaceAll("\\s+", "").split("/,");
			   String param_type[]=child.getType().split("/,");
			   //System.out.println(p_node.m_symtab);
			   for (int i=0;i<param.length;i++)
			   { 				   
				   param[i]+=":";
				   for (SymTabEntry str:p_node.m_symtab.m_symlist)
				   {
					  // System.out.println(str.m_name);
					   //System.out.println(param[i]);
					   if (str.m_kind.equals("fParam")
						   && str.m_name.contains(param[i]))
					   	   {
						       param[i]+=str.my_dims;
						      
					   	   }
				   }
				  
				   //System.out.println("debugger:"+param[i].split(":").length);
				  
				   if (param[i].split(":").length!=0 && param[i].split(":").length<2)
				   {
					   param[i]+="[]:";  // added for AddOP which has no indicelist
					   parameter+=param_type[i]+":"+param[i]+"/,";
					  
				   }
				   
				   else if (param[i].split(":").length==0) //added--> for no paramater fcall
				   {
					   param[i]="";
					   parameter+=param[i]+"/,";
					 
				   }
				   else				   
					   parameter+=param_type[i]+":"+param[i]+"/,"; 
									   			     
			   }		  
		   }
		   
		  
		  }
	
		//System.out.println("debuggung"+fname+" "+parameter);
		
		String type=AST_T.Prog.m_symtab.lookupFunctionCall(fname+" "+parameter);
		
		//System.out.println("checking on: "+fname+" "+parameter);
		System.out.println("Result: "+type);
		 if (!p_node.getParent().getNodeType().equals("DotOPNode") && type.equals(""))
		 {
			 p_node.setType("typeerror");
				this.m_errors += "FunctionCall error : missing matching function Declaration"
			                     +fname+" "+parameter
			                     +" at line number:"
			                     + p_node.getLine()
			                     +"\n";
				p_node.setType("FunctionCallerror");
				  if (p_node.m_symtabentry!=null)
				p_node.m_symtabentry.m_type=p_node.getType();
		 }
		 else
		 {
			 
			 p_node.setType(type);
			  if (p_node.m_symtabentry!=null)
			 p_node.m_symtabentry.m_type=p_node.getType();
			 
		 }
		     
		
	   }
	}
		
	public void visit(IfNode p_node)
	{
		
		if (p_node.m_symtab!=null)
		{// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		System.out.println("Visiting IfNode");
		for (Node child : p_node.getChildren() )
		{
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
			
		p_node.setType(p_node.getChildren().get(0).getType());
	    }
		
	}
		
	


	
	
	
}







