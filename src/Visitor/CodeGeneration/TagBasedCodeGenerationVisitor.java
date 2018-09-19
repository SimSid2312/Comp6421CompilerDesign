package Visitor.CodeGeneration;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import AST.*;
import LexParser.AST_T;
import SymbolTable.SymTabEntry;
import Visitors.Visitor;

public class TagBasedCodeGenerationVisitor extends Visitor{

	  public Stack<String> m_registerPool   = new Stack<String>();
	    public Integer       m_tempVarNum     = 0;
	    public String        m_moonExecCode   = new String();               // moon code instructions part
	    public String        m_moonDataCode   = new String();               // moon code data part
	    public String        m_mooncodeindent = new String("           ");
	    public String        m_outputfilename = new String(); 
	    
	    
	    public TagBasedCodeGenerationVisitor() {
	       	// create a pool of registers as a stack of Strings
	    	// assuming only r1, ..., r12 are available
	    	for (Integer i = 12; i>=1; i--)
	    		m_registerPool.push("r" + i.toString());
	    }
	    
	    public TagBasedCodeGenerationVisitor(String p_filename) {
	    	this.m_outputfilename = p_filename; 
	       	// create a pool of registers as a stack of Strings
	    	// assuming only r1, ..., r12 are available
	    	for (Integer i = 12; i>=1; i--)
	    		m_registerPool.push("r" + i.toString());
	    }
	    
	    public void visit(ProgNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
	    	{	    		
			    System.out.println("Visiting ProgNode");
			    for (Node child : p_node.getChildren())
				    child.accept(this);	
			    if (!this.m_outputfilename.isEmpty()) {
				    File file = new File(this.m_outputfilename);
				try (PrintWriter out = new PrintWriter(file)) {
				    out.println(this.m_moonExecCode);
				    out.println(this.m_moonDataCode);}		
				catch(Exception e){
					e.printStackTrace();}
			   }
	    	}
		}
	    
	    public void visit(VarDeclNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
	    	{
	    		System.out.println("Visiting VarDeclNode");
			    for (Node child : p_node.getChildren() )
				       child.accept(this);
			// Then, do the processing of this nodes' visitor
			    System.out.println(p_node.getChildren().get(1).getData());
			    if(p_node.m_symtabentry!=null)
			    System.out.println(p_node.m_symtabentry.m_size);
			    if (p_node.getChildren().get(0).getData().equals("int"))
				    m_moonDataCode += m_mooncodeindent + "% space for variable " + p_node.getChildren().get(1).getData() + "\n";
				if (p_node.m_symtabentry!=null && !(p_node.m_symtabentry.m_size==0))    
			    m_moonDataCode += String.format("%-10s" ,p_node.getChildren().get(1).getData()) + " res "+p_node.m_symtabentry.m_size+"\n";
				else
			    m_moonDataCode += String.format("%-10s" ,p_node.getChildren().get(1).getData()) + " res 4"+"\n";	
	    	}
	    
	    }
	    
