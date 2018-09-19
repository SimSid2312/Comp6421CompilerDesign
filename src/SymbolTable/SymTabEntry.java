package SymbolTable;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SymTabEntry {
	public String          m_kind       = null;
	public String          m_type       = null;
	public String          m_name       = null;
	public int             m_size       = 0;
	public int             m_offset     = 0;
	public SymTab          m_subtable   = null;
	
	public Vector<Integer> m_dims       = new Vector<Integer>();
	public String my_dims       =null;
	//Below Vectors added by me-->
	public String m_fParams       = null;
	
	public HashMap<String,SymTab> m_inherlist=null;
	public Vector<String> p_fdeclparam  = new Vector<String>();
	public SymTabEntry() {}
	
	public SymTabEntry(String p_kind, String p_type, String p_name, SymTab p_subtable){
		m_kind = p_kind;
		m_type = p_type;
		m_name = p_name;
		m_subtable = p_subtable;
	}
	
	//added by me - for inheritList
	public SymTabEntry (String p_kind,HashMap<String,SymTab> p_inherlist)
	{
		m_inherlist=new HashMap <String,SymTab>();
		m_kind=p_kind;
		m_inherlist=p_inherlist;
	}
	
	
}