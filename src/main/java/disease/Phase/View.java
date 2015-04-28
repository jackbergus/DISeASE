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


package disease.Phase;

import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;






import javax.swing.*;
import org.apache.commons.lang3.StringUtils;





/**
*
* @author Alexander Pollok
*/
public class View extends JFrame {



	private static final long serialVersionUID = 1L;
	private JLabel queryLabel = new JLabel("query:");
	private JTextArea queryText = new JTextArea();
	
	private JButton execButton = new JButton("Classify");
	
	private JLabel classification1 = new JLabel();
	private JLabel classification2 = new JLabel();
	private JLabel classification3 = new JLabel();
	
	private JProgressBar progressBar1 = new JProgressBar(0,1000);
	private JProgressBar progressBar2 = new JProgressBar(0,1000);
	private JProgressBar progressBar3 = new JProgressBar(0,1000);
	


	private JLabel quality1 = new JLabel();
	private JLabel quality2 = new JLabel();
	private JLabel quality3 = new JLabel();
	
	
	
	public View(){
		
		JPanel diseasePanel = new JPanel();
		//diseasePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800,380);
		this.setLocationRelativeTo(null);
	
		this.setTitle("DISEASE medical record classification by Giacomo Bergami and Alexander Pollok");
		
		diseasePanel.add(queryLabel);
		queryText.setWrapStyleWord(true);
		queryText.setLineWrap(true);
		diseasePanel.add(queryText);
		diseasePanel.add(execButton);
		diseasePanel.add(classification1);
		diseasePanel.add(classification2);
		diseasePanel.add(classification3);
		diseasePanel.add(progressBar1);
		diseasePanel.add(progressBar2);
		diseasePanel.add(progressBar3);
		diseasePanel.add(quality1);
		diseasePanel.add(quality2);
		diseasePanel.add(quality3);
		
		diseasePanel.setLayout(null);
		
		//object.setBounds(x, y, width, height);
		queryLabel.setBounds(20, 20, 60, 50);
		queryText.setBounds(80, 20, 680, 50);
	
		execButton.setBounds(20, 90, 740, 20);
		
		classification1.setBounds(150, 200, 70, 20);
		classification2.setBounds(150, 230, 70, 20);
		classification3.setBounds(150, 260, 70, 20);
		
		progressBar1.setBounds(250, 200, 300, 20);
		progressBar2.setBounds(250, 230, 300, 20);
		progressBar3.setBounds(250, 260, 300, 20);

		progressBar1.setVisible(false);
		progressBar2.setVisible(false);
		progressBar3.setVisible(false);
		
		quality1.setBounds(590,200,70,20);
		quality2.setBounds(590,230,70,20);
		quality3.setBounds(590,260,70,20);
        
		this.add(diseasePanel);
		

		this.setVisible(true);
                


		
	}
	
	public String getQueryText(){
		return queryText.getText();
	}
	
	public void setClassificationSolution(GUIResult guiresult) {
		ArrayList<Double> keys = new ArrayList();
		ArrayList<String> values = new ArrayList();
                for (Entry<Double,Set<String>> entry : guiresult.getRankedResults().entrySet()) {
			  Double key = (double)Math.round(entry.getKey()*1000)/1000; 
                          
			  String value = StringUtils.join(entry.getValue()," && ");
			  keys.add(key);
			  values.add(value);
        }

        int length = guiresult.getRankedResults().size();
        for (   Pair<Double, Set<String>> x : new ConcreteMapIterator<>(guiresult.getRankedResults())) {
            System.out.println(x.getFirst() + " ~ " +StringUtils.join(x.getSecond()));
        }
		//classification1.setText("ICD: " + values.get(length-1));
		//classification2.setText("ICD: " + values.get(length-2));
		//classification3.setText("ICD: " + values.get(length-3));
		
		//progressBar1.setVisible(true);
		//progressBar2.setVisible(true);
		//progressBar3.setVisible(true);
		
		//Double scaledkey1 = keys.get(length-1)*1000;
		//Double scaledkey2 = keys.get(length-2)*1000;
		//Double scaledkey3 = keys.get(length-3)*1000;
		//progressBar1.setValue(scaledkey1.intValue());
		//progressBar2.setValue(scaledkey2.intValue());
		//progressBar3.setValue(scaledkey3.intValue());
		
		//quality1.setText(keys.get(length-1).toString());
		//quality2.setText(keys.get(length-2).toString());
		//quality3.setText(keys.get(length-3).toString());
		
	}
	
	public void addExecutionListener(ActionListener listenerForExecButton) {
		execButton.addActionListener(listenerForExecButton);
	}
	
	void displayErrorMessage(String errorMessage){
		JOptionPane.showMessageDialog(this, errorMessage);
	}
	
	

	
}