	    public void visit(GetStatNode p_node)
		{
	    	if(p_node.m_symtab!=null)
	    	{
	    		System.out.println("Visiting GetStatNode");
	    		for (Node child : p_node.getChildren())
	    			child.accept(this);
	    		 
	    		String localRegister1     = this.m_registerPool.pop();
	    		System.out.println("get here "+p_node.getChildren().get(0).m_moonVarName);
	    		//System.out.println(p_node.getChildren().get(0).m_moonVarName);
	    		
	    		if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
	    		{m_moonExecCode += m_mooncodeindent + "% processing: get("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
	    		m_moonExecCode += m_mooncodeindent + "addi " + localRegister1 + ",r0, buf\n";
	    		m_moonExecCode += m_mooncodeindent + "sw -8(r14)," + localRegister1 + "\n";
	    		m_moonExecCode += m_mooncodeindent + "jl r15, getstr\n";
	    		m_moonExecCode += m_mooncodeindent + "jl r15, strint\n";
	    		m_moonExecCode += m_mooncodeindent + "sw " + p_node.getChildren().get(0).m_moonVarName + "(r0),r13\n";}
	    		
	    		else
	    		{
	    			//String localRegister = this.m_registerPool.pop();
	    			System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
	    			if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
	    			   && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().size()>0)
	    			{
	    				System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar());
	    				m_moonExecCode += m_mooncodeindent + "% processing: get("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
	    				// Loop over the dimension of the array
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
	    				
	    				for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().size();i++)
	    				{
	    					String localRegister2 = this.m_registerPool.pop();
				    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().get(i);
				    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
				    		
				    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
				    		//deallocate local register
							this.m_registerPool.push(localRegister2);
	    				}
	    				m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
	    				m_moonExecCode += m_mooncodeindent + "addi " + localRegister1 + ",r0, buf\n";
	    				m_moonExecCode += m_mooncodeindent + "sw -8(r14)," + localRegister1 + "\n";
	    	    		m_moonExecCode += m_mooncodeindent + "jl r15, getstr\n";
	    	    		m_moonExecCode += m_mooncodeindent + "jl r15, strint\n";
	    	    		m_moonExecCode += m_mooncodeindent + "sw " + p_node.getChildren().get(0).getData().split(":")[0] + "(r0),r13\n";
	    				//System.out.println(m_moonExecCode);
	    			}
	    			
	    		}
	    		
	    		//deallocate local register
				this.m_registerPool.push(localRegister1);	
	    	}
		}
	    
	    public void visit(NumNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null) 
	    	{
	    		System.out.println("Visiting NumNode");
			    for (Node child : p_node.getChildren() )
				    child.accept(this);
			    // Then, do the processing of this nodes' visitor
			   // create a local variable and allocate a register to this subcomputation 
			    String localRegister = this.m_registerPool.pop();
			   //generate code
			  //System.out.printlnln("p_node.getData(): "+p_node.getData());
			  //System.out.printlnln("p_node.m_moonVarName: "+p_node.m_moonVarName);
			    //if (!m_moonDataCode.contains(p_node.getData()))
			  m_moonDataCode += m_mooncodeindent + "% space for constant " + p_node.getData() + "\n";
			  m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";		    
			  m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName  + " := " + p_node.getData() + "\n";
			  m_moonExecCode += m_mooncodeindent + "addi " + localRegister + ",r0," + p_node.getData() + "\n"; 
			  m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			  //System.out.println("m_moonDataCode: "+m_moonDataCode);
			  //System.out.println("m_moonExecCode: "+m_moonExecCode);
			  //System.out.println("----------------------------");
			 // deallocate the register for the current node
			 this.m_registerPool.push(localRegister);
		  }
	   }

	    public void visit(AddOpNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{
	    		System.out.println("Visiting AddOpNode");
			    for (Node child : p_node.getChildren() )
				   child.accept(this);
			// Then, do the processing of this nodes' visitor
			// create a local variable and allocate a register to this subcomputation 
			    String localRegister      = this.m_registerPool.pop();
			    String localRegister1 =this.m_registerPool.pop();
			    String leftChildRegister  = this.m_registerPool.pop();
			    String rightChildRegister = this.m_registerPool.pop();
			// generate code
			 
			 // ------------------------------------- THE ADD OPERATOR----------------------------------------------------------------------------------->				
			  if (p_node.get_OP().equals("+"))
			    {  m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " + " + p_node.getChildren().get(1).m_moonVarName + "\n";
			    System.out.println("addop"+p_node.getChildren().get(0).m_moonVarName);
			    //--------------------------------->CHECKING THE LEFTCHILD----------------------------->
				if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
				{
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
						    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
						{
							System.out.println("here:"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
							// Loop over the dimension of the array
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
					    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
					    	{
					    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
					    		String localRegister2 = this.m_registerPool.pop();
					    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
					    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
					    		//deallocate local register
								this.m_registerPool.push(localRegister2);
					    	}
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
					    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
					    	
					    	//deallocate local register
							this.m_registerPool.push(localRegister1);
						}		
				
				
				}
				else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
				{
					 m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister +  "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";	
				}
				
				//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
				if (p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
					System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
						    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
						{
							System.out.println("here:"+p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
							// Loop over the dimension of the array
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
					    	for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
					    	{
					    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
					    		String localRegister2 = this.m_registerPool.pop();
					    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
					    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
					    		//deallocate local register
								this.m_registerPool.push(localRegister2);
					    	}
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
					    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
					    	
					    	//deallocate local register
							this.m_registerPool.push(localRegister1);
						}		
					
				}
				else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					 m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
				}			    
			    
			    
			      
			      
			       m_moonExecCode += m_mooncodeindent + "add " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n"; 
			       m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " + " + p_node.getChildren().get(1).m_moonVarName + "\n";
			       m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
			       m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			    }
// ----------------------------------------------- MINUS OPERATOR --------------------------------------------------------------------------------------------->
			    else if (p_node.get_OP().equals("-"))
			    {
			    	m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " - " + p_node.getChildren().get(1).m_moonVarName + "\n";
			    	 //--------------------------------->CHECKING THE LEFTCHILD----------------------------->
			    	if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
					{
			    		System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
						System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
						if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
							    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
							{
								System.out.println("here:"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
								// Loop over the dimension of the array
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
						    	{
						    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
						    		String localRegister2 = this.m_registerPool.pop();
						    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
						    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
						    		//deallocate local register
									this.m_registerPool.push(localRegister2);
						    	}
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
						    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
						    	
						    	//deallocate local register
								this.m_registerPool.push(localRegister1);
							}		
					}
					else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
					{
						m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister +  "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
					}
			    	//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
			    	if (p_node.getChildren().get(1).m_moonVarName.contains("["))
					{
			    		System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
						System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
						if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
							    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
							{
								System.out.println("here:"+p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
								// Loop over the dimension of the array
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						    	for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
						    	{
						    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
						    		String localRegister2 = this.m_registerPool.pop();
						    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
						    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
						    		//deallocate local register
									this.m_registerPool.push(localRegister2);
						    	}
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
						    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
						    	
						    	//deallocate local register
								this.m_registerPool.push(localRegister1);
							}		
					}
					else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
					{
						m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
					}    	
			    	
			    	
				    
				    m_moonExecCode += m_mooncodeindent + "sub " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n"; 
				    m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " - " + p_node.getChildren().get(1).m_moonVarName + "\n";
				    m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
				    m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			    	
			    }
			  
//---------------------------------------------------OR OPERATOR------------------------------------------------------------------------------------------------->
			    else if (p_node.get_OP().equals("or"))
			    {
			    	m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " or " + p_node.getChildren().get(1).m_moonVarName + "\n";
			    	 //--------------------------------->CHECKING THE LEFTCHILD----------------------------->
			    	if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
					{
			    		System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
						System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
						if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
							    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
							{
								System.out.println("here:"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
								// Loop over the dimension of the array
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
						    	{
						    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
						    		String localRegister2 = this.m_registerPool.pop();
						    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
						    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
						    		//deallocate local register
									this.m_registerPool.push(localRegister2);
						    	}
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
						    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
						    	
						    	//deallocate local register
								this.m_registerPool.push(localRegister1);
							}		
					}
					else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
					{
						m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister +  "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
					}
			    	//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
			    	if (p_node.getChildren().get(1).m_moonVarName.contains("["))
					{
			    		System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
						System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
						if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
							    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
							{
								System.out.println("here:"+p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
								// Loop over the dimension of the array
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						    	for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
						    	{
						    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
						    		String localRegister2 = this.m_registerPool.pop();
						    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
						    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
						    		//deallocate local register
									this.m_registerPool.push(localRegister2);
						    	}
						    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
						    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
						    	
						    	//deallocate local register
								this.m_registerPool.push(localRegister1);
							}		
					}
					else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
					{
						 m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
					}
			    	
			    	
			    			    	
			    	
				   
				    m_moonExecCode += m_mooncodeindent + "or " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n"; 
				    m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " or " + p_node.getChildren().get(1).m_moonVarName + "\n";
				    m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
				    m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			    }
			   
			 
			
			    
			    
			// deallocate the registers for the two children, and the current node
			   this.m_registerPool.push(leftChildRegister);
			   this.m_registerPool.push(rightChildRegister);
			   this.m_registerPool.push(localRegister);
			   //System.out.println(m_moonExecCode);
		    }

	    }
	    	    
	    public void visit(MultOpNode p_node){ 
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{
	    		System.out.println("Visiting MultOpNode");
			    for (Node child : p_node.getChildren() )
				    child.accept(this);
			// Then, do the processing of this nodes' visitor		
			// create a local variable and allocate a register to this subcomputation 
			String localRegister      = this.m_registerPool.pop();
			String localRegister1 =this.m_registerPool.pop();
			String leftChildRegister  = this.m_registerPool.pop();
			String rightChildRegister = this.m_registerPool.pop();
			
			// generate code
// ------------------------------------- THE MULTIPLICATIOn OPERATOR----------------------------------------------------------------------------------->	
			if(p_node.get_OP().equals("*"))
			{m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " * " + p_node.getChildren().get(1).m_moonVarName + "\n";
			//--------------------------------->CHECKING THE LEFTCHILD----------------------------->
			if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
			{
				//System.out.println("left child --> for array"); 
				//System.out.println("here for evaluation:"+p_node.getChildren().get(0).m_moonVarName);
				System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
				System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
				if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
				    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
				{
					System.out.println("here:"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getNodeType());
					// Loop over the dimension of the array
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
			    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
			    	{
			    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
			    		String localRegister2 = this.m_registerPool.pop();
			    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
			    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
			    		//deallocate local register
						this.m_registerPool.push(localRegister2);
			    	}
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
			    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
			    	
			    	//deallocate local register
					this.m_registerPool.push(localRegister1);
				}
					
				
			}
			else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
			{
				m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
			}
			
			//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
			if (p_node.getChildren().get(1).m_moonVarName.contains("["))
			{
				System.out.println("right child --> for array"); 
				System.out.println("here for evaluation:"+p_node.getChildren().get(1).m_moonVarName);
				System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
				if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
					    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
					{
					//System.out.println("here:"+p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getNodeType());
					   m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
					   for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
				    	{
				    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
				    		String localRegister2 = this.m_registerPool.pop();
				    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
				    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
				    		//deallocate local register
							this.m_registerPool.push(localRegister2);
				    	}
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
				    	m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
				    	//deallocate local register
						this.m_registerPool.push(localRegister1);
									
					}
				
			}
			else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
			{
				m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
			}					
			
			m_moonExecCode += m_mooncodeindent + "mul " + localRegister      + "," + leftChildRegister + "," + rightChildRegister + "\n"; 
			m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " * " + p_node.getChildren().get(1).m_moonVarName + "\n";
			m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
			m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			//System.out.println(m_moonExecCode);
			}
			
// ------------------------------------- THE DIVISION OPERATOR----------------------------------------------------------------------------------->			
			 else if (p_node.get_OP().equals("/"))
			{
				m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " / " + p_node.getChildren().get(1).m_moonVarName + "\n";
				//m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
				//m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
				if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
				{
					//System.out.println("left child --> for array"); 
					//System.out.println("here for evaluation:"+p_node.getChildren().get(0).m_moonVarName);
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
					    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
					{
						System.out.println("here");
						// Loop over the dimension of the array
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
				    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
				    	{
				    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
				    		String localRegister2 = this.m_registerPool.pop();
				    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
				    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
				    		//deallocate local register
							this.m_registerPool.push(localRegister2);
				    	}
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
				    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
				    	//deallocate local register
						this.m_registerPool.push(localRegister1);
					}
						
					
				}
				else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
				{
					m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
				}
				
				//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
				if (p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					System.out.println("right child --> for array"); 
					System.out.println("here for evaluation:"+p_node.getChildren().get(1).m_moonVarName);
					System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
						    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
						{
						   m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						   for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
					    	{
					    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
					    		String localRegister2 = this.m_registerPool.pop();
					    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
					    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
					    		//deallocate local register
								this.m_registerPool.push(localRegister2);
					    	}
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
					    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
					    	//deallocate local register
							this.m_registerPool.push(localRegister1);
										
						}
					
				}
				else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
				}					
				
				
				
				
				m_moonExecCode += m_mooncodeindent + "div " + localRegister      + "," + leftChildRegister + "," + rightChildRegister + "\n"; 
				m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " / " + p_node.getChildren().get(1).m_moonVarName + "\n";
				m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
				m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			}
// ----------------------------------------- THE LOGICAL AND OPERATOR ---------------------------------------------------->
			else if (p_node.get_OP().equals("and"))
			{
				m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " and " + p_node.getChildren().get(1).m_moonVarName + "\n";
				if(p_node.getChildren().get(0).m_moonVarName.contains("[")) //evaluate 
				{
					//System.out.println("left child --> for array"); 
					//System.out.println("here for evaluation:"+p_node.getChildren().get(0).m_moonVarName);
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size());
					System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
					    && p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
					{
						System.out.println("here");
						// Loop over the dimension of the array
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
				    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
				    	{
				    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
				    		String localRegister2 = this.m_registerPool.pop();
				    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
				    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
				    		//deallocate local register
							this.m_registerPool.push(localRegister2);
				    	}
				    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
				    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
				    	//deallocate local register
						this.m_registerPool.push(localRegister1);
					}
						
					
				}
				else if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
				{
					m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
				}
				
				//--------------------------------->CHECKING THE RIGHTCHILD----------------------------->
				if (p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					System.out.println("right child --> for array"); 
					System.out.println("here for evaluation:"+p_node.getChildren().get(1).m_moonVarName);
					System.out.println(p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
					if (p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
						    && p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
						{
						   m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + "r0" + "," +"1" + "\n";
						   for (int i=0;i<p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size();i++)
					    	{
					    		String ele=p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.get(i);
					    		String localRegister2 = this.m_registerPool.pop();
					    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
					    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister1 + "," + localRegister1 + "," +localRegister2+ "\n";
					    		//deallocate local register
								this.m_registerPool.push(localRegister2);
					    	}
					    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister1 + "," + localRegister1 + "," +4+ "\n";
					    	m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister  + "," + p_node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getData().split(":")[0]+"("+localRegister1+")"+"\n";
					    	//deallocate local register
							this.m_registerPool.push(localRegister1);
										
						}
					
				}
				else if (!p_node.getChildren().get(1).m_moonVarName.contains("["))
				{
					m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(1).m_moonVarName + "(r0)\n";
				}					
				
				
				
				
				m_moonExecCode += m_mooncodeindent + "and " + localRegister      + "," + leftChildRegister + "," + rightChildRegister + "\n"; 
				m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + " and " + p_node.getChildren().get(1).m_moonVarName + "\n";
				m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
				m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			}
			
			// deallocate the registers for the two children, and the current node
			this.m_registerPool.push(leftChildRegister);
			this.m_registerPool.push(rightChildRegister);
			this.m_registerPool.push(localRegister);
		   }
	    }
	    
	    public void visit(RelExpr p_node)
		{
			if(p_node.m_symtab!=null)
			{
				System.out.println("Visiting RelExpr");
				for (Node child : p_node.getChildren() )
					child.accept(this);
				
				// Then, do the processing of this nodes' visitor
				// create a local variable and allocate a register to this subcomputation 
				   String localRegister      = this.m_registerPool.pop();
				  // String localRegister1 =this.m_registerPool.pop();
				   String leftChildRegister  = this.m_registerPool.pop();
				   String rightChildRegister = this.m_registerPool.pop();
				// generate code
				   
				   
			    m_moonExecCode += m_mooncodeindent + "% processing: " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " "+p_node.getChildren().get(1).m_moonVarName+" " + p_node.getChildren().get(2).m_moonVarName + "\n";  
			    m_moonExecCode += m_mooncodeindent + "lw "  + leftChildRegister +  "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
			    m_moonExecCode += m_mooncodeindent + "lw "  + rightChildRegister + "," + p_node.getChildren().get(2).m_moonVarName + "(r0)\n";
			    if(p_node.getChildren().get(1).m_moonVarName.equals(">"))
			    	m_moonExecCode += m_mooncodeindent + "cgt " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    else if (p_node.getChildren().get(1).m_moonVarName.equals(">="))
			    	m_moonExecCode += m_mooncodeindent + "cge " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    else if (p_node.getChildren().get(1).m_moonVarName.equals("<"))
			    	m_moonExecCode += m_mooncodeindent + "clt " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    else if (p_node.getChildren().get(1).m_moonVarName.equals("<="))
			    	m_moonExecCode += m_mooncodeindent + "cle " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    else if (p_node.getChildren().get(1).m_moonVarName.equals("<>"))
			    	m_moonExecCode += m_mooncodeindent + "cne " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    else if (p_node.getChildren().get(1).m_moonVarName.equals("=="))
			    	m_moonExecCode += m_mooncodeindent + "ceq " + localRegister +      "," + leftChildRegister + "," + rightChildRegister + "\n";
			    
			    m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localRegister + "\n";
			    
			    m_moonDataCode += m_mooncodeindent + "% space for " + p_node.getChildren().get(0).m_moonVarName + p_node.getChildren().get(1).m_moonVarName + p_node.getChildren().get(2).m_moonVarName + "\n";
			    m_moonDataCode += String.format("%-10s",p_node.m_moonVarName) + " res 4\n";
			    
			 // deallocate the registers for the two children, and the current node
				   this.m_registerPool.push(leftChildRegister);
				   this.m_registerPool.push(rightChildRegister);
				   this.m_registerPool.push(localRegister);
			}

		}
	   
	    public void visit(RelationOP p_node)
	    {
	    	System.out.println("Visiting RelationOP");
	    	for (Node child : p_node.getChildren() )
				child.accept(this);
	    }
	    
	    
	    public void visit(AssignStatNode p_node){ 
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{
	    		System.out.println("Visiting AssignStatNode");
			    for (Node child : p_node.getChildren() )
				  child.accept(this);
			// Then, do the processing of this nodes' visitor
			// allocate local register
			String localRegister = this.m_registerPool.pop();
			//generate code
			String rightChild = "";
			String func_type="";
			for (SymTabEntry ch:AST_T.Prog.m_symtab.m_symlist)
			{
				if(ch.m_name.contains(p_node.getChildren().get(1).m_moonVarName))
				{
					//System.out.println(ch.m_kind);
					func_type=ch.m_kind;
				}
			}			
			
			if(func_type.equals("func"))
			{				
				rightChild=p_node.getChildren().get(1).m_moonVarName+"return";
			}
			else
			{
				rightChild=p_node.getChildren().get(1).m_moonVarName;
			}
			if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
			{m_moonExecCode += m_mooncodeindent + "% processing: "  + p_node.getChildren().get(0).m_moonVarName + " := " + rightChild + "\n";
			m_moonExecCode += m_mooncodeindent + "lw " + localRegister + "," + rightChild + "(r0)\n";
			m_moonExecCode += m_mooncodeindent + "sw " + p_node.getChildren().get(0).m_moonVarName + "(r0)," + localRegister + "\n";
			
			}
			 else //Part that run for dimension for an array
			 {
				
				//System.out.println(p_node.getChildren().get(0).get);
				System.out.println(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType());
				
			    if(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode") 
			    		&& p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar.size()>0)
			    {
			    	m_moonExecCode += m_mooncodeindent + "% processing: "  + p_node.getChildren().get(0).m_moonVarName + " := " + rightChild + "\n";
			    	
			    	System.out.println("here"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).my_indiceList_moonVar);
			    	// Loop over the dimension of the array
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister + "," + "r0" + "," +"1" + "\n";
			    	
			    	
			    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().size();i++)
			    	{
			    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().get(i);
			    		String localRegister1 = this.m_registerPool.pop();
			    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister1 + "," + ele+ "(r0)\n";
			    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister + "," + localRegister + "," +localRegister1+ "\n";
			    		//deallocate local register
						this.m_registerPool.push(localRegister1);
			    	}
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister + "," + localRegister + "," +4+ "\n";
			    	//System.out.println();
			    	String localRegister1 = this.m_registerPool.pop();
			    	m_moonExecCode += m_mooncodeindent + "lw " + localRegister1 + "," + rightChild+ "(r0)\n";
			    	m_moonExecCode += m_mooncodeindent + "sw " + p_node.getChildren().get(0).getData().split(":")[0]+"("+localRegister+")" + "," + localRegister1 + "\n";
			    	//System.out.println(m_moonExecCode);	
			    	this.m_registerPool.push(localRegister1);
			    }
				
			 }
			//deallocate local register
			this.m_registerPool.push(localRegister);
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		   }
	    }
	    
	    public void visit (FuncDefStatBlock p_node) // this block comes after program
	    {		
	 	  if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("Prog")) // Condition that checks this is Program Block
	 	  {
	 		// generate moon program's entry point
			System.out.println("Visiting ProgramBlockNode");
			m_moonExecCode += m_mooncodeindent + "entry\n";
			m_moonExecCode += m_mooncodeindent + "addi r14,r0,topaddr\n";
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			for (Node child : p_node.getChildren())
				child.accept(this);
			// generate moon program's end point
			m_moonDataCode += m_mooncodeindent + "% buffer space used for console output\n";
			m_moonDataCode += String.format("%-11s", "buf") + "res 20\n";
			m_moonExecCode += m_mooncodeindent + "hlt\n";
	 	  }
	 	
	 	 else if (p_node.m_symtab!=null && p_node.getParent().getNodeType().equals("funcDef"))//FuncDefNode
	 	 {
	 		System.out.println("Visiting IdNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
	 	 }
	 	
	 	
	    }
	    
	    public void visit(fParamNode p_node)
	    {
	    	// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
	    	{
	    		System.out.println("Visiting fParamNode");
			    for (Node child : p_node.getChildren() )
				       child.accept(this);
			// Then, do the processing of this nodes' visitor
			    System.out.println(p_node.m_moonVarName);
			    if (p_node.getChildren().get(0).getData().equals("int"))
				    m_moonDataCode += m_mooncodeindent + "% space for variable " + p_node.getChildren().get(1).getData() + "\n";
				    m_moonDataCode += String.format("%-10s" ,p_node.getChildren().get(1).getData()) + " res 4\n";
		   }
	    }
	   
        public void visit(PutSatNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{System.out.println("Visiting PutStatNode");
			
			for (Node child : p_node.getChildren())
				{  
				  child.accept(this);
				}
			

	
			// Then, do the processing of this nodes' visitor		
			// create a local variable and allocate a register to this subcomputation 
			String localRegister      = this.m_registerPool.pop();
			System.out.println(p_node.getChildren().get(0).m_moonVarName);
			//generate code
			if (!p_node.getChildren().get(0).m_moonVarName.contains("["))
			{m_moonExecCode += m_mooncodeindent + "% processing: put("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
			m_moonExecCode += m_mooncodeindent + "lw " + localRegister + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
			m_moonExecCode += m_mooncodeindent + "% put value on stack\n";	
			m_moonExecCode += m_mooncodeindent + "sw -8(r14)," + localRegister + "\n";
			m_moonExecCode += m_mooncodeindent + "% link buffer to stack\n";	
			m_moonExecCode += m_mooncodeindent + "addi " + localRegister + ",r0, buf\n";
			m_moonExecCode += m_mooncodeindent + "sw -12(r14)," + localRegister + "\n";
			m_moonExecCode += m_mooncodeindent + "% convert int to string for output\n";	
			m_moonExecCode += m_mooncodeindent + "jl r15, intstr\n";	
			m_moonExecCode += m_mooncodeindent + "sw -8(r14),r13\n";
			m_moonExecCode += m_mooncodeindent + "% output to console\n";	
			m_moonExecCode += m_mooncodeindent + "jl r15, putstr\n";}
			else
			{
				
				String localRegister1     = this.m_registerPool.pop();
				if (p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getNodeType().equals("indexListNode")
					&& p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().size()>0)
				{
					System.out.println("here"+p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar());
					m_moonExecCode += m_mooncodeindent + "% processing: put("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
					
					// Loop over the dimension of the array
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister + "," + "r0" + "," +"1" + "\n";
			    	String ele_id_data=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData();
			    	for (int i=0;i<p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().size();i++)
			    	{
			    		String localRegister2 = this.m_registerPool.pop();
			    		String ele=p_node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).get_indiceList_moonvar().get(i);
			    		m_moonExecCode += m_mooncodeindent + "lw " + localRegister2 + "," + ele+ "(r0)\n";
			    		
			    		m_moonExecCode += m_mooncodeindent + "mul " + localRegister + "," + localRegister + "," +localRegister2+ "\n";
			    		//deallocate local register
						this.m_registerPool.push(localRegister2);   		
			    	}
			    	m_moonExecCode += m_mooncodeindent + "muli " + localRegister + "," + localRegister + "," +4+ "\n";
			    	m_moonExecCode += m_mooncodeindent + "lw " + localRegister1 + "," + ele_id_data.split(":")[0] + "("+localRegister+")"+"\n";
			    	m_moonExecCode += m_mooncodeindent + "% put value on stack\n";
			    	m_moonExecCode += m_mooncodeindent + "sw -8(r14)," + localRegister1 + "\n";
			    	m_moonExecCode += m_mooncodeindent + "% link buffer to stack\n";	
					m_moonExecCode += m_mooncodeindent + "addi " + localRegister1 + ",r0, buf\n";
					m_moonExecCode += m_mooncodeindent + "sw -12(r14)," + localRegister1 + "\n";
					m_moonExecCode += m_mooncodeindent + "% convert int to string for output\n";	
					m_moonExecCode += m_mooncodeindent + "jl r15, intstr\n";	
					m_moonExecCode += m_mooncodeindent + "sw -8(r14),r13\n";
					m_moonExecCode += m_mooncodeindent + "% output to console\n";	
					m_moonExecCode += m_mooncodeindent + "jl r15, putstr\n";
					//deallocate local register
					this.m_registerPool.push(localRegister1);
			    	//System.out.println(m_moonExecCode);
				}
			}
			//deallocate local register
			this.m_registerPool.push(localRegister);		
		  }
	    }
	    	    
	    public void visit(FuncDefNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal

	    	if(p_node.m_symtab!=null)
			{System.out.println("Visiting FuncDefNode");
			m_moonExecCode += m_mooncodeindent + "% processing function definition: "  + p_node.m_moonVarName + "\n";
			//create the tag to jump onto 
			m_moonExecCode += String.format("%-10s",p_node.getData());
			// copy the jumping-back address value in a tagged cell named "fname" appended with "link"
			m_moonDataCode += String.format("%-11s", p_node.getData() + "link") + "res 4\n";
			m_moonExecCode += m_mooncodeindent + "sw " + p_node.getData() + "link(r0),r15\n";
			// tagged cell for return value
			// here assumed to be integer (limitation)
			m_moonDataCode += String.format("%-15s", p_node.getData() + "return") + "res 4\n";
			//generate the code for the function body
			for (Node child : p_node.getChildren())
				child.accept(this);
			// copy back the jumping-back address into r15
			   m_moonExecCode += m_mooncodeindent + "lw r15," + p_node.getData() + "link(r0)\n";
			// jump back to the calling function
			   m_moonExecCode += m_mooncodeindent + "jr r15\n";
			
		  }
	      System.out.println("-----------------------------");
	    }
	    
	    public void visit(FCallNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{System.out.println("Visiting FuncCallNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			String localregister1 = this.m_registerPool.pop();
			// pass parameters directly in the function's local variables
			// it is assumed that the parameters are the first n entries in the 
			// function's symbol table 
			// here we assume that the parameters are the size of a word, 
			// which is not true for arrays and objects. 
			// In those cases, a loop copying the values e.g. byte-by-byte is necessary
			//System.out.println(p_node.m_symtab);
			
			String fcall_lookup=p_node.getData()+":"+p_node.getChildren().get(1).getData();
		
			System.out.println(AST_T.Prog.getChildren().get(1).m_symtab.lookup_fcall_funcDef(fcall_lookup));
			System.out.println("------------------------");
			//System.out.println(AST_T.Prog.getChildren().get(1).m_symtab);
			//SymTabEntry tableentryofcalledfunction = p_node.m_symtab.lookupName(p_node.getData());
			SymTabEntry tableentryofcalledfunction = AST_T.Prog.getChildren().get(1).m_symtab.lookup_fcall_funcDef(fcall_lookup);
		    System.out.println(tableentryofcalledfunction.m_name);
			
		    int indexofparam = 0;
			m_moonExecCode += m_mooncodeindent + "% processing: function call to "  + p_node.getChildren().get(0).m_moonVarName+ "link(r0)" + " \n";
			
						
			for(Node param : p_node.getChildren().get(1).getChildren()){
				
				m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + param.m_moonVarName + "(r0)\n";
			    String nameofparam = tableentryofcalledfunction.m_subtable.m_symlist.get(indexofparam).m_name;
			    m_moonExecCode += m_mooncodeindent + "sw " + nameofparam + "(r0)," + localregister1 + "\n";
				indexofparam++;
			}
			// jump to the called function's code
			// here the name of the label is assumed to be the function's name
			// a unique label generator is necessary in the general case (limitation)
			m_moonExecCode += m_mooncodeindent + "jl r15," + p_node.getData() + "\n";
			// copy the return value in a tagged memory cell
			m_moonDataCode += m_mooncodeindent + "% space for function call expression factor\n";		
			m_moonDataCode += String.format("%-11s", p_node.m_moonVarName) + "res 4\n";
			m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + p_node.getData() + "return(r0)\n";
			m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_moonVarName + "(r0)," + localregister1 + "\n";
			this.m_registerPool.push(localregister1);	
		   }
	    }
	    
	    public void visit(ReturnStatNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{System.out.println("Visiting ReturnStatNode");
			String localregister1 = this.m_registerPool.pop();
			for (Node child : p_node.getChildren() )
				child.accept(this);
			// copy the result of the return value in a cell tagged with the name "function name" + "return", e.g. "f1return"
			// get the function name from the symbol table
			String m_tag;
			if(p_node.m_symtab.m_name.contains("::"))
			{
				m_tag=p_node.m_symtab.m_name.split("::")[1].split(":")[0];
			}
			else
				m_tag=p_node.m_symtab.m_name.split(":")[0];
			
			System.out.println(m_tag);
			m_moonExecCode += m_mooncodeindent + "% processing: return("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
			m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
			m_moonExecCode += m_mooncodeindent + "sw "   + m_tag + "return(r0)," + localregister1 + "\n";
			this.m_registerPool.push(localregister1);	
		   }
	    }

	    public void visit(IfNode p_node)
	    {
	    	// propagate accepting the same visitor to all the children
	    	// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
	    	{
	    		
	    		System.out.println("Visiting IfNode");
	    		String localregister1 = this.m_registerPool.pop();
	    		//for (Node child : p_node.getChildren().get(0).)
					//child.accept(this);
	    		p_node.getChildren().get(0).accept(this);
	    		//System.out.println("0"+p_node.getChildren().get(0).getData()+" "+p_node.getChildren().get(0).m_moonVarName);
	    		//System.out.println("1"+p_node.getChildren().get(1).getData()+" "+p_node.getChildren().get(1).m_moonVarName);
	    		//System.out.println("2"+p_node.getChildren().get(2).getData()+" "+p_node.getChildren().get(2).m_moonVarName);
	    		m_moonExecCode += m_mooncodeindent + "% processing: if("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
	    		m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + p_node.getChildren().get(0).m_moonVarName + "(r0)\n";
	    		m_moonExecCode += m_mooncodeindent + "bz " + localregister1 + ",else1"+"\n";
	    		
	    		
	    		p_node.getChildren().get(1).accept(this);
	    		m_moonExecCode += m_mooncodeindent + "j endif1" + "\n";
	    		
	    		m_moonExecCode += "else1"+ "\n";
	            p_node.getChildren().get(2).accept(this);
	            m_moonExecCode += "endif1" + "\n";
	            this.m_registerPool.push(localregister1);
	    	}
	    }
	
	    public void visit(ForStatNode p_node)
	    {
	    	if(p_node.m_symtab!=null)
	    	{
	    		System.out.println("Visiting ForStatNode");
	    		p_node.getChildren().get(0).accept(this);
	            p_node.getChildren().get(1).accept(this);
	            p_node.getChildren().get(2).accept(this);
	            String localregister1 = this.m_registerPool.pop();
	            
	            
	            
	           // System.out.println(p_node.getChildren().get(2).m_moonVarName+"---"+p_node.getChildren().get(1).m_moonVarName+"----"+p_node.getChildren().get(3).m_moonVarName);
	            
	            m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," +p_node.getChildren().get(2).m_moonVarName+"(r0)"+"\n";
	            m_moonExecCode += m_mooncodeindent + "sw " + p_node.getChildren().get(1).m_moonVarName+"(r0)" +" , "+ localregister1 + "\n";
	            
	            m_moonExecCode += "goFor1" + "\n";
	            p_node.getChildren().get(3).accept(this);
	            m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + p_node.getChildren().get(3).m_moonVarName+"(r0)"+"\n";
	            m_moonExecCode += m_mooncodeindent + "bz " + localregister1 + ",EndgoFor1" +"\n";
	            
	            p_node.getChildren().get(5).accept(this);

	            p_node.getChildren().get(4).accept(this);
	            
	            m_moonExecCode += m_mooncodeindent + "j goFor1"+"\n";
	            m_moonExecCode += "EndgoFor1" + "\n";
	            if (p_node.getChildren().get(0).getData().equals("int"))
				    m_moonDataCode += m_mooncodeindent + "% space for variable " + p_node.getChildren().get(1).getData() + "\n";
				    m_moonDataCode += String.format("%-10s" ,p_node.getChildren().get(1).getData()) + " res 4\n";
	            this.m_registerPool.push(localregister1);
	    	}
	    }
	    
	 // Below are the visit methods for node types for which this visitor does
     // not apply. They still have to propagate acceptance of the visitor to
     // their children.
	    
	    public void visit(ParamListNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
	    	{System.out.println("Visiting ParamListNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
	        }
	    }
	    
	    
	    public void visit(ClassListNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
	    	if(p_node.m_symtab!=null)
			{System.out.println("Visiting ClassListNode");
			for (Node child : p_node.getChildren())
				child.accept(this);
			}
		}

		public void visit(ClassNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting ClassNode:"+p_node.getChildren().get(0).getData()+p_node.getChildren().get(0).getLine());
			for (Node child : p_node.getChildren())
				child.accept(this);
			}
			System.out.println("-------------");
		}

		public void visit(DimListNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting DimListNode");
			for (Node child : p_node.getChildren())
				child.accept(this);
			}
		}

		public void visit(FuncDefListNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting FuncDefListNode");
			for (Node child : p_node.getChildren())
				child.accept(this);
			}
		}

		public void visit(StatBlockNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			System.out.println("Visiting StatBlockNode");
			for (Node child : p_node.getChildren())
				child.accept(this);
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}

		public void visit(TypeNode p_node) {
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting TypeNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
	    }	

		public void visit(IdNode p_node){
			// propagate accepting the same visitor to all the children
			// this effectively achieves Depth-First AST Traversal
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting IdNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
		}
	    
	    // Below function added by me --------------------------------------------->
		public void visit(FactorNumNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting FactorNumNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}
	    		
		public void visit(StatNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting StatNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}
 
		public void visit(FactorNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting FactorNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}

		public void visit(TermNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting TermNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}
		
		public void visit(ArithExpr p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting ArithExpr");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}
		
		public void visit(Expr p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting Expr");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
		}
		
		public void visit(DataMemberNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting DataMemberNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			
		}
		
		public void visit(VarNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting VarNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			
		}
		
		public void visit (IndiceListNode p_node)
		{
			List<String> indice_moonname=new ArrayList<String>();
			if(p_node.m_symtab!=null)
			{
				System.out.println("Visiting IndiceListNode");
			   for (Node child : p_node.getChildren() )
			   {  
				
				child.accept(this);
				indice_moonname.add(child.m_moonVarName);
			   }
			}
			if (indice_moonname.size()>=1)
			{
				System.out.println(indice_moonname);
				p_node.set_indiceList_moonVar(indice_moonname);
			}
			
			System.out.println(p_node.get_indiceList_moonvar());
			
			
		}
		
		public void visit(indiceNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting indiceNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			
			p_node.m_moonVarName=p_node.getChildren().get(0).m_moonVarName;
			System.out.println("indiceNode"+p_node.m_moonVarName);
		}
		
		public void visit (MemberListNode p_node)
		{
			if(p_node.m_symtab!=null)
			{System.out.println("Visiting MemberListNode");
			for (Node child : p_node.getChildren() )
				child.accept(this);
			}
			
		}
		
		public void visit(DotOp p_node) {
			  
			  if(p_node.m_symtab!=null)
				{System.out.println("Visiting DotOp");
				for (Node child : p_node.getChildren() )
					child.accept(this);
				}
		}
			 
		public void visit(DotOpNode p_node){
				
				 if(p_node.m_symtab!=null)
					{System.out.println("Visiting DotOpNode");
					for (Node child : p_node.getChildren() )
						child.accept(this);
					
				}
			 }

		
		
			
}
		
		
		


