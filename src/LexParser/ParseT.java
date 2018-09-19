package LexParser;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import AST.*;
import Visitor.AST.*;
import Visitor.CodeGeneration.TagBasedCodeGenerationVisitor;
import Visitor.SemanticChecking.TypeCheckingVisitor;
import Visitor.SymbolTable.*;



public class ParseT {

	public static HashMap <String,ArrayList> Productions = new HashMap<String,ArrayList>();
	public static ArrayList<String> Terminals = new ArrayList<String>();
	public static HashMap <String,ArrayList> first_Set = new HashMap <String,ArrayList>();
	public static HashMap <String,ArrayList> follow_Set = new HashMap <String,ArrayList>();
	public static String [][] Parse_Table ={
	
	{"","program",";","class","id","{","}",":",",","(",")","int","float","[","integer","]","sr","if","then","else","get","put","return","for","not",".","+","-","or","eq","neq","lt","gt","leq","geq","*","/","and","=","$"},
	//{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{"prog","1","109","1","1","109","109","109","109","109","109","1","1","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","108"},
	{"classDeclaration","3","109","2","3","109","109","109","109","109","109","3","3","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"functionDeclaration","5","109","109","4","109","109","109","109","109","109","4","4","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"classDecl","108","109","6","108","109","109","109","109","109","109","108","108","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"opid","109","109","109","109","8","109","7","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"cid","109","109","109","109","10","109","109","9","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"varfuncdecl","109","109","109","11","109","12","109","109","109","109","11","11","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"varfunctail","109","13","109","109","109","108","109","109","14","109","109","109","13","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"funcDeclRep","109","109","109","15","109","16","109","109","109","109","15","15","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"funcDef","108","109","109","17","109","109","109","109","109","109","17","17","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"funcHead","109","109","109","18","108","109","109","109","109","109","18","18","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"funcDecl","109","109","109","19","109","108","109","109","109","109","19","19","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"fParams","109","109","109","20","109","109","109","109","109","21","20","20","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"fpTail","109","109","109","109","109","109","109","22","109","23","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"fParamsTail","109","109","109","109","109","109","109","24","109","108","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"arr","109","26","109","109","109","109","109","26","109","26","109","109","25","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"type","109","109","109","29","109","109","109","109","109","109","27","28","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"arraySize","109","108","109","109","109","109","109","108","109","108","109","109","30","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"oidsrpid","109","109","109","109","109","109","109","109","32","109","109","109","109","109","109","31","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"funcBody","109","108","109","109","33","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"varstat","109","109","109","34","109","38","109","109","109","109","35","36","109","109","109","109","37","109","109","37","37","37","37","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"varstatPrime","109","109","109","39","109","108","109","109","40","109","109","109","40","109","109","109","109","109","109","109","109","109","109","109","40","109","109","109","109","109","109","109","109","109","109","109","109","40","109"},
	{"statementPrime","109","109","109","108","109","108","109","109","109","109","109","109","109","109","109","109","41","109","109","43","44","45","42","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"ST","109","109","109","46","109","47","109","109","109","109","109","109","109","109","109","109","46","109","109","46","46","46","46","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"statement","109","108","109","48","109","108","109","109","109","109","109","109","109","109","109","109","49","109","108","51","52","53","50","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"forLoop","109","108","109","108","109","108","109","109","109","109","109","109","109","109","109","109","108","109","108","108","108","108","54","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"assignStat","109","108","109","55","109","109","109","109","109","108","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"statBlock","109","58","109","57","56","109","109","109","109","109","109","109","109","109","109","109","57","109","58","57","57","57","57","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"expr","109","108","109","59","109","109","109","108","59","108","109","59","109","59","109","109","109","109","109","109","109","109","109","59","109","59","59","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"exprTail","109","60","109","109","109","109","109","60","109","60","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","61","61","61","61","61","61","109","109","109","109","109"},
	{"relExpr","109","108","109","62","109","109","109","109","62","109","109","62","109","62","109","109","109","109","109","109","109","109","109","62","109","62","62","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"arithExpr","109","108","109","63","109","109","109","108","63","108","109","63","109","63","108","109","109","109","109","109","109","109","109","63","109","63","63","109","108","108","108","108","108","108","109","109","109","109","109"},
	{"arithExprTAIL","109","65","109","109","109","109","109","65","109","65","109","109","109","109","65","109","109","109","109","109","109","109","109","109","109","64","64","64","65","65","65","65","65","65","109","109","109","109","109"},
	{"term","109","108","109","66","109","109","109","108","66","108","109","66","109","66","108","109","109","109","109","109","109","109","109","66","109","66","66","108","108","108","108","108","108","108","109","109","109","109","109"},
	{"termTail","109","68","109","109","109","109","109","68","109","68","109","109","109","109","68","109","109","109","109","109","109","109","109","109","109","68","68","68","68","68","68","68","68","68","67","67","67","109","109"},
	{"factor","109","108","109","69","109","109","109","108","72","108","109","71","109","70","108","109","109","109","109","109","109","109","109","73","109","74","74","108","108","108","108","108","108","108","108","108","108","109","109"},
	{"factorTemp","109","108","109","75","109","109","109","108","109","108","109","109","109","109","108","109","109","109","109","109","109","109","109","109","109","108","108","108","108","108","108","108","108","108","108","108","108","109","109"},
    {"factorPrime","109","76","109","109","109","109","109","76","77","76","109","109","76","109","76","109","109","109","109","109","109","109","109","109","76","76","76","76","76","76","76","76","76","76","76","76","76","109","109"},
	{"factorTempTemp","109","79","109","109","109","109","109","79","109","79","109","109","109","109","79","109","109","109","109","109","109","109","109","109","78","79","79","79","79","79","79","79","79","79","79","79","79","109","109"},
	{"indiceTail","109","81","109","109","109","109","109","81","109","81","109","109","80","109","81","109","109","109","109","109","109","109","109","109","81","81","81","81","81","81","81","81","81","81","81","81","81","81","109"},
	{"indice","109","108","109","109","109","109","109","108","109","108","109","109","82","109","108","109","109","109","109","109","109","109","109","109","108","108","108","108","108","108","108","108","108","108","108","108","108","108","109"},
	{"variable","109","109","109","83","109","109","109","109","109","108","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","108","109"},
    {"variableTail","109","109","109","109","109","109","109","109","85","84","109","109","84","109","109","109","109","109","109","109","109","109","109","109","84","109","109","109","109","109","109","109","109","109","109","109","109","84","109"},
	{"variablePrime","109","109","109","109","109","109","109","109","109","87","109","109","109","109","109","109","109","109","109","109","109","109","109","109","86","109","109","109","109","109","109","109","109","109","109","109","109","87","109"},
	{"aParams","109","109","109","88","109","109","109","109","88","89","109","88","109","88","109","109","109","109","109","109","109","109","109","88","109","88","88","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"aParamsTailRep","109","109","109","109","109","109","109","90","109","91","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"aParamsTail","109","109","109","109","109","109","109","92","109","108","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"addOp","109","109","109","108","109","109","109","109","108","109","109","108","109","108","109","109","109","109","109","109","109","109","109","108","109","93","94","95","109","109","109","109","109","109","109","109","109","109","109"},
	{"relOp","109","109","109","108","109","109","109","109","108","109","109","108","109","108","109","109","109","109","109","109","109","109","109","108","109","108","108","109","96","97","98","99","100","101","109","109","109","109","109"},
	{"sign","109","109","109","108","109","109","109","109","108","109","109","108","109","108","109","109","109","109","109","109","109","109","109","108","109","102","103","109","109","109","109","109","109","109","109","109","109","109","109"},
	{"multOp","109","109","109","108","109","109","109","109","108","109","109","108","109","108","109","109","109","109","109","109","109","109","109","108","109","108","108","109","109","109","109","109","109","109","104","105","106","109","109"},
	{"assignOp","109","109","109","108","109","109","109","109","108","109","109","108","109","108","109","109","109","109","109","109","109","109","109","108","109","108","108","109","109","109","109","109","109","109","109","109","109","107","109"}
  };
	
	public static Stack<String> Parse_Stack= new Stack<String>();
	public static Lexer my_lex;
	public static boolean error;
	public static Stack<String> derivation= new Stack<String>();
	public static BufferedWriter derive;
	public static BufferedWriter syntatical_error;
	//public static BufferedWriter semantic_error;
	public static BufferedWriter symtab_file;
	public static String a;// this is the nextToken
	public static AST_T ast = new AST_T();
	public static TermSemanticAction terminal_Semaction = new TermSemanticAction();
	public static BufferedWriter AST_Buffer;
	public static BufferedWriter Semantic_err;
	public static String  m_outputfilename = "TypeCheckSymbolTable.symtab"; 
	
	public static List<String> final_error_file=new ArrayList<>();
	public static BufferedWriter ferror;
	static
	{	
		Productions.put("0",new ArrayList<String>(Arrays.asList("0")));
		Productions.put("1",new ArrayList<String>(Arrays.asList("prog -> classDeclaration SEMANTIC_MAKE_CLASS_LIST functionDeclaration SEMANTIC_MAKE_FUNCDEF_LIST program SEMANTIC_MARK_BEGIN_BODY funcBody ; SEMANTIC_MAKE_PROG")));
		Productions.put("2",new ArrayList<String>(Arrays.asList("classDeclaration -> classDecl SEMANTIC_MAKE_CLASS_DECL classDeclaration")));
		Productions.put("3",new ArrayList<String>(Arrays.asList("classDeclaration -> EPSILON")));
		Productions.put("4",new ArrayList<String>(Arrays.asList("functionDeclaration -> funcDef functionDeclaration")));
		Productions.put("5",new ArrayList<String>(Arrays.asList("functionDeclaration -> EPSILON")));
		Productions.put("6",new ArrayList<String>(Arrays.asList("classDecl -> class id SEMANTIC_MARK_CLASS_NAME opid { varfuncdecl SEMANTIC_MAKE_MAKE_MEMBER_LIST } ;")));
		Productions.put("7",new ArrayList<String>(Arrays.asList("opid -> : id cid")));
		Productions.put("8",new ArrayList<String>(Arrays.asList("opid -> EPSILON SEMANTIC_MAKE_INHER_SIBLING")));
		Productions.put("9",new ArrayList<String>(Arrays.asList("cid -> , id cid")));
		Productions.put("10",new ArrayList<String>(Arrays.asList("cid -> EPSILON SEMANTIC_MAKE_INHER_SIBLING")));
		Productions.put("11",new ArrayList<String>(Arrays.asList("varfuncdecl -> type id varfunctail")));
		Productions.put("12",new ArrayList<String>(Arrays.asList("varfuncdecl -> EPSILON")));
		Productions.put("13",new ArrayList<String>(Arrays.asList("varfunctail -> arr ; SEMANTIC_MAKE_VAR_DECL varfuncdecl")));
		Productions.put("14",new ArrayList<String>(Arrays.asList("varfunctail -> ( fParams ) ; SEMANTIC_MAKE_FUNCDECL funcDeclRep")));
		Productions.put("15",new ArrayList<String>(Arrays.asList("funcDeclRep -> funcDecl SEMANTIC_MAKE_FUNCDECL funcDeclRep")));
		Productions.put("16",new ArrayList<String>(Arrays.asList("funcDeclRep -> EPSILON SEMANTIC_MAKE_FUNCDECL")));
		Productions.put("17",new ArrayList<String>(Arrays.asList("funcDef -> SEMANTIC_MARK_BEGIN_FUNCHEAD funcHead SEMANTIC_MARK_BEGIN_BODY funcBody ; SEMANTIC_MAKE_FuncDeF")));
		Productions.put("18",new ArrayList<String>(Arrays.asList("funcHead -> type id oidsrpid ( fParams )")));
		Productions.put("19",new ArrayList<String>(Arrays.asList("funcDecl -> type id ( fParams ) ;")));
		Productions.put("20",new ArrayList<String>(Arrays.asList("fParams -> type id arr SEMANTIC_MAKE_FPARAM_NODE fpTail")));
		Productions.put("21",new ArrayList<String>(Arrays.asList("fParams -> EPSILON")));
		Productions.put("22",new ArrayList<String>(Arrays.asList("fpTail -> fParamsTail fpTail")));
		Productions.put("23",new ArrayList<String>(Arrays.asList("fpTail -> EPSILON")));
		Productions.put("24",new ArrayList<String>(Arrays.asList("fParamsTail -> , type id arr SEMANTIC_MAKE_FPARAM_NODE")));
		Productions.put("25",new ArrayList<String>(Arrays.asList("arr -> arraySize arr")));
		Productions.put("26",new ArrayList<String>(Arrays.asList("arr -> EPSILON SEMANTIC_MAKE_DIM_LIST")));
		Productions.put("27",new ArrayList<String>(Arrays.asList("type -> int")));
		Productions.put("28",new ArrayList<String>(Arrays.asList("type -> float")));
		Productions.put("29",new ArrayList<String>(Arrays.asList("type -> id")));
		Productions.put("30",new ArrayList<String>(Arrays.asList("arraySize -> [ integer ]")));
		Productions.put("31",new ArrayList<String>(Arrays.asList("oidsrpid -> sr SEMANTIC_MARK_SCOPE_SPEC id")));
		Productions.put("32",new ArrayList<String>(Arrays.asList("oidsrpid -> EPSILON")));
		Productions.put("33",new ArrayList<String>(Arrays.asList("funcBody -> { varstat } SEMANTIC_MAKE_STATEBLOCK")));
		Productions.put("34",new ArrayList<String>(Arrays.asList("varstat -> id varstatPrime")));
		Productions.put("35",new ArrayList<String>(Arrays.asList("varstat -> int id arr ; SEMANTIC_MAKE_VAR_DECL varstat")));
		Productions.put("36",new ArrayList<String>(Arrays.asList("varstat -> float id arr ; SEMANTIC_MAKE_VAR_DECL varstat")));
		Productions.put("37",new ArrayList<String>(Arrays.asList("varstat -> statementPrime SEMANTIC_MAKE_STATEMENT ST")));
		Productions.put("38",new ArrayList<String>(Arrays.asList("varstat -> EPSILON"))); //SEMANTIC_MAKE_STATEBLOCK -- removed - 22:01 23-03-2018
		Productions.put("39",new ArrayList<String>(Arrays.asList("varstatPrime -> id arr ; SEMANTIC_MAKE_VAR_DECL varstat")));
		Productions.put("40",new ArrayList<String>(Arrays.asList("varstatPrime -> variableTail SEMANTIC_MAKE_VAR assignOp expr ; SEMANTIC_MAKE_ASSIGN_STATEMENT SEMANTIC_MAKE_STATEMENT ST")));
		Productions.put("41",new ArrayList<String>(Arrays.asList("statementPrime -> if ( expr ) then statBlock else statBlock ; SEMANTIC_MAKE_IF_STATEMENT")));
		Productions.put("42",new ArrayList<String>(Arrays.asList("statementPrime -> forLoop")));
		Productions.put("43",new ArrayList<String>(Arrays.asList("statementPrime -> get ( variable ) ; SEMANTIC_MAKE_GET_STATEMENT")));
		Productions.put("44",new ArrayList<String>(Arrays.asList("statementPrime -> put ( expr ) ; SEMANTIC_MAKE_PUT_STATEMENT")));
		Productions.put("45",new ArrayList<String>(Arrays.asList("statementPrime -> return ( expr ) ; SEMANTIC_MAKE_RETURN_STATEMENT")));
		Productions.put("46",new ArrayList<String>(Arrays.asList("ST -> statement SEMANTIC_MAKE_STATEMENT ST")));
		Productions.put("47",new ArrayList<String>(Arrays.asList("ST -> EPSILON")));
		Productions.put("48",new ArrayList<String>(Arrays.asList("statement -> assignStat ;")));
		Productions.put("49",new ArrayList<String>(Arrays.asList("statement -> if ( expr ) then statBlock else statBlock ; SEMANTIC_MAKE_IF_STATEMENT")));
		Productions.put("50",new ArrayList<String>(Arrays.asList("statement -> forLoop")));
		Productions.put("51",new ArrayList<String>(Arrays.asList("statement -> get ( variable ) ; SEMANTIC_MAKE_GET_STATEMENT")));
		Productions.put("52",new ArrayList<String>(Arrays.asList("statement -> put ( expr ) ; SEMANTIC_MAKE_PUT_STATEMENT")));
		Productions.put("53",new ArrayList<String>(Arrays.asList("statement -> return ( expr ) ; SEMANTIC_MAKE_RETURN_STATEMENT")));
		Productions.put("54",new ArrayList<String>(Arrays.asList("forLoop -> for ( type id assignOp expr ; relExpr ; assignStat ) statBlock ; SEMANTIC_MAKE_FOR_STATEMENT")));
		Productions.put("55",new ArrayList<String>(Arrays.asList("assignStat -> variable assignOp expr SEMANTIC_MAKE_ASSIGN_STATEMENT")));
		Productions.put("56",new ArrayList<String>(Arrays.asList("statBlock -> { ST } SEMANTIC_MAKE_STATEMENT_BLOCK")));
		Productions.put("57",new ArrayList<String>(Arrays.asList("statBlock -> statement SEMANTIC_MAKE_STATEMENT_BLOCK")));
		Productions.put("58",new ArrayList<String>(Arrays.asList("statBlock -> EPSILON SEMANTIC_MAKE_STATEMENT_BLOCK")));
		
		Productions.put("59",new ArrayList<String>(Arrays.asList("expr -> arithExpr exprTail SEMANTIC_MAKE_EXPR")));
		Productions.put("60",new ArrayList<String>(Arrays.asList("exprTail -> EPSILON")));
		Productions.put("61",new ArrayList<String>(Arrays.asList("exprTail -> relOp arithExpr SEMANTIC_MAKE_RELEXPR")));
		Productions.put("62",new ArrayList<String>(Arrays.asList("relExpr -> arithExpr relOp arithExpr SEMANTIC_MAKE_RELEXPR")));
		Productions.put("63",new ArrayList<String>(Arrays.asList("arithExpr -> term arithExprTAIL")));
		Productions.put("64",new ArrayList<String>(Arrays.asList("arithExprTAIL -> addOp term SEMANTIC_MAKE_ADD_CONSTRUCT arithExprTAIL")));
		Productions.put("65",new ArrayList<String>(Arrays.asList("arithExprTAIL -> EPSILON SEMANTIC_MAKE_ARITH_EXPR")));
		Productions.put("66",new ArrayList<String>(Arrays.asList("term -> factor termTail")));
		Productions.put("67",new ArrayList<String>(Arrays.asList("termTail -> multOp factor SEMANTIC_MAKE_MULT_CONSTRUCT termTail")));
		Productions.put("68",new ArrayList<String>(Arrays.asList("termTail -> EPSILON SEMANTIC_MAKE_TERM")));
		
		Productions.put("69",new ArrayList<String>(Arrays.asList("factor -> factorTemp SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("70",new ArrayList<String>(Arrays.asList("factor -> integer SEMANTIC_FACTOR_NUM_NODE SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("71",new ArrayList<String>(Arrays.asList("factor -> float SEMANTIC_FACTOR_NUM_NODE SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("72",new ArrayList<String>(Arrays.asList("factor -> ( arithExpr ) SEMANTIC_MAKE_FACTOR_ARITH SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("73",new ArrayList<String>(Arrays.asList("factor -> SEMANTIC_PUT_NOT_TOSEMSTACK not factor SEMANTIC_MAKE_FACTOR_NOT SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("74",new ArrayList<String>(Arrays.asList("factor -> SEMANTIC_PUT_SIGN_TOSEMSTACK sign factor SEMANTIC_MAKE_FACTOR_SIGN SEMANTIC_MAKE_FACTOR_NODE")));
		Productions.put("75",new ArrayList<String>(Arrays.asList("factorTemp -> id factorPrime SEMANTIC_MAKE_FACTOR_VAR_FCALL")));
		Productions.put("76",new ArrayList<String>(Arrays.asList("factorPrime -> indiceTail SEMANTIC_MAKE_DATA_MEMBER factorTempTemp"))); //
		Productions.put("77",new ArrayList<String>(Arrays.asList("factorPrime -> ( aParams ) SEMANTIC_MAKE_FCALL factorTempTemp"))); //
		Productions.put("78",new ArrayList<String>(Arrays.asList("factorTempTemp -> . factorTemp")));
		Productions.put("79",new ArrayList<String>(Arrays.asList("factorTempTemp-> EPSILON SEMANTIC_MAKE_DOT_CONSTRUCT")));
		
		Productions.put("80",new ArrayList<String>(Arrays.asList("indiceTail -> indice indiceTail")));
		Productions.put("81",new ArrayList<String>(Arrays.asList("indiceTail -> EPSILON SEMANTIC_MAKE_INDICE_LIST")));
		Productions.put("82",new ArrayList<String>(Arrays.asList("indice -> [ arithExpr ] SEMANTIC_MAKE_INDICE_NODE")));
		
		Productions.put("83",new ArrayList<String>(Arrays.asList("variable -> id variableTail SEMANTIC_MAKE_VAR"))); 
		Productions.put("84",new ArrayList<String>(Arrays.asList("variableTail -> indiceTail SEMANTIC_MAKE_DATA_MEMBER  variablePrime"))); 
		Productions.put("85",new ArrayList<String>(Arrays.asList("variableTail -> ( aParams ) SEMANTIC_MAKE_FCALL . variable"))); //SEMANTIC_MAKE_DOT_CONSTRUCT
		Productions.put("86",new ArrayList<String>(Arrays.asList("variablePrime -> . variable")));
		Productions.put("87",new ArrayList<String>(Arrays.asList("variablePrime -> EPSILON SEMANTIC_MAKE_DOT_CONSTRUCT")));
		
		Productions.put("88",new ArrayList<String>(Arrays.asList("aParams -> expr aParamsTailRep SEMANTIC_MAKE_APARAMS_LIST")));
		Productions.put("89",new ArrayList<String>(Arrays.asList("aParams -> EPSILON SEMANTIC_MAKE_APARAMS_LIST")));
		Productions.put("90",new ArrayList<String>(Arrays.asList("aParamsTailRep -> aParamsTail aParamsTailRep")));
		Productions.put("91",new ArrayList<String>(Arrays.asList("aParamsTailRep -> EPSILON")));
		Productions.put("92",new ArrayList<String>(Arrays.asList("aParamsTail -> , expr")));
		Productions.put("93",new ArrayList<String>(Arrays.asList("addOp -> +")));
		Productions.put("94",new ArrayList<String>(Arrays.asList("addOp -> -")));
		Productions.put("95",new ArrayList<String>(Arrays.asList("addOp -> or")));
		Productions.put("96",new ArrayList<String>(Arrays.asList("relOp -> eq")));
		Productions.put("97",new ArrayList<String>(Arrays.asList("relOp -> neq")));
		Productions.put("98",new ArrayList<String>(Arrays.asList("relOp -> lt")));
		Productions.put("99",new ArrayList<String>(Arrays.asList("relOp -> gt")));
		Productions.put("100",new ArrayList<String>(Arrays.asList("relOp -> leq")));
		Productions.put("101",new ArrayList<String>(Arrays.asList("relOp -> geq")));
		Productions.put("102",new ArrayList<String>(Arrays.asList("sign -> +")));
		Productions.put("103",new ArrayList<String>(Arrays.asList("sign -> -")));
		Productions.put("104",new ArrayList<String>(Arrays.asList("multOp -> *")));
		Productions.put("105",new ArrayList<String>(Arrays.asList("multOp -> /")));
		Productions.put("106",new ArrayList<String>(Arrays.asList("multOp -> and")));
		Productions.put("107",new ArrayList<String>(Arrays.asList("assignOp -> =")));
		Productions.put("108",new ArrayList<String>(Arrays.asList("pop")));
		Productions.put("109",new ArrayList<String>(Arrays.asList("scan")));
		
		//Terminals ----------------------------------------------------------------------------------------->
		
		Terminals.add("program");
		Terminals.add(";");
		Terminals.add("class");
		Terminals.add("id");
		Terminals.add("{");
		Terminals.add("}");
		Terminals.add(":");
		Terminals.add(",");
		Terminals.add("(");
		Terminals.add(")");
		Terminals.add("float");
		Terminals.add("int");
		Terminals.add("[");
		Terminals.add("integer");
		Terminals.add("]");
		Terminals.add("sr");
		Terminals.add("if");
		Terminals.add("then");
		Terminals.add("else");
		Terminals.add("get");
		Terminals.add("put");
		Terminals.add("return");
		Terminals.add("for");
		Terminals.add("not");
		Terminals.add(".");
		Terminals.add("+");
		Terminals.add("-");
		Terminals.add("or");
		Terminals.add("eq");
		Terminals.add("geq");
		Terminals.add("gt");
		Terminals.add("leq");
		Terminals.add("lt");
		Terminals.add("neq");
		Terminals.add("*");
		Terminals.add("/");
		Terminals.add("and");
		Terminals.add("=");
		
		//First_Set----------------------------------------------------------------------------------------------------->
		
		first_Set.put("program",new ArrayList<String>(Arrays.asList("program")));
		first_Set.put(";",new ArrayList<String>(Arrays.asList(";")));
		first_Set.put("EPSILON",new ArrayList<String>(Arrays.asList("EPSILON")));
		first_Set.put("class",new ArrayList<String>(Arrays.asList("class")));
		first_Set.put("id",new ArrayList<String>(Arrays.asList("id")));
		first_Set.put("{",new ArrayList<String>(Arrays.asList("{")));
		first_Set.put("}",new ArrayList<String>(Arrays.asList("}")));
		first_Set.put(":",new ArrayList<String>(Arrays.asList(":")));
		first_Set.put(",",new ArrayList<String>(Arrays.asList(",")));
		first_Set.put("(",new ArrayList<String>(Arrays.asList("(")));
		first_Set.put(")",new ArrayList<String>(Arrays.asList(")")));
		first_Set.put("int",new ArrayList<String>(Arrays.asList("int")));
		first_Set.put("float",new ArrayList<String>(Arrays.asList("float")));
		first_Set.put("[",new ArrayList<String>(Arrays.asList("[")));
		first_Set.put("integer",new ArrayList<String>(Arrays.asList("integer")));
		first_Set.put("]",new ArrayList<String>(Arrays.asList("]")));
		first_Set.put("sr",new ArrayList<String>(Arrays.asList("sr")));
		first_Set.put("if",new ArrayList<String>(Arrays.asList("if")));
		first_Set.put("then",new ArrayList<String>(Arrays.asList("then")));
		first_Set.put("else",new ArrayList<String>(Arrays.asList("else")));
		first_Set.put("get",new ArrayList<String>(Arrays.asList("get")));
		first_Set.put("put",new ArrayList<String>(Arrays.asList("put")));
		first_Set.put("return",new ArrayList<String>(Arrays.asList("return")));
		first_Set.put("for",new ArrayList<String>(Arrays.asList("for")));
		first_Set.put("not",new ArrayList<String>(Arrays.asList("not")));
		first_Set.put(".",new ArrayList<String>(Arrays.asList(".")));
		first_Set.put("+",new ArrayList<String>(Arrays.asList("+")));
		first_Set.put("-",new ArrayList<String>(Arrays.asList("-")));
		first_Set.put("or",new ArrayList<String>(Arrays.asList("or")));
		first_Set.put("eq",new ArrayList<String>(Arrays.asList("eq")));
		first_Set.put("neq",new ArrayList<String>(Arrays.asList("neq")));
		first_Set.put("lt",new ArrayList<String>(Arrays.asList("lt")));
		first_Set.put("gt",new ArrayList<String>(Arrays.asList("gt")));
		first_Set.put("leq",new ArrayList<String>(Arrays.asList("leq")));
		first_Set.put("geq",new ArrayList<String>(Arrays.asList("geq")));
		first_Set.put("*",new ArrayList<String>(Arrays.asList("*")));
		first_Set.put("/",new ArrayList<String>(Arrays.asList("/")));
		first_Set.put("and",new ArrayList<String>(Arrays.asList("and")));
		first_Set.put("=",new ArrayList<String>(Arrays.asList("=")));
		first_Set.put("prog",new ArrayList<String>(Arrays.asList("program","EPSILON","class","int","float","id")));
		first_Set.put("classDeclaration",new ArrayList<String>(Arrays.asList("EPSILON","class")));
		first_Set.put("functionDeclaration",new ArrayList<String>(Arrays.asList("EPSILON","int","float","id")));
		first_Set.put("classDecl",new ArrayList<String>(Arrays.asList("class")));
		first_Set.put("opid",new ArrayList<String>(Arrays.asList(":","EPSILON")));
		first_Set.put("cid",new ArrayList<String>(Arrays.asList(",","EPSILON")));
		first_Set.put("varfuncdecl",new ArrayList<String>(Arrays.asList("EPSILON","int","float","id")));
		first_Set.put("varfunctail",new ArrayList<String>(Arrays.asList(";","(","EPSILON","[")));
		first_Set.put("funcDeclRep",new ArrayList<String>(Arrays.asList("EPSILON","int","float","id")));
		first_Set.put("fParams",new ArrayList<String>(Arrays.asList("EPSILON","int","float","id")));
		first_Set.put("fpTail",new ArrayList<String>(Arrays.asList("EPSILON",",")));
		first_Set.put("fParamsTail",new ArrayList<String>(Arrays.asList(",")));
		first_Set.put("arr",new ArrayList<String>(Arrays.asList("EPSILON","[")));
		first_Set.put("type",new ArrayList<String>(Arrays.asList("int","float","id")));
		first_Set.put("arraySize",new ArrayList<String>(Arrays.asList("[")));
		first_Set.put("oidsrpid",new ArrayList<String>(Arrays.asList("sr","EPSILON")));
		first_Set.put("funcBody",new ArrayList<String>(Arrays.asList("{")));
		first_Set.put("varstat",new ArrayList<String>(Arrays.asList("id","int","float","EPSILON","if","get","put","return","for")));
		first_Set.put("varstatPrime",new ArrayList<String>(Arrays.asList("id","(","EPSILON","[",".","=")));
		first_Set.put("statementPrime",new ArrayList<String>(Arrays.asList("if","get","put","return","for")));
		first_Set.put("ST",new ArrayList<String>(Arrays.asList("EPSILON","if","get","put","return","id","for")));
		first_Set.put("statement",new ArrayList<String>(Arrays.asList("if","get","put","return","id","for")));
		first_Set.put("forLoop",new ArrayList<String>(Arrays.asList("for")));
		first_Set.put("statBlock",new ArrayList<String>(Arrays.asList("{","EPSILON","if","get","put","return","id","for")));
		first_Set.put("exprTail",new ArrayList<String>(Arrays.asList("EPSILON","eq","neq","lt","gt","leq","geq")));
		first_Set.put("arithExprTAIL",new ArrayList<String>(Arrays.asList("EPSILON","+","-","or")));
		first_Set.put("termTail",new ArrayList<String>(Arrays.asList("EPSILON","*","/","and")));
		first_Set.put("factor",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		first_Set.put("factorTemp",new ArrayList<String>(Arrays.asList("id")));
		first_Set.put("factorPrime",new ArrayList<String>(Arrays.asList("(","EPSILON","[",".")));
		first_Set.put("factorTempTemp",new ArrayList<String>(Arrays.asList(".","EPSILON")));
		first_Set.put("indiceTail",new ArrayList<String>(Arrays.asList("EPSILON","[")));
		first_Set.put("indice",new ArrayList<String>(Arrays.asList("[")));
		first_Set.put("variable",new ArrayList<String>(Arrays.asList("id")));
		first_Set.put("variableTail",new ArrayList<String>(Arrays.asList("(","EPSILON","[",".")));
		first_Set.put("variablePrime",new ArrayList<String>(Arrays.asList(".","EPSILON")));
		first_Set.put("aParams",new ArrayList<String>(Arrays.asList("EPSILON","integer","float","(","not","id","+","-")));
		first_Set.put("aParamsTailRep",new ArrayList<String>(Arrays.asList("EPSILON",",")));
		first_Set.put("aParamsTail",new ArrayList<String>(Arrays.asList(",")));
		first_Set.put("addOp",new ArrayList<String>(Arrays.asList("+","-","or")));
		first_Set.put("relOp",new ArrayList<String>(Arrays.asList("eq","neq","lt","gt","leq","geq")));
		first_Set.put("sign",new ArrayList<String>(Arrays.asList("+","-")));
		first_Set.put("multOp",new ArrayList<String>(Arrays.asList("*","/","and")));
		first_Set.put("assignOp",new ArrayList<String>(Arrays.asList("=")));
		first_Set.put("funcHead",new ArrayList<String>(Arrays.asList("int","float","id")));
		first_Set.put("funcDecl",new ArrayList<String>(Arrays.asList("int","float","id")));
		first_Set.put("assignStat",new ArrayList<String>(Arrays.asList("id")));
		first_Set.put("funcDef",new ArrayList<String>(Arrays.asList("int","float","id")));
		first_Set.put("term",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		first_Set.put("arithExpr",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		first_Set.put("expr",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		first_Set.put("relExpr",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));

        //Follow_Set------------------------------------------------------------------------------------------------------->
		
		follow_Set.put("prog",new ArrayList<String>(Arrays.asList("$")));
		follow_Set.put("classDeclaration",new ArrayList<String>(Arrays.asList("program","int","float","id")));
		follow_Set.put("functionDeclaration",new ArrayList<String>(Arrays.asList("program")));
		follow_Set.put("classDecl",new ArrayList<String>(Arrays.asList("class","program","int","float","id")));
		follow_Set.put("opid",new ArrayList<String>(Arrays.asList("{")));
		follow_Set.put("cid",new ArrayList<String>(Arrays.asList("{")));
		follow_Set.put("varfuncdecl",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("varfunctail",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("funcDeclRep",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("funcDef",new ArrayList<String>(Arrays.asList("int","float","id","program")));
		follow_Set.put("funcHead",new ArrayList<String>(Arrays.asList("{")));
		follow_Set.put("funcDecl",new ArrayList<String>(Arrays.asList("int","float","id","}")));
		follow_Set.put("fParams",new ArrayList<String>(Arrays.asList(")")));
		follow_Set.put("fpTail",new ArrayList<String>(Arrays.asList(")")));
		follow_Set.put("fParamsTail",new ArrayList<String>(Arrays.asList(",",")")));
		follow_Set.put("arr",new ArrayList<String>(Arrays.asList(";",",",")")));
		follow_Set.put("type",new ArrayList<String>(Arrays.asList("id")));
		follow_Set.put("arraySize",new ArrayList<String>(Arrays.asList("[",";",",",")")));
		follow_Set.put("oidsrpid",new ArrayList<String>(Arrays.asList("(")));
		follow_Set.put("funcBody",new ArrayList<String>(Arrays.asList(";")));
		follow_Set.put("varstat",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("varstatPrime",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("statementPrime",new ArrayList<String>(Arrays.asList("if","get","put","return","id","for","}")));
		follow_Set.put("ST",new ArrayList<String>(Arrays.asList("}")));
		follow_Set.put("statement",new ArrayList<String>(Arrays.asList("if","get","put","return","id","for",";","else","}")));
		follow_Set.put("forLoop",new ArrayList<String>(Arrays.asList("if","get","put","return","id","for",";","else","}")));
		follow_Set.put("assignStat",new ArrayList<String>(Arrays.asList(")",";")));
		follow_Set.put("statBlock",new ArrayList<String>(Arrays.asList(	";","else")));
		follow_Set.put("expr",new ArrayList<String>(Arrays.asList(",",";",")")));
		follow_Set.put("exprTail",new ArrayList<String>(Arrays.asList(",",";",")")));
		follow_Set.put("relExpr",new ArrayList<String>(Arrays.asList(";")));
		follow_Set.put("arithExpr",new ArrayList<String>(Arrays.asList("]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("arithExprTAIL",new ArrayList<String>(Arrays.asList("]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("term",new ArrayList<String>(Arrays.asList("+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("termTail",new ArrayList<String>(Arrays.asList("+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("factor",new ArrayList<String>(Arrays.asList("*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("factorTemp",new ArrayList<String>(Arrays.asList("*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("factorPrime",new ArrayList<String>(Arrays.asList("*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("factorTempTemp",new ArrayList<String>(Arrays.asList("*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("indiceTail",new ArrayList<String>(Arrays.asList(".","=","*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("indice",new ArrayList<String>(Arrays.asList("[",".","=","*","/","and","+","-","or","]",")","eq","neq","lt","gt","leq","geq",";",",")));
		follow_Set.put("variable",new ArrayList<String>(Arrays.asList("=",")")));
		follow_Set.put("variableTail",new ArrayList<String>(Arrays.asList("=",")")));
		follow_Set.put("variablePrime",new ArrayList<String>(Arrays.asList("=",")")));
		follow_Set.put("aParams",new ArrayList<String>(Arrays.asList(")")));
		follow_Set.put("aParamsTailRep",new ArrayList<String>(Arrays.asList(")")));
		follow_Set.put("aParamsTail",new ArrayList<String>(Arrays.asList(",",")")));
		follow_Set.put("addOp",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		follow_Set.put("relOp",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		follow_Set.put("sign",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		follow_Set.put("multOp",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
		follow_Set.put("assignOp",new ArrayList<String>(Arrays.asList("integer","float","(","not","id","+","-")));
	

	}

	
	public static void build_parsetable ()
	{// Putting the productions in place of the rule number
		
		for (int i=1;i<Parse_Table.length;i++)
		{
			for (int j=1;j<Parse_Table[i].length;j++)
			{
				Parse_Table[i][j]=Productions.get(Parse_Table[i][j]).toString();
				
			}
			
		}
	}
	
	public static String top()
	{
		return Parse_Stack.peek();
	}
	
	public static String ParseT_getProduction(String x,String a)
	{
		int NT_row = 0,NT_col = 0,T_row = 0,T_col = 0;
		for (int i=0;i<Parse_Table.length;i++)
		{
			for (int j=0;j<Parse_Table[i].length;j++)
			{
				if (Parse_Table[i][j].equals(x) )
				{
					NT_row=i;
					NT_col=j;
					
				}
				 if (Parse_Table[i][j].equals(a.split(" ")[0]))
				{
					T_row=i;
					T_col=j;
				}					
			}// end - inner for loop
		}// end - outer for loop
		
		//System.out.println(Parse_Table[NT_row][T_col]);
		return Parse_Table[NT_row][T_col];
	}
	
	public static void buildDerivation(String x,String production) throws IOException
	{
		//System.out.println("Building Derivation-------------->");
		production=(String) production.subSequence(1, production.length()-1);
		String[] arr=production.split(" ");
		int index_to_push_at=-1;
		boolean first_match=false;
		for (int i=0;i<derivation.size();i++)
		{
			if(derivation.get(i).contains(x) && first_match==false)
			{
				index_to_push_at=i;
				derivation.remove(i);
				first_match=true;// indicates the first match was found
			}
		}
		index_to_push_at--;
		for (int i=2;i<arr.length;i++)
		{
			if (!arr[i].equals("EPSILON") && !arr[i].contains("SEMANTIC"))
			{
				++index_to_push_at;
				derivation.add(index_to_push_at, arr[i]);
			}
		}
		//System.out.println("derivation Updated to:"+derivation);
	}
	
	public static void skipErrors(String stack_top) throws IOException
	{			
	    if ( !(stack_top.split(" ")[0].equals("Terminal_error")) ) // The skipError call from NOn-terminal related Error condition
		{
	    	System.out.println("Syntax error at line:"+a.split(" ")[2]+" in input: "+a);
	    	syntatical_error.write("Syntax error at line:"+a.split(" ")[2]+" in input: "+a);
	    	final_error_file.add("Syntax error at line:"+a.split(" ")[2]+" in input: "+a);
	    	syntatical_error.newLine();
	    	String error_type=ParseT_getProduction(stack_top,a);
			if (error_type.equals("[pop]"))
			{
			   Parse_Stack.pop();
		    }
		   else //if (error_type.equals("[scan]"))
		   {
			
			  while(error_type.equals("[scan]"))
			  {
				System.out.println("error.. scan for"+stack_top+a);
				a=my_lex.nextToken();
				System.out.println("Reading new Token --> a:"+a);
				if (a.equals("$"))
					return;	
				error_type=	ParseT_getProduction(stack_top,a);
			  }
	       }
		} // end of loop for NON-Terminal Error check
	    else if (stack_top.split(" ")[0].equals("Terminal_error")) // The skipError call from terminal related Error condition
	    {
	    	stack_top=top();// re-constructing the stack_top variable - to remove the string Terminal_error added during its calling
	    	System.out.println("The stack top is:"+stack_top+"the input is:"+a);
	    	System.out.println("Reporting error for a missing expected terminal "+stack_top+" in the input string before line "+a.split(" ")[2]+" and input:"+a);
	    	syntatical_error.write("Syntax error for a missing expected terminal "+stack_top+" in the input string before line "+a.split(" ")[2]+" and input:"+a);
	    	final_error_file.add("Syntax error for a missing expected terminal "+stack_top+" in the input string before line "+a.split(" ")[2]+" and input:"+a);
	    	syntatical_error.newLine();
	    	System.out.println("Reading the next entry from the parsing stack");
	    	Parse_Stack.pop();
	    	return;
	    }
	    	 
	}
	
	public static boolean parse() throws IOException
	{
		Parse_Stack.push("$");
		Parse_Stack.push("prog");
		derivation.push("prog");
		derive.write(derivation.elementAt(0));
		derive.newLine();
		a=my_lex.nextToken();
		
				
		while(top()!="$")
		{
			//System.out.println("---");
			//System.out.println("Stack is:"+Parse_Stack);
			//System.out.println("derivation is:"+ derivation);
					
			String x= top();// x holds the top of the stack
			
			if (x.split(" ")[0].contains("SEMANTIC")) // Semantic action performing on the grammar
			{
				String action=Parse_Stack.pop();
				//System.out.println("Popped out semantic action from Parse_Stack:"+action);
				
				ast.semantic_Action(action);
								
			}
			else // not a semantic action- accept for terminals i.e makeNode
			{	
				if (Terminals.contains(x))
			   {				
					terminal_Semaction.terminal_action(a); // Performing semantic action for parsing terminals
				  			   
				  //------------------------------------------------------------------------------		   
				  if (x.equals(a.split(" ")[0]))
				  {		
					
					String c=Parse_Stack.pop();
					//System.out.println("Popped out:"+c);
					a=my_lex.nextToken();
					
				  }
				  else
				  {
					   x="Terminal_error "+x;
					   skipErrors(x);
					   error=true;
				  }
			   }// end of if - Terminals
			   else  // A check on the Non-terminal and formula matching 
			   {
				   //System.out.println("x="+x+" "+"a="+a.split(" ")[0]);
				   String getProduction=ParseT_getProduction(x,a);
				
				//----------------------logic for Parse_Stack maintenance--->
				   //System.out.println("Production is :"+getProduction);
				   if (!getProduction.equals("[pop]") && !getProduction.equals("[scan]"))
				   {
					   String c=Parse_Stack.pop();
					   //inverseRHSMultiplePush(TT[x,a]);
					   //Below is the logic for stack maintenance
					   String arr []=((String) getProduction.subSequence(1, ((getProduction).length()-1))).split(" ");
					   for (int i=arr.length-1;i>1;i--)
					   {
						  if (arr[i].matches("\\w+") 
							  || arr[i].matches("\\;") 
							  || arr[i].matches("\\{") 
							  || arr[i].matches("\\}") 
							  ||arr[i].matches("\\$")
							  || arr[i].matches("\\*") 
							  || arr[i].matches("\\+") 
							  || arr[i].matches("\\d") 
							  || arr[i].matches("\\(") 
							  || arr[i].matches("\\)")
							  || arr[i].matches("\\:")
							  || arr[i].matches("\\,")
							  || arr[i].matches("\\[")
							  || arr[i].matches("\\]")
							  || arr[i].matches("\\.")
							  ||arr[i].matches("\\-")
							  ||arr[i].matches("\\/")
							  ||arr[i].matches("\\="))
						    {
							     if (!arr[i].equals("EPSILON"))
								 {    
					                 Parse_Stack.push(arr[i]);
								  }							
						    }
					     }
					 
					    //Below is the logic to build Derivation
					    buildDerivation(x,getProduction);
					    for (int i=0;i<derivation.size();i++)
					    {
						   derive.write(derivation.elementAt(i)+" ");
					    }
					    derive.newLine();				 
				   }
				   else
				  {
					 skipErrors(x);
					 error=true;
				  }				
								
			  }//end of else - this is for non-terminals
					    
			}
		}//end of - while loop
		
		if (top().equals("$") && a.split(" ")[0].equals("$"))
	     {
		    System.out.println("parsing complete here!");
		    if (error!=true)
		    derive.write("success");
		    derive.newLine();
	     }
		
		
		System.out.println("checking the value of error:"+error);
		if (!(a.equals("$")) || (error ==true)) 
	        return(false); 
	    else 
	        return(true);
		
	}//end of - parse()
	
	public static void display_parsetable()
	{
		for (int i=0;i<Parse_Table.length;i++)
		{
			for (int j=0;j<Parse_Table[i].length;j++)
			{
				System.out.print(Parse_Table[i][j]+"|");
				
			}
			System.out.println();			
		}
		
	}
	
	public static void symtab_typecheck()
	{
		
		System.out.println("==TREE CREATED=======");

		System.out.println("==PRINTING TREE======");
		ASTPrinterVisitor ASTPVisitor = new ASTPrinterVisitor("AST.ast");
		AST_T.Prog.accept(ASTPVisitor);		
		System.out.println("==TREE PRINTED=======");
							
		System.out.println("==VISITING TREE WITH SYMBOL TABLE VISITOR======");
	    SymTabCreationVisitor STCVisitor  = new SymTabCreationVisitor("SymbolTable.symtab"); 
		 AST_T.Prog.accept(STCVisitor);		 
		System.out.println("==TREE VISITED WITH SYMBOL TABLE VISITOR=======");
		
			
		System.out.println("==VISITING TREE WITH TYPE CHECKING VISITOR======");
		TypeCheckingVisitor TCVisitor = new TypeCheckingVisitor(); 
		AST_T.Prog.accept(TCVisitor);	
		System.out.println("==TREE VISITED WITH TYPE CHECKING VISITOR=======");
			
	  		
		ASTPrinterVisitor simran = new ASTPrinterVisitor("TypeCheckedAST.ast");
		AST_T.Prog.accept(simran);
		if (!m_outputfilename.isEmpty()) {
			File file = new File(m_outputfilename);
			try (PrintWriter out = new PrintWriter(file)){out.println(AST_T.Prog.m_symtab);}
			catch(Exception e){e.printStackTrace();}
		}
				
		ComputeMemSizeVisitor CMSVisitor  = new ComputeMemSizeVisitor("ComputedMemSizeSymbolTable.symtab");
		AST_T.Prog.accept(CMSVisitor);
		
		System.out.println("==Building Tag File======");
		TagBasedCodeGenerationVisitor  CGVisitor   = new TagBasedCodeGenerationVisitor("TagFile.tags.m");
		AST_T.Prog.accept(CGVisitor);
		 
	}
	
	public static void main(String [] args) throws IOException
   {
	   derive= new BufferedWriter(new FileWriter("derivation_ParseT.txt"));
	   syntatical_error=new BufferedWriter(new FileWriter("syntax_error_ParseT.txt"));
	   AST_Buffer=new BufferedWriter(new FileWriter("AST_SemanticStack.txt"));
	   symtab_file=new BufferedWriter(new FileWriter("SymbolTable.txt"));
	   Semantic_err=new BufferedWriter(new FileWriter("SemanticError.txt"));
	   ferror=new BufferedWriter(new FileWriter("final_error_file.txt"));
	   
	   //------------------------------ RUN/UNCOMMENT BELOW FOR TEST CASES TO RUN----------->
	         // my_lex = new Lexer("src/LexParser/Test_Case1"); 
	           my_lex = new Lexer("src/LexParser/Test_Case2");
	          //my_lex = new Lexer("src/LexParser/Test_Case2.txt");
	         
	     
	   //------------------------------------------------------------------------------------>
	     
	   build_parsetable();
	  //display_parsetable();
	   boolean result=parse();
	   if(result==false)
		{
			System.out.println("error - end");
		}
	   derive.flush();
	   syntatical_error.flush();
	   System.out.println("---------------------------");
	   System.out.println("Semantic Stack is:");
		for (int i=0;i<AST_T.Sem_Stack.size();i++)
		{
			System.out.print(AST_T.Sem_Stack.get(i));
			AST_Buffer.write(AST_T.Sem_Stack.get(i).toString()+"------>"+((Node)AST_T.Sem_Stack.get(i)).getChildren().toString());
			AST_Buffer.newLine();
			System.out.print("|");
			AST_Buffer.write("|");
		}
		AST_Buffer.newLine();
		System.out.println();
		AST_Buffer.flush();
		
		symtab_typecheck(); // building symbol Table and Type check
		Semantic_err.flush();
		for (int i=0;i<final_error_file.size();i++)
		{
			ferror.write(final_error_file.get(i));
			ferror.newLine();
			ferror.flush();
		}
		
   }
}
