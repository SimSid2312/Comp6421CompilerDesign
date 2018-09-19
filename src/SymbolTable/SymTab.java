package SymbolTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import AST.*;
import LexParser.*;
public class SymTab {
	public String                 m_name       = null;
	public ArrayList<SymTabEntry> m_symlist    = null; 
	public int                    m_size       = 0;
	public int                    m_tablelevel = 0;
	public SymTab                 m_uppertable = null;

	public SymTab()
	{
		
	}
	
	public SymTab(int p_level, SymTab p_uppertable){
		m_tablelevel = p_level;
		m_name = null;
		m_symlist = new ArrayList<SymTabEntry>();
		m_uppertable = p_uppertable;
	}
	
	public SymTab(int p_level, String p_name, SymTab p_uppertable){
		m_tablelevel = p_level;
		m_name = p_name;
		m_symlist = new ArrayList<SymTabEntry>();
		m_uppertable = p_uppertable;
	}
	
	public void addEntry(SymTabEntry p_entry){
		m_symlist.add(p_entry);	
	}
	
	public SymTabEntry lookupName(String p_tolookup) {
		SymTabEntry returnvalue = new SymTabEntry();
		boolean found = false;
		//System.out.println("to find:"+p_tolookup);
		//System.out.println();
		for( SymTabEntry rec : m_symlist) {
			 System.out.println(rec.m_name);
			if (rec.m_name.equals(p_tolookup)) {
				returnvalue = rec;
				//System.out.println("debugger1:"+rec.m_type);
				found = true;
			}
		}
		if (!found) {
			if (m_uppertable != null) {
				returnvalue = m_uppertable.lookupName(p_tolookup);
				//System.out.println("debugger1:"+returnvalue.m_type);
			}
		}
		//System.out.println(found);
		return returnvalue;
	}
	
	
	
	public SymTabEntry lookupName_ClassName(String p_tolookup) {
		SymTabEntry returnvalue = new SymTabEntry();
		boolean found = false;
		for( SymTabEntry rec : m_symlist) {
			//System.out.println(rec.m_name);
			if (rec.m_name.split(":")[0].equals(p_tolookup)) {
				returnvalue = rec;
				//System.out.println("debugger1:"+rec.m_type);
				found = true;
			}
		}
		if (!found) {
			if (m_uppertable != null) {
				returnvalue = m_uppertable.lookupName(p_tolookup);	
			}
		}
		return returnvalue;
	}
	

	
	//added by me--> lookup functiondecl for a function call
	public String lookupFunctionCall(String p_tolookup)
	{
		String found = "";
	    System.out.println("checking for:  "+p_tolookup);
		for( SymTabEntry rec : m_symlist)
		{
			if (rec.m_name!=null && rec.m_name.split(":")[0].equals(p_tolookup.split(" ")[0]) && (rec.m_kind.equals("func")))//Name of the function is matched
			{
			 //  System.out.println("here"+rec.m_name);
			   String look_param[]=p_tolookup.split(" ")[1].split("/,");
			   String rec_param[]=rec.m_name.split(":")[1].split("/,");
			   List<String>result=new ArrayList<String>();
			   //System.out.println(look_param.length==rec_param.length);
			   for (int i =0;i<look_param.length;i++)
				   System.out.println(look_param[i]);
			   for (int i=0;i<rec_param.length;i++)
				   System.out.println(rec_param[i]);
			  // System.out.println(look_param.length==rec_param.length);
			   if (look_param.length==rec_param.length) //# of paramter matched
			   {				   
				   for (int i=0;i<look_param.length;i++)
				   {
					  //System.out.println("here for:"+look_param[i].split(":")[1]+look_param[i].split(":").length);
					  	
					
					   if (look_param[i].split(":")[0].equals(rec_param[i].split(" ")[0])
						   &&look_param[i].split(":")[2].split(",").length==(rec_param[i].split(" ")[2].split(",").length))
					   {
						   
						   result.add("yes");
					   }
					   else
					   {
						   result.add("no");
					   }
						  			   
				   }
				  
				   if(!result.contains("no"))
					   found=rec.m_type;
			   }
			}
		}
		
		return found;
	}
	//added by me--> lookup_Vardecl
	public Boolean lookup_Variable(String p_tolookup) {
		
		boolean found = false;
		//System.out.println("Trying to find any matchn:"+p_tolookup);
		for( SymTabEntry rec : m_symlist) {
			
			//System.out.println("from: "+rec.m_name);
			if (rec.m_name!=null && rec.m_name.split(":")[0].equals(p_tolookup.split(":")[0]) && (rec.m_kind.equals("var") || rec.m_kind.equals("fParam"))) {
				found = true;
			}
		}
			
		return found;
	}
	
