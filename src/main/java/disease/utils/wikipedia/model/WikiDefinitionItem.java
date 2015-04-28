package disease.utils.wikipedia.model;

import disease.configurations.DefaultConfs;
import org.sweble.wikitext.lazy.parser.DefinitionDefinition;
import org.sweble.wikitext.lazy.parser.DefinitionTerm;

public class WikiDefinitionItem {

	private WikiContent name = null;
	private WikiContent descr = null;
	
	public WikiDefinitionItem(DefinitionTerm dt, DefinitionDefinition dd) {
		this.name = new WikiContent(DefaultConfs.nodeToString(dt.getContent()));
		if (dd!=null)
			this.descr = new WikiContent(DefaultConfs.nodeToString(dd.getContent()));
		else
			this.descr = new WikiContent("");
	}
	
	public WikiContent getDefinition() {
		return this.name;
	}
	public WikiContent getDescription() {
		return this.descr;
	}
	
	
}
