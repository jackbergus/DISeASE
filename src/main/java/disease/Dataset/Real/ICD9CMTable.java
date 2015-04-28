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
package disease.Dataset.Real;

import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.Real.EventParsing.EventException;
import disease.Dataset.Real.EventParsing.EventMatcher;
import disease.Dataset.Real.EventParsing.ParsingEvents;
import static disease.Dataset.Real.EventParsing.ParsingEvents.DOSKIP;
import disease.Dataset.Real.EventParsing.PeekIterator;
import disease.Dataset.Real.EventParsing.StateMachine;
import disease.Dataset.interfaces.Dictionary;
import disease.Dataset.interfaces.EnrichedTree;
import disease.Dataset.interfaces.WordList;
import disease.Phase.cleaner.CleanItalian;
import disease.Phase.Annotator;
import disease.ontologies.ICD9CMCode;
import disease.similarities.*;
import disease.utils.AStringUtils;
import disease.utils.Correction;
import disease.utils.DictionaryType;
import disease.datatypes.ConcreteMapIterator;
import disease.datatypes.MapIterator;
import disease.utils.PDFToText;
import disease.utils.datatypes.Pair;
import disease.utils.Storage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Giacomo Bergami
 */
public class ICD9CMTable implements EnrichedTree<ICD9CMCode,String> {
    
    private static ICD9CMTable self = null;
    //private static GraphDB db = GraphDB.getInstance();
    private String filename = "data"+File.separator+"disease_list.pdf";
    //private String field = null;
    private  String disease = "";
    private  String subdisease = "";
    private  String new_code = "";
    private  String new_subcode = "";
    private  String descr = "";
    private  boolean doskip = false;
    private  boolean excluded = false;
    //private String lineexlu = "";
    private  boolean indescr = false;
    private  boolean first = true;
    private  LinkedList<String> descrs = new LinkedList<>();
    private CleanItalian dc = CleanItalian.getInstance();
    
    private Dictionary<String,String> med_dict =  OnlineMedicalDictionary.stemmedDictionary();
    private WordList med_list = OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.ITALIAN_MEDICAL_DICTIONARY);
    private Similarity lcr = LowConfidenceRank.getInstance();
    
        
    //private  Map<String,String> supercode_meaning = new HashMap<>(); //maps each title to the desired code
    private  Map<String,String> code_description_map = new HashMap<>(); //maps each icd code to the description
    private  Map<String,String> code_expanded_description = new HashMap<>(); //maps each icd code to the expanded description
    private  Map<String,String> specification_to_code = new HashMap<>(); //maps each specification to its code
    private  Map<String,List<String>> code_to_specifications = new HashMap<>(); //maps each specification to its code
    private  Map<String,List<String>> code_to_expanded_specifications = new HashMap<>(); //maps each expanded specification to its code
    private Map<String,List<String>> excluded_to_listof_codes = new HashMap<>();
    private Map<String,List<String>> expanded_excluded_to_listof_codes = new HashMap<>();
    
    
    //private static final String SUPERCODE_MEANING = "data"+File.separator+"supercode_meaning.ser";
    private static final String CODE_DESCRIPTION = "data"+File.separator+"code_description_map.ser";
    private static final String CODE_EX_DESCRIPTION = "data"+File.separator+"code_expanded_description.ser";
    private static final String SPEC_TO_CODE = "data"+File.separator+"specification_to_code.ser";
    private static final String CODE_TO_SPEC = "data"+File.separator+"code_to_specifications.ser";
    private static final String CODE_TO_ESPEC = "data"+File.separator+"code_to_expanded_specifications.ser";
    private static final String EXCLUDED_STEMMED = "data"+File.separator+"excluded_s.ser";
    private static final String EXCLUDED_EXPANDED = "data"+File.separator+"excluded_e.ser";
    private boolean release = false;
    
    private ICD9CMTable() {
    }
    
    
