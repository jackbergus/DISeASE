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

import disease.ontologies.ICD9CMCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.tweetsmining.model.graph.database.Entity;

/**
 * - code:       original ICD9CM Code
 * - (extended)Description: the official ICD9CM description
 * - (expanded)Suggestion(s): something that could be used to refine the title
 * @author Giacomo Bergami
 */
public class ICD9CM extends Entity {
    private ICD9CMCode code;
    public ICD9CMCode getCode() {
        return code;
    }
    /*public void setCode(ICD9CMCode c) {
        this.code = c;
    }*/
    
    private String stemmedDescription;
    public String getStemmedDescription() {
        return this.stemmedDescription;
    }
    /*public void setStemmedDescription(String s) {
        this.stemmedDescription = s;
    }*/
    
    private String expandedDescription;
    public String getExpandedDescription() {
        return this.expandedDescription;
    }
    /*public void setExpandedDescription(String s) {
        this.expandedDescription = s;
    }*/
    
    private List<String> suggestions;
    public List<String> getSuggestions() {
        return this.suggestions;
    }
    /*public void setSuggestions(List<String> s) {
        this.suggestions = s;
    }
    public void addSuggestion(String s) {
        suggestions.add(s);
    }*/
    
    private List<String> expandedSuggestions;
    public List<String> getExpandedSuggestions() {
        return this.expandedSuggestions;
    }
    /*public void setExpandedSuggestions(List<String> s) {
        this.expandedSuggestions = s;
    }
    public void addExpandedSuggestion(String s) {
       
        expandedSuggestions.add(s);
    }*/
    
    
    private List<String> detailments;
    public List<String> getDetailments() {
        return this.detailments;
    }
    /*public void setDetailments(List<String> s) {
        this.detailments = s;
    }
    public void addDetailments(String s) {
        detailments.add(s);
    }*/
    
    private List<String> expandedDetailments;
    public List<String> getExpandedDetailments() {
        return this.expandedDetailments;
    }
    /*public void setExpandedDetailments(List<String> s) {
        this.expandedDetailments = s;
    }
    public void addExpandedDetailments(String s) {
        expandedDetailments.add(s);
    }*/
    
    /*public ICD9CM() {this.detailments = new ArrayList<>();
    this.expandedDetailments = new ArrayList<>();
this.expandedSuggestions = new ArrayList<>();
this.suggestions = new ArrayList<>();
  }*/
    
    
    /*public ICD9CM(ICD9CMCode code, String ed, String sd, List<String> sug, List<String> esug, List<String> det, List<String> edet) {
        this.code = code;
        this.expandedDescription = ed;
        this.stemmedDescription = sd;
        this.suggestions = sug;
        this.expandedSuggestions = esug;
        this.detailments = det;
        this.expandedDetailments = edet;
    }*/
    
    public ICD9CM(long pos, Object[] arg) {
        super(pos, arg);
        this.code = (ICD9CMCode) arg[0];
        this.expandedDescription = (String) arg[1];
        this.stemmedDescription = (String) arg[2];
        this.suggestions = (List<String>) arg[3];
        this.expandedSuggestions = (List<String>) arg[4];
        this.detailments = (List<String>) arg[5];
        this.expandedDetailments = (List<String>) arg[6];
    }
    
    /**
     * Creates a new ICD9CMCode
     * @param code     Original Code
     * @param ed       Extended Description
     * @param sd       Stemmed Description
     * @param sug      Suggestions
     * @param esug      Expanded Suggestions
     * @param det
     * @param edet
     * @return 
     */
    public static Object[] createArrayArg(ICD9CMCode code, String ed, String sd, List<String> sug, List<String> esug, List<String> det, List<String> edet) {
        return new Object[]{code,ed,sd,sug,esug,det,edet};
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ICD9CM other = (ICD9CM) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.stemmedDescription, other.stemmedDescription)) {
            return false;
        }
        if (!Objects.equals(this.expandedDescription, other.expandedDescription)) {
            return false;
        }
        if (!Objects.equals(this.suggestions, other.suggestions)) {
            return false;
        }
        if (!Objects.equals(this.expandedSuggestions, other.expandedSuggestions)) {
            return false;
        }
        if (!Objects.equals(this.detailments, other.detailments)) {
            return false;
        }
        if (!Objects.equals(this.expandedDetailments, other.expandedDetailments)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.code);
        hash = 97 * hash + Objects.hashCode(this.stemmedDescription);
        hash = 97 * hash + Objects.hashCode(this.expandedDescription);
        hash = 97 * hash + Objects.hashCode(this.suggestions);
        hash = 97 * hash + Objects.hashCode(this.expandedSuggestions);
        hash = 97 * hash + Objects.hashCode(this.detailments);
        hash = 97 * hash + Objects.hashCode(this.expandedDetailments);
        return hash;
    }

    
}
