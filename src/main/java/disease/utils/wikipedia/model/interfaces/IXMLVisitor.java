package disease.utils.wikipedia.model.interfaces;


import org.sweble.wikitext.lazy.parser.XmlElement;
import org.sweble.wikitext.lazy.parser.XmlElementEmpty;
import org.sweble.wikitext.lazy.parser.XmlElementOpen;
import org.sweble.wikitext.lazy.preprocessor.TagExtension;
import org.sweble.wikitext.lazy.preprocessor.XmlComment;
import org.sweble.wikitext.lazy.utils.XmlAttribute;
import org.sweble.wikitext.lazy.utils.XmlAttributeGarbage;
import org.sweble.wikitext.lazy.utils.XmlCharRef;
import org.sweble.wikitext.lazy.utils.XmlEntityRef;

public interface IXMLVisitor extends ITextVisitor {

	public void visit(XmlElement e);
	public void visit(XmlElementEmpty a);
	public void visit(XmlElementOpen a);
	public void visit(TagExtension a);
	public void visit(XmlAttribute a);
	public void visit(XmlAttributeGarbage g);
	public void visit(XmlCharRef ref);
	public void visit(XmlEntityRef ref);
	public void visit(XmlComment ref);
}
