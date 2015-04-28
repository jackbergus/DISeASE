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
import disease.utils.AbbreviationExpansion;

/**
 *
 * @author vasistas
 */
public class QuickAssoc {
    
    private QuickAssoc() {
        this.dcc = AbbreviationExpansion.getInstance();
    }
    private AbbreviationExpansion dcc = null;
    private static QuickAssoc self = null;
    
    public static QuickAssoc getInstance() {
        if (self==null) {
            self = new QuickAssoc();
        }
        return self;
    }

    public WikiPageView idToPage(ICD9CMCode id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setIdToPage(ICD9CMCode id, WikiPageView p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void learn_assoc_fromAnnotation(Annotator a) {
        //TODO: use dcc
        //alla fine:
        dcc.save();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Returns the corrector learned by the previous training phase
     * @return 
     */
    public AbbreviationExpansion getLearnedCorrector() {
        return this.dcc;
    }
    
}
