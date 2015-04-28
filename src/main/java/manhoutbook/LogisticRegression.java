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
package manhoutbook;

import disease.Dataset.DataIntegration.SmallOntology;
import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.Real.ICD9CMTable;
import disease.Dataset.interfaces.WordList;
import disease.Phase.Annotator;
import disease.ontologies.ICD9CMCode;
import disease.similarities.LowConfidenceRank;
import disease.similarities.MultiWordSimilarity;
import disease.utils.DictionaryType;
import disease.utils.MedicalRecord;
import disease.utils.ToObservation;
import disease.utils.wikipedia.WikipediaSingleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.classifier.OnlineLearner;

import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.math.Vector;

/**
 * 
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class LogisticRegression {
    
    private static final String TRAINED_MODEL_PATH = "data" + File.separator + "logmodel.mod";
    private static LogisticRegression self = null;
    private LogisticRegression() {}
    
    public static LogisticRegression getInstance() {
        if (self==null)
            self = new LogisticRegression();
        return self;
    }
    /*
	public static void main(String[] args) {
		LogisticRegression logisticRegression = new LogisticRegression();

                
		// Load the input data
		List<Observation> trainingData = logisticRegression
				.parseInputFile("input/inputData.csv");

		// Train a model
		logisticRegression.train(trainingData,TRAINED_MODEL_PATH);

		// Test the model
		logisticRegression.testModel(logisticRegression.readStoredModel(TRAINED_MODEL_PATH));
	}
*/
    
    
    /**
     *
     * @param inputFile
     * @return
     */
