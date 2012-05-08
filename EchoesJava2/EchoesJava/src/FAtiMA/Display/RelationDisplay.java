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

import FAtiMA.socialRelations.LikeRelation;
import FAtiMA.socialRelations.Relation;
import FAtiMA.socialRelations.RespectRelation;
import FAtiMA.util.enumerables.RelationType;


/**
 * @author João Dias
 *
 */
public class RelationDisplay {
    
    JPanel _panel;
    JProgressBar _bar;
    
    public RelationDisplay(Relation rd) {
        _panel = new JPanel();
        _panel.setMaximumSize(new Dimension(300,60));

        _bar = new JProgressBar(-10,10);
        _bar.setStringPainted(true);
        
        if(rd instanceof LikeRelation){
        	_panel.setBorder(BorderFactory.createTitledBorder(RelationType.getName(RelationType.LIKE) + " " + rd.getTarget()));
        	_bar.setForeground(new Color(255,0,0));
        }else if(rd instanceof RespectRelation){
        	_panel.setBorder(BorderFactory.createTitledBorder(RelationType.getName(RelationType.RESPECT) + " " + rd.getTarget()));
        	_bar.setForeground(new Color(0,100,0));
        }
        
        this.setValue(rd.getValue());
        _panel.add(_bar);
    }
    
    public JPanel getPanel() {
        return _panel;
    }
    
    public JProgressBar getBar() {
        return _bar;
    }
    
    public void setValue(float relationValue) {
        Float aux = new Float(relationValue);
        _bar.setString(aux.toString());
        _bar.setValue(aux.intValue());
    }

}
