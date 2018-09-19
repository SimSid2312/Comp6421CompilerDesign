package LexParser;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	public static ArrayList<Character>  my_buffer1 = new ArrayList<Character>(8192);//8KB Buffer1 to take input from file
	public static ArrayList<Character>  my_buffer2 = new ArrayList<Character>(8192);//8KB buffer2
	public static StringBuffer input = new StringBuffer();// Data structure That takes input from the two Buffer- to avoid data loss from the two buffers

	int frwd_1=0; // Pointer on my_buffer1
    int frwd_2=0; //Pointer on my_buffer2
    private static int buffer_to_load; // Variable that check which Buffer has data to be loaded into StringBuilder input
    public static Character curr_char; //Current character
    public static int Character_count=0;
    public static int line_num = 1; // Line_number counter
    public static int pt=0;
    public static HashMap<String,String> Reserved_words=new HashMap<String,String>();
    static{
    	Reserved_words.put("if", "if");
    	Reserved_words.put("then", "then");
    	Reserved_words.put("else", "else");
    	Reserved_words.put("for", "for");
    	Reserved_words.put("class", "class");
    	Reserved_words.put("int", "int");
    	Reserved_words.put("float", "float");
    	Reserved_words.put("get", "get");
    	Reserved_words.put("put", "put");
    	Reserved_words.put("return", "return");
    	Reserved_words.put("program", "program");
    	Reserved_words.put("and","and");
    	Reserved_words.put("not","not");
    	Reserved_words.put("or","or");
    }
    
      
    public static HashMap<String,String> Punctuations_Operators=new HashMap<String,String>();
    static
    {
    	Punctuations_Operators.put(";",";");
    	Punctuations_Operators.put(".", ".");
    	Punctuations_Operators.put(":",":");
    	Punctuations_Operators.put("::","sr");
    	Punctuations_Operators.put("(","(");
    	Punctuations_Operators.put(")",")");
    	Punctuations_Operators.put("{","{");
    	Punctuations_Operators.put("}","}");
    	Punctuations_Operators.put("[","[");
    	Punctuations_Operators.put("]","]");
    	Punctuations_Operators.put("/*","Open_Multiline_Comment");
    	Punctuations_Operators.put("*/","Close_mutipline_Comment");
    	Punctuations_Operators.put("//","Open_comment");
    	Punctuations_Operators.put("==", "eq");
    	Punctuations_Operators.put("<>","neq");
    	Punctuations_Operators.put("<","lt");
    	Punctuations_Operators.put(">","gt");
    	Punctuations_Operators.put("<=","leq");
    	Punctuations_Operators.put(">=","geq");
    	Punctuations_Operators.put("+","+");
    	Punctuations_Operators.put("-","-");
    	Punctuations_Operators.put("*","*");
    	Punctuations_Operators.put("/","/");
    	Punctuations_Operators.put("=","=");
    	Punctuations_Operators.put(",",",");
    }
    
    public static ArrayList <Token> Token_output_file = new ArrayList<Token>();
    public static ArrayList <Token> Token_error_file = new ArrayList<Token>();
       
    
    public void load_my_buffers(String filename) throws IOException
    {
    	InputStream in = new FileInputStream(filename);
		Reader r = new InputStreamReader(in ,"UTF-8");
		int intch = r.read();
		
		while (intch != -1) {		  	
		  
		   if (frwd_1<8192 && frwd_2<8192) //Filling the buffer1
		  {
			  
			      my_buffer1.add(frwd_1,(char)intch);
				  intch=r.read();
				  frwd_1++;
				  buffer_to_load=1;//Uploading only buffer1
				     
		  }
		  else if ((frwd_1)==8192 && frwd_2<8192) //Buffer1 full - filling buffer2
		  {
			  my_buffer2.add(frwd_2,(char)intch);
			  intch=r.read();
			  frwd_2++;
			  buffer_to_load=2;//Upload both the buffers
			  
		  }		  
		  else if (frwd_2==8192 && frwd_1==8192)//Re-filling buffer1			  
		  {
			  frwd_1=0;
			  frwd_2=0;			 
			  for(int i=0;i<my_buffer1.size();i++)
			  {
				  //if(my_buffer1.get(i)!=null)
				  //{
					  input.append(my_buffer1.get(i));
			      //}
				  
			  }
			  for(int i=0;i<my_buffer2.size();i++)
			  {
				  input.append(my_buffer2.get(i));
			  }			  
			  my_buffer1.clear();
			  my_buffer2.clear();
			  	  		      
		  }
	}
		//System.out.println(my_buffer1);
		//System.out.println("----buffer2-------");
	    //System.out.println(my_buffer2);
	          
	    if (buffer_to_load==1)
	    {
	    	for(int i=0;i<my_buffer1.size();i++)
			  {
				  input.append(my_buffer1.get(i));
			  }
	    }
	    
	    if (buffer_to_load==2)
	    {
	    	  for(int i=0;i<my_buffer1.size();i++)
			  {
				  input.append(my_buffer1.get(i));			  
			 }
	    	
	    	for (int i=0;i<my_buffer2.size();i++)
	    	{
	    		input.append(my_buffer2.get(i));
	    	}
	    	
	    }
	    while (input.length() > 0 && Character.isWhitespace(input.charAt(input.length() - 1)))
	    	input.deleteCharAt(input.length() - 1);
	    
	    
    }
    
      
    public String nextToken() 
    {    	
    	if(pt<Token_output_file.size())
    	{
       		String t;
    		t=Token_output_file.get(pt).type+" "+Token_output_file.get(pt).lexeme+" "+Token_output_file.get(pt).line_num;
    		pt++;
    		return t;
    	}
    	
        return "$"; // This return a $ at the end of the file
    }
    
    public Lexer(String filename) throws IOException 
	{
    	BufferedWriter out = new BufferedWriter(new FileWriter("Token_output_file.txt"));
    	BufferedWriter out_err = new BufferedWriter(new FileWriter("Token_error_file.txt"));
    	BufferedWriter a2cc_out = new BufferedWriter(new FileWriter("Token_a2cc_output_file.txt"));
    	
		load_my_buffers(filename);
		 
		while(Character_count<input.length())
		{			
			my_scanner();
						
     	 }
		
		for(int i=0;i<Token_output_file.size();i++)
		{   
			out.write(Token_output_file.get(i).type+" "+Token_output_file.get(i).lexeme+" "+Token_output_file.get(i).line_num+"\n");
			//Below line can be uncommented to see the output on console
			//System.out.println(Token_output_file.get(i).type+","+Token_output_file.get(i).lexeme+","+Token_output_file.get(i).line_num);
		    out.flush();
		    if (Token_output_file.get(i).type.equals("id"))
		    {
		      a2cc_out.write("id ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals("==") || Token_output_file.get(i).type.equals("eq"))
		    {
		      a2cc_out.write("eq ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals("<>") || Token_output_file.get(i).type.equals("neq"))
		    {
		      a2cc_out.write("neq ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals("<") || Token_output_file.get(i).type.equals("lt"))
		    {
		      a2cc_out.write("lt ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals(">") || Token_output_file.get(i).type.equals("gt"))
		    {
		      a2cc_out.write("gt ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals("<=") || Token_output_file.get(i).type.equals("leq"))
		    {
		      a2cc_out.write("leq ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals(">=") || Token_output_file.get(i).type.equals("geq"))
		    {
		      a2cc_out.write("geq ");
		      a2cc_out.flush(); 
		    }
		    else if (Token_output_file.get(i).type.equals("integer"))
		    {
		    	a2cc_out.write("integer ");
		    	a2cc_out.flush(); 
		    }
		    else  if (Token_output_file.get(i).type.equals("float"))
		    {
		    	a2cc_out.write("float ");
		    	a2cc_out.flush();
		    }
		    else if (Token_output_file.get(i).type.equals("Open_comment") || Token_output_file.get(i).type.equals("Open_Multiline_Comment") || Token_output_file.get(i).type.equals("Close_mutipline_Comment"))
		    {
		    	continue;
		    }
		    else
		    {
		       
		    	a2cc_out.write(Token_output_file.get(i).lexeme+" ");
		    	a2cc_out.flush();		    	
		    }
		    
		    
		}
				
		for(int i=0;i<Token_error_file.size();i++)
		{		
			out_err.write("error("+Token_error_file.get(i).type+","+Token_error_file.get(i).lexeme+","+Token_error_file.get(i).line_num+")\n");
			//ParseT.final_error_file.add("LexicalError:"+Token_error_file.get(i).type+","+Token_error_file.get(i).lexeme+","+Token_error_file.get(i).line_num);
			//below comment can uncommented - to view the output on console
			//System.out.println(Token_error_file.get(i).type+","+Token_error_file.get(i).lexeme+","+Token_error_file.get(i).line_num);
			out_err.flush();
		}
		
	}
    
    public static Character readChar()
    {   	
    		if (Character_count<input.length())
    	    {    			
    		    curr_char=input.charAt(Character_count);
    		    return curr_char;
    		}
    		
    		else 
    		{
    			curr_char='$'; //Marks the end of file
    		}
    		return curr_char;
    		
     }
    
    public boolean isPunctation(String punc)
    {
    	if(Punctuations_Operators.containsKey(punc))
    	{
    		Set<String> it= Punctuations_Operators.keySet();
    		 for(String iter:it)
				{ 
					if(iter.equals(punc))
					{
						Token_output_file.add(new Token(Punctuations_Operators.get(iter),punc,line_num));
					}
				}   
    		return true;
    	}
    	
    	return false;
    }
    public boolean isReserved(String token_b)
    {  
    	if (Reserved_words.containsKey(token_b.toLowerCase()) || Reserved_words.containsKey(token_b.toUpperCase()) )
    	{
    				Set<String> it= Reserved_words.keySet();
    				 for(String iter:it)
    				{ 
    					if(iter.equals(token_b))
    					{
    						Token_output_file.add(new Token(Reserved_words.get(iter),token_b,line_num));
    					}
    				}    			    		
         		return true;
     	}
     		return false;
     		
    }
    
      public void my_scanner()
    {
    	
    	readChar();
    	for(;;)//Counter for the line # and ignoring tab and spaces
    	{    		
    		
    		if(curr_char.equals('\r') || curr_char.equals('\n')){
    			line_num=line_num+1;
    			
    			Character_count+=2;//in my case a newline occupies two character
    			
    			if(Character_count<input.length())
    			{    				
    			   readChar();
    			  
       			}
    			    			
    		}
    		else if(curr_char.equals('\t') || curr_char.equals(' ')) 
    		{
    			Character_count++;
    			//if ((curr_char+1)<input.length())
    			{readChar();
       			}
    		}
    		else if (curr_char.equals('/'))
    		{
    			
    			Character_count++;
    					   				
    			   readChar();
       			
    			
    			if (curr_char.equals('/'))
    			{
    				
    				while (true) // if it is a single line comment keep on reading characters till newline character
    				{
    					Character_count++;
    	    			readChar();
    	    			if(curr_char.equals('\r') || curr_char.equals('\n'))
    	    			{
    	    				break;
    	    			}
    				}
    			}
    			else if (curr_char.equals('*')) // Removing multiline comment
    			{    	
    				char found='n';//ensure the matching ending */ is found
    				while (Character_count<input.length())
    				{
    					Character_count++;
    					readChar();
    					if(curr_char.equals('\r') || curr_char.equals('\n'))
    					{
    						line_num=line_num+1;
    						Character_count+=2;//in my case a newline occupies two character
    		    			readChar();
    		    				    			       			
    					}
    					 if (curr_char.equals('*'))
    					{
    						//System.out.println(input.charAt(Character_count));
    						Character_count++;
        					if (readChar().equals('/'))
        					{
        						Character_count++;
        						found='y';
        						break;
        				    }       					
        					
    					}   					
    					 
       				}
    				if (Character_count==input.length() && found=='n') // Loop return to calling function - if file end is reached 
    				{
    					System.out.println("matching */ is missing at end of program at line:"+line_num);
    					Token_error_file.add(new Token("matching */ is missing at end of program","*/",line_num));
    					Character_count++;
    					if (readChar().equals('$'))
    					{
    						return; // returning to the calling funtion - program ended
    					}
    					break;
    				}
    				else if  (Character_count==input.length() && found=='y') // Loop return to calling function - if file end is reached 
    				{ 
    					System.out.println("matching */ found and program execution ended!");
    					Character_count++;
    					if (readChar().equals('$'))
    					{
    						return; // returning to the calling funtion - program ended
    					}
    					break;
    				}
    			}
    			
    			else if (curr_char!='/' && curr_char!='*')
    			{
    				Character_count--;
    				
    				readChar();
    				
					break;
    			}
       			
    		}
    		
    		else
    		{
    			break;
       		}
    	} //end of for - counting lines
    	
    	/*if (curr_char=='$')
    	{
    		System.out.println("The program has exhausted!");
    	}
    	*/
    	
    	
 //Operator 
    	if (curr_char=='+'||curr_char=='-'||curr_char=='=' || curr_char=='<'||curr_char=='>'||curr_char==',')
    	{
    		StringBuilder op_b = new StringBuilder();
    		op_b.append(curr_char);
    		
    		if ((curr_char=='+' || curr_char=='-' || curr_char==',')&&Character_count<input.length())
    		{
    			isPunctation(op_b.toString());
    			Character_count++;
    		}
    		else if(Character_count<input.length()&&curr_char=='=')
    		{
    			Character_count++;
    			readChar();
    			if(Character_count<input.length()&&curr_char=='=')
    			{
    				String new_string="==";
					isPunctation(new_string);
					op_b.append(curr_char);
					Character_count++;	
    			}
    			else
    			{
    				isPunctation(op_b.toString());
					op_b.append(curr_char);
    			}
    		}
    		
    		else if(Character_count<input.length()&&curr_char=='>')
    		{
    			Character_count++;
    			readChar();
    			if(Character_count<input.length()&&curr_char=='=')
    			{
    				String new_string=">=";
					isPunctation(new_string);
					op_b.append(curr_char);
					Character_count++;	
    			}
    			else
    			{
    				isPunctation(op_b.toString());
					op_b.append(curr_char);
    			}
    		}
    		
    		else if(Character_count<input.length()&&curr_char=='<')
    		{
    			Character_count++;
    			readChar();
    			if(Character_count<input.length()&&curr_char=='>')
    			{
    				//String new_string="<>";
					isPunctation("<>");
					op_b.append(curr_char);
					Character_count++;
    			}
    			if(Character_count<input.length()&&curr_char=='=')
    			{
    				String new_string="<=";
					isPunctation(new_string);
					op_b.append(curr_char);
					Character_count++;
    			}
    			else if (Character_count<input.length()&&curr_char!='>')
    			{   //System.out.println(op_b);
    				isPunctation(op_b.toString());
					op_b.append(curr_char);
    			}
    			
    		}
    		
    	}
    	
    	
 // Punctuation
    	if((curr_char==';' || curr_char=='*' || curr_char==':' || curr_char =='/' || curr_char=='.' || curr_char=='(' || curr_char==')' || curr_char=='{' || curr_char == '}' || curr_char == '[' || curr_char == ']'))
    	{
    		StringBuilder punc_b = new StringBuilder();
    		punc_b.append(curr_char);    		
    		
    		if(curr_char==';'||curr_char=='.'||curr_char=='('||curr_char==')'||curr_char=='{'||curr_char=='}'||curr_char=='['||curr_char==']') 
    		{
    	    		
    			isPunctation(punc_b.toString());
    			Character_count++;
    		}
    		    		
    		else if((curr_char=='/' || curr_char=='*' || curr_char==':'))
    		{
    		      
    			if (curr_char==':')
    			{
    				Character_count++;
    				readChar();
    				
    				if(Character_count<input.length()&&curr_char==':')
    				{
    					String new_string="::";
    					isPunctation(new_string);
    					punc_b.append(curr_char);
    					Character_count++;
    				}
    				else
    				{    					
    					isPunctation(punc_b.toString());
    					punc_b.append(curr_char);
    					//Character_count++;
    				}
    			}
    			
    		else  if (curr_char=='/')
    		   {
    			   Character_count++;
       			   readChar();
       			   boolean bool=false;
       			  
       			  if(Character_count<input.length()&&curr_char=='*')
       			  {
       				isPunctation("/*");
   					punc_b.append(curr_char);
   					Character_count++;
   					bool=true;
       			  }
       			  if(Character_count<input.length()&&curr_char=='/')
       			  {
       				String new_string="//";
   					isPunctation(new_string);
   					punc_b.append(curr_char);
   					Character_count++;
   					bool=true;
       			  }
       			  if (bool==false)
      			 {   
       				 
       				isPunctation(punc_b.toString());
       				punc_b.append(curr_char);       				
       				//Character_count++;
       			}
    			  
    		  }
    		   
    		  
    		else if (curr_char=='*')
    		  {
    			  Character_count++;
    			  readChar();
    			  if(Character_count<input.length()&&curr_char=='/')
    			  {
    				  String new_string="*/";
  					  isPunctation(new_string);
  					  punc_b.append(curr_char);
  					  Character_count++;
    			  }
    			     else //for multiplication operation
    				{
    			    	 
    					isPunctation(punc_b.toString());
    					punc_b.append(curr_char);
    				}
    		   }
    		   	
    		}
    	    
    		
    		
    	}
    	   	
 //Tokenizing id and keywords - taking the input as alphabets and underscore
    	 if((curr_char >='A' && curr_char <='Z') || (curr_char >= 'a' && curr_char<='z') || (curr_char=='_'))
    	{   		
    		StringBuilder token_b = new StringBuilder();
    		token_b.append(curr_char);
    		Character_count++;
    		readChar();//Read the next character
    		
    		while( Character_count<input.length() &&( (curr_char>='0' && curr_char<='9')||(curr_char=='_')||(curr_char >='A' && curr_char <='Z')||(curr_char >= 'a' && curr_char<='z')) )
    		{
    			token_b.append(curr_char);
    			Character_count++;
    			readChar();
    			
    		}
    		if (token_b.charAt(0)=='_')
    		{
    			Token_error_file.add(new Token("Invalid_Identifier",token_b.toString(),line_num));	
       		}
    		else 
    		{
    			boolean res=isReserved(token_b.toString());
    		    		
    		    if(res==false) // code for the identifier
    		   {
    			  Token_output_file.add(new Token("id",token_b.toString(),line_num));
    		   }
    	   }
    	}// end of if - Tokenizing if and keywords
    	
//Tokenizing int and float  	
    	 if(curr_char>='0' && curr_char<='9')
    	{
    		StringBuilder num_b = new StringBuilder();
    		num_b.append(curr_char);
    		Character_count++;
    		readChar();//Read the next character
    		
    		while (Character_count<input.length()&&( (curr_char>='0' && curr_char<='9') ))
    		{
    			num_b.append(curr_char);
    			Character_count++;
    			readChar();
    		} //progress only when a digit is read after another digit
    		
    		
    		if (num_b.charAt(0)=='0' && (num_b.toString()).length()>1)
    		{
    			if(curr_char.equals('.'))
    			{
    				   num_b.append(curr_char);
    				   Character_count++;
    				   readChar();
    				
    				  if(!(curr_char>='0' && curr_char<='9'))//not a digit after a .
    				  {
    					 Token_error_file.add(new Token("Invalid_Num",num_b.toString(),line_num));	
    				  }
    				
    				  while(Character_count<input.length()&&(curr_char>='0' && curr_char<='9'))
    				  {
    					    num_b.append(curr_char);
    			    	    Character_count++;
    			    	    readChar();
    				  }
    				
    				  if (num_b.charAt(num_b.length()-1)=='0' && num_b.charAt(num_b.length()-2)!='.') //last digit is 0 but second last is not a '.' -invalid number
    			      {
    			    	   Token_error_file.add(new Token("Invalid_Float_Num",num_b.toString(),line_num));
    			      }
    				
    				  else
    				  {
    					    if (curr_char.equals('e'))
    					   {
    						   num_b.append(curr_char);//put it in sb
        			    	   Character_count++;	
        			    	  readChar();
        			    	
        			    	 if (curr_char.equals('+') || curr_char.equals('-'))
        			    	 {
        			    		num_b.append(curr_char);
        			    		Character_count++;
        			    		readChar();
        			    	}
        			    	
        			    	//giving the definition of int        			    	
        			    	if (!(curr_char >= '0' && curr_char <= '9'))
        			    	{
        			    		Token_error_file.add(new Token("Invalid_Float_Num",num_b.toString(),line_num));
        			    	}
        			    	else
        			    	{
        			    		StringBuilder temp_storage = new StringBuilder();
        			    		
        			    		while(Character_count<input.length()&&(curr_char>='0' && curr_char<='9'))
        			    		{
        			    			temp_storage.append(curr_char);
        			    			
        			    			Character_count++;
        			    			readChar();
        			    		}
        			    		num_b.append(temp_storage);
        			    		 
        			    		if (temp_storage.charAt(0)=='0' && (temp_storage.toString()).length()>1)
        			    		{        			    			      			    			
        			    			Token_error_file.add(new Token("Invalid_float_Num",num_b.toString(),line_num));
        			       		}
        			    		else
        			    		{
        			    			System.out.println("here we go");
        			    			Token_output_file.add(new Token("Invalid_float_Num",num_b.toString(),line_num));
        			    		}
        			    		
        			    	 }
        			         			    			    	
    					   }//end of reading e notation- without it also the number is float
    					    					
    					else //without e notation - still a valid float
    					{	
    						Token_error_file.add(new Token("Invalid_float_Num",num_b.toString(),line_num));	
    					}
    				
    				  }		
    				}//go by '.' 
    		     	else
    			   {
    				    Token_error_file.add(new Token("Invalid_Int_Num",num_b.toString(),line_num));
    			   }   				
    				
    			}//end of wrong int 
    			
       		    		
    		else //a valid integer
    		{
    			if(curr_char.equals('.'))
    			{
    				num_b.append(curr_char);
    				Character_count++;
    				readChar();
    				
    				if(!(curr_char>='0' && curr_char<='9'))//not a digit after a .
    				{
    					Token_error_file.add(new Token("Invalid_Num",num_b.toString(),line_num));	
    				}
    				
    				while(Character_count<input.length()&&(curr_char>='0' && curr_char<='9'))
    				{
    					num_b.append(curr_char);
    			    	Character_count++;
    			    	readChar();
    				}
    				

    				if (num_b.charAt(num_b.length()-1)=='0' && num_b.charAt(num_b.length()-2)!='.') //last digit is 0 but second last is not a '.' -invalid number
    			    {
    			    	Token_error_file.add(new Token("Invalid_Float_Num",num_b.toString(),line_num));
    			    }
    				
    				else
    				{
    					if (curr_char.equals('e'))
    					{
    						num_b.append(curr_char);//put it in sb
        			    	Character_count++;	
        			    	readChar();
        			    	
        			    	if (curr_char.equals('+') || curr_char.equals('-'))
        			    	{
        			    		num_b.append(curr_char);
        			    		Character_count++;
        			    		readChar();
        			    	}
        			    	
        			    	//giving the definition of int 
        			    	
        			    	if (!(curr_char >= '0' && curr_char <= '9'))
        			    	{
        			    		Token_error_file.add(new Token("Invalid_Float_Num",num_b.toString(),line_num));
        			    	}
        			    	else
        			    	{
        			    		StringBuilder temp_storage = new StringBuilder();
        			    		
        			    		while(Character_count<input.length()&&(curr_char>='0' && curr_char<='9'))
        			    		{
        			    			temp_storage.append(curr_char);
        			    			Character_count++;
        			    			readChar();
        			    		}
        			    		
        			    		num_b.append(temp_storage);
        			    		if (temp_storage.charAt(0)=='0' && (temp_storage.toString()).length()>1)
        			    		{        			    			      			    			
        			    			Token_error_file.add(new Token("Invalid_float_Num",num_b.toString(),line_num));
        			       		}
        			    		else
        			    		{
        			    			Token_output_file.add(new Token("float",num_b.toString(),line_num));
        			    		}
        			    		
        			    	}      			    			    	
        			    	
    					}//end of reading e notation- without it also the number is float
    					
    					else //without e notation - still a valid float
    					{	
    			    	     Token_output_file.add(new Token("float",num_b.toString(),line_num));	
    					}
    				}
    				
    			}
    			
    			
    		   else//else if for a '.'--float
    		   {
    			 Token_output_file.add(new Token("integer",num_b.toString(),line_num));
    		   }
    		}
    		
    	}
    	 	
    	
    }//End of scanner function
}