package disease.utils.wikipedia.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.sweble.wikitext.lazy.preprocessor.Template;
import org.sweble.wikitext.lazy.preprocessor.TemplateArgument;
import org.sweble.wikitext.lazy.preprocessor.TemplateParameter;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import disease.configurations.DefaultConfs;
import disease.utils.wikipedia.printer.NullWriter;

public class WikiTemplate  {
	
    private boolean hasmet = false;
    private String hasICD9 = "";
	private String name;
	private HashMap<String,String> arg_val = new HashMap<>();
	public static class Argument {
		private String name;
		private boolean reference;
		
		protected Argument(String name, boolean isRef) {
			this.name = name;
			this.reference = isRef;
		}
		
		public boolean isReference() {
			return reference;
		}
		
		public String getParam() {
			return name;
		}
		
	};
	
	public static class Parameter {
		
		private String param;
		public Parameter(TemplateParameter p) {
			param = DefaultConfs.nodeToString(p.getName());
		}
		public Parameter(String s) {
			this.param = s;
		}
		public Parameter(int i) {
			this.param = Integer.toString(i);
		}
		public String get() {
			return param;
		}
		@Override
		public String toString() {
			return param;
		}
		
	}
	private ArrayList<Argument> args = new ArrayList<>();
	
	public boolean put (String name, String val) {
            boolean toret = false;
                if (val.toLowerCase().equals("medicina")) {
                    //System.out.println(this.name+" OK~~");
                    toret = true;
                }
		Argument a = new Argument(val,true);
		args.add(a);
		arg_val.put(name, val);
                return toret;
	}
	public void put (String param) {
		Argument a = new Argument(param,false);
		args.add(a);
	}
	public boolean put (TemplateArgument arg) {
		
		if (arg.getHasName()) {
                    Argument a = new Argument(DefaultConfs.nodeToString(arg.getValue()),false);
                    args.add(a);
                    return false;
		} else {
			return put(DefaultConfs.nodeToString(arg.getName()),
				DefaultConfs.nodeToString(arg.getValue()));
		}
		
	}
//	@Deprecated
//	public void visit(TemplateArgument arg) {
//		put(arg);
//	}
	
	public WikiTemplate(Template t) {
		this.name = DefaultConfs.nodeToString(t.getName());
                //System.out.println(this.name);
                
		for (AstNode arg : t.getArgs()) {
			if (arg instanceof TemplateArgument) {
			    if (this.put((TemplateArgument)arg))
                                    hasmet = true;
                            if (DefaultConfs.nodeToString(((TemplateArgument)arg).getName()).equals("icd9")) {
                                this.hasICD9 = DefaultConfs.nodeToString(((TemplateArgument)arg).getValue());
                            }
			}
		}
	}
	
        public String getICD9() {
            return this.hasICD9;
        }
        
	public String get(int index) {
		if ((index>=0) && (args.size()>index)) {
			Argument a = args.get(index);
			if (a.isReference())
				return arg_val.get(a.getParam());
			else
				return a.getParam();
		} else
			return null;
	}
	
	public String getName(int index) {
		if ((index>=0) && (args.size()>index)) {
			return args.get(index).getParam();
		} else
			return null;
	}
	
	public String get(String key) {
		return arg_val.get(key);
	}
        
        public boolean contains_value(String key) {
            for (Argument x : args) {
                if (x == null) {
                     System.err.println("Error: null element");
                    System.exit(0);
                }
                if (x.getParam()==null) {
                    System.err.println("Error: null");
                    System.exit(0);
                }
                if (key==null) {
                    System.err.println("Error: null key");
                    System.exit(0);
                }
                if (x.getParam().equals(key))
                    return true;
            }
            return false;
        }
	
	public String get(Parameter tp) {
		String key = tp.get();
		try {
			int keyint = Integer.parseInt(key);
			return get(keyint);
		} catch (Throwable t) {
			return get(key);
		}
	}
	
	public String getTemplateName() {
		return name;
	}
        
        public boolean hasMetMedicine() {
            return hasmet;
        }
	
}
