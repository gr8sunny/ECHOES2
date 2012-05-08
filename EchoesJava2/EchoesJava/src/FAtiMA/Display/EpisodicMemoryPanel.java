/** 
 * EpisodicMemoryPanel.java - 
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
 * Created: 19/Jul/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 19/Jul/2006 - File created
 * **/

package FAtiMA.Display;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FAtiMA.Agent;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.autobiographicalMemory.MemoryEpisode;


public class EpisodicMemoryPanel extends AgentDisplayPanel {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList _memoryEpisodeDisplays;
    private JPanel _memoryEpisodes;
    
    public EpisodicMemoryPanel() {
        
        super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
      	_memoryEpisodeDisplays = new ArrayList();
		
		_memoryEpisodes = new JPanel();
		_memoryEpisodes.setBorder(BorderFactory.createTitledBorder("Memory Events"));
		_memoryEpisodes.setLayout(new BoxLayout(_memoryEpisodes,BoxLayout.Y_AXIS));
		
		_memoryEpisodes.setMaximumSize(new Dimension(550,400));
		_memoryEpisodes.setMinimumSize(new Dimension(550,400));
		
		JScrollPane eventsScrool = new JScrollPane(_memoryEpisodes);
		
		this.add(eventsScrool);
    }
    
    
    public boolean Update(Agent ag) {
    	
    	boolean update = false;
        
        if(countMemoryDetails() != AutobiographicalMemory.GetInstance().countMemoryDetails()) {
        	update = true;
        	_memoryEpisodes.removeAll();
        	_memoryEpisodeDisplays.clear();
        	
        	
        	synchronized(AutobiographicalMemory.GetInstance().GetSyncRoot()){
        		Iterator it = AutobiographicalMemory.GetInstance().GetAllEpisodes().iterator();
            	MemoryEpisodeDisplay mDisplay;
            	MemoryEpisode episode;
            	while(it.hasNext()) {
            		episode = (MemoryEpisode) it.next();
            		mDisplay = new MemoryEpisodeDisplay(episode);
            		_memoryEpisodes.add(mDisplay.getMemoryEpisodePanel());
            		_memoryEpisodeDisplays.add(mDisplay);
            	}
        	}
        }
          
        return update;
    }
    
    private int countMemoryDetails()
	{
    	int aux=0;
    	ListIterator li = this._memoryEpisodeDisplays.listIterator();
    	while(li.hasNext())
    	{
    		aux += ((MemoryEpisodeDisplay) li.next()).countMemoryDetails();
    	}
    	
    	return aux;
	}
	

}
