/** 
 * EmotionalStatePanel.java - Graphical Swing Panel that shows the character's emotional
 * state by displaying the intensity of each emotion trough EmotionDisplay's
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

import java.awt.Color;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import FAtiMA.Agent;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.EmotionalState;


public class EmotionalStatePanel extends AgentDisplayPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JProgressBar _moodBar;
    
    JPanel _emotionsPanel;
    
    protected Hashtable _emotionDisplays;
    
    public EmotionalStatePanel() {
        super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        _emotionDisplays = new Hashtable();

        
        /*JPanel arousalPanel = new JPanel();
		arousalPanel.setBorder(BorderFactory.createTitledBorder("Arousal"));
		arousalPanel.setMaximumSize(new Dimension(300,60));
		
		_arousalBar = new JProgressBar(0,100);
		_arousalBar.setValue(5);
		_arousalBar.setStringPainted(true);
		_arousalBar.setForeground(new Color(255,0,255));
		arousalPanel.add(_arousalBar);*/

		
        JPanel moodPanel = new JPanel();
		moodPanel.setBorder(BorderFactory.createTitledBorder("Mood"));
		moodPanel.setMaximumSize(new Dimension(300,60));
			
		_moodBar = new JProgressBar(-100,100);
		_moodBar.setValue(0);
		_moodBar.setStringPainted(true);
		_moodBar.setForeground(new Color(0,0,0));
		moodPanel.add(_moodBar);
		
		_emotionsPanel = new JPanel();
		_emotionsPanel.setLayout(new BoxLayout(_emotionsPanel,BoxLayout.Y_AXIS));
		
		JScrollPane emotionsScroll = new JScrollPane(_emotionsPanel);
		emotionsScroll.setBorder(BorderFactory.createTitledBorder("Emotions"));
		
		//this.add(arousalPanel);
		this.add(moodPanel);
		this.add(emotionsScroll);
    }
    
    public boolean Update(Agent ag) {
        Float aux;
        ActiveEmotion em;
        EmotionalState es = EmotionalState.GetInstance();
        
        //aux = new Float(es.GetArousal());
        //_arousalBar.setString(aux.toString());
        //_arousalBar.setValue(Math.round(es.GetArousal()*10));
      
        aux = new Float(es.GetMood());
        _moodBar.setString(aux.toString());
        _moodBar.setValue(Math.round(es.GetMood()*10));
        
        if(_emotionDisplays.keySet().equals(es.GetEmotionKeysSet())) {
            //in this case, we just have to update the values for the intensity of emotions
            //since the emotions displayed in the previous update are the same emotions
            //in the current update
             Iterator it = es.GetEmotionsIterator();
             EmotionDisplay emotionDisplay;
             while(it.hasNext()) {
                 em = (ActiveEmotion) it.next();     
                 emotionDisplay = (EmotionDisplay) _emotionDisplays.get(em.GetHashKey());
                 emotionDisplay.SetValue(em.GetIntensity());
             }    
        }
        else {
            //in this case, there's a new emotion added to or removed from  the emotional
            //state, so we have to clear all emotions and start displaying them all again
            
            _emotionsPanel.removeAll(); //removes all displayed emotions from the panel
            _emotionDisplays.clear();
            
            Iterator it = es.GetEmotionsIterator();
            EmotionDisplay emotionDisplay;
            while(it.hasNext()) {
                em = (ActiveEmotion) it.next();
                emotionDisplay = new EmotionDisplay(em);
                
                _emotionsPanel.add(emotionDisplay.GetEmotionPanel());
        
                _emotionDisplays.put(em.GetHashKey(),emotionDisplay);      
           }
            DisplayStrongestEmotion(es);
            return true;
        }
        
        return false;
    }
    
    private void DisplayStrongestEmotion(EmotionalState es) {
        ActiveEmotion em = es.GetStrongestEmotion();
        
        if(em != null) {
        	Iterator it = _emotionDisplays.values().iterator();
	        EmotionDisplay emotionDisplay;
	        while(it.hasNext()) {
	            emotionDisplay = (EmotionDisplay) it.next();
	            emotionDisplay.GetEmotionPanel().setOpaque(true);
	        }
	        emotionDisplay = (EmotionDisplay) _emotionDisplays.get(em.GetHashKey());
	        if(emotionDisplay != null) {
	        	emotionDisplay.GetEmotionPanel().setBackground(new Color(255,0,0));
	        }
        }
    }

}
