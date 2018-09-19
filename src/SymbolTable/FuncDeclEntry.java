package SymbolTable;

import java.util.Vector;

public class FuncDeclEntry extends SymTabEntry{

	public FuncDeclEntry(String p_kind, String p_type, String p_name, Vector<String> p_fparam){
		super(p_kind, p_type, p_name, null);
		String dim=p_fparam.toString().replace(", ", ",");
		m_fParams = dim;

		p_fdeclparam=p_fparam;
	}
		
	public String toString(){
		return 	String.format("%-12s" , "| " + m_kind) +
				String.format("%-12s" , "| " + m_name) + 
				String.format("%-12s"  , "| " + m_type) + 
               // String.format("%-12"  , "| " + m_dims) + 
				String.format("%-8s"  , "| " + m_size) + 
				String.format("%-8s"  , "| " + m_offset)
		        + "|";
	}
}
