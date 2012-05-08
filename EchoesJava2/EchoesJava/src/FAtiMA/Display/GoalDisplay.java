/** 
 * GoalDisplay.java - Graphical Swing object that displays information about a 
 * given goal
 *  
 * Copyright (C) 2006 GAIPS/INESC-ID 
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Company: GAIPS/INESC-ID
 * Project: FAtiMA
 * Created: 26/10/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 26/10/2005 - File created
 */
package FAtiMA.Display;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import FAtiMA.deliberativeLayer.goals.ActivePursuitGoal;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.goals.InterestGoal;


/**
 * @author João Dias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoalDisplay {

	JPanel _panel;
	JLabel _goalType;
    JLabel _impOfSuccess;
    JLabel _impOfFailure;
    

    public GoalDisplay(Goal g) {
    	_panel = new JPanel();
        _panel.setBorder(BorderFactory.createTitledBorder(g.GetName().toString()));
        _panel.setLayout(new BoxLayout(_panel,BoxLayout.Y_AXIS));
        _panel.setMaximumSize(new Dimension(350,300));
        _panel.setMinimumSize(new Dimension(350,300));
        
        _impOfSuccess = new JLabel();
        _impOfFailure = new JLabel();
        _goalType = new JLabel();
        
        Dimension d1 = new Dimension(70,25);
        Dimension d2 = new Dimension(30,25);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        JLabel lbl = new JLabel("I. Success:");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        pnl.add(lbl);
        
        _impOfSuccess.setMaximumSize(d2);
        _impOfSuccess.setMinimumSize(d2);
        pnl.add(_impOfSuccess);
        lbl = new JLabel("I. Failure:");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        pnl.add(lbl);
        pnl.add(_impOfFailure);
        
        _panel.add(pnl);
        
        /*pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        lbl = new JLabel("I. Failure");
        lbl.setMaximumSize(new Dimension(100,25));
        pnl.add(lbl);
        pnl.add(_impOfFailure);
        
        _panel.add(pnl);*/
        
        pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        lbl = new JLabel("Goal Type:");
        lbl.setMaximumSize(d1);
        pnl.add(lbl);
        pnl.add(_goalType);
        
        _panel.add(pnl);
        
        Float aux;
        
        aux = new Float(g.GetImportanceOfSuccess());
        _impOfSuccess.setText(aux.toString());
        
        aux = new Float(g.GetImportanceOfFailure());
        _impOfFailure.setText(aux.toString());
        
        if(g.getClass().equals(ActivePursuitGoal.class)) {
        	init((ActivePursuitGoal) g);
        	
        }
        else {
        	init((InterestGoal) g);
        }
    }
    
    private void init(InterestGoal g) {
        _goalType.setText("Interest");
        ConditionsPanel protectedConditions = new ConditionsPanel("Protected Constraints",
        		g.getProtectionConstraints().listIterator());
        _panel.add(protectedConditions);
    }
    
    private void init(ActivePursuitGoal g) {
    	_goalType.setText("ActivePursuit");
    	ConditionsPanel preconditions = new ConditionsPanel("Preconditions",
        		g.GetPreconditions().listIterator());
        _panel.add(preconditions);
        
        ConditionsPanel successConditions = new ConditionsPanel("Success Conditions",
        		g.GetSuccessConditions().listIterator());
        _panel.add(successConditions);
        
        ConditionsPanel failureConditions = new ConditionsPanel("Failure Conditions",
        		g.GetFailureConditions().listIterator());
        _panel.add(failureConditions);
        
    }
  
    public JPanel getGoalPanel() {
        return _panel;
    }
}
