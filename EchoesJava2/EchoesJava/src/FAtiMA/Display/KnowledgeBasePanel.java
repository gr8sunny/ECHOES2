/** 
 * KnowledgeBasePanel.java - Graphical Swing Panel that shows all the facts stored in the
 * KnowledgeBase
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

import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FAtiMA.Agent;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.knowledgeBase.KnowledgeSlot;


public class KnowledgeBasePanel extends AgentDisplayPanel {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList _knowledgeFactList;
    
    private JPanel _knowledgeFactsPanel;
    
    public KnowledgeBasePanel() {
        super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        _knowledgeFactList = new ArrayList();
        
        _knowledgeFactsPanel = new JPanel();
		_knowledgeFactsPanel.setLayout(new BoxLayout(_knowledgeFactsPanel,BoxLayout.Y_AXIS));
		
		JScrollPane emotionsScroll = new JScrollPane(_knowledgeFactsPanel);
		
		this.add(emotionsScroll);
		
    }
   
    public boolean Update(Agent ag) {
        KnowledgeBase kb = KnowledgeBase.GetInstance();
        
        ListIterator li = kb.GetFactList();
        
        KnowledgeSlot slot;
        KnowledgeFactDisplay kDisplay;
        int index;
        
        boolean newFactAdded = false;
        
        while (li.hasNext()) {
            index = li.nextIndex();
            slot = (KnowledgeSlot) li.next();
            if(index >= _knowledgeFactList.size()) {
                newFactAdded = true;
                kDisplay = new KnowledgeFactDisplay(slot.getName(),slot.getValue().toString());
                _knowledgeFactList.add(kDisplay);
                _knowledgeFactsPanel.add(kDisplay.GetPanel());
            }
            else {
                kDisplay = (KnowledgeFactDisplay) _knowledgeFactList.get(index);
                kDisplay.SetValue(slot.getValue().toString());
            }
        }
        
        return newFactAdded;
    }

}
