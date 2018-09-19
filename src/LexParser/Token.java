package LexParser;

public class Token {

	public  String type;
	public  String lexeme;
	public  int line_num;


	public Token(String type,String lexeme,int line_num) // constructor to be used for valid identifiers
	{
		this.type=type;
		this.lexeme=lexeme;
		this.line_num=line_num;
	
	}
}
