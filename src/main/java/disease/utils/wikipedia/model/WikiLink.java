package disease.utils.wikipedia.model;


import org.sweble.wikitext.lazy.parser.ExternalLink;
import org.sweble.wikitext.lazy.parser.InternalLink;
import org.sweble.wikitext.lazy.parser.Url;
import org.sweble.wikitext.lazy.preprocessor.Redirect;

import disease.configurations.DefaultConfs;

public class WikiLink {
	
	private String text;
	private String url;
	private boolean internal;
	private boolean redirect;
	
	public WikiLink(String url) {
		this.text = url;
		this.url = url;
		this.internal = false;
		this.redirect = false;
	}
	
	public WikiLink(InternalLink n) {
		//Setting the link name
		text = n.getPrefix();
		if (n.getTitle().getContent().isEmpty()) {
			text = text + n.getTarget();
		} else {
			text = text + DefaultConfs.nodeToString(n.getTitle().getContent());
		}
		text = text + n.getPostfix();
		
		//Adding the url
		url = n.getTarget();
		this.redirect = false;
	}
	
	public WikiLink(ExternalLink n) {
		//Setting the link name
		url = n.getTarget().getProtocol()+":"+n.getTarget().getPath();
		if (n.getTitle().isEmpty())
			text = url;
		else
			text = DefaultConfs.nodeToString(n.getTitle());
		
		this.internal = false;
		this.redirect = false;
	}
	public WikiLink(Redirect r) {
		this.redirect = true;
		this.internal = true;
		this.text = r.getTarget();
		this.url = this.text;
	}
	
	public String getText() {
		return text;
	}
	public String getURL() {
		return url;
	}
	public boolean isInternal() {
		return internal;
	}
	public boolean isRedirect() {
		return redirect;
	}
	
	public WikiLink(Url u){
		this(u.getProtocol()+":"+u.getPath());
	}

}
