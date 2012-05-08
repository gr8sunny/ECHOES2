/** 
 * MemoryEpisodeDisplay.java - 
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
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import FAtiMA.autobiographicalMemory.ActionDetail;
import FAtiMA.autobiographicalMemory.MemoryEpisode;


public class MemoryEpisodeDisplay {

	JPanel _panel;
	JTextArea _abstract;
    JTextArea _time;
    JTextArea _people;
    JTextArea _location;
    JTextArea _objects;
    
    JPanel _details;
    
    int _numberOfDetails;
    

    public MemoryEpisodeDisplay(MemoryEpisode episode) {
    	_numberOfDetails = 0;
		
    	_panel = new JPanel();
        _panel.setBorder(BorderFactory.createEtchedBorder());
        _panel.setLayout(new BoxLayout(_panel,BoxLayout.Y_AXIS));
        _panel.setMaximumSize(new Dimension(550,250));
        _panel.setMinimumSize(new Dimension(550,250));
        
        Dimension d1 = new Dimension(100,20);
        Dimension d2 = new Dimension(100,100);
        Dimension d3 = new Dimension(115,80);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(550,100));
        
        //ABSTRACT 
        JPanel aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.Y_AXIS));
        aux.setMaximumSize(d2);  
        aux.setMinimumSize(d2);
        aux.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        JLabel lbl = new JLabel("Abstract");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        aux.add(lbl);
        //_abstract = new JTextArea(episode.getAbstract());
        _abstract = new JTextArea("");
        _abstract.setLineWrap(true);
        _abstract.setMaximumSize(d3);
        _abstract.setMinimumSize(d3);
        aux.add(_abstract);
        pnl.add(aux);
       
        
        
        //TIME
        aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.Y_AXIS));
        aux.setMaximumSize(d2);
        aux.setMinimumSize(d2);
        aux.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl = new JLabel("Time");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        aux.add(lbl);
        _time = new JTextArea(episode.getTime().toString());
        _time.setLineWrap(true);
        _time.setMaximumSize(d3);
        _time.setMinimumSize(d3);
        aux.add(_time);
        pnl.add(aux);
        
        //PEOPLE 
        aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.Y_AXIS));
        aux.setMaximumSize(d2); 
        aux.setMinimumSize(d2);
        aux.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl = new JLabel("People");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        aux.add(lbl);
        _people = new JTextArea(episode.getPeople().toString());
        _people.setLineWrap(true);
        _people.setMaximumSize(d3);
        _people.setMinimumSize(d3);
        aux.add(_people);
        pnl.add(aux);
        
        //LOCATION
        aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.Y_AXIS));
        aux.setMaximumSize(d2); 
        aux.setMinimumSize(d2);
        aux.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl = new JLabel("Location");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        aux.add(lbl);
        _location = new JTextArea(episode.getLocation());
        _location.setLineWrap(true);
        _location.setMaximumSize(d3);
        _location.setMinimumSize(d3);
        aux.add(_location);
        pnl.add(aux);
        
        //OBJECTS
        aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.Y_AXIS));
        aux.setMaximumSize(d2); 
        aux.setMinimumSize(d2);
        aux.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl = new JLabel("Objects");
        lbl.setMaximumSize(d1);
        lbl.setMinimumSize(d1);
        aux.add(lbl);
        _objects = new JTextArea(episode.getObjects().toString());
        _objects.setLineWrap(true);
        _objects.setMaximumSize(d3);
        _objects.setMinimumSize(d3);
        aux.add(_objects);
        pnl.add(aux);
        
        _panel.add(pnl);
        
        //DETAILS
        _details = new JPanel();
        _details.setBorder(BorderFactory.createTitledBorder("Details"));
        _details.setLayout(new BoxLayout(_details,BoxLayout.Y_AXIS));
        
        aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.X_AXIS));
        aux.setMinimumSize(new Dimension(550,30));
        aux.setMaximumSize(new Dimension(550,30));
        
        lbl = new JLabel("ID");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(30,30));
        lbl.setMaximumSize(new Dimension(30,30));
        aux.add(lbl);
        
        /*lbl = new JLabel("Ca.");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(30,30));
        lbl.setMaximumSize(new Dimension(30,30));
        aux.add(lbl);
        
        lbl = new JLabel("Eff.");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(30,30));
        lbl.setMaximumSize(new Dimension(30,30));
        aux.add(lbl);*/
        
        lbl = new JLabel("Who?");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(50,30));
        lbl.setMaximumSize(new Dimension(50,30));
        aux.add(lbl);
        
        lbl = new JLabel("What?");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(80,30));
        lbl.setMaximumSize(new Dimension(80,30));
        aux.add(lbl);
        
        lbl = new JLabel("Whom?");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(80,30));
        lbl.setMaximumSize(new Dimension(80,30));
        aux.add(lbl);
        
        lbl = new JLabel("How?");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(100,30));
        lbl.setMaximumSize(new Dimension(100,30));
        aux.add(lbl);
        
        lbl = new JLabel("Feeling");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(100,30));
        lbl.setMaximumSize(new Dimension(100,30));
        aux.add(lbl);
        
        lbl = new JLabel("Evaluation");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        lbl.setMinimumSize(new Dimension(100,30));
        lbl.setMaximumSize(new Dimension(100,30));
        aux.add(lbl);
        
        _details.add(aux);
        
        JPanel prop = new JPanel();
		prop.setLayout(new BoxLayout(prop,BoxLayout.Y_AXIS));
		prop.setMaximumSize(new Dimension(550,150));
		prop.setMinimumSize(new Dimension(550,150));
		
		JScrollPane propertiesScroll = new JScrollPane(prop);
		
		ListIterator li = episode.getDetails().listIterator();
		while(li.hasNext())
		{
			prop.add(new MemoryDetailPanel((ActionDetail)li.next()));
			_numberOfDetails ++;
		}
		
		_details.add(propertiesScroll);
        
		_panel.add(_details);
		
    }
    
    public JPanel getMemoryEpisodePanel()
    {
    	return this._panel;
    }
    
    public int countMemoryDetails()
    {
    	return this._numberOfDetails;
    }

}
