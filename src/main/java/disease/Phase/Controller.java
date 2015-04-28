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

import disease.Dataset.DataIntegration.SmallOntology;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import disease.Phase.GUIResult;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;




/**
*
* @author Alexander Pollok
*/

public class Controller {
	
	private View view;

	
	public Controller(View view){
		
		this.view = view;
                
		view.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                  int confirmed = JOptionPane.showConfirmDialog(null, 
                      "Are you sure you want to exit the program?", "Exit Program Message Box",
                      JOptionPane.YES_NO_OPTION);

                  if (confirmed == JOptionPane.YES_OPTION) {
                      SmallOntology.getInstance().saveAll();
                        view.dispose();
                  }
                }
              });
		this.view.addExecutionListener(new ExecuteListener());
	}
	
	
	/**
	 * is executed when the classify-button is pressed on the GUI
	 * 
	 * @author Alex
	 *
	 */
	class ExecuteListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			String querytext = "";
			
			try{
				querytext = view.getQueryText();
				GUIResult guiresult = Orchestrator.run_the_algorithm(querytext);

				
				view.setClassificationSolution(guiresult);
			}
			catch(Error e){
                            e.printStackTrace();
				view.displayErrorMessage(e.toString());
			}
			
			
		}
		
		
	}
	
}
