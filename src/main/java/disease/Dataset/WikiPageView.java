/*
 * Copyright (C) 2015 Giacomo Bergami
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package disease.Dataset;

import disease.Phase.Annotator;
import disease.ontologies.ICD9CMCode;
import disease.utils.wikipedia.WikipediaSingleton;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Giacomo Bergami
 */
public class WikiPageView {
    
    private List<ICD9CMCode> codeList;
    private String title;
    private String stemmedTitle;
    private String paragraphs;
    
    private List<String> links;
    public List<String> getRawLinks() {
        return this.links;
    }
    
    public WikiPageView(String title, String stemmed, List<ICD9CMCode> codelist, String content, List<String> links) {
        this.title = title;
        this.codeList = codelist;
        this.stemmedTitle = stemmed;
        paragraphs = content;
        this.links = links;
    }
    
    public WikiPageView(String expanded, List<ICD9CMCode> codelist, String content, List<String> links) {
        this(expanded,expanded,codelist,content,links);
    }
    
    
    
    private transient final WikipediaSingleton reference = WikipediaSingleton.getInstance();
    
    /**
     * Returns the view over the page titles that a page possesses. 
     * @return 
     */
    public List<WikiPageView> getLinkedPages() {
        //Improvements: we could have been follow the redirections. This is not done in this current implementation
        return links.stream()
                .filter((String t) -> reference.containsPage(t))
                .map((String t) -> reference.createPageView(t))
                .collect(Collectors.toList());
    }
    
    public List<WikiPageView> getExpandedLinkedPages() {
        //Improvements: we could have been follow the redirections. This is not done in this current implementation
        return links.stream()
                .filter((String t) -> reference.containsPage(t))
                .map((String t) -> reference.createExpandedPageView(t))
                .collect(Collectors.toList());
    }
    
    /** Given the wiki page, it returns the paragraphs that support the information contained in the annotated text
     * @param a         Annotated text
     * @return          Subchunks of the Page that support the main idea */
    public Set<String> getSupportParagraph(Annotator a) {
        String cleaned = a.returnCleanedDocument();
        //TODO: do I have to do the ranking??
        return null;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getStemmedTitle() {
        return stemmedTitle;
    }
    
    public List<ICD9CMCode> getCodeList() {
        return this.codeList;
    }
   

    public String getContent() {
        return this.paragraphs;
    }
    
    public String[] getParagraphs() {
        return this.paragraphs.split("\n\n");
    }
    
}
