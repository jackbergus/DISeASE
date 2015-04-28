package disease.utils.wikipedia.model.interfaces;

import org.sweble.wikitext.lazy.parser.Enumeration;
import org.sweble.wikitext.lazy.parser.EnumerationItem;
import org.sweble.wikitext.lazy.parser.Itemization;
import org.sweble.wikitext.lazy.parser.ItemizationItem;

public interface IListVisitor extends Iterable<String>, ITextVisitor {
	
	public void visit(Itemization i);
	public void visit(Enumeration i);
	public void visit(ItemizationItem i);
	public void visit(EnumerationItem i);

}
