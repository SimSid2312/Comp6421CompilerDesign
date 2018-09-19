package SymbolTable;

import java.util.HashMap;
import java.util.Vector;

public class ClassInheritanceEntry extends SymTabEntry{

	public ClassInheritanceEntry(String p_name,  HashMap<String,SymTab> p_dims){
		super(p_name,p_dims);
	}
		
	public String toString(){
		return 	String.format("%-12s" , "| " + m_kind) +
				String.format("%-12s" , "| " + m_inherlist.keySet())+
				String.format("%-12s" , "| " + "") +
				String.format("%-8s"  , "| " + m_size) + 
				String.format("%-8s"  , "| " + m_offset)
		        + "|";
				
	}
}
