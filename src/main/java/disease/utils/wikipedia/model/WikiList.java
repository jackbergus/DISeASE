package disease.utils.wikipedia.model;


import java.util.ArrayList;
import java.util.Iterator;

import org.sweble.wikitext.lazy.parser.Enumeration;
import org.sweble.wikitext.lazy.parser.EnumerationItem;
import org.sweble.wikitext.lazy.parser.Itemization;
import org.sweble.wikitext.lazy.parser.ItemizationItem;

import de.fau.cs.osr.ptk.common.ast.Text;
import disease.utils.wikipedia.model.interfaces.*;
import disease.configurations.DefaultConfs;
import disease.utils.wikipedia.printer.NullWriter;


public class WikiList  extends de.fau.cs.osr.ptk.common.PrinterBase implements IListVisitor {
	
	private boolean visited = false;
	public enum Kind {
		ORDERED,
		UNORDERED
	};
	private ArrayList<String> el = new ArrayList<>();
	
	private WikiList.Kind wlk;
	public WikiList(WikiList.Kind k) {
		super(new NullWriter());
		this.wlk = k;
	}
	public WikiList(Enumeration e) {
		super(new NullWriter());
		this.wlk = Kind.ORDERED;
		this.go(e);
	}
	public WikiList(Itemization e) {
		super(new NullWriter());
		this.wlk = Kind.UNORDERED;
		this.go(e);
	}
	
	public void add(EnumerationItem i) {		
		el.add(DefaultConfs.nodeToString(i.getContent()));
	}
	public void add(ItemizationItem i) {
		el.add(DefaultConfs.nodeToString(i.getContent()));
	}

	///////////////////////////////////////////////////////
	public void visit(ItemizationItem i) {
		el.add(DefaultConfs.nodeToString(i.getContent()));
	}
	public void visit(EnumerationItem i) {
		add(i);
	}
	///////////////////////////////////////////////////////
	
	public Kind getKind() {
		return wlk;
	}
	
	@Override
	public Iterator<String> iterator() {
		return el.iterator();
	}
	@Override
	public void visit(Text visit) {
		System.err.println("ERROR: TEXT VISITING");
	}
	@Override
	public void visit(Itemization i) {
		if (!visited) {
			visited = true;
			iterate(i.getContent());
		}
	}
	@Override
	public void visit(Enumeration i) {
		if (!visited) {
			visited = true;
			iterate(i.getContent());
		}
	}

}
