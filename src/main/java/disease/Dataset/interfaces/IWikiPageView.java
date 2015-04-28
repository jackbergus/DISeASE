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
package disease.Dataset.interfaces;

import disease.ontologies.ICD9CMCode;
import java.util.List;

/**
 *
 * @author Giacomo Bergami
 */
public interface IWikiPageView {
    /**
     * Returns the view over the page titles that a page possesses. 
     * @return 
     */
    public List<IWikiPageView> getLinkedPages() ;
    public List<IWikiPageView> getExpandedLinkedPages();
    public List<String> getRawLinks();
    
    /** Given the wiki page, it returns the paragraphs that support the information contained in the annotated text
     * @param a         Annotated text
     * @return          Subchunks of the Page that support the main idea */
    public String getTitle() ;
    public String getStemmedTitle();
    public List<ICD9CMCode> getCodeList();
    public String getContent();
    public String[] getParagraphs() ;
    
}
