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
package disease;
//disease = DIagnosis StAtistic SEmantic 



import disease.Phase.View;
import disease.Phase.Controller;
import disease.Phase.Orchestrator;
import disease.Phase.TestPhase;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author vasistas
 */
public class DISeASEMain {
	

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean train = false ; 
        boolean interrupted = false;
        //Training part: all the training is persisted in hard disk, so you don't have to
        //spend hours on coding and running the algorithm
        
        if ((args.length>0) && (args[0].equals("interrupted"))) {
            train = true;
            interrupted = true;
        }
        
        else if ((args.length>0) && (args[0].equals("train"))) {
            train = true;
            //Clean previous results - remove previous partial training ones
            File toremove = new File("Related");
            Path directory = Paths.get("Related");
            String dirs[] = {"Related", "data"+File.separator+"smallOntology.g","db_id_counter"};

            SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
                {
                   Files.delete(file);
                   return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            };
            for (String x : dirs) {
                Path folder = Paths.get(x);
                try {
                    if (folder.toFile().exists())
                        Files.walkFileTree(folder, visitor);
                } catch (IOException ex) {
                    Logger.getLogger(DISeASEMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //running the actual training --> FOLLOW THIS METHOD
        }
        
        //Double-check
        else  {
            train = false;
            interrupted = false;
        }
        
        //This operation brings in main memory the references as singleton
        //of the persisted data. This call is mandatory.
        Orchestrator.load_the_model(train,interrupted);
        
        if ((args.length>0) && (args[0].equals("benchmark"))) {
            TestPhase tf = new TestPhase();
            tf.validate_the_model();
            System.exit(0);
        }
        
        {
            //GUI mode
            //TODO: disable the view and import the method for classification
            View view = new View();
            Controller controller = new Controller(view);
            
            //Set the gui visible #blocks here
        } 
        
        
        
        
        
        
    }
    






}