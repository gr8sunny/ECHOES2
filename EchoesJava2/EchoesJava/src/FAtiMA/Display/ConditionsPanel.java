/** 
 * ConditionsPanel.java - Graphical Swing Panel used to show conditions 
 * (Preconditions in goals, effects, step's preconditions, etc) 
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
 * Created: 17/01/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 17/01/2006 - File created
 */

package FAtiMA.Display;

import java.awt.Dimension;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FAtiMA.conditions.Condition;


public class ConditionsPanel extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConditionsPanel(String title, ListIterator conditions) {
		
		super();
		this.setBorder(BorderFactory.createTitledBorder(title));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        JPanel prop = new JPanel();
		//prop.setBorder(BorderFactory.createTitledBorder(title));
		prop.setLayout(new BoxLayout(prop,BoxLayout.Y_AXIS));
		prop.setMaximumSize(new Dimension(350,75));
		prop.setMinimumSize(new Dimension(350,75));
		
		JScrollPane propertiesScroll = new JScrollPane(prop);
		
		this.add(propertiesScroll);
		
		JLabel lbl;
		Condition c;
		
		while(conditions.hasNext()) {
			c = (Condition) conditions.next();
			lbl = new JLabel(c.toString());
			prop.add(lbl);
		}
		
	}
}
