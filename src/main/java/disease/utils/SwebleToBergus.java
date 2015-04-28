package disease.utils;


import org.sweble.wikitext.lazy.encval.IllegalCodePoint;
import org.sweble.wikitext.lazy.parser.ExternalLink;
import org.sweble.wikitext.lazy.parser.InternalLink;
import org.sweble.wikitext.lazy.parser.Url;
import org.sweble.wikitext.lazy.parser.XmlElementEmpty;
import org.sweble.wikitext.lazy.parser.XmlElementOpen;
import org.sweble.wikitext.lazy.preprocessor.Redirect;
import org.sweble.wikitext.lazy.preprocessor.TagExtension;
import org.sweble.wikitext.lazy.preprocessor.Template;
import org.sweble.wikitext.lazy.preprocessor.TemplateParameter;
import org.sweble.wikitext.lazy.utils.XmlAttribute;
import de.fau.cs.osr.ptk.common.ast.Text;
import disease.utils.wikipedia.model.WikiLink;
import disease.utils.wikipedia.model.WikiTemplate;
import nu.xom.Element;
import nu.xom.*;

public class SwebleToBergus {

	public static WikiLink convert(InternalLink n) {
		return new WikiLink(n);
	}
	public static WikiLink convert(ExternalLink n) {
		return new WikiLink(n);
	}
	public static WikiLink convert(Redirect n) {
		return new WikiLink(n);
	}
	public static WikiLink convert(Url u){
		return new WikiLink(u);
	}
	public static String convert(Text text) {
               
		return text.getContent();
	}
	
	
	//To XOM Elements
	public static Element convert(XmlElementEmpty elem) {
		return new Element(elem.getName());
	}
	public static Element updateElement(Element e, XmlElementEmpty a) {
		e.appendChild(convert(a));
		return e;
	}
	public static Element convert(XmlElementOpen elem) {
		return new Element(elem.getName());
	}
	public static Element updateElement(Element e, XmlElementOpen a) {
		e.appendChild(convert(a));
		return e;
	}
	public static Element convert(TagExtension elem) {
		return new Element(elem.getName());
	}
	public static Element updateElement(Element e, TagExtension a) {
		e.appendChild(convert(a));
		return e;
	}
	public static Element updateElement(Element e, XmlAttribute a) {
		String name = a.getName();
		String val = a.getValue().get(0).toString();
		e.addAttribute(new Attribute(name,val));
		return e;
	}
	public static WikiTemplate.Parameter convert(TemplateParameter tp) {
		return new WikiTemplate.Parameter(tp);
	}
	public static WikiTemplate convert(Template t) {
		return new WikiTemplate(t);
	}
	
}
