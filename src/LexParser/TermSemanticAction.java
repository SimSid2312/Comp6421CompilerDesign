package LexParser;

public class TermSemanticAction {

	public static AST_T ast = new AST_T();// making the single object for ast class
	public void terminal_action(String a)
	{
		//------------> Logic to make node for the terminal---------------------------------------
		  String node_type;
		  String node_value;
		  String node_line;
		 
		  if (a.split(" ")[0].equals("id")) 
        {
			 node_type=a.split(" ")[0];
			 node_value=a.split(" ")[1];
			 node_line =a.split(" ")[2];
			 
			 ast.semantic_Action("SEMANTIC_MAKE_ID_NODE"+" "+node_value+" "+node_line);
        }
		  else if (a.split(" ")[0].equals("int") || (a.split(" ")[0].equals("float") && a.split(" ")[1].equals("float"))) // This is for float and int type node 
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			  ast.semantic_Action("SEMANTIC_MAKE_TYPE_NODE"+" "+node_value+" "+node_line);
		  }
		  else if (a.split(" ")[0].equals("integer"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
		
			  ast.semantic_Action("SEMANTIC_MAKE_INTEGER_NUM_NODE"+" "+node_value+" "+node_line);
		  }
		   else if (a.split(" ")[0].equals("sr"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			
			  ast.semantic_Action("SEMANTIC_ADD_SCOPE_OPERATOR"+" "+node_value+" "+node_line);
		  }
		  else if (a.split(" ")[0].equals("="))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
	
			  ast.semantic_Action("SEMANTIC_MAKE_ASSIGNOP"+" "+node_value+" "+node_line);
		  }
		  else if (a.split(" ")[0].equals("*")|| a.split(" ")[0].equals("/") || a.split(" ")[0].equals("and"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			  
			  ast.semantic_Action("SEMANTIC_MAKE_MULTOP"+" "+node_value+" "+node_line);
		  }
		  else if (a.split(" ")[0].equals("+") || a.split(" ")[0].equals("-") || a.split(" ")[0].equals("or"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			 
		
			  ast.semantic_Action("SEMANTIC_MAKE_ADDOP"+" "+node_value+" "+node_line);
		  }
		  else if (a.split(" ")[0].equals("float") && !a.split(" ")[1].equals("float"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			  
			  ast.semantic_Action("SEMANTIC_MAKE_FLOAT_NUM_NODE"+" "+node_value+" "+node_line);
		  }
		 
		 else if (a.split(" ")[0].equals("lt")
				  ||a.split(" ")[0].equals("eq") 
				  ||a.split(" ")[0].equals("neq") 
				  || a.split(" ")[0].equals("gt")
				  ||a.split(" ")[0].equals("leq")
				  ||a.split(" ")[0].equals("geq"))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			  
			  ast.semantic_Action("SEMANTIC_REL_OPERATOR"+" "+node_value+" "+node_line);
		  }
		    else if(a.split(" ")[0].equals("."))
		  {
			  node_type=a.split(" ")[0];
			  node_value=a.split(" ")[1];
			  node_line =a.split(" ")[2];
			  ast.semantic_Action("SEMANTIC_DOT_OPERATOR"+" "+node_value+" "+node_line);
		  }
		 
	}
}
