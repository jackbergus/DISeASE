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
package disease.Phase.cleaner;

import disease.Phase.cleaner.blog.ItalianStopWords;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.Version;


/**
 *
 * @author vasistas
 */
public class CleanItalian implements Cleanser {

    private Version v;
    private ItalianAnalyzer an;
    public CleanItalian(Version matchVersion) {
        this.v = matchVersion;
        CharArraySet stopSet = new CharArraySet(this.v,
            (Arrays.asList(ItalianStopWords.getStopWords())),
            true);
        this.an = new ItalianAnalyzer(this.v,stopSet);
    }
    
    private CleanItalian() {
        this.v = Version.LUCENE_CURRENT;
        CharArraySet stopSet = new CharArraySet(this.v,
            (Arrays.asList(ItalianStopWords.getStopWords())),
            true);
        this.an = new ItalianAnalyzer(this.v,stopSet);
    }
    
    private static CleanItalian self = null;
    public static CleanItalian getInstance() {
        if (self==null)
            self = new CleanItalian();
        return self;
    }
    
    /**
     * @author  Gian Luca Farina Perseu (http://21-style.com/blog/2011/09/apache-lucene-e-la-gestione-degli-apostrofi/)
     * @param reader   Input a stream reader (as required by Lucene)
     * @return 
     */
    public TokenStream tokenStream(Reader reader) {
        try {
            return this.an.tokenStream(null, reader);
            //return result;
        } catch (Throwable ex) {
            Logger.getLogger(CleanItalian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static void main(String[] args) {
        
        String s = "viva la peppa con la peppona peppina";
        StringReader stream = new StringReader(s);
        CleanItalian ci = new CleanItalian();
        TokenStream tokenStream =  ci.tokenStream(stream);
        
        // Iteration over tokens of strings
        Attribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                
                System.out.println((cattr.toString()));
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException ex) {
            Logger.getLogger(CleanItalian.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * The String cleaner provide some tokenization and stemming which is
     * language-dependant, and returns the original string as a array of
     * tokenized strings
     * @param dirty
     * @return 
     */
    @Override
    public String[] cleanedStringAsArray(String dirty) {
        return (this.cleanedStringList(dirty).toArray(new String[]{}));
    }
    
    /**
     * The String cleaner provide some tokenization and stemming which is
     * language-dependant, and returns the original string as a array of
     * tokenized strings
     * @param dirty
     * @return 
     */
    @Override
    public ArrayList<String> cleanedStringList(String dirty) {
        StringReader stream = new StringReader(dirty.toLowerCase());
        TokenStream tokenStream = tokenStream(stream);
        Attribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        ArrayList<String> al = new ArrayList<>();
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                al.add(cattr.toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException ex) {
            Logger.getLogger(CleanItalian.class.getName()).log(Level.SEVERE, null, ex);
        }
        return al;
    }
    
    @Override
    public String cleanedString(String dirty) {
        StringReader stream = new StringReader(dirty.toLowerCase());
        TokenStream tokenStream = tokenStream(stream);
        Attribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        StringBuilder sb = new StringBuilder();
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                sb.append(cattr.toString()).append(" ");
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException ex) {
            Logger.getLogger(CleanItalian.class.getName()).log(Level.SEVERE, null, ex);
        }
        String str = sb.toString();
        if (str.length() > 0 && str.charAt(str.length()-1)==' ') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
    
}
