/*
 * Copyright (C) 2015 vasistas
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

import disease.ontologies.ICD9CMCode;
import disease.utils.AStringUtils;
import disease.utils.wikipedia.model.WikiContent;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author vasistas
 */
class MedicalWikiRepositoryCreator implements PageCallbackHandler 
{
    private Semaphore semaphore;
    private WikipediaSingleton ws = WikipediaSingleton.getInstance();
    
    /**
     * Initialization for the parsing phase. Semaphores are required due tu
     * concurrency issues
     * @param semaphore 
     */
    public MedicalWikiRepositoryCreator(Semaphore semaphore){
        this.semaphore = semaphore;
    }
    //Override
    //This is the callback
    @Override
    public void process(WikiPage page)
    {
        try {
            this.semaphore.acquire(); //only one process can enter
        } catch (InterruptedException ex) {
            Logger.getLogger(MedicalWikiRepositoryCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        String pt = "";
        try{
            WikiPage p = new WikiPage();
            p.setWikiText(page.getWikiText());
            String title = (page.getTitle().replace("\n", ""));
            p.setTitle(title);
            pt = page.getTitle();
            WikiContent wc = new WikiContent();
            wc = wc.init(title,page.getWikiText(),0);
            if (wc==null)
                return;
            if (wc.hasMetMedicine()) {
                ws.putWikiToContent(title, wc.getTextContent());
                ws.putTitleToMulti(title, wc.getRelatedLinks());
                String icd = AStringUtils.deleteWhitespace(wc.getICD9());
                if (icd.length()!=0) {
                    //System.out.println(icd.length()+"--"+icd);
                    if (icd.contains(",")) { //multiple codes
                        List<ICD9CMCode> list = new LinkedList<>();
                        for (String c : icd.split(",")) {
                            c = c.replace("icd9=", "");
                            ICD9CMCode cd = new ICD9CMCode(c);
                            if (cd.toString().length()>0) {
                                list.add(cd);
                                ws.putCodeToWikiTitle(cd, title);
                                //System.out.println( c+ " is "+title);
                            }
                        }
                        if (!list.isEmpty())
                            ws.putWikiToCode(title, list);
                    } else {
                        icd = icd.replace("icd9=", "");
                        ICD9CMCode cd = new ICD9CMCode(icd);
                            if (cd.toString().length()>0) {
                                ws.putCodeToWikiTitle(cd, title);
                                //System.out.println( cd+ " e "+title);
                                List<ICD9CMCode> list = new LinkedList<>();
                                list.add(cd);
                                ws.putWikiToCode(title, list);
                            }
                    }
                } 
            }
            
            //System.out.println(wc.toString());
        } catch (Throwable t) { 
            System.out.println("Error on" + pt);
            //t.printStackTrace();
            System.exit(1);
        } finally{
            semaphore.release();
        }
    }

    //private Map<ICD9CMCode,String> code_to_wikipagetitle = new HashMap<>();
    //private Map<String,List<ICD9CMCode>> wikipagetitle_to_codes = new TreeMap<>();
    //private Map<String,String> wikipagetitle_to_content = new TreeMap<>();
    //private Map<String,List<String>> wikipagetitle_to_wikipagetitle = new TreeMap<>();
    
}