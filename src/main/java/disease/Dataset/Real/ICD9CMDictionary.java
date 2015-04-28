/*
 * Copyright (C) 2015 Alexander Pollok
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

package disease.Dataset.Real;

/**
 * This class parses the contents of the ICD9CM-it.pdf and stores them into a Tree
 *
 * @author Alexander Pollok <alexander.pollok@dlr.de>
 */

import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.interfaces.FileDictionary;
import disease.Dataset.interfaces.WordList;
import disease.datatypes.serializabletree.Tree;
import disease.datatypes.serializabletree.TreeNode;
import disease.similarities.*;
import disease.utils.DictionaryType;
import disease.utils.PDFToText;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

import disease.Phase.Annotator;
import disease.Phase.cleaner.*;
import disease.ontologies.ICD9CMCode;


public class ICD9CMDictionary {
    private static String FILENAME = "data"+File.separator+"ICD9CM-it.pdf"; //path and filename of pdf without the ".pdf" extension
    private static String FIRST_STEP_FILENAME = FILENAME + ".txt";
    private static String SECOND_STEP_FILENAME = FILENAME + "_cleaned.txt";
    private static String LAST_STEP_FILENAME = FILENAME + "_cleaned2.txt";
    private static String DICTIONARY_SIMILARITY = "data"+File.separator+"dictionary_similarity.ser";
    
    private static CleanItalian ci = CleanItalian.getInstance(); //Retrieving a singleton of the stemmer
    
    private static ICD9CMDictionary self = null;
    private static String TREE_SER = "data"+File.separator+"icd9_tree.ser";
    private static String TREE_EXP_SER = "data"+File.separator+"icd9_tree_exp.ser";
    private ICD9CMDictionary() {}

    public void init() {
        if (!(new File(FIRST_STEP_FILENAME).exists() && new File(SECOND_STEP_FILENAME).exists() &&
                new File(LAST_STEP_FILENAME).exists() && new File(DICTIONARY_SIMILARITY).exists())) {
            readIcdPdf();
            cleanUpTxt();
            cleanUpTxt2();
        }
    }
    
