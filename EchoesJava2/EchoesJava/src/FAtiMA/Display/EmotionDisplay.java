/** 
 * EmotionDisplay.java - Graphical Swing object that shows an emotion's intensity
 * by using a coloured progress bar. 
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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.util.enumerables.EmotionType;


/**
 * @author João Dias
 *
 */
public class EmotionDisplay {
    
    JPanel _panel;
    JProgressBar _bar;
    
    public EmotionDisplay(ActiveEmotion em) {
        _panel = new JPanel();
        _panel.setBorder(BorderFactory.createTitledBorder(EmotionType.GetName(em.GetType()) + " " + em.GetCause().toString()));
        _panel.setMaximumSize(new Dimension(300,60));

        _bar = new JProgressBar(0,100);
        _bar.setStringPainted(true);
        switch (em.GetType()) {
        	case EmotionType.FEAR: {
        	    _bar.setForeground(new Color(255,0,0));
        	    break;
        	}
        	case EmotionType.HOPE: {
        	    _bar.setForeground(new Color(0,255,0));
        	    break;
        	}
        	case EmotionType.DISTRESS: {
        	    _bar.setForeground(new Color(100,0,0));
        	    break;
        	}
        	case EmotionType.JOY: {
        	    _bar.setForeground(new Color(0,100,0));
        	    break;
        	}
        }
        
        this.SetValue(em.GetIntensity());
        
        _panel.add(_bar);
    }
    
    public JPanel GetEmotionPanel() {
        return _panel;
    }
    
    public JProgressBar GetEmotionBar() {
        return _bar;
    }
    
    public void SetValue(float intensity) {
        Float aux = new Float(intensity);
        _bar.setString(aux.toString());
        _bar.setValue(Math.round(intensity*10));
    }

}
