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
package disease.Dataset.Real;

import com.opencsv.CSVReader;
import disease.ontologies.ICD9CMCode;
import disease.utils.MedicalRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasistas
 */
public class MedicalRecordDataset {
    
    Set<MedicalRecord> mrd;
    
    public MedicalRecordDataset() {
        this.mrd = new HashSet<>();
    }
    
    public Set<MedicalRecord> getWholeDataset() {
        return this.mrd;
    }
    
    public MedicalRecordDataset feedWholeDataset(String datafolder_path, boolean fed_only_disease_code) {
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(Paths.get(datafolder_path));
        } catch (IOException ex) {
            Logger.getLogger(MedicalRecordDataset.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        for (Path p : stream) {
            if ((!Files.isDirectory(p)) && p.getFileName().toString().endsWith(".csv")) {
                //Stores each part of the data
                System.out.println(p.toString());
                feedCSVTraining(p.toString(),fed_only_disease_code);
            }
        }
        System.out.println("Finito");
        return this;
    }
    
    public void feedCSVTraining(String filename, boolean fed_only_disease_code) {
        CSVReader r = null;
        try {
            r = new CSVReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MedicalRecordDataset.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        Iterator<String[]> it = r.iterator();
        String[] row = it.next();
        String record = row[0];
        Set<ICD9CMCode> icd9list = new HashSet<>();
        System.out.println(row.length+" "+filename);
        if (row[1].matches("\\d(.)*") || (!fed_only_disease_code)) { //Do not store
            icd9list.add(new ICD9CMCode(row[1]));
        }
        
        while (it.hasNext()) {
            row = it.next();
            if (!record.equals(row[0])) {
                this.mrd.add(new MedicalRecord(record,icd9list));
                icd9list = new HashSet<>();
                record = row[0];
            }
            if (row[1].matches("\\d(.)*") || (!fed_only_disease_code)) { //Do not store
                icd9list.add(new ICD9CMCode(row[1]));
            }
        }
        //add last medical record
        this.mrd.add(new MedicalRecord(record,icd9list));
        try {
            r.close();
        } catch (IOException ex) {
            Logger.getLogger(MedicalRecordDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Removes ambiguous entries in the dataaset
    public void filter_by_data_quality() {
        System.out.println("NOTHING DONE ON DATA QUALITY FILTERING");
    }
    
    //Testing the CSV reading
    public static void main(String []s) {
        MedicalRecordDataset training = new MedicalRecordDataset();
        training.feedWholeDataset("data"+File.separator+"Classified", true);
        System.out.println(training.mrd.size());
    }
    
}
