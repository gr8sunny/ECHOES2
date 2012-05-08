/** 
 * MemoryDetailPanel.java - 
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
 * Created: 20/Jul/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 20/Jul/2006 - File created
 * **/

package FAtiMA.Display;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import FAtiMA.autobiographicalMemory.ActionDetail;
import FAtiMA.util.enumerables.EmotionType;


public class MemoryDetailPanel extends JPanel {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTextField _id;
    /*JTextField _cause;
    JTextField _effect;*/
	
	JTextField _subject;
    JTextField _action;
    JTextField _target;
    JTextField _parameters;
    
    JTextField _feeling;
    JTextField _evaluation;
	
	public MemoryDetailPanel(ActionDetail detail) {
		
		super();
		this.setBorder(BorderFactory.createRaisedBevelBorder());
        this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        this.setMinimumSize(new Dimension(550,30));
        this.setMaximumSize(new Dimension(550,30));
        
        _id = new JTextField(new Integer(detail.getID()).toString());
        _id.setMinimumSize(new Dimension(30,30));
        _id.setMaximumSize(new Dimension(30,30));
        _id.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_id);
        
        /*_cause = new JTextField(new Integer(detail.getCause()).toString());
        _cause.setMinimumSize(new Dimension(30,30));
        _cause.setMaximumSize(new Dimension(30,30));
        _cause.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_cause);
        
        _effect = new JTextField(new Integer(detail.getEffect()).toString());
        _effect.setMinimumSize(new Dimension(30,30));
        _effect.setMaximumSize(new Dimension(30,30));
        _effect.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_effect);*/
        
        _subject = new JTextField(detail.getSubject());
        _subject.setMinimumSize(new Dimension(50,30));
        _subject.setMaximumSize(new Dimension(50,30));
        _subject.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_subject);
        
        _action = new JTextField(detail.getAction());
        _action.setMinimumSize(new Dimension(80,30));
        _action.setMaximumSize(new Dimension(80,30));
        _action.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_action);
        
        _target = new JTextField(detail.getTarget());
        _target.setMinimumSize(new Dimension(80,30));
        _target.setMaximumSize(new Dimension(80,30));
        _target.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_target);
        
        _parameters = new JTextField(detail.getParameters().toString());
        _parameters.setMinimumSize(new Dimension(100,30));
        _parameters.setMaximumSize(new Dimension(100,30));
        _parameters.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_parameters);
	
        _feeling = new JTextField(EmotionType.GetName(detail.getEmotion().GetType()) + "-" 
        		+ detail.getEmotion().GetPotential());
        _feeling.setMinimumSize(new Dimension(110,30));
        _feeling.setMaximumSize(new Dimension(110,30));
        _feeling.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_feeling);
        
        
    	_evaluation = new JTextField(detail.getEvaluation().toString());
        _evaluation.setMinimumSize(new Dimension(100,30));
        _evaluation.setMaximumSize(new Dimension(100,30));
        _evaluation.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        this.add(_evaluation);
	}

}
