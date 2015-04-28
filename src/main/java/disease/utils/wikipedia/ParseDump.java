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
package disease.utils.wikipedia;

import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giacomo Bergami
 */
public class ParseDump {
    
    
    
    public void parseWiki(){
        // initialize Semaphore object
        Semaphore semaphore = new Semaphore(1); 
        //pass it to callback handler so it can release semaphore latter
        MedicalWikiRepositoryCreator handler = new MedicalWikiRepositoryCreator(semaphore);
        try {
            //This is the async operation, right?
            WikiXMLSAXParser.parseWikipediaDump("/watson/itwiki-20150121-pages-articles-multistream.xml", handler );
            semaphore.acquire();
            WikipediaSingleton.store();
            //wait until a permit is available (when semaphore.release() is called)
            //semaphore.acquire(); //this throw InterruptedException, please handle it else where
        } catch (Exception ex) {
            Logger.getLogger(ParseDump.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
    }
    
    public static void main(String args[]) {
        new ParseDump().parseWiki();
    }
    
}
