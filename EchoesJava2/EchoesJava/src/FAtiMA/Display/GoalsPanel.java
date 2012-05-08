/** 
 * GoalsPanel.java - Graphical Swing Panel that shows all of the agent's goals
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
 * Created: 02/11/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 02/11/2005 - File created
 */

package FAtiMA.Display;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import FAtiMA.Agent;
import FAtiMA.deliberativeLayer.EmotionalPlanner;
import FAtiMA.deliberativeLayer.Intention;
import FAtiMA.deliberativeLayer.goals.Goal;


public class GoalsPanel extends AgentDisplayPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap _intentionDisplays;
    private ArrayList _goalDisplays;
    private JPanel _goals;
    private JPanel _intentions;
    
    public GoalsPanel() {
        
        super();
        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        _intentionDisplays = new HashMap();
        _goalDisplays = new ArrayList();
		
		_goals = new JPanel();
		_goals.setBorder(BorderFactory.createTitledBorder("Goals"));
		_goals.setLayout(new BoxLayout(_goals,BoxLayout.Y_AXIS));
		
		_goals.setMaximumSize(new Dimension(350,200));
		_goals.setMinimumSize(new Dimension(350,200));
		
		JScrollPane goalsScrool = new JScrollPane(_goals);
		
		splitter.add(goalsScrool);
		
		_intentions = new JPanel();
		_intentions.setBorder(BorderFactory.createTitledBorder("Active Intentions"));
		_intentions.setLayout(new BoxLayout(_intentions,BoxLayout.Y_AXIS));
		
		JScrollPane intentionsScroll = new JScrollPane(_intentions);
		
		splitter.add(intentionsScroll);
		
		this.add(splitter);
    }
    
    
    public boolean Update(Agent ag) {
    	
    	boolean update = false;
        
        EmotionalPlanner planner = ag.getDeliberativeLayer().getEmotionalPlanner();
        
        if(_goalDisplays.size() != ag.getDeliberativeLayer().GetGoals().size()) {
        	update = true;
        	_goals.removeAll();
        	_goalDisplays.clear();
        	
        	Iterator it = ag.getDeliberativeLayer().GetGoals().iterator();
        	GoalDisplay gDisplay;
        	Goal g;
        	while(it.hasNext()) {
        		g = (Goal) it.next();
        		gDisplay = new GoalDisplay(g);
        		
        		_goals.add(gDisplay.getGoalPanel());
        		_goalDisplays.add(gDisplay);
        	}
        }
        
        if(_intentionDisplays.keySet().equals(planner.GetIntentionKeysSet())) {
            //in this case, we just have to update the values for the intensity of emotions
            //since the emotions displayed in the previous update are the same emotions
            //in the current update
             Iterator it = planner.GetIntentionsIterator();
             IntentionDisplay iDisplay;
             Intention i;
             while(it.hasNext()) {
                 
                 i = (Intention) it.next();     
                 iDisplay = (IntentionDisplay) _intentionDisplays.get(i.getGoal().GetName().toString());
                 iDisplay.Update(i);
             }    
        }
        else {
        	update = true;
            _intentions.removeAll(); //removes all displayed intentions from the panel
            _intentionDisplays.clear();
            
            Iterator it = planner.GetIntentionsIterator();
            IntentionDisplay iDisplay;
            Intention i;
            while(it.hasNext()) {
                i = (Intention) it.next();
                iDisplay = new IntentionDisplay(i);
                
                _intentions.add(iDisplay.getIntentionPanel());
                _intentionDisplays.put(i.getGoal().GetName(),iDisplay);
          
           }
        }   
        return update;
    }
}
