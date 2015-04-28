package disease.utils.wikipedia.model;

import java.util.ArrayList;

import org.sweble.wikitext.lazy.parser.DefinitionDefinition;
import org.sweble.wikitext.lazy.parser.DefinitionList;
import org.sweble.wikitext.lazy.parser.DefinitionTerm;

import de.fau.cs.osr.ptk.common.ast.NodeList;
import disease.utils.wikipedia.model.interfaces.*;
import disease.utils.wikipedia.printer.*;

public class WikiDefinition extends de.fau.cs.osr.ptk.common.PrinterBase implements IDescriptorVisitor {

	private boolean start=true;
	private DefinitionTerm name = null;
	private DefinitionDefinition desc = null;
	private ArrayList<WikiDefinitionItem> descrs = new ArrayList<>();
	
	protected WikiDefinition(DefinitionList dl) {
		super(new NullWriter());
//		System.out.println("+++"+DefaultConfs.nodeToString(dl.getContent()));
		this.go(dl);
	}

	@Override
	public void visit(DefinitionList dl) {
		if (start = true) {
			start = false;
			if (!dl.getContent().isEmpty()) {
				iterate(dl.getContent());
			}
			visit((DefinitionTerm)null);
		} else {
			System.err.println("ERror: unexpected Definition Sublist");
		}
	}

	@Override
	public void visit(DefinitionTerm dt) {
		if (name==null && desc ==null) {
			name = dt;
		} else {
			descrs.add(new WikiDefinitionItem(name,desc));
			name = null;
			desc = null;
			name = dt;
		}
	}

	@Override
	public void visit(DefinitionDefinition dd) {
		if (name!=null) {
			
			if (desc!=null) {
				//System.out.println(desc.get(0).getClass().getName());
				desc.get(0).addAll((NodeList)dd.getContent());
			} else
				desc = dd;
			
		}
	}

}
