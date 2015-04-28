package disease.utils.wikipedia.model.interfaces;

import org.sweble.wikitext.lazy.parser.DefinitionDefinition;
import org.sweble.wikitext.lazy.parser.DefinitionList;
import org.sweble.wikitext.lazy.parser.DefinitionTerm;

public interface IDescriptorVisitor {

	public void visit(DefinitionList dl) ;
	public void visit(DefinitionTerm dt);
	public void visit(DefinitionDefinition dd);
	
}
