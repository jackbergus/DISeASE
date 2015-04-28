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
package disease.datatypes.hypergraphdb.DataNodes;

import disease.Dataset.interfaces.IWikiPageView;
import disease.ontologies.ICD9CMCode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author Giacomo Bergami
 */
public class GraphWikiPage extends Entity {

    
    private transient IWikiPageView wpv_extended = null;
    //private transient WikipediaSingleton ws = null;
    //private transient GraphDB g = GraphDB.getInstance();
    
    
    //private transient IWikiPageView wpv_notextended = null;
    /*public List<String> getWikiLinks() {
        return this.wpv_notextended.getRawLinks();
    }*/
    
    public static List<String> getWikiLinks(IWikiPageView wpv_notextended) {
        return wpv_notextended.getRawLinks();
    }
    
    /////////////////////////////////////////
    private String wikiTitle;
    public String getWikiTitle() {
        return wikiTitle;
    }
    /*public void setWikiTitle(String title) {
        this.wikiTitle = title;
    }*/
    
    /////////////////////////////////////////
    private String wikiStemmedTitle;
    public String getWikiStemmedTitle() {
        return this.wikiStemmedTitle;
    }
    /*public void setWikiStemmedTitle(String title) {
        this.wikiStemmedTitle = title;
    }*/
    
    /////////////////////////////////////////
    private String wikiExpandedTitle;
    public String getWikiExpandedTitle() {
        return this.wikiExpandedTitle;
    }
    /*public void setWikiExpandedTitle(String t) {
        this.wikiExpandedTitle = t;
    }*/
    
    /////////////////////////////////////////
    private String contentStemmed;
    public String getContentStemmed() {
        return this.contentStemmed;
    }
    /*public void setContentStemmed(String c) {
        this.contentStemmed = c;
    }*/
    
    /////////////////////////////////////////
    private String contentExpanded;
    public String getContentExpanded() {
        return this.contentExpanded;
    }
    /*public void setContentExpanded(String s) {
        this.contentExpanded = s;
    }*/
    
    public static List<ICD9CMCode> getICD9CMCodes(IWikiPageView wpv_notextended) {
        return wpv_notextended.getCodeList().stream().filter((ICD9CMCode t) -> (!t.isEmpty())).collect(Collectors.toList());
    }
    
    public GraphWikiPage(long pos, Object[] array) {
        super(pos,array);
        this.wikiTitle = (String)array[0];
        this.wikiStemmedTitle = (String)array[1];
        this.wikiExpandedTitle = (String)array[2];
        this.contentStemmed = (String)array[3];
        this.contentExpanded = (String)array[4];
    }
    
    public static Object[] createArrayArgs(IWikiPageView notextended, IWikiPageView extended) {
        return new Object[]{notextended.getTitle(),notextended.getStemmedTitle(),extended.getTitle(),notextended.getContent(),extended.getContent()};
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphWikiPage))
            return false;
        GraphWikiPage other = (GraphWikiPage)o;
        return (contentExpanded.equals(other.contentExpanded) && 
                contentStemmed.equals(other.contentStemmed) && 
                this.wikiExpandedTitle.equals(other.wikiExpandedTitle) &&
                this.wikiStemmedTitle.equals(other.wikiStemmedTitle) &&
                this.wikiTitle.equals(other.wikiTitle));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.wikiTitle);
        hash = 67 * hash + Objects.hashCode(this.wikiStemmedTitle);
        hash = 67 * hash + Objects.hashCode(this.wikiExpandedTitle);
        hash = 67 * hash + Objects.hashCode(this.contentStemmed);
        hash = 67 * hash + Objects.hashCode(this.contentExpanded);
        return hash;
    }
   
    
}
