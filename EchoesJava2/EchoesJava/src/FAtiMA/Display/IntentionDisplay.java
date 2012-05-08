/** 
 * IntentionDisplay.java - Graphical Swing object that displays information about
 * a given intention
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
 * Created: 02/11/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 02/11/2005 - File created
 */

package FAtiMA.Display;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import FAtiMA.deliberativeLayer.Intention;


public class IntentionDisplay {
    
    JPanel _panel;
    JLabel _impOfSuccess;
    JLabel _impOfFailure;
    JLabel _numberOfPlans;
    JLabel _probability;
    EmotionDisplay _hope;
    EmotionDisplay _fear;
    
    public IntentionDisplay(Intention i) {
        
        _panel = new JPanel();
        _panel.setBorder(BorderFactory.createTitledBorder(i.getGoal().GetName().toString()));
        _panel.setLayout(new BoxLayout(_panel,BoxLayout.Y_AXIS));
        _panel.setMaximumSize(new Dimension(300,200));
        
        _impOfSuccess = new JLabel();
        _impOfFailure = new JLabel();
        _numberOfPlans = new JLabel();
        _probability = new JLabel();
        

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        JLabel lbl = new JLabel("Imp. Success");
        //lbl.setMinimumSize(new Dimension(100,25));
        lbl.setMaximumSize(new Dimension(100,25));
        pnl.add(lbl);
        pnl.add(_impOfSuccess);
        
        _panel.add(pnl);
        
        pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        lbl = new JLabel("Imp. Failure");
        lbl.setMaximumSize(new Dimension(100,25));
        pnl.add(lbl);
        pnl.add(_impOfFailure);
        
        _panel.add(pnl);
        
        pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        lbl = new JLabel("# of Plans");
        lbl.setMaximumSize(new Dimension(100,25));
        pnl.add(lbl);
        pnl.add(_numberOfPlans);
        
        _panel.add(pnl);
        
        pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl,BoxLayout.X_AXIS));
        pnl.setMaximumSize(new Dimension(200,30));
        
        lbl = new JLabel("Probability");
        lbl.setMaximumSize(new Dimension(100,25));
        pnl.add(lbl);
        pnl.add(_probability);
        
        _panel.add(pnl);
        this.Update(i);
    }
    
    public void Update(Intention i) {
        Float aux;
        Integer aux2;
        
        aux = new Float(i.getGoal().GetImportanceOfSuccess());
        _impOfSuccess.setText(aux.toString());
        
        aux = new Float(i.getGoal().GetImportanceOfFailure());
        _impOfFailure.setText(aux.toString());
        
        aux2 = new Integer(i.NumberOfAlternativePlans());
        _numberOfPlans.setText(aux2.toString());
        
        aux = new Float(i.GetProbability());
        _probability.setText(aux.toString());
    }
    
    public JPanel getIntentionPanel() {
        return _panel;
    }

}
