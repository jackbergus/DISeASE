/*
 * Copyright (C) 2015 Giacomo Bergami mailto:giacomo@openmailbox.org
 *                    Alexander Pollok
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
package disease.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author Giacomo Bergami mailto:giacomo@openmailbox.org
 */
public class PDFToText {
    
    public static String processFile(String filename) {
        return processFile(filename,false,0);
    }
    
    
    /** @author Alexander Pollok
     * 
     * @param filename
     * @param setendpage
     * @param endpage
     * @return 
     */
    public static String processFile(String filename, boolean setendpage, int endpage) {
            StringWriter sb = new StringWriter();
		
		 try {
                        PDDocument pd;
                        BufferedWriter wr;
		         File input = new File(filename);
		         pd = PDDocument.load(input);
		         PDFTextStripper stripper = new PDFTextStripper();
		         stripper.setStartPage(1); //inclusive
		         //when test mode is active only read the first 2 pages (much faster for debugging	 
                         
                         if (setendpage)
                            stripper.setEndPage(endpage+1); //Recognizes only diseases
                         else
                            stripper.setEndPage(pd.getNumberOfPages()); //Recognizes even what you could do
                            
                        
                        stripper.writeText(pd, sb);
                        //bufReader = new BufferedReader(new StringReader(sb.toString()));
                        

		 } catch (Exception e){
		         e.printStackTrace();
		 }
        return sb.toString();
    }
    
}