	//added by me --->lookup_VarDecl_Type
	public String lookup_VarDecl_Type(String p_lookup,String func_name)
	{ 
		 String found="";
		 //System.out.println(p_lookup+" a member of function "+func_name);
		 // Checking if the variable is a class member -- defined in the class)//		 else 
			 //-------------------------------------- 1. WITHIN THE FUNCTION CHECK ------------------------------------------------>
		 for( SymTabEntry rec : m_symlist)
		 {
			 //System.out.println(rec.m_name);
			 if (rec.m_name!=null && rec.m_name.split(":")[0].equals(p_lookup.split(":")[0])) //Match the name
			 {
				 
				  if (rec.m_kind.equals("fParam"))
				  {
					  String[] lookup_param=p_lookup.split(":")[1].split(",");
					  String [] rec_param=rec.my_dims.split(",");
					  //System.out.println("lookup_param "+lookup_param[0]+lookup_param.length);
					  //System.out.println("rec_param "+rec_param[0]+rec_param.length);
					  if (lookup_param.length==rec_param.length)
					  {
						  if (lookup_param.length==1&&lookup_param[0].equals("[]") && !(rec_param[0].equals("[]")))
							  found="";
						  else
						  {
							  found=rec.m_type;
						  }
					  }
					  	
											  
				  }
				  else if (rec.m_kind.equals("for"))
				  {
					String variable_name=p_lookup.split(":")[0];
					//System.out.println("fo1:"+variable_name);
					//System.out.println("fo2:"+rec.m_name);
					
					if (variable_name.equals(rec.m_name))
						found=rec.m_type;
				  }
				  else //for var - both with and without indices
				  {
					  String[] lookup_param=p_lookup.split(":")[1].split(",");
					 
					  String [] rec_param=rec.m_name.split(":")[1].split(",");
					// System.out.println("elen:"+lookup_param.length);
					  if (lookup_param.length==rec_param.length)
					  {
						  if (lookup_param.length==1&&lookup_param[0].equals("[]") && !(rec_param[0].equals("[]")))
							  found="";
						  else
							  found=rec.m_type;
					  }
				  }
			 }
			 
		 }
		 //-------------------------------------------2. WITHIN THE CLASS------------------------------------------------------------->
		
		 if (found=="" && func_name.contains("::"))
		 {
			 String class_name= func_name.split("::")[0]; 
			 for( SymTabEntry rec : AST_T.Prog.m_symtab.m_symlist)
			 {
				 if (rec.m_name!=null && rec.m_name.equals(class_name)) // if the class exist...
				 {
					 
					 for( SymTabEntry rec1 : rec.m_subtable.m_symlist) // find the variable in the class
					 {
						if(rec1.m_kind.equals("var") && rec1.m_name!=null 
							&& rec1.m_name.split(":")[0].equals(p_lookup.split(":")[0]))
						 {
							 String[] lookup_param=p_lookup.split(":")[1].split(",");
							 String [] rec1_param=rec1.m_name.split(":")[1].split(",");
							 //System.out.println(lookup_param.length==rec1_param.length);
							  if (lookup_param.length==rec1_param.length)
							  {
								  if (lookup_param.length==1&&lookup_param[0].equals("[]") && !(rec1_param[0].equals("[]")))
									  found="";
								  else
									  found=rec1.m_type;
							  }
						 }
					 }
				 }
				  
					 
			 }
		 }
		 // --------------------------------------------3. WITHIN THE CLASS - checking in the inherited class -- if any!------------------------------------------------------->
		 //
		 if (found=="" && func_name.contains("::"))
		 {
			 String class_name= func_name.split("::")[0];
			 for( SymTabEntry rec : AST_T.Prog.m_symtab.m_symlist)
			 {
				 if (rec.m_name!=null && rec.m_name.equals(class_name))
				 {
					 for( SymTabEntry rec1 : rec.m_subtable.m_symlist)
					 {
						 if(rec1.m_kind.equals("inheritlist") && rec1.m_inherlist!=null)
						 {
							 HashMap<String,SymTab> tempHashT=rec1.m_inherlist;
							 Set<String> i=tempHashT.keySet(); // Iterating over the different inherited class
							 List<String> found_type = new ArrayList<String>();
							 for (String itr:i)
							 {
								 for( SymTabEntry rec2 : (tempHashT.get(itr)).m_symlist)
								 {
									if (rec2.m_kind.equals("var")&& rec2.m_name!=null 
							        && rec2.m_name.split(":")[0].equals(p_lookup.split(":")[0]))
									{
										 String[] lookup_param=p_lookup.split(":")[1].split(",");
										 String [] rec2_param=rec2.m_name.split(":")[1].split(",");
										  //System.out.println(lookup_param.length==rec2_param.length);
										  if (lookup_param.length==rec2_param.length)
										  {
											  if (lookup_param.length==1&&lookup_param[0].equals("[]") && !(rec2_param[0].equals("[]")))
												  found="";
											  else
											  {
												  found_type.add(rec2.m_type);
											  }
										  }
										
									}
								 }
								 
							 }
							// System.out.println(found_type);
							 if (found_type.size()==1)
							 	 found=found_type.get(0);
							 								 
							 else if (found_type.size()>1)
							     found="Error:ambiguity:"+class_name+":"+tempHashT.keySet(); // more than once defined - over the multiple inherited class
								
							
						 }
					 }
				 }
			 }
		 }		 
	
		
	    //System.out.println("Result:" + found);
		return found;
		
	}
	
