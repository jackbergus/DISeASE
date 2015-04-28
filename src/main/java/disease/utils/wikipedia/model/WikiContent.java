package disease.utils.wikipedia.model;

import disease.utils.wikipedia.model.interfaces.ITemplateResolver;

import disease.utils.wikipedia.model.interfaces.IContentVisitor;
import disease.configurations.DefaultConfs;
import disease.utils.wikipedia.printer.NullWriter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.Page;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.lazy.LinkTargetException;
import org.sweble.wikitext.lazy.encval.IllegalCodePoint;
import org.sweble.wikitext.lazy.parser.*;
import org.sweble.wikitext.lazy.preprocessor.*;
       

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WikiContent extends de.fau.cs.osr.ptk.common.PrinterBase implements IContentVisitor, ITemplateResolver {
	
	public enum Kind {
		TEXT,
		ILLEGALCODE,
		MAGICWORD,
		SECTION
	};
	private Kind wck;
	
        private boolean hasmet = false;
	private Map<String,Method> cache = new TreeMap<>(); //method cache
	private String title;
	private String content;
	private boolean istitle;
	private boolean first = true;
	private ArrayList<String> magic_words = new ArrayList<>();
	//private ArrayList<WikiContent> sections = new ArrayList<>();
	//private ArrayList<XMLVisitor> forest = new ArrayList<>();
	private ArrayList<WikiList> lists = new ArrayList<>();
	private ArrayList<WikiLink> links = new ArrayList<>();
	private ArrayList<WikiLink> redirect = new ArrayList<>();
	private HashMap<String,WikiTemplate> pageTemplate = new HashMap<>();
	private ArrayList<WikiDefinition> definitions = new ArrayList<>();
	private int level = 0;
        private String ICD9 = "";
	
	public ITemplateResolver getTemplateResolver() {
		return this;
	}
	
	public WikiContent(Kind k, String title, String content) {
		super(new NullWriter());
		this.title = title;
		if (title!=null)
			this.istitle = true;
		this.content = content;
		this.wck = k;
	}
	public WikiContent(Kind k, String content) {
		super(new NullWriter());
		this.content = content;
		this.istitle = false;
		this.wck = k;
	}
	
	
	
	public WikiContent(String content) {
		this(Kind.TEXT,content);
	}
        
        
        public WikiContent init(String title, String content, long id) {
            
		this.title = title.replaceAll("\n", "");
		Compiler compiler = new Compiler(DefaultConfs.config);
		PageTitle pageTitle = null;
		try {
			pageTitle = PageTitle.make(DefaultConfs.config, this.title);
		} catch (LinkTargetException e) { 
                    System.out.println("Link Target Error:"+title);
                }
		PageId pageId = new PageId(pageTitle, id);
		CompiledPage cp = null;
		try {
			cp = compiler.postprocess(pageId, content, null);
                        Page pg = cp.getPage();
                        this.istitle = true;
                        this.wck = Kind.SECTION;
                        this.go(pg);
		} catch (Throwable e) {	
                    System.out.println("Wrongful page:"+title);
                    //e.printStackTrace();
                    return null;
                }
                return this;
            
        }
	
	public WikiContent() {
		super(new NullWriter());
		
	}
	
	public String getTitle() {
		return title;
	}
	public boolean hasTitle() {
		return istitle;
	}
	public String getTextContent() {
		return content;
	}
	public int getLevel() {
		return level;
	}
	
	@Override 
	public String toString() {
		return content;
	}
	public Kind getKind() {
		return this.wck;
	}
	@Override
	public void visit(AstNode astNode) {
		//Nodo astratto --> 
	}
	@Override
	public void visit(NodeList l) {
		self_iterate(l);
	}
	@Override
	public void visit(Page page) {
		first = false;
		Iterator<AstNode> ni = page.getContent().iterator();
		AstNode n;
		//Scan all the text before the first section
		NodeList preSec = new NodeList();
		NodeList posSec = new NodeList();
		boolean gate = true;
		while (ni.hasNext()) {
			n = ni.next();
			if (n instanceof Section) {
				gate = false;
			} else if ((n instanceof Template)) {
				visit(n);
			}
			if (gate)
				preSec.add(n);
			else
				posSec.add(n);
		}
		
		//Grasp all the introductory text
		this.content = DefaultConfs.nodeToString(preSec,this.getTemplateResolver());
		//Grasp all the links & marks
		self_iterate(preSec);
		//Deepen the subject for all the other parts
		self_iterate(posSec);
	}
	@Override
	public void visit(MagicWord n) {
		this.magic_words.add(n.getWord());
	}
	public Iterator<String> getMagicWords() {
		return magic_words.iterator();
	}
	@Override
	public void visit(Text text) {
		// TODO Auto-generated method stub
	}
	@Override
	public void visit(Italics n) {
		self_iterate(n.getContent());
	}
	@Override
	public void visit(Bold n) {
		self_iterate(n.getContent());
	}
	@Override
	public void visit(Whitespace n) {
		//self_iterate(n.getContent());
	}
	
	public static boolean isParagraphEmpty(Paragraph p)
	{
		if (!p.isEmpty())
		{
			List<AstNode> l = (List<AstNode>) p.getAttribute("blockLevelElements");
			if (l == null || p.size() - l.size() > 0)
				return false;
		}
		return true;
	}
        
        
	public void visit(ImageLink il) {
            
        }
	@Override
	public void visit(Paragraph p) {
            if (!isParagraphEmpty(p)) {
                self_iterate(p.getContent());
            }
	}
	@Override
	public void visit(SemiPre sp) {
		self_iterate(sp.getContent());
	}
	@Override
	public void visit(SemiPreLine line) {
		self_iterate(line.getContent());
	}
        private void self_iterate(NodeList nl) {
            
            NodeList n = new NodeList();
            for (AstNode x : nl) {
                if (x!=null) {
                    try {
                        if (cache.containsKey(x.getNodeTypeName()))
                            cache.get(x.getNodeTypeName()).invoke(this, x);
                        else {
                            Method m = WikiContent.class.getDeclaredMethod("visit", x.getClass());
                            cache.put(x.getNodeTypeName(), m);
                            m.invoke(this, x);
                        }
                    } catch (NoSuchMethodException  ex) {
                        //Skip: if not implemented, it is not useful
                    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
                        Logger.getLogger(WikiContent.class.getName()+" with "+x.getNodeTypeName()).log(Level.SEVERE, null, ex2);
                        System.err.println(x.getNodeTypeName());
                        System.err.println("Cache got:");
                        System.err.println(cache.keySet());
                        System.exit(0);
                    }
                }
            }
            
        }
	
	@Override
	public void visit(Section section) {
		if (first) {
			this.first = false;
			this.title = DefaultConfs.nodeToString(section.getTitle());
			this.level = section.getLevel();
			this.content = DefaultConfs.nodeToString(section.getBody());
		} else {
			//TODO:extract infos
		}
                self_iterate(section.getBody());
	}
	@Override
	public void visit(XmlElement e) {
		//this.forest.add(new XMLVisitor(e,this));
	}
	@Override
	public void visit(TagExtension e) {
		//this.forest.add(new XMLVisitor(e,this));
	}
	@Override
	public void visit(XmlElementEmpty e) {
		//this.forest.add(new XMLVisitor(e,this));
	}
	@Override
	public void visit(XmlElementOpen e) {
		//this.forest.add(new XMLVisitor(e,this));
	}
	@Override
	public void visit(XmlElementClose e) {
		return;
	}
	@Override
	public void visit(DefinitionList n) {
		this.definitions.add(new WikiDefinition(n));
	}
	@Override
	public void visit(Enumeration n) {
		this.lists.add(new WikiList(n));
	}
	@Override
	public void visit(Itemization n) {
		this.lists.add(new WikiList(n));
	}
	@Override
	public void visit(ExternalLink link) {
		this.links.add(new WikiLink(link));
	}
	@Override
	public void visit(Url url) {
		this.links.add(new WikiLink(url));
	}
	@Override
	public void visit(InternalLink n) {
		this.links.add(new WikiLink(n));
	}
        public List<String> getRelatedLinks() {
            ArrayList<String> al = new ArrayList<>();
            for (WikiLink w : this.links) {
                al.add(w.getText());
            }
            return al;
        }
	@Override
	public void visit(HorizontalRule rule) {
		return;
	}
	@Override
	public void visit(Signature sig) {
		return;
	}
	@Override
	public void visit(Redirect n) {
		this.redirect.add(new WikiLink(n));
	}
	@Override
	public void visit(IllegalCodePoint n) {
		//System.err.println("Skipped Illegal Code Point");
	}
	@Override
	public void visit(Template tmpl) {
		WikiTemplate t = new WikiTemplate(tmpl);
                if (t.hasMetMedicine()) {
                    this.hasmet = true;
                }
                //System.out.println(t.getTemplateName() + "");
		this.pageTemplate.put(t.getTemplateName(), t);
                this.ICD9 += t.getICD9();
	}
        
        public String getICD9() {
            return this.ICD9;
        }
        
        public boolean hasMetMedicine() {
            return this.hasmet;
        }
        
	@Override
	public String getValueByTemplateParam(String name_var) {
		for (String key : this.pageTemplate.keySet()) {
			WikiTemplate t = this.pageTemplate.get(key);
			String get = t.get(name_var);
			if (get!=null)
				return get;
		}
		return null;
	}
        
        public boolean hasTemplate(String key) {
            return this.pageTemplate.containsKey(key.toLowerCase());
        }
        
        public WikiTemplate getTemplate(String key) {
            return this.pageTemplate.get(key.toLowerCase());
        }
        
        

}