/*
    public List<Observation> parseInputFile(String inputFile) {
		List<Observation> result = new ArrayList<Observation>();
		BufferedReader br = null;
		String line = "";
		try {
			// Load the file which contains training data
			br = new BufferedReader(new FileReader(new File(inputFile)));
			// Skip the first line which contains the header values
			line = br.readLine();
			// Prepare the observation data
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				result.add(new Observation(values));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
    
    
*/
    
    private ICD9CMTable it = ICD9CMTable.init();
    private MultiWordSimilarity sim = MultiWordSimilarity.getInstance();
    private WordList it_medical_dict = OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.WHOLE_WORDS);//TODO: aggiungere parole mediche
    private SmallOntology dist = SmallOntology.getInstance();
    private WikipediaSingleton ws = WikipediaSingleton.getInstance();
    private LowConfidenceRank lcr = LowConfidenceRank.getInstance();
    
    @FunctionalInterface
   public interface LoopWithIndexAndSizeConsumer<T> {
       void accept(T t, int i, int n);
   }
    
   public static <T> void forEach(Collection<T> collection,
                                  LoopWithIndexAndSizeConsumer<T> consumer) {
      int index = 0;
      for (T object : collection){
         consumer.accept(object, index++, collection.size());
      }
   }
   
   private int count = 1;
   private void dit() {
       if (count%10==0)
                    System.out.println(count+" deca over "+sz);
                count++;
   }
   private int sz = 0;
    
    /**
     * Trains the logistic regression with the observation data, and then stores 
     * it in the default file path
     * @param trainData
     * @param tostore
     * @return
     */
    public OnlineLearner train(Collection<MedicalRecord> trainData, String tostore) {
        int size = 3;
        
        //size checking
        /*for (Observation o : trainData) {
            if (size==-1) {
                size = o.getTrainDataSize();
            } else {
                if (size != o.getTrainDataSize()) 
                {
                    System.err.println("Data has not the same size");
                    System.exit(1);
                }˙˙
            }
        }*/
        System.out.println("Whole Dataset");
            AdaptiveLogisticRegression olr = new AdaptiveLogisticRegression(2, size, new L1());
		// Train the model using 30 passes
            count = 1;
            sz = trainData.size();
             System.out.println("ticker to start = "+sz);
            
           
            for (MedicalRecord mr: trainData) {
                System.out.println("tick");
                this.dit();
                Annotator er = new Annotator(((mr.getCleanedRecord())));
                er.identitySemantics();
                for (ICD9CMCode exp: mr.getCodes()) {
                    String expected = exp.toString();
                    String father = expected.substring(0,3);
                    
                    {
            //Phase 1
            it.candidateGenerationForICD9CMTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 1;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());//map.put(c.getCorrectedWord().substring(0, 3), c.getScore());
            });
            it.candidateGenerationFromCodeSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                /*String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);*/
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 2;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
            });
            it.candidateGenerationFromCodeSpecificationsWithTitle(er, sim, 0.4).stream().forEach((c) -> {
                /*String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);*/
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 3;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
            });
            
            ws.candidateGenerationForWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                c.getCorrectedWord().stream().forEach((candidate) -> {
                    /*Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);*/
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 4;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                });
                
            });
            
            /*ws.candidateGenerationForWikiContent(er, sim, 0.4).stream().filter((t1) -> {
                if (t1.getCorrectedWord().isEmpty()) {
                    return false;
                } else
                    return true;
            }).forEach((c) -> {
                ///c.getCorrectedWord().stream().map((xx) -> {
                    return xx.getThreeDigitsFather().toString();
                }).forEach((xx) -> {
                    Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);
                });///
                c.getCorrectedWord().stream().forEach((candidate) -> {
                    ///Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);///
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 5;
                double sem = dist.getWeight(candidate.toString(), expected);
                int equals = candidate.toString().substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                });
            });*/
            
            it.candidateGenerationForICD9CMExpandedTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 6;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                
            });
            /*it.candidateGenerationFromCodeExpandedSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 7;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                });
            it.candidateGenerationFromCodeSpecificationsWithTitleBothExpanded(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 8;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
               Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                
            });
            
            ws.candidateGenerationForExpandedWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((candidate) -> {
                    ///Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);///
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 9;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                
                });
                
            });
            
            ws.candidateGenerationForExpandedWikiContent(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((candidate) -> {
                    ///Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);///
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 10;
                double sem = dist.getWeight(candidate.toString(), expected);
                int equals = candidate.toString().substring(0, 3).equals(father) ? 1 : 0;
                Observation observation = (new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                olr.train(observation.getOkClass(), observation.getTrainData());
                });
            });*/
            
        }
                    
                }
            }
            
		
                /*
			// Every 10 passes check the accuracy of the trained model
			if (pass % 10 == 0) {
				Auc eval = new Auc(0.5);
				for (Observation observation : trainData) {
					eval.add(observation.getActual(),
							olr.classifyScalar(observation.getVector()));
				}
				System.out.format(
						"Pass: %2d, Learning rate: %2.4f, Accuracy: %2.4f\n",
						pass, olr.currentLearningRate(), eval.auc());
			}
		}*/
            olr.close();
            try {
                ModelSerializer.writeBinary(tostore,
                        olr.getBest().getPayload().getLearner());
                return olr.getBest().getPayload().getLearner();
            } catch (IOException ex) {
                Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            
                
	}
        
        public CrossFoldLearner readStoredModel(String whereto) {
            InputStream in;
            try {
                in = new FileInputStream(whereto);
                CrossFoldLearner best;
                Class<CrossFoldLearner> c = CrossFoldLearner.class;
                best = ModelSerializer.<CrossFoldLearner>readBinary(in, c);
                in.close();
                return best;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

	
        
        
        
        private CrossFoldLearner learnt;
        public LogisticRegression(String learnt_model_path) {
            InputStream in;
            learnt = null;
            try {
                in = new FileInputStream(learnt_model_path);
                Class<CrossFoldLearner> c = CrossFoldLearner.class;
                learnt = ModelSerializer.<CrossFoldLearner>readBinary(in, c);
                in.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public LogitResult testModel(CrossFoldLearner olr, Observation obs) {
		//Observation newObservation = new Observation(new String[] { "family",
		//		"10", "100000", "0" });
		Vector result = olr.classifyFull(obs.getTrainData());
                return new LogitResult(result.get(1),result.get(0));
		/*System.out.println("------------- Testing -------------");
		System.out.format("Probability of not fraud (0) = %.3f\n",
				result.get(0));
		System.out.format("Probability of fraud (1)     = %.3f\n",
				result.get(1));*/
	}
        
        public LogitResult testModel(Observation obs) {
            return testModel(learnt,obs);
        }

	/*public class Observation {
		private DenseVector vector = new DenseVector(4);
		private int actual;

		public Observation(String[] values) {
			ConstantValueEncoder interceptEncoder = new ConstantValueEncoder(
					"intercept");
			StaticWordValueEncoder encoder = new StaticWordValueEncoder(
					"feature");

			interceptEncoder.addToVector("1", vector);
			vector.set(0, Double.valueOf(values[1]));
			// Feature scaling, divide mileage by 10000
			vector.set(1, Double.valueOf(values[2]) / 10000);
			encoder.addToVector(values[0], vector);

			this.actual = Integer.valueOf(values[3]);
		}

		public Vector getVector() {
			return vector;
		}

		public int getActual() {
			return actual;
		}
	}*/

    public void setLearner(CrossFoldLearner ol) {
        this.learnt = ol;
    }
}