       //added by me --->lookup_funcDecl 
      public Boolean lookup_funcDecl(String p_tolookup) {
	   	
    	  boolean found = false;
    	  String fname=p_tolookup.split(":")[0];
    	  //System.out.println(fname);
    	  String[] fparam=p_tolookup.split(":")[1].split("/,");
    	  //System.out.println(fparam[0]);
    	 int count=0;
    	  
    	if (fparam.length>0)
        {  
   		     for( SymTabEntry rec : m_symlist) {
   			
			     if (rec.m_name!=null && rec.m_kind.equals("func")) {
			 	    String symb_func_name=rec.m_name.split(":")[0];
				    String[] symb_func_param=rec.m_name.split(":")[1].split("/,");
		        
				if (symb_func_name.equals(fname) && fparam.length==symb_func_param.length)
				{
					for (int i=0;i<fparam.length;i++)
					{
						if (fparam[i].split(" ")[0].equals(symb_func_param[i].split(" ")[0])
							&& fparam[i].split(" ")[2].equals(symb_func_param[i].split(" ")[2]))
						{		
							count++;
						}
					}
				}
			}
		} //end of -- for loop
   		
   		     if (count==fparam.length && fparam.length!=0) //fparam.length!=0 - ensure we dont compare for the funcdecl with fparam.length=0
   			   found=true;
   		     else
   			    found=false;
        }      
    	else if (fparam.length==0) // for the funcdecl with fparam =0
    	{
    		for( SymTabEntry rec : m_symlist) {
    			
    			 if (rec.m_name!=null && rec.m_kind.equals("func"))
    			 {
    				 String symb_func_name=rec.m_name.split(":")[0];
    				 String[] symb_func_param=rec.m_name.split(":")[1].split("/,");
    				 if (symb_func_name.equals(fname) && fparam.length==symb_func_param.length && fparam.length==0)
    					 found=true;
    			 }    			
    			
    		}//end of - for loop
    	} // end of the else - where fparam =0
   		//System.out.println(found);
		return found;
	}
	
