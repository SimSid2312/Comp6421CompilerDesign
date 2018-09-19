package LexParser;
import AST.*;
import Visitor.SymbolTable.SymTabCreationVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AST_T {

	public static Stack Sem_Stack=new Stack();
	public static Node Prog;
	
	public void semantic_Action(String action)
	{
		String take_action=action.split(" ")[0];
		
		if (take_action.equals("SEMANTIC_MAKE_ID_NODE"))
		{
			Node node= new IdNode(action.split(" ")[1]);//this will input the Utilityi.e value of id
			node.setNodeType("id");
			node.setLine(action.split(" ")[2]);
			Sem_Stack.push(node);
			//node.print();
		}
		else if (take_action.equals("SEMANTIC_ADD_SCOPE_OPERATOR"))
		{
			Node node= new FunScope(action.split(" ")[1]);//this will input the Utilityi.e value of id
			node.setNodeType("FunScope");
			node.setLine(action.split(" ")[2]);
			Sem_Stack.push(node);
			//node.print(); 
		}
		
		else if (take_action.equals("SEMANTIC_MAKE_INHER_SIBLING"))
		{
			List<Node> nodelist=new ArrayList<Node>();			
			//System.out.println("Rule:SEMANTIC_MAKE_INHER_SIBLING");
			while(Sem_Stack.size()>1 && ((Node)Sem_Stack.peek()).getclass_name()==null)
			{
				//System.out.println(((Node)Sem_Stack.peek()).getclass_name());
				nodelist.add((Node) Sem_Stack.pop());
			}
		   Node inherList=new InherListNode(nodelist);
		   inherList.setNodeType("inherListNode");
		   Sem_Stack.push(inherList);
		   //inherList.print();
					
		}
		else if (take_action.equals("SEMANTIC_MAKE_TYPE_NODE"))
		{
			//System.out.println(action.split(" ")[1]);
			Node node= new TypeNode(action.split(" ")[1]);
			node.setNodeType(action.split(" ")[1]);
			node.setLine(action.split(" ")[2]);
			Sem_Stack.push(node);
			//node.print();
		}
		else if (take_action.equals("SEMANTIC_MAKE_DIM_LIST"))
		{
			List<Node> nodelist=new ArrayList<Node>();
			//System.out.println("Rule:SEMANTIC_MAKE_DIM_LIST");
			//System.out.println(((Node)Sem_Stack.peek()).getNodeType());
			while(((Node)Sem_Stack.peek()).getNodeType().equals("integer"))
			{
				nodelist.add((Node) Sem_Stack.pop());
			}
			Node dimList=new DimListNode(nodelist);
			dimList.setNodeType("dimListNode");
			Sem_Stack.push(dimList);
			//dimList.print();
		}
		else if (take_action.equals("SEMANTIC_MAKE_INTEGER_NUM_NODE"))
		{
			Node node=new NumNode(action.split(" ")[1],"integer");
			node.setNodeType("integer");
			node.setLine(action.split(" ")[2]);
			Sem_Stack.push(node);
			//node.print();
		}
		
		else if (take_action.equals("SEMANTIC_MAKE_FLOAT_NUM_NODE"))
		{
			Node node=new NumNode(action.split(" ")[1],"float");
			node.setNodeType("float");
			node.setLine(action.split(" ")[2]);
			Sem_Stack.push(node);
			//node.print();
		}
		else if (take_action.equals("SEMANTIC_MARK_CLASS_NAME"))
		{
			if (((Node)Sem_Stack.peek()).getNodeType().equals("id"))
			{
				((Node)Sem_Stack.peek()).setclass_name();
				//System.out.println(((Node)Sem_Stack.peek()).getData());
				//System.out.println(((Node)Sem_Stack.peek()).getclass_name());
			}
		}
		else if (take_action.equals("SEMANTIC_MAKE_VAR_DECL"))
		{
			List<Node> nodelist=new ArrayList<Node>();
			//System.out.println("SEMANTIC_MAKE_VAR_DECL");
			int count=0;
			while (count<3)
			{
				nodelist.add((Node) Sem_Stack.pop());
				count++;
			}
			Node varDecl= new VarDeclNode(nodelist.get(2), nodelist.get(1), nodelist.get(0));
			varDecl.setNodeType("varDecl");
			Sem_Stack.push(varDecl);
			//varDecl.print();
		}
		else if (take_action.equals("SEMANTIC_MAKE_FPARAM_NODE"))
		{
			List<Node> nodelist=new ArrayList<Node>();
			int count=0;
			//System.out.println("SEMANTIC_MAKE_FPARAM_NODE");
			//System.out.println("here--"+((Node)Sem_Stack.peek()).getNodeType());
		    if ( ((Node)Sem_Stack.peek()).getNodeType().equals("dimListNode") )
		    {  
		    	while(count<3)
		    	{
		    		nodelist.add((Node) Sem_Stack.pop());
		    		count++;
		    	}
		    	
		    	Node fParamsNode=new fParamNode(nodelist.get(2), nodelist.get(1), nodelist.get(0));
		    	fParamsNode.setNodeType("fParamNode");
		    	Sem_Stack.push(fParamsNode);
		    	//fParamsNode.print();
		    }
		    else 
		    {
		    	count=2;
		    	while (count<2)
				{
					nodelist.add((Node) Sem_Stack.pop());
					count++;
				}
		    	
		    	Node fParamsNode=new fParamNode(nodelist.get(2), nodelist.get(1), nodelist.get(0));
		    	fParamsNode.setNodeType("fParamNode");
		    	Sem_Stack.push(fParamsNode);
		    	//fParamsNode.print();
		    }
		}
		else if (take_action.equals("SEMANTIC_MAKE_FUNCDECL"))
		{//varDecl
			List<Node> nodelist=new ArrayList<Node>();
			List<Node> fParamnodelist=new ArrayList<Node>();
			//System.out.println("SEMANTIC_MAKE_FUNCDECL");
		    while ( !((Node)Sem_Stack.peek()).getNodeType().equals("varDecl") && !((Node)Sem_Stack.peek()).getNodeType().equals("ClassDeclNode") && !((Node)Sem_Stack.peek()).getNodeType().equals("funcDecl") )
		    {
		    	//System.out.println(((Node)Sem_Stack.peek()).getNodeType());
		    	 if (((Node)Sem_Stack.peek()).getNodeType().equals("id") || ((Node)Sem_Stack.peek()).getNodeType().equals("int"))
		    	{
		    		 nodelist.add((Node) Sem_Stack.pop());
		    		
		    	}
		    	else if (((Node)Sem_Stack.peek()).getNodeType().equals("fParamNode"))
			    {
		    		fParamnodelist.add((Node) Sem_Stack.pop());
			    }
		    }
		    
		    if (nodelist.size()>0 || fParamnodelist.size()>0)
		    {
		    	//System.out.println(nodelist);
		        //System.out.println(fParamnodelist);
		        Node funcDecl = new funcDeclNode(nodelist.get(1),nodelist.get(0),fParamnodelist);
		        funcDecl.setNodeType("funcDecl");
		        Sem_Stack.push(funcDecl);
		        //funcDecl.print();
		        /*System.out.println("Semantic Stack is:");
			    for (int i=0;i<AST_T.Sem_Stack.size();i++)
			   {
				 System.out.print(AST_T.Sem_Stack.get(i));
				 //AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
				 System.out.print("|");
				 //AST_Buffer.write(",");
			   }*/
		     }
		}
		
		else if (take_action.equals("SEMANTIC_MAKE_MAKE_MEMBER_LIST"))
		{
			//System.out.println("SEMANTIC_MAKE_MAKE_MEMBER_LIST");
			List<Node> nodelist=new ArrayList<Node>();
			int count=0;
			
			while (!((Node)Sem_Stack.peek()).getNodeType().equals("inherListNode") && Sem_Stack.size()>1)
			{
				
				nodelist.add((Node) Sem_Stack.pop());
			}
			
			Node memberList=new MemberListNode(nodelist);
			memberList.setNodeType("memberList");
	    	Sem_Stack.push(memberList);
	    	//memberList.print();
			
		}
		
		else if (take_action.equals("SEMANTIC_MAKE_CLASS_DECL"))
		{
			//System.out.println("SEMANIC_MAKE_CLASS_DECL");
			List<Node> nodelist=new ArrayList<Node>();
			//System.out.println(((Node)Sem_Stack.peek()).getNodeType());
			
				if (((Node)Sem_Stack.peek()).getNodeType().equals("memberList")) 
			   {
				 nodelist.add((Node) Sem_Stack.pop());
               }

               if (((Node)Sem_Stack.peek()).getNodeType().equals("inherListNode")) 
              {
            	  nodelist.add((Node) Sem_Stack.pop());
              }
             
		     if (((Node)Sem_Stack.peek()).getNodeType().equals("id"))
			 {nodelist.add((Node) Sem_Stack.pop());}
		     
             Node ClassDeclNode=new ClassNode(nodelist);
             ClassDeclNode.setNodeType("ClassDeclNode");
             Sem_Stack.push(ClassDeclNode);
             //ClassDeclNode.print();
		
	     }
		  else if (take_action.equals("SEMANTIC_MAKE_CLASS_LIST"))
		 {
			//System.out.println("SEMANTIC_MAKE_CLASS_LIST");
			List<Node> nodelist=new ArrayList<Node>();
			//System.out.println(((Node)Sem_Stack.peek()).getNodeType());
			if  ( Sem_Stack.size()>=1 && ((Node)Sem_Stack.peek()).getNodeType().equals("ClassDeclNode") )
			{
				while (( Sem_Stack.size()>=1 && ((Node)Sem_Stack.peek()).getNodeType().equals("ClassDeclNode") ))
				{
					nodelist.add((Node) Sem_Stack.pop());
				}
				Node ClassList=new ClassListNode(nodelist);
				ClassList.setNodeType("ClassListNode");
				Sem_Stack.push(ClassList);
				//ClassList.print();
			}
			else if (Sem_Stack.size() < 1) // empty stack - still making empty class list node
			{
				Node ClassList=new ClassListNode();
				ClassList.setNodeType("ClassListNode");
				Sem_Stack.push(ClassList);
				//ClassList.print();
				
			}
			
			
		}
		  else if (take_action.equals("SEMANTIC_MAKE_FUNCDEF_LIST"))
		  {
			  //System.out.println("SEMANTIC_MAKE_FUNCDEF_LIST");
				List<Node> nodelist=new ArrayList<Node>();
				if (!((Node)Sem_Stack.peek()).getNodeType().equals("funcDef")) //No funcDef in the program
				{
					Node FuncDefList=new FuncDefListNode();
					FuncDefList.setNodeType("FunDefListNode");
					Sem_Stack.push(FuncDefList);
					//FuncDefList.print();
					
				}
				else 
				{
					while ( Sem_Stack.size()>1 && ((Node)Sem_Stack.peek()).getNodeType().equals("funcDef") )
				   {
					  nodelist.add((Node) Sem_Stack.pop());
				   }
									
				  Node FuncDefList=new FuncDefListNode(nodelist);
				  FuncDefList.setNodeType("FunDefListNode");
				  Sem_Stack.push(FuncDefList);
				  //FuncDefList.print();
				}
		  }
		
		  else if (take_action.equals("SEMANTIC_MARK_SCOPE_SPEC"))
		  {
			 // System.out.println("SEMANTIC_MARK_SCOPE_SPEC");
			  ((Node)Sem_Stack.peek()).set_scope_spec(((Node)Sem_Stack.peek()).getData());
		  }
		
		
		   else if (take_action.equals("SEMANTIC_MAKE_PROG"))
		  {
			   
			  /* System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}
				//AST_Buffer.newLine();
				System.out.println();*/
			   
			 Node stateBlock = null,funcdeflist = null,classlist = null;   
			//System.out.println("SEMANTIC_MAKE_PROG");
			if(((Node) Sem_Stack.peek()).getNodeType().equals("func_def_stateBlock"))
			{ 
				//System.out.println( ((Node)Sem_Stack.peek()).getNodeType() ) ;
				stateBlock=(Node) Sem_Stack.pop(); 
		    }
			
			if (((Node) Sem_Stack.peek()).getNodeType().equals("FunDefListNode"))
			{
				//System.out.println( ((Node)Sem_Stack.peek()).getNodeType() ) ;
				 funcdeflist=(Node) Sem_Stack.pop();
			}
            
            if (((Node) Sem_Stack.peek()).getNodeType().equals("ClassListNode"))
            {
            	//System.out.println( ((Node)Sem_Stack.peek()).getNodeType() ) ;
            	classlist=(Node) Sem_Stack.pop();
            }
             	 
            
			
			
			Prog=new ProgNode(classlist,funcdeflist,stateBlock);
			Prog.setNodeType("Prog");
			Sem_Stack.push(Prog);
			//Prog.print();
			       
		  } 
		   else if (take_action.equals("SEMANTIC_MAKE_ASSIGNOP"))
		  {
			  //System.out.println("SEMANTIC_MAKE_ASSIGNOP");
			 // System.out.println(action);
			  Node AssingOPnode= new AssignOPNode(action.split(" ")[1]);//this will input the Utilityi.e value of id
			  AssingOPnode.setNodeType("AssignOP");
			  AssingOPnode.setLine(action.split(" ")[2]);
			  Sem_Stack.push(AssingOPnode);
			  //AssingOPnode.print(); 
		  }
		   else if (take_action.equals("SEMANTIC_MAKE_MULTOP"))
			  {
				 // System.out.println("SEMANTIC_MAKE_MULTOP");
				  //System.out.println(action);
				  Node MultOPnode= new MultiplicationOP(action.split(" ")[1]);//this will input the Utilityi.e value of id
				  MultOPnode.setNodeType("MultOP");
				  MultOPnode.setLine(action.split(" ")[2]);
				  Sem_Stack.push(MultOPnode);
				  //MultOPnode.print();
			  }
		   else if (take_action.equals("SEMANTIC_MAKE_ADDOP"))
			  {
				 // System.out.println("SEMANTIC_MAKE_ADDOP");
				  //System.out.println(action);
				  Node AddOPnode= new AdditionOP(action.split(" ")[1]);//this will input the Utilityi.e value of id
				  AddOPnode.setNodeType("AddOP");
				  AddOPnode.setLine(action.split(" ")[2]);
				  //System.out.println(AddOPnode.getLine());
				  Sem_Stack.push(AddOPnode);
				  Node n=(Node) Sem_Stack.peek(); 
			  }
		     else if (take_action.equals("SEMANTIC_MAKE_ASSIGN_STATEMENT"))
		    {
		    	 //System.out.println();
		    	 //System.out.println("SEMANTIC_MAKE_ASSIGN_STATEMENT:Semantic Stack is:");
		 		/*for (int i=0;i<AST_T.Sem_Stack.size();i++)
		 		{
		 			System.out.print(AST_T.Sem_Stack.get(i));
		 			//AST_Buffer.write(AST_T.Sem_Stack.get(i).toString()+"------>"+((Node)AST_T.Sem_Stack.get(i)).getChildren().toString());
		 			//AST_Buffer.newLine();
		 			System.out.print("|");
		 			//AST_Buffer.write("|");
		 		}*/
		    	  // System.out.println("SEMANTIC_MAKE_ASSIGN_STATEMENT"); 
				   Node right_child=(Node) Sem_Stack.pop();
				  // System.out.println("debgger1:"+right_child.getNodeType()+right_child.getData());
				   Node assign_op=(Node) Sem_Stack.pop();
				 // System.out.println("debgger2:"+assign_op.getData());
				   Node left_child=(Node) Sem_Stack.pop();
				 //  System.out.println("debgger3:"+left_child.getNodeType()+right_child.getData());
				   Node AssignOPNode= new AssignStatNode(assign_op.getData(),left_child,right_child);
				   AssignOPNode.setNodeType("AssignStateNode");
				   Sem_Stack.push(AssignOPNode);
				   //AssignOPNode.print();	
		   }
		    
		   else if (take_action.equals("SEMANTIC_MAKE_ADD_CONSTRUCT"))
		   {
			   //System.out.println("SEMANTIC_MAKE_ADD_CONSTRUCT"); 
			   Node right_child=(Node) Sem_Stack.pop();
			   Node add_op=(Node) Sem_Stack.pop();
			   Node left_child=(Node) Sem_Stack.pop();
			   Node AddOPNode= new AddOpNode(add_op.getData(),left_child,right_child);
			   AddOPNode.setNodeType("AddOPNode");
			   AddOPNode.setLine(add_op.getLine());
			   AddOPNode.set_OP(add_op.getData());
			   Sem_Stack.push(AddOPNode);
			   //AddOPNode.print();			  
		   }
				   
		  else if (take_action.equals("SEMANTIC_MAKE_MULT_CONSTRUCT")) //making division and multiplication statment
		  {
			//  System.out.println("SEMANTIC_MAKE_MULT_CONSTRUCT");
			  Node right_child=(Node) Sem_Stack.pop();
			  if (((Node)Sem_Stack.peek()).getNodeType().equals("MultOP"))
			  {
				  Node mult_op=(Node) Sem_Stack.pop();
			      Node left_child=(Node) Sem_Stack.pop();
			      Node MultOPNode= new MultOpNode(mult_op.getData(),left_child,right_child);
			      MultOPNode.setNodeType("DivOPNode");
			      MultOPNode.setLine(mult_op.getLine());
			      MultOPNode.set_OP(mult_op.getData());
			      Sem_Stack.push(MultOPNode);
			      //MultOPNode.print();
				  
			  }
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_INDICE_LIST"))
		  {
			//  System.out.println("SEMANTIC_MAKE_INDICE_LIST");
			  List<Node> nodelist=new ArrayList<Node>();
			  if (((Node)Sem_Stack.peek()).getNodeType().equals("indiceNode"))
			  {
					while (((Node)Sem_Stack.peek()).getNodeType().equals("indiceNode"))
				   {				
						//System.out.println(((Node)Sem_Stack.peek()).getData()+((Node)Sem_Stack.peek()).getNodeType());
					    nodelist.add((Node) Sem_Stack.pop());
				   }
				   Node indiceListNode = new IndiceListNode(nodelist);
				   indiceListNode.setNodeType("indexListNode");
				   Sem_Stack.push(indiceListNode);
				   //indiceListNode.print();
			   }
				else if (!((Node)Sem_Stack.peek()).getNodeType().equals("indiceNode"))
				{
					Node indiceListNode = new IndiceListNode();
					indiceListNode.setNodeType("indexListNode");
					Sem_Stack.push(indiceListNode);
					//indiceListNode.print();
				}
			  
		  }
		  
		   else if (take_action.equals("SEMANTIC_MAKE_APARAMS_LIST"))
		  {
			 // System.out.println("SEMANTIC_MAKE_APARAMS_LIST");
			  /*System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}
				//AST_Buffer.newLine();
				System.out.println();*/
				//AST_Buffer.flush();
				//Expr
				List<Node> nodelist=new ArrayList<Node>();
				if (((Node)Sem_Stack.peek()).getNodeType().equals("Expr"))
				{
					while (((Node)Sem_Stack.peek()).getNodeType().equals("Expr"))
				   {
					//System.out.println(((Node)Sem_Stack.peek()).getData()+((Node)Sem_Stack.peek()).getNodeType());
					nodelist.add((Node) Sem_Stack.pop());
				   }
				    Node paramListNode = new ParamListNode(nodelist);
				    paramListNode.setNodeType("ParamListNode");
				    Sem_Stack.push(paramListNode);
				    //paramListNode.print();
			   }
				else if (!((Node)Sem_Stack.peek()).getNodeType().equals("Expr"))
				{
					Node paramListNode = new ParamListNode();
				    paramListNode.setNodeType("ParamListNode");
				    Sem_Stack.push(paramListNode);
				    //paramListNode.print();
				}
				
				/* System.out.println("SEMANTIC_MAKE_APARAMS_LIST");
				  System.out.println("Semantic Stack is:");
					for (int i=0;i<AST_T.Sem_Stack.size();i++)
					{
						System.out.print(AST_T.Sem_Stack.get(i));
						//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
						System.out.print("|");
						//AST_Buffer.write(",");
					}*/
		  }
		
		   else if (take_action.equals("SEMANTIC_MAKE_FCALL"))
		   {
			  // System.out.println("SEMANTIC_MAKE_FCALL");
			   List<Node> nodelist=new ArrayList<Node>();
			   if (((Node)Sem_Stack.peek()).getNodeType().equals("ParamListNode"))
			   {
				   //while ()
				   nodelist.add( (Node)Sem_Stack.pop() ); // put - ParamListNode
				   nodelist.add( (Node)Sem_Stack.pop() );//put - id
			   }
			 Node fCall= new FCallNode(nodelist);
			 fCall.setNodeType("FcallNode");
			 Sem_Stack.push(fCall);
			 //fCall.print();
			 
			/* System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
			   
		   }
		 
		   else if (take_action.equals("SEMANTIC_MAKE_VAR"))
		   {	
			  // System.out.println();
			   
			  /* System.out.println("SEMANTIC_MAKE_VAR");
			   System.out.println("Before Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
			   
			   List<Node> nodelist=new ArrayList<Node>();
			   if (((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("DataMemberNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("DotOPNode")  )
			   {while (((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("DotOPNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("DataMemberNode"))
			   {
				   nodelist.add( (Node)Sem_Stack.pop());
			   }
			   
			   Node var= new VarNode(nodelist);
			   var.setNodeType("varNode");
			   Sem_Stack.push(var);
			  // var.print();	
			   }
			   
			  /* System.out.println("SEMANTIC_MAKE_VAR");
			   System.out.println("After Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
			  
		   }
		  
		  
		  
		  else if (take_action.equals("SEMANTIC_MAKE_ARITH_EXPR"))
		  {
			//  System.out.println("SEMANTIC_MAKE_ARITH_EXPR");
			  Node arithExprNode= new ArithExpr((Node) Sem_Stack.pop());
			  arithExprNode.setNodeType("ArithExpr");
			  Sem_Stack.push(arithExprNode);
			  //arithExprNode.print();
			    
		  }
		//SEMATIC_MAKE_TERM
		  else if (take_action.equals("SEMANTIC_MAKE_TERM"))
		  {
			//  System.out.println("SEMANTIC_MAKE_TERM");
			  Node termNode= new TermNode((Node) Sem_Stack.pop());
			  termNode.setNodeType("TermNode");
			  Sem_Stack.push(termNode);
			  //termNode.print();
		  }
		  
		  else if (take_action.equals("SEMANTIC_PUT_NOT_TOSEMSTACK"))
		  {
			 // System.out.println("SEMANTIC_PUT_NOT_TOSEMSTACK");
			  Node notnode= new notNode("not");
			  notnode.setNodeType("notNode");
			  Sem_Stack.push(notnode);
			  //notnode.print();
		  }
		
		  else if (take_action.equals("SEMANTIC_PUT_SIGN_TOSEMSTACK"))
		  {
			 // System.out.println("SEMANTIC_PUT_SIGN_TOSEMSTACK");
			  Node signnode= new signNode("sign");
			  signnode.setNodeType("signNode");
			  Sem_Stack.push(signnode);
			  //signnode.print();
		  }
		  
		  else if(take_action.equals("SEMANTIC_MAKE_FACTOR_VAR_FCALL")){
			 
			  		  
			//  System.out.println("SEMANTIC_MAKE_FACTOR_VAR_FCALL");
			  List<Node> nodelist=new ArrayList<Node>();
			  
			 if(((Node)Sem_Stack.peek()).getNodeType().equals("DataMemberNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode"))
			  {  while (((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("DataMemberNode"))
				   {
					   nodelist.add( (Node)Sem_Stack.pop());
				   }
				   Node var= new VarNode(nodelist);
				   var.setNodeType("varNode");
				   Sem_Stack.push(var);
				  // var.print();	    
			  }
			  
			/*  System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_FACTOR_NODE"))
		  {
			  List<Node> nodelist=new ArrayList<Node>();
			  
			 // System.out.println("SEMANTIC_MAKE_FACTOR_NODE");
			  if ( ((Node) Sem_Stack.peek()).getNodeType().equals("FactorNumNode") 
				 //|| ((Node) Sem_Stack.peek()).getNodeType().equals("varNode")
				// || ((Node) Sem_Stack.peek()).getNodeType().equals("FcallNode")
				 || ((Node) Sem_Stack.peek()).getNodeType().equals("factorNotNode") 
				 || ((Node) Sem_Stack.peek()).getNodeType().equals("factorSignNode")
				 || ((Node) Sem_Stack.peek()).getNodeType().equals("factorarithExpr")) 
			  {
				  Node factorNode = new FactorNode((Node) Sem_Stack.pop());
				  factorNode.setNodeType("factorNode");
				  Sem_Stack.push(factorNode);
				  //factorNode.print();
			  }
			  
			  else if( ((Node) Sem_Stack.peek()).getNodeType().equals("varNode") 
					  || ((Node) Sem_Stack.peek()).getNodeType().equals("FcallNode") )
			  {
				  while (((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode") || ((Node)Sem_Stack.peek()).getNodeType().equals("varNode"))
				  {
					  nodelist.add( (Node)Sem_Stack.pop());
				  }
				  
				  Node factorNode = new FactorNode(nodelist);
				  
				  factorNode.setNodeType("varNode");
				   Sem_Stack.push(factorNode);
				   //factorNode.print();	
			  }
			  
			/*  System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
		  }
		  else if (take_action.equals("SEMANTIC_FACTOR_NUM_NODE"))
		  {
			//  System.out.println("This is rule for :SEMANTIC_FACTOR_NUM_NODE");
			  Node FactorNumNode= new FactorNumNode((Node) Sem_Stack.pop());//last input
			  FactorNumNode.setNodeType("FactorNumNode");
			  Sem_Stack.push(FactorNumNode);
			  //FactorNumNode.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_FACTOR_NOT"))
		  {
			 // System.out.println("This is rule for :SEMANTIC_MAKE_FACTOR_NOT");
			  List<Node> nodelist = new ArrayList<>();
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("factorNode"))
			  {
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			  if  (((Node) Sem_Stack.peek()).getNodeType().equals("notNode"))
			  {
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			  
			  Node factorNotNode= new FactorNotNode (nodelist);
			  factorNotNode.setNodeType("factorNotNode");
			  Sem_Stack.push(factorNotNode);
			  //factorNotNode.print();
			 
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_FACTOR_SIGN"))
		  {
			//  System.out.println("This is rule for :SEMANTIC_MAKE_FACTOR_SIGN");
			  List<Node> nodelist = new ArrayList<>();
			  //System.out.println(((Node) Sem_Stack.peek()).getNodeType());
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("factorNode"))
			  {
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			  //System.out.println(((Node) Sem_Stack.peek()).getNodeType());
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("AddOP"))
			  {
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			 // System.out.println(((Node) Sem_Stack.peek()).getNodeType());
			  if  (((Node) Sem_Stack.peek()).getNodeType().equals("signNode"))
			  {
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			  			  
			  Node factorSignNode= new FactorSignNode (nodelist);
			  factorSignNode.setNodeType("factorSignNode");
			  Sem_Stack.push(factorSignNode);
			  //factorSignNode.print();
			/*  System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
			 
		  }
		  else if(take_action.equals("SEMANTIC_MAKE_FACTOR_ARITH"))
		  {
			 // System.out.println("SEMANTIC_MAKE_FACTOR_ARITH");
			 // Node child = null;
			  List<Node> nodelist = new ArrayList<>();
			  if ( ((Node) Sem_Stack.peek()).getNodeType().equals("ArithExpr"))
			  {
				  while (((Node) Sem_Stack.peek()).getNodeType().equals("ArithExpr"))
				  {
					  nodelist.add((Node) Sem_Stack.pop());
				  }
			  }
			  Node factorarith= new FactorArithExprNode(nodelist);
			  factorarith.setNodeType("factorarithExpr");
			  Sem_Stack.push(factorarith);
			  //factorarith.print();
		  }
		
		
		  else if (take_action.equals("SEMANTIC_REL_OPERATOR"))
		  {
			//  System.out.println("SEMANTIC_REL_OPERATOR");
			 // System.out.println(action);
			  Node RelOPNode= new RelationOP(action.split(" ")[1]);//this will input the Utilityi.e value of id
			  RelOPNode.setNodeType("RelationOPeratorNode");
			  RelOPNode.setLine(action.split(" ")[2]);
			  Sem_Stack.push(RelOPNode);
			  //RelOPNode.print();
			  		  
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_EXPR"))
		  {
			 // System.out.println("SEMANTIC_MAKE_EXPR");
			  Node ExprNode= new Expr((Node) Sem_Stack.pop());
			  ExprNode.setNodeType("Expr");
			  Sem_Stack.push(ExprNode);
			  //ExprNode.print();
			  		  
		  }
		
		  else if (take_action.equals("SEMANTIC_MAKE_RELEXPR"))
		  {
			 // System.out.println("SEMANTIC_MAKE_RELEXPR");
			  
			  Node rightArithExpr = (Node)Sem_Stack.pop();
			  Node relOpNode = (Node)Sem_Stack.pop();
			  Node leftArithExpr = (Node)Sem_Stack.pop();
			  
			  Node RelExprNode= new RelExpr("");
			  RelExprNode.addChild(leftArithExpr);
			  RelExprNode.addChild(relOpNode);
			  RelExprNode.addChild(rightArithExpr);
			  RelExprNode.setNodeType("RelExpr");
			  RelExprNode.setLine(relOpNode.getLine());
			  RelExprNode.set_OP(relOpNode.getData());
			  Sem_Stack.push(RelExprNode);
			  //RelExprNode.print();
		  }
		  else if (take_action.equals("SEMANTIC_DOT_OPERATOR"))
		  {	 	
			  Node Dotnode= new DotOp(action.split(" ")[1]);
			  Dotnode.setData(".");
			  Dotnode.setNodeType("DotOP");
			  Dotnode.setLine(action.split(" ")[2]);
			  Dotnode.set_OP(action.split(" ")[1]);
			 // Dotnode.print();
			   Sem_Stack.push(Dotnode);
			  //DotOPNode.print();		  
			 		 
		  }
		
		  else if (take_action.equals("SEMANTIC_MAKE_DOT_CONSTRUCT"))
		   {
			
			  Node right_child = null;
			  Node dot_op = null;
			  Node left_child = null;
			  Node DOTOPNode=null;
			 
			  
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("DataMemberNode") ||  ((Node) Sem_Stack.peek()).getNodeType().equals("ArithExpr")
					  || ((Node)Sem_Stack.peek()).getNodeType().equals("FcallNode"))//|| ((Node) Sem_Stack.peek()).getNodeType().equals("VarNode") )
			  {
				    right_child=(Node) Sem_Stack.pop();
			      // System.out.println("here"+right_child.getChildren().get(0).getData());
			  }
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("DotOP"))
			   {
				  dot_op=(Node) Sem_Stack.pop();
				  
				 // System.out.println(dot_op);
			   
			     if (((Node) Sem_Stack.peek()).getNodeType().equals("DataMemberNode") )//|| ((Node) Sem_Stack.peek()).getNodeType().equals("VarNode") )
			     {
				  left_child=(Node) Sem_Stack.pop();
				  //System.out.println(left_child);
			     }
			   }
			  if (right_child!=null&&dot_op!=null&&left_child!=null)
			  {DOTOPNode= new DotOpNode(dot_op.getData(),left_child,right_child);
			    DOTOPNode.setNodeType("DotOPNode");
			    DOTOPNode.setLine(dot_op.getLine());
			  Sem_Stack.push(DOTOPNode);
			  //DOTOPNode.print();
			  }
			  else if (right_child!=null&&dot_op==null&&left_child==null)
			  {
				  Sem_Stack.push(right_child);
			  }
			  //System.out.println();
			  
		   }
		
		  else if (take_action.equals("SEMANTIC_MAKE_DATA_MEMBER"))
		  {
			 /* System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
			//  System.out.println("SEMANTIC_MAKE_DATA_MEMBER");
			  List <Node> nodelist = new ArrayList();
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("indexListNode"))
			  {
				  nodelist.add( (Node)Sem_Stack.pop() ); // put - indexListNode
				  //System.out.println(((Node) Sem_Stack.peek()).getData());
				  nodelist.add( (Node)Sem_Stack.pop() );//put - id
			  }
			  Node datamember= new DataMemberNode(nodelist);
			  datamember.setNodeType("DataMemberNode");
			  Sem_Stack.push(datamember);
			  //datamember.print();
			  
			/*  System.out.println("Semantic Stack is:");
				for (int i=0;i<AST_T.Sem_Stack.size();i++)
				{
					System.out.print(AST_T.Sem_Stack.get(i));
					//AST_Buffer.write(AST.Semantic_Stack.get(i).type.toString());
					System.out.print("|");
					//AST_Buffer.write(",");
				}*/
		  }
		 
		  else if (take_action.equals("SEMANTIC_MARK_BEGIN_BODY")) //Marker for the begin for the funcBody
		  {
			//  System.out.println("This is the begin of the function body, rule :SEMANTIC_MARK_BEGIN_BODY");
			  Node marker_begin_functionBody= new BeginFuncBodyNode("funcBodyBegin");
			  marker_begin_functionBody.setNodeType("funcBodyBeginNode");
			  Sem_Stack.push(marker_begin_functionBody);
			  //marker_begin_functionBody.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_STATEBLOCK")) // picks stuff from stack- till funcBodyBeginNode
		  {
			 // System.out.println("SEMANTIC_MAKE_STATEBLOCK");
			  List<Node> nodelist=new ArrayList<Node>();
			  while (!((Node) Sem_Stack.peek()).getNodeType().equals("funcBodyBeginNode"))
			  {
				 // System.out.println(((Node) Sem_Stack.peek()).getNodeType());
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			 // System.out.println("Top of the stack is :"+((Node) Sem_Stack.peek()).getNodeType());
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("funcBodyBeginNode"))
			  Sem_Stack.pop();// removing the node - marked as the begin of function body
			  Node func_def_stateBlock= new FuncDefStatBlock(nodelist);
			  func_def_stateBlock.setNodeType("func_def_stateBlock");
			  Sem_Stack.push(func_def_stateBlock);
			  //func_def_stateBlock.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_STATEMENT_BLOCK"))
		  {
			  //System.out.println("SEMANTIC_MAKE_STATEBLOCK");
			  List<Node> nodelist=new ArrayList<Node>();
			  while (((Node) Sem_Stack.peek()).getNodeType().equals("varDecl") || ((Node) Sem_Stack.peek()).getNodeType().equals("statNode"))
			  {
				  //System.out.println(((Node) Sem_Stack.peek()).getNodeType());
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			  Node stateBlock= new StatBlockNode(nodelist);
			  stateBlock.setNodeType("statBlock");
			  Sem_Stack.push(stateBlock);
			  //stateBlock.print();
		  }
		  else if (take_action.equals("SEMANTIC_MARK_BEGIN_FUNCHEAD")) //Marker for the begin for the funcHead
		  {
			 // System.out.println("Top of the stack is :"+((Node) Sem_Stack.peek()).getNodeType());
			 // System.out.println("This is the begin of the function head, rule :SEMANTIC_MARK_BEGIN_FUNCHEAD");
			  Node marker_begin_functionHead= new BeginFuncHeadNode("funcHeadBegin");
			  marker_begin_functionHead.setNodeType("funcheadbeginNode");
			  Sem_Stack.push(marker_begin_functionHead);
			  //System.out.println("Top of the stack is :"+((Node) Sem_Stack.peek()).getNodeType());
			  //marker_begin_functionHead.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_FuncDeF")) // picks stuff from stack- till funcheadbeginNode
		  {
			 // System.out.println("SEMANTIC_MAKE_FuncDeF");
			  //System.out.println("Semantic Stack is:");
			  List<Node> nodelist=new ArrayList<Node>();
			  //System.out.println("Top of the stack is :"+((Node) Sem_Stack.peek()).getNodeType());
			  while (!((Node) Sem_Stack.peek()).getNodeType().equals("funcheadbeginNode"))
			  {
				 // System.out.println(((Node) Sem_Stack.peek()).getNodeType());
				  nodelist.add((Node) Sem_Stack.pop());
			  }
			 // System.out.println("Top of the stack is :"+((Node) Sem_Stack.peek()).getNodeType());
			  if (((Node) Sem_Stack.peek()).getNodeType().equals("funcheadbeginNode"))
			  Sem_Stack.pop();// removing the node - marked as the begin of function body
			  Node func_def= new FuncDefNode(nodelist);
			  func_def.setNodeType("funcDef");
			  Sem_Stack.push(func_def);
			  //func_def.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_INDICE_NODE"))
		  {
			 // System.out.println("SEMANTIC_MAKE_INDICE_NODE"); //ArithExpr
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  if  (((Node) Sem_Stack.peek()).getNodeType().equals("ArithExpr"))
			  {
				  while (((Node) Sem_Stack.peek()).getNodeType().equals("ArithExpr")
						  || ((Node) Sem_Stack.peek()).getNodeType().equals("DotOP")
						  || ((Node) Sem_Stack.peek()).getNodeType().equals("DataMemberNode") )
				  {
					  //System.out.println(((Node) Sem_Stack.peek()).getNodeType());
					  nodelist.add((Node) Sem_Stack.pop());
				  }
				  
				  Node indice_node= new indiceNode(nodelist);
				  indice_node.setNodeType("indiceNode");
				  Sem_Stack.push(indice_node);
				  //indice_node.print();
			  }
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_IF_STATEMENT"))// SEMANTIC_MAKE_IF_STATEMENT
		  {
			//  System.out.println("SEMANTIC_MAKE_IF_STATEMENT"); 
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  while  (((Node) Sem_Stack.peek()).getNodeType().equals("statBlock"))
			  {				 
					 // System.out.println(((Node) Sem_Stack.peek()).getNodeType());
					  nodelist.add((Node) Sem_Stack.pop());
			  }  
 			  
 			 nodelist.add((Node) Sem_Stack.pop());// relExpr
			 Node ifState_node= new IfNode(nodelist);
			 ifState_node.setNodeType("ifStateNode");
			  Sem_Stack.push(ifState_node);
		      //indice_node.print();
			  
		  }
		
		  else if (take_action.equals("SEMANTIC_MAKE_GET_STATEMENT"))
		  {
			//  System.out.println("SEMANTIC_MAKE_GET_STATEMENT"); 
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  nodelist.add((Node) Sem_Stack.pop());// relExpr
			  Node getState_node= new GetStatNode(nodelist);
			  getState_node.setNodeType("getStateNode");
			  Sem_Stack.push(getState_node);
		      //indice_node.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_PUT_STATEMENT"))
		  {
			//  System.out.println("SEMANTIC_MAKE_PUT_STATEMENT"); 
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  nodelist.add((Node) Sem_Stack.pop());
			  Node putState_node= new PutSatNode(nodelist);
			  putState_node.setNodeType("putStateNode");
			  Sem_Stack.push(putState_node);
		      //indice_node.print();
			  
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_RETURN_STATEMENT"))
		  {
			 // System.out.println("SEMANTIC_MAKE_RETURN_STATEMENT"); 
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  nodelist.add((Node) Sem_Stack.pop());
			  Node returnState_node= new ReturnStatNode(nodelist);
			  returnState_node.setNodeType("returnStateNode");
			  Sem_Stack.push(returnState_node);
		      //indice_node.print();
		  }
		  else if (take_action.equals("SEMANTIC_MAKE_FOR_STATEMENT"))
		  {
			//  System.out.println("SEMANTIC_MAKE_FOR_STATEMENT"); 
			  List <Node> nodelist =new ArrayList<Node>(); 
 			  if(((Node) Sem_Stack.peek()).getNodeType().equals("statBlock")){
 				 nodelist.add((Node) Sem_Stack.pop()); 				    
 			  }
 			 nodelist.add((Node) Sem_Stack.pop()); // assign stat 				    
 			 nodelist.add((Node) Sem_Stack.pop()); //  rel expr				    
 			 nodelist.add((Node) Sem_Stack.pop()); // expr				    
 			 Sem_Stack.pop();
 			 nodelist.add((Node) Sem_Stack.pop()); // id node	
 			 nodelist.add((Node) Sem_Stack.pop()); // type node				    
  			  
			  Node forState_node= new ForStatNode(nodelist);
			  forState_node.setNodeType("forStateNode");
			  Sem_Stack.push(forState_node);
		      
		  }

		  else if (take_action.equals("SEMANTIC_MAKE_STATEMENT"))
		  {
			//  System.out.println("SEMANTIC_MAKE_STATEMENT"); 
			  Node state_node= new StatNode((Node) Sem_Stack.pop());
			  state_node.setNodeType("statNode");
			  Sem_Stack.push(state_node);
		      //indice_node.print();
		  }
		
	}
}

