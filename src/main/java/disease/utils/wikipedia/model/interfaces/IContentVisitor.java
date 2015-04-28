package disease.utils.wikipedia.model.interfaces;

import org.sweble.wikitext.engine.*;
import org.sweble.wikitext.lazy.encval.*;
import org.sweble.wikitext.lazy.parser.*;
import org.sweble.wikitext.lazy.preprocessor.*;
import de.fau.cs.osr.ptk.common.ast.*;

public interface IContentVisitor {

	public void visit(AstNode astNode);
	public void visit(NodeList l);
	public void visit(Page page);
	public void visit(MagicWord n);
	public void visit(Text text);
	public void visit(Italics n);
	public void visit(Bold n);
	public void visit(Whitespace n);
	public void visit(Paragraph p);
	public void visit(SemiPre sp);
	public void visit(SemiPreLine line);
	public void visit(Section s);
	public void visit(XmlElement e);
	public void visit(TagExtension e);
	public void visit(XmlElementEmpty e);
	public void visit(XmlElementOpen e);
	public void visit(XmlElementClose e);

	/////
	public void visit(DefinitionList n);
	public void visit(Enumeration n);
	public void visit(Itemization n);
	public void visit(ExternalLink link);
	public void visit(Url url);
	public void visit(InternalLink n);
	public void visit(HorizontalRule rule);
	public void visit(Signature sig);
	public void visit(Redirect n);
	public void visit(IllegalCodePoint n);
	public void visit(Template tmpl);
	
}
