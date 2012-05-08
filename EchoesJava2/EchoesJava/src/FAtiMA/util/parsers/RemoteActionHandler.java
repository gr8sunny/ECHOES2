/** 
 * RemoteActionHandler.java - Parses a XML RemoteAction
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
 * Created: 05/10/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 05/10/2006 - File created
 */

package FAtiMA.util.parsers;

import org.xml.sax.Attributes;

import FAtiMA.sensorEffector.RemoteAction;


/**
 * @author João Dias
 *
 */
public class RemoteActionHandler extends ReflectXMLHandler {

	private RemoteAction _action;
	
	public RemoteActionHandler () {
		_action = new RemoteAction();
	}
	
	public void RemoteAction(Attributes attributes)
	{
	}
	
	public void Emotion(Attributes attributes) {
		/*Name type;
		Name cause;
		Name direction;
		float intensity;
		
		type = Name.ParseName(attributes.getValue("type"));
		cause = Name.ParseName(attributes.getValue("cause"));
		direction = Name.ParseName(attributes.getValue("direction"));
		intensity = Float.parseFloat(attributes.getValue("intensity"));*/
		//_speechAct.AddEmotion(new Emotion(type,0,intensity,cause,direction));
	}
	
	public RemoteAction getRemoteAction() {
		return _action; 
	}
	
	public void TargetCharacters(String target) {
		_action.setTarget(target);
	}
	
	public void SubjectCharacters(String sender) 
	{
		_action.setSubject(sender);
	}
	
	public void TypeCharacters(String type) {
		_action.setActionType(type);
	}
	
	public void ParamCharacters(String param)
	{
		_action.AddParameter(param);
	}
}