package Visitor.SymbolTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import AST.*;
import LexParser.*;
import SymbolTable.*;
import Visitors.Visitor;

/**
 * Visitor to create symbol tables and their entries.  
 * 
 * This concerns only nodes that either:  
 * 
 * (1) represent identifier declarations/definitions, in which case they need to assemble 
 * a symbol table record to be inserted in a symbol table. These are:  VarDeclNode, ClassNode,  
 * and FuncDefNode. 
 * 
 * (2) represent a scope, in which case they need to create a new symbol table, and then 
 * insert the symbol table entries they get from their children. These are:  ProgNode, ClassNode, 
 * FuncDefNode, and StatBlockNode.   
 */

public class SymTabCreationVisitor extends Visitor {

	 public static Integer m_tempVarNum     = 0;
	 public String  m_outputfilename = new String(); 
	 public String m_errors         = new String();
	 private static PrintWriter  out_error;
	 public SymTabCreationVisitor() {
		}
		
	public SymTabCreationVisitor(String p_filename) {
			this.m_outputfilename = p_filename; 
		}
	    
	 public String getNewTempVarName(){
	    	m_tempVarNum++;
	    	return "t" + m_tempVarNum.toString();  
	    }
	
	public void visit(ProgNode p_node){
		System.out.println("visiting ProgNode");
		p_node.m_symtab = new SymTab(0,"global", null);
		
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() ) {
		 //make all children use this scopes' symbol table
		  child.m_symtab = p_node.m_symtab;
		  child.accept(this);
		}
		
		
		if (!this.m_outputfilename.isEmpty()) {
			File file = new File(this.m_outputfilename);
			try (PrintWriter out = new PrintWriter(file)){out.println(p_node.m_symtab);}
			catch(Exception e){e.printStackTrace();}
		}
		
		
		