      public SymTabEntry lookup_fcall_funcDef(String p_tolookup)
      {
    	  SymTabEntry returnvalue = new SymTabEntry();
		 //System.out.println("findin:"+p_tolookup);
		 String fname=p_tolookup.split(":")[0];
		 String[] fparam=p_tolookup.split(":")[1].split("/,");
		 
		 if (fparam.length>0)
		 {
			 for( SymTabEntry rec : m_symlist)
			 {
				 
				 if (rec.m_name!=null 
				    && rec.m_kind.equals("func") 
				    && rec.m_name.contains("::") 
				    && rec.m_name.contains(fname)
				    && rec.m_name.split("::")[1].split(":")[0].equals(fname))
				 {
					//System.out.println("foubf:"+rec.m_name);
					returnvalue=rec;
			     }
				 else if (rec.m_name!=null 
						 && rec.m_kind.equals("func") 
						 && !rec.m_name.contains("::") 
						 && rec.m_name.contains(fname)
						 && rec.m_name.split(":")[0].equals(fname))
				 {
					 System.out.println("fssada"+rec.m_name);
					 returnvalue=rec;
				 }
				 
			    }
			 }
		     	  
    	  
    	  return returnvalue;
    	  
      }
      
      
       //added by me ---> lookup_funcDef
      public Boolean lookup_funcDef(String p_tolookup)
      {
    	  Boolean found = false;
    	  if (!p_tolookup.contains("::"))
    		  found=lookup_funcDecl(p_tolookup);
    	  else if(p_tolookup.contains("::"))
    	  {
    		  String cname=p_tolookup.split("::")[0];
    		  String fname=p_tolookup.split("::")[1].split(":")[0];
    		  String[] fparam=p_tolookup.split("::")[1].split(":")[1].split("/,");
    		  //System.out.println(p_tolookup);
    		  //System.out.println(cname);
    		  //System.out.println(fname);
    		  int count=0;
    			if (fparam.length>0)
    	        {  
    	   		     for( SymTabEntry rec : m_symlist) {
    	   			
    				     if (rec.m_name!=null && rec.m_kind.equals("func")) {
    				    	String symb_func_class_name=rec.m_name.split("::")[0];
    				 	    String symb_func_name=rec.m_name.split("::")[1].split(":")[0];
    					    String[] symb_func_param=rec.m_name.split("::")[1].split(":")[1].split("/,");
    			        
    					if (symb_func_class_name.equals(cname) && symb_func_name.equals(fname) && fparam.length==symb_func_param.length)
    					{
    						for (int i=0;i<fparam.length;i++)
    						{
    							if (fparam[i].split(" ")[0].equals(symb_func_param[i].split(" ")[0])
    								&& fparam[i].split(" ")[2].equals(symb_func_param[i].split(" ")[2]))
    							{		
    								count++;
    							}
    						}
    					}
    				}
    			} //end of -- for loop
    	   		
    	   		     if (count==fparam.length && fparam.length!=0) //fparam.length!=0 - ensure we dont compare for the funcdecl with fparam.length=0
    	   			   found=true;
    	   		     else
    	   			    found=false;
    	        }//end of --if--fparam.length>0
    			
    			else if (fparam.length==0) // for the funcdecl with fparam =0
    	    	{
    	    		for( SymTabEntry rec : m_symlist) {
    	    			
    	    			 if (rec.m_name!=null && rec.m_kind.equals("func"))
    	    			 {
    	    				 String symb_func_class_name=rec.m_name.split("::")[0];
    	    				 String symb_func_name=rec.m_name.split("::")[1].split(":")[0];
    	    				 String[] symb_func_param=rec.m_name.split(":")[1].split("/,");
    	    				 if (symb_func_class_name.equals(cname) && symb_func_name.equals(fname) && fparam.length==symb_func_param.length && fparam.length==0)
    	    					 found=true;
    	    			 }    			
    	    			
    	    		}//end of - for loop
    	    	} // end of the else - where fparam =0
    			   		  
    	  }
    	 // System.out.println(found);
    	  return found;
      }
      
