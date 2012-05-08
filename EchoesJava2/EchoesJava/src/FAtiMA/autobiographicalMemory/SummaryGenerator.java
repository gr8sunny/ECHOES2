/** 
 * SummaryGenerator.java - Abstract class that acts as a method repository of methods
 * used to generate Memory Summaries 
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
 * Created: 04/April/2007 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 04/April/2007 - File created
 * **/

package FAtiMA.autobiographicalMemory;

import java.util.ArrayList;

import FAtiMA.emotionalState.BaseEmotion;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.SpeechAct;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.wellFormedNames.Name;

/**
 * Abstract class that acts as a method repository of methods
 * used to generate Memory Summaries
 * 
 * @author Jovem Engenheiro
 */
public abstract class SummaryGenerator {
	 
	public static String GenerateActionSummary(ActionDetail action)
	{
		
		String actionSummary = "<Subject>";
		
		if(action.getSubject().equals(AutobiographicalMemory.GetInstance().getSelf()))
		{
			actionSummary += "I";
		}
		else
		{
			actionSummary += translateNameToDisplayName(action.getSubject());
		}
		
		actionSummary += "</Subject><Action>"; 
		
		if(SpeechAct.isSpeechAct(action.getAction()))
		{
			ArrayList params = action.getParameters();
			actionSummary += params.get(0);
			
			if(action.getAction().equals(SpeechAct.Reply))
			{
				actionSummary += params.get(1);
			}
				
				/*if(action.getTarget().equals(AutobiographicalMemory.GetInstance().getSelf()))
				{
					description += "my ";
				}
				else
				{
					description += action.getTarget() + "'s ";
				}
				
				description += params.get(0);
				
				return description;
			*/
		}
		else 
		{
			actionSummary += action.getAction();
		}
		
		actionSummary += "</Action>";
		
		
		if(action.getTarget() != null)
		{
			actionSummary += "<Target>";
			
			if(action.getTarget().equals(AutobiographicalMemory.GetInstance().getSelf()))
			{
				actionSummary += "me";
			}
			else
			{
				Object aux = action.getTargetDetails("type");
				if(aux != null)
				{
					if(aux.equals("object"))
					{
						Object aux2 = action.getTargetDetails("owner");
						if(aux2 != null)
						{
							if(AutobiographicalMemory.GetInstance().getSelf().equals(aux2))
							{
								actionSummary += "my ";
							
							}
							else
							{
								actionSummary += translateNameToDisplayName(aux2.toString()) + "'s "; 
							}
						}
					}
				}
				
				actionSummary += translateNameToDisplayName(action.getTarget());
			}
			
			actionSummary += "</Target>";
		}
		
		if(action.getParameters().size() > 0)
		{
			actionSummary += "<Param>" + 
				translateNameToDisplayName(action.getParameters().get(0).toString()) +
				"</Param>";
		}
		
		return actionSummary;
	}
	
	public static String GenerateEmotionSummary(BaseEmotion em)
	{
		String EMSummary = "<Emotion intensity=\"";
		
		if(em.GetPotential() > 5) 
		{
			EMSummary += "high";
		}
		else if(em.GetPotential() > 3)
		{
			EMSummary += "normal";
		}
		else
		{			
			EMSummary += "little";
		}
		EMSummary += "\" ";
		
		if(em.GetDirection() != null)
		{
			String direction = translateNameToDisplayName(em.GetDirection().toString());
			
			EMSummary += "direction=\"" + direction + "\"";
		}
		
		EMSummary += ">"+EmotionType.GetName(em.GetType()) + "</Emotion>";
		
		return EMSummary;
	}
	
	public static String generateTimeDescription(long time)
	{
		int months = Math.round(time/259200000); //months
		
		if(months > 0)
		{
			return "<Time count=\"" + months + "\">month</Time>";
		}
		
		int weeks = Math.round(time/60480000); //weeks
		
		if(weeks > 0)
		{
			return "<Time count=\"" + weeks + "\">week</Time>";
		}
		
		int days = Math.round(time/8640000); //days
		if(days > 0)
		{
			return "<Time count=\"" + days + "\">day</Time>";
		}
		
		int hours = Math.round(time/360000); //hours 
		return "<Time count=\"" + hours + "\">hour</Time>";
	}
	
	public static String translateNameToDisplayName(String name)
	{
		Object displayName = KnowledgeBase.GetInstance().AskProperty(Name.ParseName(name + "(displayName)"));
		if(displayName != null)
		{
			return displayName.toString();
		}
		else return name;
	}

}
