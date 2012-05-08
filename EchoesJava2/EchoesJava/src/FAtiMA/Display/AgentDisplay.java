/** 
 * AgentDisplay.java - Graphical Swing Display used to show the internal state of the 
 * agent's mind: its emotional state, active goals and intentions, knowledge
 * about the world, etc...
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
 * Created: 18/10/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 18/10/2005 - File created
 */

package FAtiMA.Display;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import FAtiMA.Agent;

/**
 * @author  bruno
 */
public class AgentDisplay {
    JFrame _frame;
    JTabbedPane _displayPane;
    Agent _ag;
    
    public AgentDisplay(Agent ag) {
        
    _ag = ag;
    _frame = new JFrame(ag.displayName());
    _frame.getContentPane().setLayout(new BoxLayout(_frame.getContentPane(),BoxLayout.Y_AXIS));
    _frame.setSize(550,650);
		
		_displayPane = new JTabbedPane();
		_frame.getContentPane().add(_displayPane);
		
		JPanel panel;
		
		panel = new EmotionalStatePanel();
		_displayPane.addTab("Emotional State",null,panel,"displays the character's emotional state");
		
		panel = new SocialRelationsPanel();
		_displayPane.addTab("Social Relations",null,panel,"displays the character's realtions state");
		
		panel = new KnowledgeBasePanel();
		_displayPane.addTab("Knowledge Base",null,panel,"displays all information stored in the KB");
		
		panel = new GoalsPanel();
		_displayPane.addTab("Goals",null,panel,"displays the character's goals and active intentions");
		
		panel = new EpisodicMemoryPanel();
		_displayPane.addTab("Episodic Memory", null, panel, "displays all the records in the character's episodic memory");
	
		
		JButton teste = new JButton("Save");
		teste.addActionListener(new TestAction(ag));
		teste.setText("Save");
		teste.setEnabled(true);
		_frame.getContentPane().add(teste);
		_frame.setVisible(true);
		_frame.setState(Frame.ICONIFIED);
    }
    
    public void update() {
        AgentDisplayPanel pnl = (AgentDisplayPanel) _displayPane.getSelectedComponent();
        if(pnl.Update(_ag)) {
            // do nothing
        }
    }
    
    public void dispose() {
    	_frame.dispose();
    }
}