	//-------------------------------------------------------------CHECKING FOR SHADOWING-----------------------------------------------	
		for (Node progchild:p_node.getChildren())
		{
			
			if (progchild.getNodeType().equals("ClassListNode"))
			{
				for (Node classDecl:progchild.getChildren())
				{
					
					for (Node classDeclChild:classDecl.getChildren())
					{		
						
						if (classDeclChild.getNodeType().equals("inherListNode"))
					   {
							//System.out.println("Class Name:"+classDecl.getChildren().get(0).m_data);
							HashMap<String,SymTab> inheritListsubTabs=null;
							//---------------------------------------- loop that gets the symbol table for the inheritList -------->
							if (classDeclChild.m_symtab!=null)
							{
								for (SymTabEntry str:classDeclChild.m_symtab.m_symlist)
							    {
								   if (str.m_kind.equals("inheritlist"))
									   inheritListsubTabs=str.m_inherlist;							
							     }
							
							//--------------------------------------------------------------------------------------------------->
												   
							//System.out.println(classDeclChild.m_symtab);
							for (SymTabEntry str:classDeclChild.m_symtab.m_symlist)
							{
								if (str.m_kind.equals("var"))
								{
									Set<String> it=inheritListsubTabs.keySet();
									for (String itr:it)
									{		
										//System.out.println("Looking for var:"+str.m_name+"inside:"+itr);
										//System.out.println(inheritListsubTabs.get(itr).lookup_Variable(str.m_name));
										if (inheritListsubTabs.get(itr).lookup_Variable(str.m_name)==true)//a vardecl shadowing error
										{
										   //LOOKING FOR LINE NUMBER - TO REPORT ERROR----------------
											String error_line = null;	
											for (Node classChild:classDecl.getChildren())
											{
												if(classChild.getNodeType().equals("memberList"))
												{
													for (Node var:classChild.getChildren())
													{
														if(var.getChildren().get(1).getData().equals(str.m_name.split(":")[0]))
															error_line=var.getChildren().get(1).getLine();
													}
												}
											}
											//----------------------------------------------------------------------
											this.m_errors="Warning: shadowed inherited member:"+str.m_name+" of class: "+itr+" in the class: "+classDecl.getChildren().get(0).m_data+" at line number: "+error_line;
											try {
												ParseT.Semantic_err.write(this.m_errors);
												ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
												ParseT.Semantic_err.newLine();
												} catch (IOException e) {e.printStackTrace();}		
										}									
									}
								}
								else if (str.m_kind.equals("func"))
								{
									Set<String> it=inheritListsubTabs.keySet();
									for (String itr:it)
									{
										//System.out.println("Looking for func:"+str.m_name+"inside:"+itr);
										//System.out.println(inheritListsubTabs.get(itr).lookup_funcDecl(str.m_name));
										if (inheritListsubTabs.get(itr).lookup_funcDecl(str.m_name)==true)//a funcdecl shadowing error
										{	
											//LOOKING FOR LINE NUMBER - TO REPORT ERROR----------------
											String error_line = null;	
											for (Node classChild:classDecl.getChildren())
											{
												if(classChild.getNodeType().equals("memberList"))
												{
													for (Node func:classChild.getChildren())
													{
														if(func.getChildren().get(1).getData().equals(str.m_name.split(":")[0]))
															error_line=func.getChildren().get(1).getLine();
													}
												}
											}
											//----------------------------------------------------------------------
											this.m_errors="Warning: shadowed inherited member:"+str.m_name+" of class: "+itr+" in the class: "+classDecl.getChildren().get(0).m_data+" at line number: "+error_line;
											try {
												ParseT.Semantic_err.write(this.m_errors);
												ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
												ParseT.Semantic_err.newLine();
												} catch (IOException e) {e.printStackTrace();}	
										}
										
									}
									
								}
							}
							
					       }
													
					   }// if - if (classDeclChild.getNodeType().equals("inherListNode"))
					} // end -for loop checking for the class that has inherListNode in it ...!
			   }
			}
		}
	// ------------------------------> SHADOWING CHECK ENDS HERE!----------------------------------------------------------------------------------
		
	
		 
		
		
			
	}
	
	
	public void visit(ClassListNode p_node) {
		System.out.println("Visiting ClassListNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	
	public void visit (FuncDefStatBlock p_node) // this block comes after program
	{
		
		
		if (p_node.getParent().getNodeType().equals("Prog")) // Condition that checks this is Program Block
		{
			System.out.println("Visiting FuncDefStatBlock (ProgramBlockNode)");
			SymTab localtable = new SymTab(1,"program", p_node.m_symtab);
		    p_node.m_symtabentry = new FuncEntry("void","program", new Vector<VarEntry>(), localtable);
			p_node.m_symtab.addEntry(p_node.m_symtabentry);
			p_node.m_symtab = localtable;
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren() ) {
					child.m_symtab = p_node.m_symtab;
					child.accept(this);
			}
		}
		
		else if (p_node.getParent().getNodeType().equals("funcDef"))//FuncDefNode's Stateblock
		{
			// propagate accepting the same visitor to all the children
						// this effectively achieves Depth-First AST Traversal
			
						for (Node child : p_node.getChildren() ) {
								child.m_symtab = p_node.m_symtab;
								//System.out.println(child.getNodeType());
								child.accept(this);
						}
						
						
		}
		
		
		
	}
	
	
	public void visit(ClassNode p_node){
		
		System.out.println("Visiting ClassNode"+p_node.getChildren().get(0).getData());
		String classname = p_node.getChildren().get(0).getData();
		SymTab localtable = new SymTab(1,classname, p_node.m_symtab);
			
	    //System.out.println("going for:"+classname);
		if (p_node.m_symtab.lookup_class(classname)==true)
		{
			for (Node child:p_node.getChildren())
			{
				if (child.getNodeType().equals("id"))
					p_node.setLine(child.getLine());
			}
			
			this.m_errors="Multiple identifier declartion:"+ classname +"at line number: "+p_node.getLine();
			try {
				ParseT.Semantic_err.write(this.m_errors);
				ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
				ParseT.Semantic_err.newLine();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			p_node.m_symtab=null;
			
		}
		else // not a duplicate class
		{
			p_node.m_symtabentry = new ClassEntry(classname, localtable);
		    p_node.m_symtab.addEntry(p_node.m_symtabentry);
		    p_node.m_symtab = localtable;
		   // propagate accepting the same visitor to all the children
		   // this effectively achieves Depth-First AST Traversal
		   for (Node child : p_node.getChildren() ) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		   }
		}
		//System.out.println("************************************");
	}
		
	public void visit(TypeNode p_node) {
		System.out.println("Visiting TypeNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	 }
	
	public void visit(IdNode p_node) {
		 System.out.println("Visiting IdNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		p_node.m_moonVarName = p_node.m_data;
	}
	
	public void visit(DimListNode p_node) {
		 System.out.println("Visiting DimListNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(InherListNode p_node){
		 System.out.println("visiting InherListNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		// loop over the list of id nodes and aggregate here 
	     HashMap<String,SymTab> inhert = new HashMap<String,SymTab>();
	    
		 for (Node inhertID : p_node.getChildren()){
			 String id_data=inhertID.getData();
			 if(inhert.containsKey(id_data)) // checking if this is a duplicate entry for Inheritance class id 
			 {
				 for (Node child:p_node.getChildren())
					{
						if (child.getNodeType().equals("id"))
							p_node.setLine(child.getLine());
					}
					
					this.m_errors="Multiple identifier declartion:"+ id_data +" at line number: "+p_node.getLine();
					try {
						ParseT.Semantic_err.write(this.m_errors);
						ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
						ParseT.Semantic_err.newLine();
						//ParseT.Semantic_err.flush();
					} catch (IOException e) {
						
						e.printStackTrace();
					}	
			 }
			 
			 else  // Not a duplicate
			 {
				 SymTab subtable=new SymTab();
				 //System.out.println("checking for:"+id_data);
				 subtable=p_node.m_symtab.lookupInheritedClass(id_data);
			    if (subtable!=null) // class existed 
			    {
			    	inhert.put(id_data,subtable);
			    	//System.out.println(id_data);
			    	//System.out.println(subtable);
			    }
			    else
			    {
			    	//System.out.println("Throw error! Inherited class is missing!");	
			    	
			    	for (Node child:p_node.getChildren())
					{
						if (child.getNodeType().equals("id"))
							p_node.setLine(child.getLine());
					}
					
					this.m_errors="Missing declaration for identifier :"+ id_data +" at line number: "+p_node.getLine();
					try {
						ParseT.Semantic_err.write(this.m_errors);
						ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
						ParseT.Semantic_err.newLine();
						//ParseT.Semantic_err.flush();
					} catch (IOException e) {
						
						e.printStackTrace();
					}	
			    }
			    
			 }
		 }
		// create the symbol table entry
	    // it will be picked-up by another node above later
		
		 
		 p_node.m_symtabentry = new ClassInheritanceEntry("inheritlist",inhert);
		 p_node.m_symtab.addEntry(p_node.m_symtabentry);
		
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
	
	
	public void visit(VarDeclNode p_node){
	    System.out.println("Visiting VarDeclNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() ) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		// Then, do the processing of this nodes' visitor
		// aggregate information from the subtree
		// get the type from the first child node and aggregate here 
		String vartype = p_node.getChildren().get(0).getData();
		// get the id from the second child node and aggregate here
		String varid = p_node.getChildren().get(1).getData();
		// loop over the list of dimension nodes and aggregate here 
		Vector<Integer> dimlist = new Vector<Integer>();
		for (Node dim : p_node.getChildren().get(2).getChildren()){
			// parameter dimension
			Integer dimval = Integer.parseInt(dim.getData()); 
			dimlist.add(dimval); 
		}
		// create the symbol table entry for this variable
		// it will be picked-up by another node above later
		varid=varid+":"+dimlist;
		//System.out.println(p_node.m_symtab);
		if (p_node.m_symtab.lookup_Variable(varid)==true)
		{
			//System.out.println(varid);
			for (Node child:p_node.getChildren())
			{
				if (child.getNodeType().equals("id"))
					p_node.setLine(child.getLine());
			}
			
			this.m_errors="Multiple identifier declartion:"+ varid +"at line number: "+p_node.getLine();
			try {
				ParseT.Semantic_err.write(this.m_errors);
				ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
				ParseT.Semantic_err.newLine();
				//ParseT.Semantic_err.flush();
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
			//p_node.m_symtab=null; //added on :08-04-2018 20:44
			 
		}
		else
		{ // A check if the var declaration is an object of a class and if the class exist!====================================
			Boolean error=false;
			SymTab subTable=null;
			if (!vartype.equals("int") && !vartype.equals("integer") && !vartype.equals("float") && !vartype.contains("error") && !(vartype==null))
			{
				System.out.println(varid+"------"+vartype);
				//A check if the class exist?
				subTable=AST_T.Prog.m_symtab.lookup_class_RetSymTab(vartype);
				if(subTable!=null)// class exist
				{
					p_node.set_var_type("classvar:"+vartype);
				}
				else //Error thrown - class missing
				{	
					for (Node child:p_node.getChildren())
					{
						if (child.getNodeType().equals("id"))
							p_node.setLine(child.getLine());
					}
					this.m_errors="Class:"+ vartype +" for the variable's declaration:"+varid+" missing , at line number: "+p_node.getLine();
					try {
						ParseT.Semantic_err.write(this.m_errors);
						ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
						ParseT.Semantic_err.newLine();
						//ParseT.Semantic_err.flush();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					
					vartype="UndefinedClasserror"; // update the type
					error=true;
					//p_node.m_symtab=null;
				}
				
			}
			//========= A check if the var declaration is an object of a class and if the class exist! ENDS HERE========================
			
		 if (p_node.get_var_type()==null)// for all the vardecl which are not of class type
		 { p_node.m_symtabentry = new VarEntry("var", vartype, varid, dimlist);
		  p_node.m_symtab.addEntry(p_node.m_symtabentry);
		 }
		 else if (subTable!=null && error!=true && p_node.get_var_type()!=null && p_node.get_var_type().contains("classvar"))
		  {// all class variable declaration -- i.e class object declarations
			  p_node.m_symtabentry = new VarEntry("var", vartype, varid, dimlist);
			  p_node.m_symtab.addEntry(p_node.m_symtabentry);
			  p_node.m_symtabentry.m_subtable=subTable;			 
		  }
		 
		}
	}
	
	
	public void visit(NumNode p_node) {
		 System.out.println("Visiting NumNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		String tempvarname = this.getNewTempVarName();
		p_node.m_moonVarName = tempvarname;
		String vartype = p_node.getType();//integer
		if (vartype.equals("integer"))
			vartype="int";
		p_node.m_symtabentry = new VarEntry("litval", vartype, p_node.m_moonVarName, new Vector<Integer>());
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
	}
		

	public void visit(funcDeclNode p_node){
		
		 System.out.println("visiting funcDeclNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		
		for (Node child : p_node.getChildren() ) {
			 child.m_symtab = p_node.m_symtab;
			 child.accept(this);
		}
		// Then, do the processing of this nodes' visitor
		// aggregate information from the subtree
		// get the type from the first child node and aggregate here 
	
		String functype = p_node.getChildren().get(0).getData();
		// get the id from the second child node and aggregate here
	    String funcid = p_node.getChildren().get(1).getData();
		// loop over the list of dimension nodes and aggregate here
	    Vector<String> fparamlist = new Vector<String>();
	    Vector<Integer> fparamintlist=new Vector<Integer>();
	    for (Node child : p_node.getChildren())
	    {
	    	if (child.getNodeType().equals("fParamNode"))
	    	{
	    		String fparam=child.getData();
	    		//Integer dimval = Integer.parseInt(child.getData()); 
	    		if (!child.getData().contains("dup_error"))
	    		{ 
	    		  fparamlist.add(fparam);
	    		  //fparamintlist.add(dimval);
	    		} 
	    	}
	    }
	    String my_fpramlist = "";
	    if (fparamlist.size()==0)
	    	my_fpramlist+=""+"/,";
	    else
	    { 
	    	for (int i=0;i<fparamlist.size();i++)
	       {
	    	my_fpramlist+=fparamlist.get(i)+"/,";
	        }
	   }
	    
	   
	   
	 // create the symbol table entry for this variable
	 // it will be picked-up by another node above later
	    funcid=funcid+":"+my_fpramlist;
	//searching for a similar funcdecl already in this scope.
	    if( p_node.m_symtab.lookup_funcDecl(funcid))
	    {
	    	 for (Node child:p_node.getChildren())
				{
					if (child.getNodeType().equals("id"))
						p_node.setLine(child.getLine());
									
				}
				
				this.m_errors="Multiple identifier declartion:"+ funcid +"at line number: "+p_node.getLine();
				try {
					ParseT.Semantic_err.write(this.m_errors);
					ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
					ParseT.Semantic_err.newLine();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				p_node.m_symtab=null;
	    }
	     
	   else{
	    	
	    	p_node.m_symtabentry = new FuncDeclEntry("func", functype, funcid, fparamlist);//,fparamintlist
	 	     p_node.m_symtab.addEntry(p_node.m_symtabentry);
	     }
		
	}
	
	public void visit(fParamNode p_node)
	{
		System.out.println("Visiting fParamNode"+p_node.getChildren().get(1).getData());
		// propagate accepting the same visitor to all the children
	    // this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() ) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		// Then, do the processing of this nodes' visitor
		// aggregate information from the subtree
		// get the type from the first child node and aggregate here 
		String fParamtype = p_node.getChildren().get(0).getData();
		// get the id from the second child node and aggregate here
		String fParamid = p_node.getChildren().get(1).getData();
		// loop over the list of dimension nodes and aggregate here 
		Vector<Integer> dimlist = new Vector<Integer>();
		for (Node dim : p_node.getChildren().get(2).getChildren()){
			// parameter dimension
			Integer dimval = Integer.parseInt(dim.getData()); 
			dimlist.add(dimval); 
		}
		// create the symbol table entry for this variable
		// it will be picked-up by another node above later
		String dim=dimlist.toString().replace(", ", ",");
		
		//System.out.println("findind in fpara :"+p_node+fParamtype+" "+fParamid+" "+dim);
		
		if (p_node.m_symtab.lookup_fparam(p_node,fParamtype+" "+fParamid+" "+dim)==true)
		{
			 for (Node child:p_node.getChildren())
				{
					if (child.getNodeType().equals("id"))
						p_node.setLine(child.getLine());
									
				}
				
				this.m_errors="Multiple identifier declartion:"+ fParamid+dimlist +"at line number: "+p_node.getLine();
				try {
					ParseT.Semantic_err.write(this.m_errors);
					ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
					ParseT.Semantic_err.newLine();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				p_node.setData(fParamtype+" "+fParamid+" "+dimlist+" "+"dup_error");
				//System.out.println(p_node.getData());
				p_node.setType("duplicateIDParamerror");
				/*System.out.println(p_node.getType());
				if (p_node.getParent().getNodeType().equals("funcDef"))
				{
					String pnodename=p_node.getParent().m_symtabentry.m_name.split("::")[0];
					String 
					String param[]=p_node.getParent().m_symtabentry.m_name.split("::")[1].split(":")[1].split("/,");
					for (int i=0;i<param.length;i++)
					{
						if (param[i].split(" ")[0].equals(fParamtype)
						    && param[i].split(" ")[1].equals(fParamid)
						    && param[i].split(" ")[2].equals(dim))
						{
						   param[i]="";	
						}
					}
				}*/
				//p_node.getParent().m_symtabentry.m_name=
				
		}
		
		else
		{
			p_node.setData(fParamtype+" "+fParamid+" "+dim);
			p_node.m_symtabentry = new VarEntry("fParam", fParamtype, fParamid, dimlist);
		    p_node.m_symtab.addEntry(p_node.m_symtabentry);
		}
	}
		
	public void visit(FuncDefListNode p_node) {
		System.out.println("Visiting FuncDefListNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	};
	
	public void visit(FuncDefNode p_node){
		
		if (!p_node.getChildren().get(2).getData().equals("::"))
		{	
			System.out.println("Visiting FuncDefNode");
			
		    String ftype = p_node.getChildren().get(0).getData();
		    String fname = p_node.getChildren().get(1).getData()+":";
		    String fparam_type = "",fparam_name = "";
		    Vector<Integer> dimlist = null;
		    SymTab localtable = new SymTab(1,fname, p_node.m_symtab);
		    Vector<VarEntry> paramlist = new Vector<VarEntry>();
		    for (Node param : p_node.getChildren()){
		    
			  if (param.getNodeType().equals("fParamNode") && !param.getData().contains("dup_error"))
			  {
				  fparam_type=param.getChildren().get(0).getData();
				  fparam_name=param.getChildren().get(1).getData();
				  dimlist = new Vector<Integer>();
			     for (Node dim : param.getChildren().get(2).getChildren()){
				 // parameter dimension
				  
				   Integer dimval = Integer.parseInt(dim.getData()); 
				   dimlist.add(dimval); 
			     }	
			    
				String dim=dimlist.toString().replace(", ", ",");
				fname+=fparam_type+" "+fparam_name+" "+dim+"/,";
				paramlist.add((VarEntry) p_node.m_symtabentry);
			  }
			  
		    }
		    
		    if (!fname.contains("/,"))
		      fname=fname+"/,";
		   // System.out.println(fname);	
		    
		     if (p_node.m_symtab.lookup_funcDef(fname)==true)
		     {
		    	 for (Node child:p_node.getChildren())
					{
						if (child.getNodeType().equals("id"))
							p_node.setLine(child.getLine());
										
					}
					
					this.m_errors="Multiple identifier declartion:"+ fname +"at line number: "+p_node.getLine();
					try {
						ParseT.Semantic_err.write(this.m_errors);
						ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
						ParseT.Semantic_err.newLine();
						
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					p_node.m_symtab=null;
		     }
		    
		     else 
		    {
		    	p_node.m_symtabentry = new FuncEntry(ftype, fname, paramlist, localtable);
		        p_node.m_symtab.addEntry(p_node.m_symtabentry);
		        p_node.m_symtab = localtable;
		        // propagate accepting the same visitor to all the children
		        // this effectively achieves Depth-First AST Traversal;
		        for (Node child : p_node.getChildren() ) {
			        child.m_symtab = p_node.m_symtab;
			        child.accept(this);
		       }
		       
		    }
		}//end of if - for checking ! of "::"- scope resolution
		
		else if (p_node.getChildren().get(2).getData().equals("::"))
		{
			System.out.println("Visiting FuncDefNode");
			String ftype = p_node.getChildren().get(0).getData();
			String fname = p_node.getChildren().get(1).getData()+p_node.getChildren().get(2).getData()+p_node.getChildren().get(3).getData()+":";
			String fparamter = "";
		    String fparam_type = "",fparam_name = "";
		    Vector<Integer> dimlist = null;
			SymTab localtable = new SymTab(1,fname, p_node.m_symtab);
			Vector<VarEntry> paramlist = new Vector<VarEntry>();
			for (Node param : p_node.getChildren()){
				
				  if (param.getNodeType().equals("fParamNode") && !param.getData().contains("dup_error"))
				  {
					 // System.out.println(param.getType());
					// parameter dimension list
					 dimlist = new Vector<Integer>();
				     fparam_type=param.getChildren().get(0).getData();
				     fparam_name=param.getChildren().get(1).getData();
				     for (Node dim : param.getChildren().get(2).getChildren()){
					 // parameter dimension				    	
					   Integer dimval = Integer.parseInt(dim.getData()); 
					   dimlist.add(dimval); 
				     }
				   
				    String dim=dimlist.toString().replace(", ", ",");
				    paramlist.add((VarEntry) p_node.m_symtabentry);
				    fname+=fparam_type+" "+fparam_name+" "+dim+"/,";
				    fparamter+=fparam_type+" "+fparam_name+" "+dim+"/,";
				  }
			    }
			
			
			    if (!fname.contains("/,"))
			         fname=fname+"/,";
			    if (!fparamter.contains("/,"))
			    	fparamter=fparamter+"/,";
			    //System.out.println(fname);
			   if (p_node.m_symtab.lookup_funcDef(fname)==true) //a duplicate function declaration
			     {
			    	 for (Node child:p_node.getChildren())
						{
							if (child.getNodeType().equals("id"))
								p_node.setLine(child.getLine());
											
						}
						
						this.m_errors="Multiple identifier declartion:"+ fname +"at line number: "+p_node.getLine();
						try {
							ParseT.Semantic_err.write(this.m_errors);
							ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
							ParseT.Semantic_err.newLine();
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}	
						p_node.m_symtab=null;
			     }
			
			   else //not a duplicate function declaration
			    { 			    	
			     Boolean match_declaration=false;
			  
	 		     for (SymTabEntry class_match:p_node.getParent().m_symtab.m_symlist)
			     {
				      if(class_match.m_name.equals(p_node.getChildren().get(1).getData())) //matches with the class
				    {
					    //System.out.println(class_match.m_subtable);
					    for (SymTabEntry class_element:class_match.m_subtable.m_symlist) //matches with the function decl
					   {
					    	//System.out.println(class_element.m_kind+class_element.m_type+class_element.m_name);
					    	//System.out.println(p_node.getChildren().get(3).getData());
					    	
						   if (class_element.m_kind.equals("func") 
							 && class_element.m_type.trim().equals(p_node.getChildren().get(0).getData()) 
							&& class_element.m_name.split(":")[0].trim().equals(p_node.getChildren().get(3).getData())) 
						   { 							
							   //System.out.println(class_element.m_name.split(":")[0]);
							   
						        //Below logic- will match and map the function decl with function definition and set the m_subtable entry in the declaration
						        String[] class_funcdecl_element_param=class_element.m_name.split(":")[1].split("/,");
						        String [] func_def_element_param=fparamter.split("/,");
						        if (class_funcdecl_element_param.length==func_def_element_param.length)
						        {
						    	   int count=0;
						    	   for (int i =0;i<class_funcdecl_element_param.length&&i<func_def_element_param.length ;i++)
						    	   {
										    								    		 
						    		   if ( class_funcdecl_element_param[i].split(" ")[0].equals(func_def_element_param[i].split(" ")[0])
						    		      && class_funcdecl_element_param[i].split(" ")[2].equals(func_def_element_param[i].split(" ")[2]))
						    		    {
						    			 
						    			  count++;
						    		    }
						    		
						    		
						           }
						    	
						         if (count==class_funcdecl_element_param.length)
						         {
						    		   
						    		    // System.out.println(class_element.m_name);
						    		    class_element.m_subtable=localtable;
						    		    // System.out.println(class_element.m_subtable);
						    		    match_declaration=true;   		    
								  
						       }
						  					  		   
						 }
						    
						     
						 }
						 
					 }
				 }
					
			  }// end of for - performs the mapping of the class's function declaration with its definition
			  
				  if (match_declaration==false)
				  {
					// System.out.println("The function:"+p_node.getChildren().get(3).getData()+fparamter+"is not declared in class"+p_node.getChildren().get(1).getData()+ "throw semantic error!");
					 for (Node child:p_node.getChildren())
						{
							if (child.getNodeType().equals("id"))
								p_node.setLine(child.getLine());
											
						}
						
						this.m_errors="The function:"+p_node.getChildren().get(3).getData()+fparamter+"is not declared in class"+p_node.getChildren().get(1).getData() +"at line number: "+p_node.getLine();
						try {
							ParseT.Semantic_err.write(this.m_errors);
							ParseT.final_error_file.add("Semantic_Error:"+this.m_errors);
							ParseT.Semantic_err.newLine();
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						p_node.m_symtab=null;
				  
				  }
				  else if (match_declaration==true)
				  {
					 // System.out.println("It is a match");
					    p_node.m_symtabentry = new FuncEntry(ftype, fname, paramlist, localtable);
				        p_node.m_symtab.addEntry(p_node.m_symtabentry);
				        p_node.m_symtab = localtable;
				
		 
				        // propagate accepting the same visitor to all the children
				        // this effectively achieves Depth-First AST Traversal;
				        for (Node child : p_node.getChildren() ) {
					       child.m_symtab = p_node.m_symtab;
					      child.accept(this);
				      }
				  }
			}
		}
		
		
	}

    public void visit (StatNode p_node) 
    {
    	System.out.println("Visiting StatNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
    	for (Node child : p_node.getChildren()) {
    		//System.out.println(child.getNodeType());
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
    }
	
	public void visit(ForStatNode p_node)
	{
		System.out.println("Visiting ForStatNode");
		// propagate accepting the same visitor to all the children
				// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() ) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
		
		String vartype = p_node.getChildren().get(0).getData();
		String varid = p_node.getChildren().get(1).getData();
		p_node.m_symtabentry = new VarEntry("for", vartype, varid, null);
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
	}
	
	//new created------------------------------------------------>
	public void visit(AddOpNode p_node) {
		System.out.println("Visiting AddOpNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}		
		String tempvarname = this.getNewTempVarName();
		p_node.m_moonVarName = tempvarname;
		p_node.m_symtabentry = new VarEntry("tempvar", p_node.getType(), p_node.m_moonVarName, p_node.m_symtab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_dims);
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
		
		//System.out.println("AddOpNode: tempvar"+ p_node.getType()+ p_node.m_moonVarName + p_node.m_symtab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_dims);
	}
	
	public void visit(MultOpNode p_node) {
		System.out.println("Visiting MultOpNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}		
		String tempvarname = this.getNewTempVarName();
		p_node.m_moonVarName = tempvarname;
		String vartype = p_node.getType();
		
		Vector<Integer> dimlist = new Vector<Integer>();
		
		/*for (Node dim : p_node.getChildren().get(1).getChildren()){
			// parameter dimension
			//System.out.println(dim.getData());
			Integer dimval = Integer.parseInt(dim.getData()); 
			dimlist.add(dimval); 
		}*/		
		p_node.m_symtabentry = new VarEntry("tempvar", vartype, p_node.m_moonVarName, dimlist);
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
		//System.out.println("MultOP: tempvar"+ vartype + p_node.m_moonVarName + dimlist);
	};

	
	  public void visit(RelExpr p_node) {
		System.out.println("Visiting RelExpr");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}		
		String tempvarname = this.getNewTempVarName();
		p_node.m_moonVarName = tempvarname;
		String vartype = p_node.getType();
		
		Vector<Integer> dimlist = new Vector<Integer>();
		
		for (Node dim : p_node.getChildren().get(1).getChildren()){
			// parameter dimension
			System.out.println("simi"+dim.getData());
			Integer dimval = Integer.parseInt(dim.getData()); 
			dimlist.add(dimval); 
		}	
		p_node.m_symtabentry = new VarEntry("tempvar", vartype, p_node.m_moonVarName, dimlist);
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
		
		System.out.println("RelExpr :tempvar"+ vartype+ p_node.m_moonVarName+ dimlist);
	};
	
	public void visit(AssignStatNode p_node) {
		System.out.println("Visiting AssignStatNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(ArithExpr p_node) {
		System.out.println("Visiting ArithExpr");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(Expr p_node) {
		System.out.println("Visiting Expr");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(VarNode p_node) {
		System.out.println("Visiting VarNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		
		
		for (Node child : p_node.getChildren()) {
			//System.out.println(child.);
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
			
			p_node.setData(p_node.getChildren().get(0).getData());				
		}
	
		
	}
	
     public void visit(DataMemberNode p_node) {
		System.out.println("Visiting DataMemberNode");
		    for (Node child : p_node.getChildren() )
			{
			  child.m_symtab = p_node.m_symtab;
			   child.accept(this);
			 
			}
		  
	}
	
     public void visit(FactorNode p_node) {
		System.out.println("Visiting FactorNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(TermNode p_node) {
		System.out.println("Visiting TermNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
	}
	
	public void visit(FactorNumNode p_node) {
		System.out.println("Visiting FactorNumNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren()) {
			child.m_symtab = p_node.m_symtab;
			child.accept(this);
		String childOperandType=p_node.getChildren().get(0).getType();
	    p_node.setData(p_node.getChildren().get(0).getData());
	  }
	}
	
	public void visit(FCallNode p_node) {
		System.out.println("Visiting FCallNode");
		// propagate accepting the same visitor to all the children
		// this effectively achieves Depth-First AST Traversal
		for (Node child : p_node.getChildren() )
		{	child.m_symtab = p_node.m_symtab;
			child.accept(this);
		}
		String tempvarname = this.getNewTempVarName();
		p_node.m_moonVarName = tempvarname;
		String vartype = p_node.getType();
		p_node.m_symtabentry = new VarEntry("retval", vartype, p_node.m_moonVarName, new Vector<Integer>());
		p_node.m_symtab.addEntry(p_node.m_symtabentry);
		
	}; 
	
	 public void visit(ParamListNode p_node) {
		 System.out.println("Visiting ParamListNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
		 for (Node child : p_node.getChildren()) {
			 child.m_symtab = p_node.m_symtab;
			 child.accept(this);
		 }
	 }

	 public void visit(PutSatNode p_node) {
			System.out.println("Visiting PutSatNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
		}
	 
	 public void visit(GetStatNode p_node)
	 {
		 System.out.println("Visiting GetStatNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	 
	 public void visit(ReturnStatNode p_node) {
			System.out.println("Visiting ReturnStatNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
		}
	 
	 public void visit(StatBlockNode p_node)
	 {
		 System.out.println("Visiting StatBlockNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	
	 //
	 public void visit(IfNode p_node)
	 {
		 System.out.println("Visiting IfNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	 
	 public void visit(indiceNode p_node)
	 {
		 System.out.println("Visiting indiceNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	 
	 public void visit(IndiceListNode p_node)
	 {
		 System.out.println("Visiting IndiceListNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	
	 public void visit(RelationOP p_node)
	 {
		 System.out.println("Visiting RelationOP");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getData();
	 }
	 
	 public void visit(DotOp p_node) {
	 
		 System.out.println("Visiting DotOp");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
			}
	 }
	 
	 public void visit(DotOpNode p_node){
		 
		 System.out.println("Visiting DotOpNode");
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren()) {
				child.m_symtab = p_node.m_symtab;
				child.accept(this);
												
			}
					SymTabEntry ch=p_node.m_symtab.lookupName_ClassName(p_node.getChildren().get(0).getChildren().get(0).getData());
					System.out.println(ch.m_type);
					p_node.set_var_type("classMem:"+ch.m_type); // this will set the left child of . and right child with var_type classMem:<class_name>
							
	}
	 
	


}
