package disease.utils.wikipedia.model;




import disease.utils.SwebleToBergus;
import org.sweble.wikitext.engine.Page;
import org.sweble.wikitext.lazy.encval.IllegalCodePoint;
import org.sweble.wikitext.lazy.parser.Bold;
import org.sweble.wikitext.lazy.parser.DefinitionList;
import org.sweble.wikitext.lazy.parser.Enumeration;
import org.sweble.wikitext.lazy.parser.ExternalLink;
import org.sweble.wikitext.lazy.parser.HorizontalRule;
import org.sweble.wikitext.lazy.parser.InternalLink;
import org.sweble.wikitext.lazy.parser.Italics;
import org.sweble.wikitext.lazy.parser.Itemization;
import org.sweble.wikitext.lazy.parser.MagicWord;
import org.sweble.wikitext.lazy.parser.Paragraph;
import org.sweble.wikitext.lazy.parser.Section;
import org.sweble.wikitext.lazy.parser.SemiPre;
import org.sweble.wikitext.lazy.parser.SemiPreLine;
import org.sweble.wikitext.lazy.parser.Signature;
import org.sweble.wikitext.lazy.parser.Url;
import org.sweble.wikitext.lazy.parser.Whitespace;
import org.sweble.wikitext.lazy.parser.XmlElement;
import org.sweble.wikitext.lazy.parser.XmlElementClose;
import org.sweble.wikitext.lazy.parser.XmlElementEmpty;
import org.sweble.wikitext.lazy.parser.XmlElementOpen;
import org.sweble.wikitext.lazy.preprocessor.Redirect;
import org.sweble.wikitext.lazy.preprocessor.TagExtension;
import org.sweble.wikitext.lazy.preprocessor.Template;
import org.sweble.wikitext.lazy.preprocessor.XmlComment;
import org.sweble.wikitext.lazy.utils.XmlAttribute;
import org.sweble.wikitext.lazy.utils.XmlAttributeGarbage;
import org.sweble.wikitext.lazy.utils.XmlCharRef;
import org.sweble.wikitext.lazy.utils.XmlEntityRef;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import disease.utils.wikipedia.model.interfaces.IContentVisitor;
import disease.utils.wikipedia.model.interfaces.IXMLVisitor;
import disease.configurations.DefaultConfs;
import disease.utils.wikipedia.printer.NullWriter;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Class used in the Wikipedia Document creation for adding the existing elements. For the
 * nested nodes, the classed is used only as a temporary constructor/visitor 
 * @author vasistas
 *
 */
public class XMLVisitor extends de.fau.cs.osr.ptk.common.PrinterBase implements IXMLVisitor, IContentVisitor {
	
	private Element xmlelement = null;
	private IContentVisitor uber = null;
	
	public XMLVisitor(XmlElementEmpty elem, IContentVisitor sup) {
		super(new NullWriter());
		this.uber = sup;
		this.go(elem);
	}
	public XMLVisitor(XmlElement elem, IContentVisitor sup) {
		super(new NullWriter());
		this.uber = sup;
		this.go(elem);
	}
        
	
	public XMLVisitor(XmlElementOpen elem, IContentVisitor sup) {
		super(new NullWriter());
		this.uber = sup;
                this.go(elem);
	}
	
	public XMLVisitor(TagExtension elem, IContentVisitor sup) {
		super(new NullWriter());
		this.uber = sup;
		this.go(elem);
	}
        @Override
	public void visit(XmlElementEmpty a) {
		if (xmlelement!=null)
			xmlelement.appendChild(new XMLVisitor(a,uber).get());
		else {
			this.xmlelement= new Element(a.getName());
			iterate(a.getXmlAttributes());
		}
	}
        @Override
	public void visit(XmlElementOpen a) {
		if (xmlelement!=null)
			xmlelement.appendChild(new XMLVisitor(a,uber).get());
		else {
			this.xmlelement= new Element(a.getName());
			iterate(a.getXmlAttributes());
		}
	}
        @Override
	public void visit(TagExtension a) {
		if (xmlelement!=null)
			this.xmlelement.appendChild(new XMLVisitor(a,uber).get());
		else {
			this.xmlelement= new Element(a.getName());
			iterate(a.getXmlAttributes());
			this.xmlelement.appendChild(a.getBody());
		}
	}
	public void visit(XmlAttribute a) {
		String name = a.getName().replaceAll("\\P{Alpha}", "");
		String val = "true";
		if (a.getValue()!=null && a.getValue().size()>0)
			val = DefaultConfs.nodeToString(a.getValue());
		if (name.equals("xmlns"))
			name = "icsmlnamespace";
		this.xmlelement.addAttribute(new Attribute(name,val));
	}
	public void visit(XmlAttributeGarbage g)
	{
		//No garbage wanted
	}
	
	
	
	//Not defined as XML elements
	public void visit(XmlCharRef ref) 
	{
		xmlelement.appendChild("&#"+ref.getCodePoint()+";");
	}
	public void visit(XmlEntityRef ref) 
	{
		xmlelement.appendChild("&"+ref.getName()+";");
	}
	public void visit(Text text) 
	{
		xmlelement.appendChild(SwebleToBergus.convert(text));
	}
	
	
	public Element get() {
		return xmlelement;
	}

	@Override
	public void visit(XmlComment ref) {
		return;
	}
	@Override
	public void visit(XmlElement e) {
		if (xmlelement!=null)
			xmlelement.appendChild(new XMLVisitor(e,uber).get());
		else {
			this.xmlelement= new Element(e.getName());
			iterate(e.getXmlAttributes());
			if (!e.getEmpty()) {
				iterate(e.getBody());
			}
		}
	}
	
	/////////////////////////
	public void visit(Italics it) {
		iterate(it.getContent());
	}
	public void visit(Bold it) {
		iterate(it.getContent());
	}
	public void visit(Whitespace it) {
		iterate(it.getContent());
	}
	@Override
	public void visit(AstNode astNode) {
		this.xmlelement.appendChild(DefaultConfs.nodeToString(astNode));
	}
	@Override
	public void visit(NodeList l) {
		iterate(l);
	}
	@Override
	public void visit(Page page) {
		System.err.println("Error: Page in XML");
	}
	@Override
	public void visit(MagicWord n) {
		this.xmlelement.appendChild(n.getWord());
	}
	@Override
	public void visit(Paragraph p) {
		iterate(p.getContent());
	}
	@Override
	public void visit(SemiPre sp) {
		iterate(sp.getContent());
	}
	@Override
	public void visit(SemiPreLine line) {
		iterate(line.getContent());
	}
	@Override
	public void visit(Section s) {
		this.uber.visit(s);
	}
	@Override
	public void visit(XmlElementClose e) {
		return;
	}
	@Override
	public void visit(DefinitionList n) {
		this.xmlelement.appendChild(DefaultConfs.nodeToString(n));
	}
	@Override
	public void visit(Enumeration n) {
		this.xmlelement.appendChild(DefaultConfs.nodeToString(n));
	}
	@Override
	public void visit(Itemization n) {
		this.xmlelement.appendChild(DefaultConfs.nodeToString(n));
	}
	@Override
	public void visit(ExternalLink link) {
		return;
	}
	@Override
	public void visit(Url url) {
		return;
	}
	@Override
	public void visit(InternalLink n) {
		return;
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
		return;
	}
	@Override
	public void visit(IllegalCodePoint n) {
		return;
	}
	@Override
	public void visit(Template tmpl) {
		return;
	}
}