/*
    public void populateOntologyFromSerialized() {
        init();
        
        
        
        for (String code : code_description_map.keySet()) {
            ICD9CMCode c = new ICD9CMCode(code);
            Set<String> stemmed_terms = new HashSet<>();
            String description = code_description_map.get(code);
            stemmed_terms.addAll(Arrays.asList(description.split(" ")));
            String extended_description = code_expanded_description.get(code);
            List<String> specifications = code_to_specifications.get(code);
            if (specifications!=null)
                specifications.stream().map((x)-> {return  Arrays.asList(x.split(" "));}).forEach(stemmed_terms::addAll);
            else
                specifications =new LinkedList<>();
            List<String> expanded_specifications = code_to_expanded_specifications.get(code);
            if (expanded_specifications!=null)
                expanded_specifications.stream().map((x)-> {return  Arrays.asList(x.split(" "));}).forEach(stemmed_terms::addAll);
            else
                expanded_specifications =new LinkedList<>();
            ICD9CM.createArrayArg(c, extended_description, description, specifications, expanded_specifications, specifications, specifications);
            ICD9CM node = new ICD9CM();
            node.setCode(c);
            node.setStemmedDescription(description);
            node.setExpandedDescription(extended_description);
            node.setSuggestions(specifications);
            node.setExpandedSuggestions(expanded_specifications);
            node.add();
            for (String x : stemmed_terms) {
                String exp = med_dict.getValue(x);
                if (exp==null) exp="";
                db.addICD9CMCodeTermRelation(node, db.hasTerm(x,exp));
            }
        }
        
        //I add all the nodes
        for (String code : code_description_map.keySet()) {
            ICD9CMCode c = new ICD9CMCode(code);
            if (!c.hasOnlyThreeDigits()) {
                db.addICD9CMFatherChildRelation(db.hasICD9CM(c), c.getThreeDigitsFather());
                
            }
        }
        
        for ( Pair<String, List<String>> x: new ConcreteMapIterator<>(excluded_to_listof_codes)) {
            String spec = x.getFirst();
            x.getSecond().stream().map((code) -> {
                db.updateICD9CMWithDetailments(new ICD9CMCode(code), spec);
                return code;
            }).forEach((code) -> {
                
                Arrays.asList(spec.split(" ")).forEach((t) -> {
                    String exp = med_dict.getValue(t);
                    if (exp==null) exp="";
                    db.addICD9CMCodeTermRelation(db.hasICD9CM(code), db.hasTerm(t,exp));});
            });
        }
        for ( Pair<String, List<String>> x: new ConcreteMapIterator<>(expanded_excluded_to_listof_codes)) {
            String spec = x.getFirst();
            x.getSecond().stream().map((code) -> {
                db.updateICD9CMWithExpandedDetailments(new ICD9CMCode(code), spec);
                return code;
            }).forEach((code) -> {
                Arrays.asList(spec.split(" ")).forEach((t) -> {
                    String exp = med_dict.getValue(t);
                    if (exp==null) exp="";
                    db.addICD9CMCodeTermRelation(db.hasICD9CM(code), db.hasTerm(t,exp));});
            });
        }
        
        db.close();
        
    }
   */
    
    private void partialAdd(String id, String icd) {
        //excluded_to_listof_codes.get(icd).add();
        if (icd.contains("-")) 
            for (String x : icd.split("-")) {
                partialAdd(id,AStringUtils.deleteWhitespace(x));
            }
        else if (icd.contains(",")) 
            for (String x : icd.split(",")) {
                partialAdd(id,AStringUtils.deleteWhitespace(x));
            }
        else {
            if ((Pattern.compile("\\d\\d\\d.\\d\\d").matcher(icd).matches() && icd.length()==6) ||
                (Pattern.compile("\\d\\d\\d.\\d").matcher(icd).matches() && icd.length()==5) ||
                (Pattern.compile("\\d\\d\\d").matcher(icd).matches() && icd.length()==3)) {
                if (!excluded_to_listof_codes.get(id).contains(icd)) {
                    excluded_to_listof_codes.get(id).add(icd);
                    //System.out.println(id+"--"+icd);
                }
                if (!expanded_excluded_to_listof_codes.get(id).contains(icd)) {
                    Annotator a = new Annotator(id);
                    a.identitySemantics();
                    a.approximateExpansion(med_list, lcr);
                    String expanded = a.returnCleanedDocument();
                    expanded_excluded_to_listof_codes.get(id).add(expanded);
                }
            }
        }
    }
    
    private boolean addExcludedCode(String line_toparse) {
        line_toparse = line_toparse.replace("Escl.", "");
        String descr[] = line_toparse.split("\\(");
        if (descr.length==1)
            return false;
        descr[0] = dc.cleanedString(descr[0]);
        if (descr[0].length()==0)
            return true;
        descr[1] = descr[1].replace(")", "");
        if (!excluded_to_listof_codes.containsKey(descr[0])) 
            //item initialization, once and for all.
            excluded_to_listof_codes.put(descr[0], new LinkedList<>());
        if (!expanded_excluded_to_listof_codes.containsKey(descr[0])) 
            //item initialization, once and for all.
            expanded_excluded_to_listof_codes.put(descr[0], new LinkedList<>());
        partialAdd(descr[0],descr[1]);
        return true;
    }
    
    
    
    
    private static ICD9CMTable getInstance() {
        if (self==null)
            self = new ICD9CMTable();
        return self;
    }
    
    public static ICD9CMTable init() {
        getInstance();
        if (!(new File(ICD9CMTable.CODE_DESCRIPTION).exists() &&
                new File(ICD9CMTable.CODE_EX_DESCRIPTION).exists() &&
                new File(ICD9CMTable.CODE_TO_ESPEC).exists() &&
                new File(ICD9CMTable.CODE_TO_SPEC).exists() &&
                new File(ICD9CMTable.EXCLUDED_EXPANDED).exists() &&
                new File(ICD9CMTable.SPEC_TO_CODE).exists() &&
                //new File(ICD9CMTable.SUPERCODE_MEANING).exists() &&
                new File(ICD9CMTable.EXCLUDED_STEMMED).exists()))
            self.init(true);
        else
            self.do_load();
        return self;
    }
    
    /**
     * Given the ICD9-CM code, returns the disease name
     * @param icd9cm ICD9-CM code
     * @return 
     */
    public String getCodeDescription(ICD9CMCode icd9cm) {
        return (code_description_map.get(icd9cm.toString()));
    }
    
    public String getCodeExpandedDescription(ICD9CMCode icd9cm) {
        return (code_expanded_description.get(icd9cm.toString()));
    }
    
    /**
     * Given the ICD9-CM code, returns the main class that groups the diseases
     * @param icd9cm
     * @return 
     */
    public String getSuperclass(ICD9CMCode icd9cm) {
        //return (supercode_meaning.get(icd9cm.toString()));
        throw new RuntimeException("Deprecated method: getSuperclass");
    }
    
    /**
     * Returns the father of key (it doesn't necessairly imply that this is the most general code as possible,
     * since the ICD-9-CM hierarchy is not well defined)
     * @param key
     * @return 
     */
    @Override
    public ICD9CMCode getFatherOf(ICD9CMCode key) {
        return key.getThreeDigitsFather();
    }

    //TODO: (non specificata), (altre) -> elementi da rimuovere
    //      xxxxx con xxxxx            -> specificazione della malattia
    //      XXXXX,                     -> prima della virgola compare il termine più importante
    @Override
    public ICD9CMCode isSemanticallyRelatedTo(ICD9CMCode key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private void finalize_first_level_codes() {
        if (disease.length()!=0 && new_code.length()!=0 /*&& field !=null && disease !=null*/) {
            if (! code_description_map.containsKey(new_code)) { //It is likely that it doesn't appear in the database too
                
                disease = dc.cleanedString(disease);
                //field = dc.cleanedString(field);
                code_description_map.put(new_code,disease);
                
                Annotator a = new Annotator(disease);
                a.identitySemantics();
                a.approximateExpansion(med_list, lcr);
                String expanded = a.returnCleanedDocument();
                code_expanded_description.put(new_code, expanded);
                
                //Add the term links
                Set<String> elem = new TreeSet<>();
                elem.addAll(Arrays.asList(disease.split(" ")));
                elem.addAll(Arrays.asList(expanded.split(" ")));
                
                //supercode_meaning.put(new_code,field);
            }
        } 
    }
    
    private void finalize_second_level_codes() {
        if (subdisease.length()!=0 && new_subcode.length()!=0 && disease.length()!=0 && new_code.length()!=0 /*&& field !=null && disease !=null*/) {
            if (! code_description_map.containsKey(new_subcode)) {
                
                subdisease = dc.cleanedString(subdisease);
                //field = dc.cleanedString(field);
                code_description_map.put(new_subcode,subdisease);
                
                Annotator a = new Annotator(subdisease);
                a.identitySemantics();
                a.approximateExpansion(med_list, lcr);
                String expanded = a.returnCleanedDocument();
                code_expanded_description.put(new_subcode, expanded);
                
                //supercode_meaning.put(new_subcode,field);
            }
        }
    }
    
    private void finalize_first_level_descriptions(String descr) {
        if (descr!=null && descr.length()!=0 ) {
            if (/*subdisease.length()==0 && new_subcode.length()==0 &&*/ disease.length()!=0 && new_code.length()!=0) {
                descr = dc.cleanedString(descr);
                specification_to_code.put(descr, new_code);
                if (!code_to_specifications.containsKey(new_code)) {
                    code_to_specifications.put(new_code, new ArrayList<>());
                    code_to_expanded_specifications.put(new_code, new ArrayList<>());
                }
                code_to_specifications.get(new_code).add(descr);
                    
                Annotator a = new Annotator(descr);
                a.identitySemantics();
                a.approximateExpansion(med_list, lcr);
                code_to_expanded_specifications.get(new_code).add(a.returnCleanedDocument());
            }
        }
    }
    
    private void finalize_second_level_descriptions(String descr) {
        if (descr!=null && descr.length()!=0 ) {
            if (subdisease.length()!=0 && new_subcode.length()!=0 &&  disease.length()!=0 && new_code.length()!=0) {
                descr = dc.cleanedString(descr);
                specification_to_code.put(descr, new_subcode);
                if (!code_to_specifications.containsKey(new_subcode)) {
                    code_to_specifications.put(new_subcode, new ArrayList<>());
                    code_to_expanded_specifications.put(new_subcode, new ArrayList<>());
                }
                
                code_to_specifications.get(new_subcode).add(descr);
                Annotator a = new Annotator(descr);
                a.identitySemantics();
                a.approximateExpansion(med_list, lcr);
                code_to_expanded_specifications.get(new_subcode).add(a.returnCleanedDocument());
            }
        }
    }
    
    //retrocompatibility code
    private void finalization() {
        // If the first input is scanned or the description is continued, do not store anything
        if (first||nodescr)
            return;
//        if (new_code.contains("369.9") || new_subcode.contains("369.9")) {
//            System.out.printf("HALT");
//        }
        
        finalize_first_level_codes();
        finalize_second_level_codes();
        
        finalize_first_level_descriptions(descr);
        finalize_second_level_descriptions(descr);
    }
    
    private boolean nodescr = true;
    private String partialnum = "";
    
    
    
    
    private String mergeTextStrings(String prev, String next) {
        return (prev.endsWith("-") ? prev.substring(0, prev.length()-1) + next : prev+" "+next);
    }
    
    private boolean startsWithUppercase(String s) {
        if (s==null||s.length()==0)
            return false;
        return Character.isUpperCase(s.charAt(0));
    }
  
    private void storeCurrentSpecification(ParsingEvents e, String eval)
    {
        switch (e) {
            case SUBDISEASE:
                finalize_second_level_descriptions(eval);
                break;
            case DISEASE:
                finalize_first_level_descriptions(eval);
                break;
        }
    }
    /**
     * Parses the Original PDF and stores as a Serialized content
     * @param only_diseases The dataset for the dictioary will be fed with the diseases only.
     */
    public void init(boolean only_diseases) {
        
        Iterator<String> elem  = Arrays.stream(PDFToText.processFile(filename, only_diseases, 394).split("\n")).iterator();
        EventMatcher<ParsingEvents> parsing_event_machine = new EventMatcher<>(elem);
        
        //Setting up the machine by adding the interesting events.
        parsing_event_machine.addInitializationPattern(
                "\\d+\\. ((\\b[A-Z]+([^A-Za-z]||_)+)||(\\(\\d\\d\\d(.)*-(.)*\\d\\d\\d\\)))+",
                ParsingEvents.DOSKIP);
        parsing_event_machine.addInitializationPattern(
                "\\d\\d\\d\\.(\\d)+(.)*", 
                ParsingEvents.SUBDISEASE);
        parsing_event_machine.addInitializationPattern(
                line ->     line.toLowerCase().startsWith("prima di")
                        ||  line.toLowerCase().startsWith("utilizzare"), 
                ParsingEvents.DOSKIP);
        parsing_event_machine.addInitializationPattern(line->line.startsWith("Escl"), ParsingEvents.EXCLUDED);
        parsing_event_machine.addInitializationPattern(line->line.startsWith("Incl"), ParsingEvents.INCLUDED);
        parsing_event_machine.addInitializationPattern(
                line -> (       !line.contains("ELENCO SISTEMATICO DELLE MALATTIE E DEI TRAUMATISMI")
                            &&  Pattern.compile("\\d\\d\\d(.)*").matcher(line).matches()), 
                ParsingEvents.DISEASE);
        
        //Strips all the strings that contain the given header
        parsing_event_machine.addToDiscardPredicate(line-> line.contains("ELENCO SISTEMATICO DELLE MALATTIE E DEI TRAUMATISMI"));
        //Checks which is the event that last occurred
        ParsingEvents lastMainEvent = null;
        
        for (Pair<ParsingEvents, Collection<String>> p : parsing_event_machine) {
            Iterator<String> group = p.getSecond().iterator();
            switch (p.getFirst()) {
                case SUBDISEASE: {
                    lastMainEvent = ParsingEvents.SUBDISEASE;
                    String header = group.next();
                    Matcher j = Pattern.compile("(\\d\\d\\d\\.(\\d)+)(.)*").matcher(header);
                    j.matches();//Bogus
                    new_subcode = j.group(1);
                    if (new_subcode.equals("663.8"))
                        System.out.println("break here");
                    //new_subcode = line.substring(0,line.indexOf(" "));
                    subdisease = header.substring(new_subcode.length()).trim();
                    
                    String current_specification = "";
                    
                    //Main header cycle
                    while (group.hasNext()) {
                        String line = group.next();
                        if ((!startsWithUppercase(line))||subdisease.length()==0) {
                            subdisease = this.mergeTextStrings(subdisease, line);
                        } else {
                            current_specification = line;
                            break; //The following elements are specifications
                        }
                    }  
                    
                    //Specification lists = they get immediately stored
                    //List<String> specs = new LinkedList<>();
                    while (group.hasNext()) {
                        String next = group.next();
                        if (!startsWithUppercase(next)) {
                            current_specification = mergeTextStrings(current_specification,next);
                        } else {
                            finalize_second_level_descriptions(current_specification);
                            current_specification = next;
                        }
                    }
                    finalize_second_level_descriptions(current_specification);
                    
                    //Stores the <code,description> values
                    finalize_second_level_codes();
                    
                    
                } break;
                case DISEASE: {
                    lastMainEvent = ParsingEvents.DISEASE;
                    String header = group.next();
                    Matcher j = Pattern.compile("(\\d\\d\\d)(.)*").matcher(header);
                    j.matches();//Bogus
                    new_code = j.group(1);
                    //new_subcode = line.substring(0,line.indexOf(" "));
                    disease = header.substring(new_code.length()).trim();
                    
                    String current_specification = "";
                    
                    //Main header cycle
                    while (group.hasNext()) {
                        String line = group.next();
                        if ((!startsWithUppercase(line))||disease.length()==0) {
                            disease = this.mergeTextStrings(disease, line);
                        }
                        else {
                            current_specification = line;
                            break; //The following elements are specifications
                        }
                    }  
                    
                    if (disease.length()==0) {
                        System.err.println("Error Here");
                    }
                    
                    //Specification lists
                    //List<String> specs = new LinkedList<>();
                    while (group.hasNext()) {
                        String next = group.next();
                        if (!startsWithUppercase(next)) {
                            current_specification = mergeTextStrings(current_specification,next);
                        } else {
                            finalize_first_level_descriptions(current_specification);
                            current_specification = next;
                        }
                    }
                    finalize_first_level_descriptions(current_specification);
                    
                    
                    //Stores the <code,description> values
                    finalize_first_level_codes();
                    
                    //stores the descriptions
                    //specs.forEach(x->finalize_first_level_descriptions(x));
                    
                } break;
                case INCLUDED: {
                    if (lastMainEvent==ParsingEvents.DOSKIP)
                        continue;
                    String current_specification = group.next();
                    current_specification = current_specification.replaceFirst("Incl", "");
                    while (group.hasNext()) {
                        String next = group.next();
                        if (!startsWithUppercase(next)) {
                            current_specification = mergeTextStrings(current_specification,next);
                        } else {
                            this.storeCurrentSpecification(lastMainEvent, current_specification);
                            current_specification = next;
                        }
                    }
                    this.storeCurrentSpecification(lastMainEvent, current_specification);
                } break;
                case EXCLUDED: {
                    if (lastMainEvent==ParsingEvents.DOSKIP)
                        continue;
                    String header = group.next();
                    header = header.replaceFirst("Escl", "");
                    String lineexlu = "";
                    do {
                        lineexlu += header;
                        if (addExcludedCode(lineexlu))
                            lineexlu = "";
                        else if (lineexlu.endsWith("-"))
                            lineexlu = lineexlu.substring(0,lineexlu.length()-1);
                        else 
                            lineexlu += " ";
                        if (group.hasNext())
                            header = group.next();
                        //Else: exit do...while loop
                    } while (group.hasNext());
                } break;
                case DOSKIP:
                    lastMainEvent = ParsingEvents.DOSKIP;
                    break; //Ignore the skipped events 
                default:
                    throw new AssertionError(p.getFirst().name());
            }
        }
        
//            for (String line : pdf.split("\n")) {
//                
//                if (line.contains("648.14"))
//                        System.out.println("Checkmeout");
//                if (line.contains("quelle malformazioni classificabili come"))
//                    continue;
//                if (nodescr) {
//                    
//                    if (Pattern.compile("\\d\\d\\d(.)*").matcher(partialnum).matches() && 
//                        Pattern.compile("\\d\\d\\d(.)*").matcher(line).matches()) {
//                        nodescr = false;
//                        partialnum = ""; //clearing a imprecise result
//                    } else
//                        line = partialnum + " " + line;
//                }
//                
//                
//                /*if (line.toLowerCase().contains("v.")) {
//                    int toseeidx = line.toLowerCase().indexOf("v.");
//                    String tosee = line.substring(toseeidx).toLowerCase();
//                    //if (!tosee.contains("indice analitico"))
//                    //    System.out.println(tosee);
//                    
//                    //System.exit(1);
//                }*/
//                if (Pattern.compile("\\d+\\. ((\\b[A-Z]+([^A-Za-z]||_)+)||(\\(\\d\\d\\d(.)*-(.)*\\d\\d\\d\\)))+").matcher(line).matches()) {
//                    finalization();
//                    doskip = false;
//                    first = false;
//                    indescr = false;
//                    nodescr = false;
//                    partialnum = "";
//                    descr = "";
//                    disease ="";
//                    new_code="";
//                    new_subcode="";
//                    subdisease = "";
//                    String tmp = line.replaceAll("[^a-zA-Z\\s]", "").toLowerCase(); 
//                    /*if (field == null)
//                        field = tmp;
//                    else if (!tmp.equals(field)) {
//                        field = tmp;
//                    }*/
//                    doskip = true;
//                } else if (Pattern.compile("\\d\\d\\d\\.(\\d)+(.)*").matcher(line).matches()) {
//                    if (disease.length()==0) {
//                        System.out.println("WARNING a): PREVIOUS available code was "+previous+" current_line="+line);
//                    }
//                    previous = new_subcode + " && " + new_code;
//                    finalization();
//                    //System.out.println("Standard: "+Pattern.compile("\\d\\d\\d\\.(\\d)+(.)+").matcher(line).matches());
//                    //System.out.println("Standard: "+Pattern.compile("(\\d\\d\\d\\.(\\d)+)(.)+").matcher(line).matches());
//                    Matcher j = Pattern.compile("(\\d\\d\\d\\.(\\d)+)(.)*").matcher(line);
//                    j.matches();//Bogus
//                    new_subcode = j.group(1);
//                    if (disease.length()==0) {
//                        System.out.println("WARNING b): PREVIOUS available code was "+previous);
//                    } else {
//                        previous = new_subcode + " && " + new_code;
//                    }
//                    //new_subcode = line.substring(0,line.indexOf(" "));
//                    subdisease = line.substring(new_subcode.length()).trim();
//                    doskip = false;
//                    excluded = false;
//                    lineexlu = "";
//                    indescr = false;
//                    first = false;
//                    nodescr = subdisease.isEmpty();
//                    partialnum = (nodescr ? new_subcode : "");
//                } else if (line.toLowerCase().startsWith("prima di")||line.toLowerCase().startsWith("utilizzare")) {
//                    finalization();
//                    doskip = true;
//                } else if (line.startsWith("Escl")) {
//                    if (line.startsWith("Escl")) {
//                        finalization();
//                        excluded = true;
//                        line = line.replaceFirst("Escl", "");
//                    } 
//                    doskip = true;
//                    indescr = false;
//                    first = false;
//                    descr = "";
//                    lineexlu += line;
//                    if (addExcludedCode(lineexlu))
//                        lineexlu = "";
//                    else if (lineexlu.endsWith("-"))
//                        lineexlu = lineexlu.substring(0,lineexlu.length()-1);
//                    else 
//                        lineexlu += " ";
//                    nodescr = false;
//                    partialnum = "";
//                } else if (
//                            (!line.contains("ELENCO SISTEMATICO DELLE MALATTIE E DEI TRAUMATISMI")) 
//                            &&  Pattern.compile("\\d\\d\\d(.)*").matcher(line).matches()) 
//                {
//                    finalization();
//                    subdisease = "";
//                    descr = "";
//                    Matcher j = Pattern.compile("\\d\\d\\d(.)*").matcher(line);
//                    j.matches();//Bogus
//                    new_code = line.substring(0, 3);
//                    new_subcode = "";
//                    subdisease = "";
//                    disease = line.substring(new_code.length()).trim();
//                    first = false;
//                    doskip = false;
//                    excluded = false;
//                    
//                    lineexlu = "";
//                    indescr = false;
//                    nodescr = disease.isEmpty();
//                    partialnum = (nodescr ? new_code : "");
//                    
//                } else if (((!line.contains("ELENCO SISTEMATICO DELLE MALATTIE E DEI TRAUMATISMI")) && !doskip && !excluded)||line.contains("Incl.")) {
//                    first = false;
//                    nodescr = false;
//                    partialnum = "";
//                    boolean already_done = false;
//                    if (line.contains("Incl.")) {
//                        doskip = false;
//                        excluded = false;
//                        finalization(); //Stores the previous element
//                        indescr = true; 
//                        line = line.replace("Incl.","");
//                        descr = line;
//                        already_done = true;
//                    }
//                    if (line.length()==0) {
//                        continue;
//                    }
//                    if (Character.isUpperCase(line.charAt(0))) {
//                        if (!already_done)
//                            finalization(); // storing previous content
//                        indescr = true;
//                        descr = line;
//                    } else {
//                        if (!indescr) {
//                            //The line continues the description of a disease or of a subdisease
//                            //finalization(); <- do not have to finalize something that is in the making
//                            if (subdisease.length()==0) {
//                                if (disease.endsWith("-")) {
//                                    disease = disease.substring(0, disease.length()-1);
//                                    disease += line;
//                                } else disease = disease+" "+line;
//                            }
//                            else if (subdisease.endsWith("-")) {
//                                subdisease = subdisease.substring(0, subdisease.length()-1);
//                                subdisease += line;
//                            } else subdisease = subdisease+" "+line;
//                        } else {
//                            if (descr.endsWith("-")) {
//                                descr = descr.substring(0, descr.length()-1);
//                                descr += line;
//                            } else descr = descr+" "+line;
//                        }
//                    }
//                } else if (excluded) {
//                    nodescr = false;
//                    partialnum = "";
//                    lineexlu += line;
//                    if (addExcludedCode(lineexlu))
//                        lineexlu = "";
//                    else if (lineexlu.endsWith("-"))
//                        lineexlu = lineexlu.substring(0,lineexlu.length()-1);
//                    else 
//                        lineexlu += " ";
//                }
//            }       
//            
//            finalization(); //Stores the last element in the data structures
            
            //storage of maps - it happens only after the loading
            //Storage.<Map<String,String>>serialize(supercode_meaning, SUPERCODE_MEANING);
            Storage.<Map<String,String>>serialize(code_description_map, CODE_DESCRIPTION);
            Storage.<Map<String,String>>serialize(code_expanded_description, CODE_EX_DESCRIPTION);
            Storage.<Map<String,String>>serialize(specification_to_code, SPEC_TO_CODE);
            Storage.<Map<String,List<String>>>serialize(code_to_specifications, CODE_TO_SPEC);
            Storage.<Map<String,List<String>>>serialize(code_to_expanded_specifications, CODE_TO_ESPEC );
            Storage.<Map<String,List<String>>>serialize(this.excluded_to_listof_codes,EXCLUDED_STEMMED);
            Storage.<Map<String,List<String>>>serialize(this.expanded_excluded_to_listof_codes,EXCLUDED_EXPANDED);
    }
    
    
    /**
     * Loads the serialized objects
     */
    private void do_load() {
        if (/*supercode_meaning.isEmpty() && */this.code_description_map.isEmpty() &&  specification_to_code.isEmpty() && this.code_to_specifications.isEmpty()) {
            //supercode_meaning = Storage.<Map<String,String>>unserialize(SUPERCODE_MEANING);
            code_description_map = Storage.<Map<String,String>>unserialize(CODE_DESCRIPTION);
            code_expanded_description = Storage.<Map<String,String>>unserialize(CODE_EX_DESCRIPTION);
            specification_to_code = Storage.<Map<String,String>>unserialize(SPEC_TO_CODE);
            code_to_specifications = Storage.<Map<String,List<String>>>unserialize(CODE_TO_SPEC);
            code_to_expanded_specifications = Storage.<Map<String,List<String>>>unserialize(CODE_TO_ESPEC);
            excluded_to_listof_codes = Storage.<Map<String,List<String>>>unserialize(EXCLUDED_STEMMED);
            expanded_excluded_to_listof_codes = Storage.<Map<String,List<String>>>unserialize(EXCLUDED_EXPANDED);
        }
        
    }
    
    /** Frees some memory */
    public static void release() {
        //self.supercode_meaning.clear();
        self.code_description_map.clear();
        self.code_expanded_description.clear();
        self.code_to_expanded_specifications.clear();
        self.code_to_specifications.clear();
        self.excluded_to_listof_codes.clear();
        self.specification_to_code.clear();
        self.expanded_excluded_to_listof_codes.clear();
    }
    
    /**
     * Returns the meaning associated to each ICD9CM code
     * @param key   The key value
     * @return 
     */
    @Override
    public String getValue(ICD9CMCode key) {
        return this.code_description_map.get(key.toString());
    }
    
    /**
     * Given the ICD9-CM code, returns the list of possible expansions
     * @param icd9cm
     * @return 
     */
    public List<String> getSpecifications(ICD9CMCode icd9cm) {
        return (code_to_specifications.get(icd9cm.toString()));
    }
    
    
    
    
    // TODO: the 386.10 code states "Vertigini periferiche, non specificate"
    // the data query says ""sindrome vertiginosa periferica crisi ipertensiva"
    // so we must find a way to make match vertigini to vertiginosa, since
    // they have the same meaning even if with a different form.
    
    
    
    public static void main(String[] args) { 
        
        /*String word1 = "etp";
        String word2 = "eyp";
        System.out.println(ScoreToRank.compareStrings(word1, word2));
        System.out.println(ScoreToRank.levenshteinSimilarity(word2, word2));
        System.exit(0);*/
        
        
        ICD9CMTable t = ICD9CMTable.init();
        System.out.println("Serialized files loaded: now putting into ontology");
        /*System.out.println("tree");

        System.out.println("tree");
        String query = "sindrome vertiginosa periferica crisi ipertensiva";
        double score = (0.8);
        System.out.println(score);
        t.queryIndexesFromSpecification(query, score);
        t.queryIndexesFromDescription(query, score);
        System.out.println(t.getCodeDescription(new ICD9CMCode("386.10")));*/
        //t.populateOntologyFromSerialized();
        System.out.println("Done.");
    }
    
    
    //////// Candidate Generation

    public List<Correction<String>> candidateGenerationFromCodeSpecificationsWithTitleBothExpanded(Annotator a, MultiWordSimilarity mws, double thereshold) {
        MapIterator<String,List<String>> mi = new MapIterator<String,List<String>>() {
            private final Iterator<String> icdcode_iterator = code_expanded_description.keySet().iterator();
            @Override
            public boolean hasNext() { return icdcode_iterator.hasNext(); }
            @Override
            public Iterator<Pair<String, List<String>>> iterator() { return this; }

            @Override
            public Pair<String, List<String>> next() {
                String code = icdcode_iterator.next();
                List<String> expanded = new LinkedList<>();
                if (code_to_expanded_specifications.containsKey(code)) {
                    for (String x: code_to_expanded_specifications.get(code)) {
                        expanded.add(code_expanded_description.get(code)+" "+x);
                    }
                } else {
                    expanded.add(code_expanded_description.get(code));
                }
                return new Pair<>(code,expanded);
            }

            
        };
        return ScoreToRank.inverseCandidateGeneration(a,mi,mws,thereshold,false);
    }
    
    
    /**
     * Returns a score for the description of each possible ICD9Code usage
     * @param a
     * @param mws
     * @param thereshold
     * @return 
     */
    public List<Correction<String>> candidateGenerationFromCodeSpecifications(Annotator a, MultiWordSimilarity mws, double thereshold) {
        return ScoreToRank.inverseCandidateGeneration(a, new ConcreteMapIterator<>(code_to_specifications), mws, thereshold,false);
    }
    
    public List<Correction<String>> candidateGenerationFromCodeExpandedSpecifications(Annotator a, MultiWordSimilarity mws, double thereshold) {
        return ScoreToRank.inverseCandidateGeneration(a, new ConcreteMapIterator<>(code_to_expanded_specifications), mws, thereshold,false);
    }
    
    /**
     * Returns a score for the integration between the description of the code and its usage
     * @param a
     * @param mws
     * @param thereshold
     * @return 
     */
    public List<Correction<String>> candidateGenerationFromCodeSpecificationsWithTitle(Annotator a, MultiWordSimilarity mws, double thereshold) {
        MapIterator<String,List<String>> mi = new MapIterator<String,List<String>>() {
            private final Iterator<String> icdcode_iterator = code_description_map.keySet().iterator();
            @Override
            public boolean hasNext() { return icdcode_iterator.hasNext(); }
            @Override
            public Iterator<Pair<String, List<String>>> iterator() { return this; }

            @Override
            public Pair<String, List<String>> next() {
                String code = icdcode_iterator.next();
                List<String> expanded = new LinkedList<>();
                if (code_to_specifications.containsKey(code)) {
                    for (String x: code_to_specifications.get(code)) {
                        expanded.add(code_description_map.get(code)+" "+x);
                    }
                } else {
                    expanded.add(code_description_map.get(code));
                }
                return new Pair<>(code,expanded);
            }

            
        };
        return ScoreToRank.inverseCandidateGeneration(a,mi,mws,thereshold,false);
    }
    
    /**
     * Returns a score for the description of the ICD9Code and the candidate generation
     * @param a
     * @param mws
     * @param thereshold
     * @return 
     */
    public List<Correction<String>> candidateGenerationForICD9CMTaxonomy(Annotator a, MultiWordSimilarity mws, double thereshold) {
        return ScoreToRank.inverseCandidateGeneration(a, new ConcreteMapIterator<>(code_description_map), mws, thereshold,false);
    }
    
    public List<Correction<String>> candidateGenerationForICD9CMExpandedTaxonomy(Annotator er, MultiWordSimilarity sim, double d) {
        return ScoreToRank.inverseCandidateGeneration(er, new ConcreteMapIterator<>(code_description_map), sim, d,false);
    }
    
    //// queries
    
    
    
    //code_description_map
    
    /**
     * Given a query, retrieves all the possible ICD9Codes associated
     * @param query         textual description 
     * @param precision     how many words in the query should be met
     * @return 
     */
    public Map<ICD9CMCode,Double> queryIndexesFromSpecification(String query, double precision) {
        String tokens[] = query.toLowerCase().split(" ");
        Map<ICD9CMCode,Double> toret = new TreeMap<>();
        for (String description : specification_to_code.keySet()) {
            String tmp = description.toLowerCase();
            double score = 0;
            for (String t : tokens)
                if (tmp.contains(t))
                    score++;
            score = score / tokens.length;
            if (score>=precision) {
                //System.out.println(description + " -> " + specification_to_code.get(description));
                toret.put(new ICD9CMCode(specification_to_code.get(description)), score);
            }
        }
        return toret;
    }
    
    public Map<ICD9CMCode,Double> queryIndexesFromDescription(String query, double precision) {
        String tokens[] = query.toLowerCase().split(" ");
        Map<ICD9CMCode,Double> toret = new TreeMap<>();
        for (String icd9cm_string : code_description_map.keySet()) {
            String description = code_description_map.get(icd9cm_string);
            String tmp = description.toLowerCase();
            double score = 0;
            for (String t : tokens)
                if (tmp.contains(t))
                    score++;
            score = score / tokens.length;
            if (score>=precision) {
                toret.put(new ICD9CMCode(icd9cm_string), score);
                //System.out.println(description + " -> " + icd9cm_string);
            }
        }
        return toret;
    }

    /**
     * Quite bizarre, but sometimes useful: retrieves the key associated to each value
     * @param value
     * @return 
     */
    @Override
    public ICD9CMCode getKeyByExactValueMatch(String value) {
        for (String key : code_description_map.keySet()) {
            if (code_description_map.get(key).equals(value))
                return new ICD9CMCode(key);
        }
        return null;
    }

    /**
     * Searches for the terms by using both the description and the specification
     * @param query
     * @param precision
     * @return 
     */
    @Override
    public Map<ICD9CMCode, Double> queryKeysByApproximateExtraction(String query, double precision) {
        Map<ICD9CMCode, Double> t1 = queryIndexesFromSpecification(query, precision);
        {
            Map <ICD9CMCode, Double> t2 = queryIndexesFromDescription(query, precision);
            for (ICD9CMCode k : t2.keySet()) {
                if (t1.containsKey(k)) {
                    // I increase the average value since the grasped value seems to be more important
                    t1.put(k,  Math.min((t2.get(k)+t1.get(k))/2+0.2, 1));
                }
            }
        }
        return t1;
    }

    
    @Override
    public Map<ICD9CMCode,Double> getKeyBySimilarityMatching(String query, Similarity single_word_similarity, double precision) {
        Map<ICD9CMCode,Double> m = new TreeMap(Collections.reverseOrder());
        MultiWordSimilarity mws = new MultiWordSimilarity(single_word_similarity);
        for (String icdcode : code_description_map.keySet()) {
            double score = mws.sim(query, code_description_map.get(icdcode));
            for (String descr : code_to_specifications.get(icdcode)) {
                score = Math.max(score,mws.sim(query, descr));
            }
            m.put(new ICD9CMCode(icdcode),score);
        }
        return m;
    }
    
    /////////////////

    @Override
    public void merge(MapIterator<ICD9CMCode, String> d) {
        throw new UnsupportedOperationException(this.getClass().getName()+": Do not add elements in here.");
    }

    /**
     * Iterates over the keys and the values
     * @return 
     */
    @Override
    public Iterator<Pair<ICD9CMCode, String>> iterator() {
        return new MapIterator<ICD9CMCode, String>(){
            private Iterator<String> elem = code_description_map.keySet().iterator();
            @Override
            public boolean hasNext() {
                return elem.hasNext();
            }

            @Override
            public Pair<ICD9CMCode, String> next() {
                String icd = elem.next();
                //if (icd.contains("755.10"))
                //    System.out.println("Breakhere");
                return new Pair<>(new ICD9CMCode(icd),code_description_map.get(icd));
            }

            @Override
            public Iterator<Pair<ICD9CMCode, String>> iterator() {
                return this;
            }
        };
    }

    public int size() {
        return code_description_map.size();
    }
    
    
}