    //added by me --->lookup_fparam
      public Boolean lookup_fparam(Node p_node,String p_tolookup)
      {
    	  boolean found = false;
    	 // System.out.println(p_node.getParent().getNodeType());
    	 List<String> childList=new ArrayList<String>();
    	
    	  if (p_node.getParent().get_fparamnode_visited()==null 
    		  && (p_node.getParent().getNodeType().equals("funcDecl") || p_node.getParent().getNodeType().equals("funcDef") ))
    	  { 		
    		  p_node.getParent().set_fparamnode_visited("visited");
    		  found=false;
    		  
    	  }
    	  else if ((p_node.getParent().get_fparamnode_visited()!=null) 
    			  && (p_node.getParent().getNodeType().equals("funcDecl") || p_node.getParent().getNodeType().equals("funcDef") ))
    	  {
    		 for(Node child:p_node.getParent().getChildren())
    		 {
    			if(child.getNodeType().equals("fParamNode")) 
    			{
    				if (child.getData()!="")
    				childList.add(child.getData());
    				
    			}
    			 
    		 }   		
    	      List<String> fin=new ArrayList<String>();
    		  for (String str:childList)
    		  { fin.add(str.split(" ")[1]);
    		  }
    		 
    		 
    		  if (fin.contains(p_tolookup.split(" ")[1]))
    		  { found=true;}
    		  else 
    			  found=false;
    		 
    	  }
    	  
    	  //System.out.println(found);
    	  return found;
    	  
      }
	
    //added by me --->lookup_class
      public Boolean lookup_class(String p_tolookup)
      {
    	  boolean found = false;
    	  System.out.println(p_tolookup);
    	  for( SymTabEntry rec : m_symlist) {
    		
    		  if (rec.m_name!=null && rec.m_name.equals(p_tolookup) && rec.m_kind.equals("class"))
    		  {
    			  found =true;
    		  }   		  
    		  
    	  }
    	  
    	  return found;
    	  
      }
      
      
      public SymTab lookup_class_RetSymTab(String p_tolookup)
      {
    	  SymTab subTab = null;
    	  System.out.println(p_tolookup);
    	  for( SymTabEntry rec : m_symlist) {
    		
    		  if (rec.m_name!=null && rec.m_name.equals(p_tolookup) && rec.m_kind.equals("class"))
    		  {
    			  subTab=rec.m_subtable;
    			  
    		  }   		  
    		  
    	  }
    	  
       	  return subTab;
    	  
      }
      
      
      public SymTab lookupInheritedClass(String id_data) {
    	  SymTab returnvalue = null;
    	  boolean found = false;
    	  for( SymTabEntry rec : m_symlist) {
  			if (rec.m_name.equals(id_data)) {
  				returnvalue=new SymTab();
  				returnvalue = rec.m_subtable;
  				found = true;
  			}
  		}
  		if (!found) {
  			if (m_uppertable != null) {
  				returnvalue=new SymTab();
  				returnvalue = m_uppertable.lookupInheritedClass(id_data);	
  			}
  		}
  		return returnvalue;
  		
  	}
  
	public String toString(){
		String stringtoreturn = new String();
		String prelinespacing = new String();
		for (int i = 0; i < this.m_tablelevel; i++)
			prelinespacing += "|    "; 
		stringtoreturn += "\n" + prelinespacing + "=====================================================\n";
		stringtoreturn += prelinespacing + String.format("%-25s" , "| table: " + m_name) + String.format("%-27s" , " scope offset: " + m_size) + "|\n";
		stringtoreturn += prelinespacing        + "=====================================================\n";
		for (int i = 0; i < m_symlist.size(); i++){
			stringtoreturn +=  prelinespacing + m_symlist.get(i).toString() + '\n'; 
		}
		stringtoreturn += prelinespacing        + "=====================================================";
		return stringtoreturn;
	}

	
	
}
