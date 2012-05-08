/** 
 * ActionDetailComparator.java - 
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
 * Created: 27/08/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 27/08/2006 - File created
 */

package FAtiMA.autobiographicalMemory;

import java.util.Comparator;

/**
 * @author João Dias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class ActionDetailComparator implements Comparator{ 
	
	public static byte CompareByEmotionIntensity = 0;
	public static byte CompareByOrder = 1;
	
	
	private byte _fieldToCompare;
	
	public ActionDetailComparator(byte compareField)
	{	
		this._fieldToCompare = compareField;
	}
	
	
	public int compare(Object a1, Object a2)
	{
		float aux;
		if(_fieldToCompare == CompareByEmotionIntensity)
		{
			aux = ((ActionDetail) a2).getEmotion().GetPotential() - ((ActionDetail)a1).getEmotion().GetPotential();
		}
		else
		{
			aux = ((ActionDetail)a2).getID() - ((ActionDetail)a1).getID();
		}
		 
		if(aux > 0) return -1;
		if(aux == 0) return 0;
		else return 1;
	}	
}
