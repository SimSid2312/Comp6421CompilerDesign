package SymbolTable;

import java.util.Vector;

public class VarEntry extends SymTabEntry{

	public VarEntry(String p_kind, String p_type, String p_name, Vector<Integer> p_dims){
		super(p_kind, p_type, p_name, null);
		m_dims = p_dims;
		//System.out.println("VarEntry"+p_name+m_dims);
		if(p_dims!=null )
		{
			String dim=p_dims.toString().replace(", ", ",");
			my_dims = dim;
		}

		
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