	public static void main(String[] args) {
	
		ICD9CMDictionary init = new ICD9CMDictionary();
                init.init();
		
                
		Tree<String,ICD9CMCode> tree = getTree();
        String test_word = "abito, abitudine/vomito";
		test_word = ci.cleanedString(test_word);
       ICD9CMCode testtree = tree.nodes.get(test_word).getValue();
		System.out.println("for example, the ICD of "+test_word+" is " + testtree);
		
                Iterator<TreeNode<String,ICD9CMCode>> c = tree.iterator("condition");
                int i=0;
                while (c.hasNext()) {
                    c.next();
                    i++;
                }
                System.out.println("visited nodes:"+(i-1));
		
		Tree<String,ICD9CMCode> treeexp = getTreeExpanded();
        test_word = "gonnorea";
		test_word = ci.cleanedString(test_word);
    	Similarity lcs = LowConfidenceRank.getInstance();
    	WordList meddict =  OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.ITALIAN_MEDICAL_DICTIONARY); 
    	//System.out.println(test_word);	    	
    	Annotator a = new Annotator(test_word);
        a.identitySemantics();
        a.approximateExpansion(meddict, lcs); 
        test_word = a.returnCleanedDocument().trim(); // Obtaining the expanded string
    	//System.out.println(test_word);
        testtree = treeexp.nodes.get(test_word).getValue();
		System.out.println("for example, the ICD of "+test_word+" is " + testtree);
		
		
		FileDictionary similarities = makeSimilarities();
		test_word = "Infante";
		test_word = ci.cleanedString(test_word);
		System.out.println("for example, the link of '"+test_word+"' is '" + similarities.getValue(test_word)+"'");
		similarities.save(DICTIONARY_SIMILARITY);
		
		
		System.out.println("finished");
	}
    

    
    public static ICD9CMDictionary getInstance() {
        if (self==null)
            self=new ICD9CMDictionary();
        return self;
    }
    
    /**
	 * this methods reads the file <filename>.pdf
	 * each text object of the pdf is exported into a single line in <filename>.txt
	 * 
	 * @param filename
	 */
	
	private void readIcdPdf() {
            BufferedWriter wr;
            try {
                wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FIRST_STEP_FILENAME)));
                wr.write(PDFToText.processFile(FILENAME));
                wr.close();
            } catch (Throwable ex) {
                Logger.getLogger(ICD9CMDictionary.class.getName()).log(Level.SEVERE, null, ex);
                
            } 
            
	}
        
    /**
	 * the txt-file <filename>.txt is read and 
	 * the following lines are discarded:
	 * - lines with a length < 4 characters (these correspond to parts of page headers)
	 * - lines for which a length is undefined
	 * - lines containing pdf page header strings
	 * - lines containing "(continua)", "Nota ", "Nota- " or "Usare la"
	 * 
	 * 
	 * if a line starts with a a small letter, a digit, [ or ( it is appended to the previous line.
	 * the results are written into <filename>-cleaned.txt
	 * 
	 * @param filename
	 */
	private void cleanUpTxt() {
		try {
		    File file = new File(SECOND_STEP_FILENAME);
		    file.createNewFile();
		    FileReader fr = new FileReader(FIRST_STEP_FILENAME);
		    FileWriter fw = new FileWriter(file);
		    BufferedReader br = new BufferedReader(fr);

		    String line = "";
		    Boolean bad = false;
		    while(line != null)
		    {
		      line = br.readLine();
		      bad = false;

		      //here all lines are flagged that are too short or give errors (for example null)
		      try{
			      if (line.length() < 4){
			    	  bad = true;
			      }
		      }
			 	  catch (Exception e){
		          bad = true;
			  }
		      		      
		      
		      
		      
		      if(bad == false && line.contains("INDICE ALFABETICO DELLE MALATTIE E DEI TRAUMATISMI")){
		    	  bad = true;
		      }
		      else if(bad == false && line.contains("SPECIFICATAPRIMARIO SECONDARIO IN SITU")){
		    	  bad = true;
		      }
		      else if(bad == false && line.contains("(continua)")){
		    	  bad = true;
		      }
		      else if(bad == false && line.contains("Nota ")){
		    	  bad = true;
		      }
		      else if(bad == false && line.contains("Nota- ")){
		    	  bad = true;
		      }		
		      else if(bad == false && line.contains("Usare la")){
		    	  bad = true;
		      }	
 

		      if (!bad){ //notbad.png
		    	  //if a line starts with a small letter, a digit, [ or ( it is appended to the previous line
			      if (!line.matches("[a-z0-9\\[\\(].*")){
			    	  fw.write("\r\n");
			      }
			      fw.write(line);

		      }    
		    }
		    
		    fw.flush();
		    fw.close();
		    br.close();
		    System.out.println("cleaned up text and wrote into " + SECOND_STEP_FILENAME);
		}
	 	catch (Exception e){
         e.printStackTrace();
	 	}

	}
        
        /**
	 * the txt-file <filename>_cleaned.txt is read and 
	 * the patterns "- V." and "(v." are searched.
	 * if found, the remaining string is discarded.
	 * the results are written into <filename>-cleaned2.txt
	 * 
	 * @param filename
	 */
	
	private void cleanUpTxt2() {
		try {
		    File file = new File(LAST_STEP_FILENAME);
		    file.createNewFile();
		    FileReader fr = new FileReader(SECOND_STEP_FILENAME);
		    FileWriter fw = new FileWriter(file);
		    BufferedReader br = new BufferedReader(fr);

		    String line = "";

		    while(line != null)
		    {
		    line = br.readLine();
		      if (line != null){
		    	  

			  if (line.matches(".*[\\(][v][\\.].*")){
			      	  int pos = line.indexOf("(v.");
			      	  line = line.substring(0, pos);
			   }  
			  else if (line.matches(".*[\\(][V][\\.].*")){
		      	  int pos = line.indexOf("(V.");
		      	  line = line.substring(0, pos);
			  }  
			  else if (line.matches(".*\\-\\s[V]\\..*")){
		      	  int pos = line.indexOf("- V.");
		      	  line = line.substring(0, pos);
		      }  
			  else if (line.matches(".*\\s\\.*")){
		      	  int pos = line.indexOf(" .");
		      	  int pos2 = line.lastIndexOf(" .");
		      	  if (pos2-pos > 1){
		      	  line = line.substring(0, pos) + line.substring(pos2,line.length());
		      	  }

		      }  
			  

			  
			  fw.write(line);
		      fw.write("\r\n");
			   
		      }
		    }
		    
		    fw.flush();
		    fw.close();
		    br.close();
		    System.out.println("cleaned up text and wrote into " + LAST_STEP_FILENAME);
		}
	 	catch (Exception e){
         e.printStackTrace();
	 	}

	}
	

	
	
	
	/**
	 * A arraylist is generated and filled with links between conditions (Adjacency list),
	 * based on <filename>_cleaned.txt
	 * 
	 * @param filename
	 * @return
	 */
	
	public static FileDictionary makeSimilarities() {
            
            if (new File(DICTIONARY_SIMILARITY).exists()) {
                return new FileDictionary(DICTIONARY_SIMILARITY);
            }
                //TODO: Add those relations directly to the graph
		FileDictionary similarities = new FileDictionary();
		
		
		try {
		    FileReader fr = new FileReader(SECOND_STEP_FILENAME);
		    BufferedReader br = new BufferedReader(fr);

		    String line = "";
		    
		    String key = "";
		    String value = "";
		    Boolean found = false;


		    while(line != null)
		    {
		    line = br.readLine();
		      if (line != null){
		    	  
		    	  key = line;
		    	  value = "";
		    	  found = false;
		    	  

		    	  if (line.contains("(v. anche")){
			      	  int pos = line.indexOf("(v. anche");
			      	  int pos2 = line.lastIndexOf(")");
			      	  if (pos2>pos) {
				      	  key = line.substring(0, pos).replace("^ ", "").trim();
				      	  value = line.substring(pos+10, pos2).trim();
				      	  found = true;
			      	  }
			      }  
		    	  
		    	  else if (line.contains("(v.anche")){
			      	  int pos = line.indexOf("(v.anche");
			      	  int pos2 = line.lastIndexOf(")");
			      	  if (pos2>pos) {
				      	  key = line.substring(0, pos).replace("^ ", "").trim();
				      	  value = line.substring(pos+8, pos2).trim();
				      	  found = true;
			      	  }
			      }  
		    	  
		    	  else if (line.contains("(v. anc he")){
			      	  int pos = line.indexOf("(v. anc he");
			      	  int pos2 = line.lastIndexOf(")");
			      	  if (pos2>pos) {
				      	  key = line.substring(0, pos).replace("^ ", "").trim();
				      	  value = line.substring(pos+10, pos2).trim();
				      	  found = true;
			      	  }
			      }  
		    	  
		    	  else if (line.contains("(v. ")){
			      	  int pos = line.indexOf("(v. ");
			      	  int pos2 = line.lastIndexOf(")");
			      	  if (pos2>pos) {
				      	  key = line.substring(0, pos).replace("^ ", "").trim();
				      	  value = line.substring(pos+3, pos2).trim();
				      	  found = true;
			      	  }
			      }  
				  
				  else if (line.contains("(V.")){
			      	  int pos = line.indexOf("(V.");
			      	  int pos2 = line.lastIndexOf(")");
			      	  key = line.substring(0, pos).replace("^ ", "").trim();
			      	  value = line.substring(pos+10, pos2).trim();
			      	  found = true;
			      }  
				  
				  
				  else if (line.contains("- V. anche")){
			      	  int pos = line.indexOf("- V. anche");
			      	  key = line.substring(0, pos).replace("^ ", "").trim();
			      	  value = line.substring(pos+10, line.length()).trim();
			      	  found = true;
			      }  
		    	  
				  else if (line.contains("- V.")){
			      	  int pos = line.indexOf("- V.");
			      	  key = line.substring(0, pos).replace("^ ", "").trim();
			      	  value = line.substring(Math.min(pos+5, line.length()), line.length()).trim();
			      	  found = true;
			      }  


				  if (found){		  
					  similarities.put(ci.cleanedString(key), ci.cleanedString(value));
				  }
		      }
		      
		    }
		    
		    br.close();
		    //System.out.println("collected " + similarities.size() + " similarities between conditions and stored into hashmap");
		}
	 	catch (Exception e){
         e.printStackTrace();
	 	}
		
		similarities.save(DICTIONARY_SIMILARITY);
		return similarities;
	}
	

	
	/**
	 * a Tree is generated and filled with the contents of <filename>_cleaned_2.txt
	 * 
	 * @param filename
	 * @return
	 */
	
	public static Tree<String,ICD9CMCode> getTree() {
		
            if (new File(TREE_SER).exists())
                return new Tree<>(TREE_SER);
            
         
            
		String toplevelname = "condition";
		ICD9CMCode toplevelicd = new ICD9CMCode();
		
		Tree<String,ICD9CMCode> tree = new Tree();
		tree.addNode(toplevelname, null, toplevelicd);
		
		
		try {

		    FileReader fr = new FileReader(LAST_STEP_FILENAME);
		    BufferedReader br = new BufferedReader(fr);
	
		    String line = "";
		    ArrayList<String> cleanedlist = new ArrayList<String>();
		    
		    while(line != null)
		    {
		      line = br.readLine();
		      if (line == null){  
		    	  continue;
		      }
	
		      cleanedlist.add(line);
		    }
		    
		    int level;
		    
		    String lvl0 = "";
		    String lvl1 = "";
		    String lvl2 = "";
		    String lvl3 = "";
		    String lvl4 = "";
		    String lvl5 = "";
		    
		    TreeNode<String,ICD9CMCode> nlvl0 = null;
		    TreeNode<String,ICD9CMCode> nlvl1 = null;
		    TreeNode<String,ICD9CMCode> nlvl2 = null;
		    TreeNode<String,ICD9CMCode> nlvl3 = null;
		    TreeNode<String,ICD9CMCode> nlvl4 = null;

		    String icdstring = "";
		    ICD9CMCode icd = null;
		    
		    
		    for (int i = 0; i < cleanedlist.size(); i++){
		    	
		    	
		    	if ((i%10000) == 0)
		    	{
		    		System.out.println((i/1000) + "k nodes generated");
		    	}
		    	
		    	level = 0;
		    	line = cleanedlist.get(i).trim().toLowerCase();

		        if (line.startsWith("^ ")){
		        	  level++;
			          if (line.startsWith("^ ^ ")){
			        	  level++;
				          if (line.startsWith("^ ^ ^ ")){
				        	  level++;
					          if (line.startsWith("^ ^ ^ ^ ")){  
					        	  level++;
					          }
					          if (line.startsWith("^ ^ ^ ^ ^ ")){  
					        	  level++;
					          }
				          }
			          }
		          }

		        //Full Code
	        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
	    	Matcher matcher = pattern.matcher(line);
	    	
	    	if (matcher.find())
	    	{
	    	    icdstring = matcher.group(matcher.groupCount()); //gives last match
	    	    icd = new ICD9CMCode(icdstring);
                    line = line.replaceAll("\\d+\\.\\d+", "").trim();
	    	}
	    	else 
	    	{
                    //Underspecified code
                    pattern = Pattern.compile("\\d\\d\\d");
                    matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        icdstring = matcher.group(matcher.groupCount()); //gives last match
                        icd = new ICD9CMCode(icdstring);
                        line = line.replaceAll("\\d\\d\\d", "").trim();
                    } else {
	    		icd = new ICD9CMCode();
                        //System.err.println(line);
                    }
	    	}

		    	line = line.replace("^ ", "");
		    	
				String spacer = "/";
		        
		        switch (level) {
		        case 0: lvl0 = line.replaceAll("\\d+\\.\\d+", "").trim();
		        		lvl0 = ci.cleanedString(lvl0).trim();
		        		nlvl0 = tree.addNode(lvl0, toplevelname, icd);
		        	break;
		        case 1: lvl1 = line.replaceAll("\\d+\\.\\d+", "").trim();
		        		lvl1 = ci.cleanedString(lvl1).trim();
		        		line = lvl0 + spacer + lvl1;
		        		nlvl1 = tree.addNode(ci.cleanedString(line).trim(), nlvl0.getKey(), icd);
	        		break;
		        case 2: lvl2 = line.replaceAll("\\d+\\.\\d+", "").trim();
		        		lvl2 = ci.cleanedString(lvl2).trim();
		        		line = lvl0 + spacer + lvl1 + spacer + lvl2;
		        		nlvl2 = tree.addNode(ci.cleanedString(line).trim(), nlvl1.getKey(), icd);
	        		break;	        	
		        case 3: lvl3 = line.replaceAll("\\d+\\.\\d+", "").trim();
		        		lvl3 = ci.cleanedString(lvl3).trim();
		        		line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3;
		        		nlvl3 = tree.addNode(ci.cleanedString(line).trim(), nlvl2.getKey(), icd);
	        		break;		        
		        case 4: lvl4 = line.replaceAll("\\d+\\.\\d+", "").trim();
        				lvl4 = ci.cleanedString(lvl4).trim();
		        		line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3 + spacer + lvl4;
		        		nlvl4 = tree.addNode(ci.cleanedString(line).trim(), nlvl3.getKey(), icd);
	        		break;		        	
		        case 5: lvl5 = line.replaceAll("\\d+\\.\\d+", "").trim();
        				lvl5 = ci.cleanedString(lvl5).trim();
        				line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3 + spacer + lvl4 + spacer + lvl5;
        				tree.addNode(ci.cleanedString(line).trim(), nlvl4.getKey(), icd);
        			break;	
		        }
		    }

		    br.close();
		    System.out.println("generated tree with " + tree.getSize() + " nodes");
                    tree.serialize(TREE_SER);
		}
	 	catch (Exception e){
	 		e.printStackTrace();
	 	}	

		return tree;
	}
	

	
	
	
	public static Tree<String,ICD9CMCode> getTreeExpanded() {
		
        if (new File(TREE_EXP_SER).exists())
            return new Tree<>(TREE_EXP_SER);

	String toplevelname = "condition";
	ICD9CMCode toplevelicd = new ICD9CMCode();
	
	Tree<String,ICD9CMCode> tree = new Tree();
	tree.addNode(toplevelname, null, toplevelicd);
	
	
	try {

	    FileReader fr = new FileReader(LAST_STEP_FILENAME);
	    BufferedReader br = new BufferedReader(fr);

	    String line = "";
	    ArrayList<String> cleanedlist = new ArrayList<>();
	    
	    while(line != null)
	    {
	      line = br.readLine();
	      if (line == null){  
	    	  continue;
	      }

	      cleanedlist.add(line);
	    }
	    
	    int level;
	    
	    String lvl0 = "";
	    String lvl1 = "";
	    String lvl2 = "";
	    String lvl3 = "";
	    String lvl4 = "";
	    String lvl5 = "";
	    
	    TreeNode<String,ICD9CMCode> nlvl0 = null;
	    TreeNode<String,ICD9CMCode> nlvl1 = null;
	    TreeNode<String,ICD9CMCode> nlvl2 = null;
	    TreeNode<String,ICD9CMCode> nlvl3 = null;
	    TreeNode<String,ICD9CMCode> nlvl4 = null;

	    String icdstring = "";
	    ICD9CMCode icd = new ICD9CMCode();
	    
	    
    	Similarity lcs = LowConfidenceRank.getInstance();
    	WordList meddict =  OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.ITALIAN_MEDICAL_DICTIONARY); 
    	
    	System.out.println("starting generation of expanded tree (might take a long time)");
	    
	    
	    for (int i = 0; i < cleanedlist.size(); i++){
	    	
	    	if ((i%1000) == 0 && i > 0)
	    	{
	    		System.out.println((i/1000) + "k nodes generated");
	    	}
	    	
	    	level = 0;
	    	line = cleanedlist.get(i).trim().toLowerCase();

	        if (line.startsWith("^ ")){
	        	  level++;
		          if (line.startsWith("^ ^ ")){
		        	  level++;
			          if (line.startsWith("^ ^ ^ ")){
			        	  level++;
				          if (line.startsWith("^ ^ ^ ^ ")){  
				        	  level++;
				          }
				          if (line.startsWith("^ ^ ^ ^ ^ ")){  
				        	  level++;
				          }
			          }
		          }
	          }
	        
	        //Full Code
	        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
	    	Matcher matcher = pattern.matcher(line);
	    	
	    	if (matcher.find())
	    	{
	    	    icdstring = matcher.group(matcher.groupCount()); //gives last match
	    	    icd = new ICD9CMCode(icdstring);
                    line = line.replaceAll("\\d+\\.\\d+", "").trim();
	    	}
	    	else 
	    	{
                    //Underspecified code
                    pattern = Pattern.compile("\\d\\d\\d");
                    matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        icdstring = matcher.group(matcher.groupCount()); //gives last match
                        icd = new ICD9CMCode(icdstring);
                        line = line.replaceAll("\\d\\d\\d", "").trim();
                    } else {
	    		icd = new ICD9CMCode();
                        //System.err.println(line);
                    }
	    	}
	    		
	    	
	        
	    	line = line.replace("^ ", "");


	    	
	    	
	    	
	    	//System.out.println("####");
	    	//System.out.println(line);	    
	    	line = ci.cleanedString(line).trim();
	    	//System.out.println(line);	
	    	Annotator a = new Annotator(line);
	        a.identitySemantics();
	        a.approximateExpansion(meddict, lcs); 
	        line = a.returnCleanedDocument().trim(); // Obtaining the expanded string
	    	//System.out.println(line);
	       
	    	
	    	
	    	
	    	
			String spacer = "/";
	        
	        switch (level) {
	        case 0: lvl0 = line;
	        		//lvl0 = ci.cleanedString(lvl0).trim();
	        		nlvl0 = tree.addNode(lvl0, toplevelname, icd);
	        		//System.out.println(lvl0 + " -> " + icd);
	        	break;
	        case 1: lvl1 = line;
	        		//lvl1 = ci.cleanedString(lvl1).trim();
	        		line = lvl0 + spacer + lvl1;
	        		nlvl1 = tree.addNode(line, nlvl0.getKey(), icd);
	        		//System.out.println(line + " -> " +  nlvl0.getKey() + " -> " + icd);
        		break;
	        case 2: lvl2 = line;
	        		//lvl2 = ci.cleanedString(lvl2).trim();
	        		line = lvl0 + spacer + lvl1 + spacer + lvl2;
	        		nlvl2 = tree.addNode(line, nlvl1.getKey(), icd);
	        		//System.out.println(line + " -> " +  nlvl1.getKey());
        		break;	        	
	        case 3: lvl3 = line;
	        		//lvl3 = ci.cleanedString(lvl3).trim();
	        		line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3;
	        		nlvl3 = tree.addNode(line, nlvl2.getKey(), icd);
	        		//System.out.println(line + " -> " +  nlvl2.getKey());
        		break;		        
	        case 4: lvl4 = line;
    				//lvl4 = ci.cleanedString(lvl4).trim();
	        		line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3 + spacer + lvl4;
	        		nlvl4 = tree.addNode(line, nlvl3.getKey(), icd);
	        		//System.out.println(line + " -> " +  nlvl3.getKey());
        		break;		        	
	        case 5: lvl5 = line;
    				//lvl5 = ci.cleanedString(lvl5).trim();
    				line = lvl0 + spacer + lvl1 + spacer + lvl2 + spacer + lvl3 + spacer + lvl4 + spacer + lvl5;
    				tree.addNode(line, nlvl4.getKey(), icd);
    				//System.out.println(line + " -> " +  nlvl4.getKey());
    			break;	
	        }

	    }

	    br.close();
	    System.out.println("generated expanded tree with " + tree.getSize() + " nodes");
                tree.serialize(TREE_EXP_SER);
	}
 	catch (Exception e){
 		e.printStackTrace();
 	}	

	return tree;
}
	

	
        		
		 /*PDDocument pd;
		 BufferedWriter wr;
		
		 try {
		         File input = new File(filename + ".pdf");
		         pd = PDDocument.load(input);
		         System.out.println("found " + pd.getNumberOfPages() + " pages");
		         System.out.println("encryption: " + pd.isEncrypted());
		
		         PDFTextStripper stripper = new PDFTextStripper();
		         stripper.setStartPage(1); //inclusive
		         stripper.setEndPage(pd.getNumberOfPages()); //inclusive
		         wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + ".txt")));
		         stripper.writeText(pd, wr);
		         wr.close();    
		         pd.close();
		         System.out.println("wrote text from " + filename + ".pdf into " + filename + ".txt-file");
		         
		 } catch (Exception e){
		         e.printStackTrace();
		 }
		 */
	

}
