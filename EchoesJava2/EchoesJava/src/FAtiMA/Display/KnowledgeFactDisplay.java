/** 
 * KnowledgeFactDisplay.java - Graphical Swing object that displays a specific fact
 * stored in the KnowledgeBase
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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class KnowledgeFactDisplay {
    
    JPanel _panel;
    JLabel _label;
    
    public KnowledgeFactDisplay(String name, String value) {
        _panel = new JPanel();
        _panel.setBorder(BorderFactory.createTitledBorder(name));
        _panel.setMaximumSize(new Dimension(300,60));

        _label = new JLabel(value);
        _panel.add(_label);
    }
    
    public void SetValue(String value) {
        _label.setText(value);
    }
    
    public JPanel GetPanel() {
        return _panel;
    }
}
