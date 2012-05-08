/** 
 * SpeechActHandler.java - Parses a XML SpeechAct
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
 * Created: 18/04/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 18/04/2004 - File created
 * João Dias: 27/11/2006 - Added parsing for context variables in SpeechActs
 */

package FAtiMA.util.parsers;

import org.xml.sax.Attributes;

import FAtiMA.sensorEffector.SpeechAct;


/**
 * @author João Dias
 *
 */
public class SpeechActHandler extends ReflectXMLHandler {

	private SpeechAct _speechAct;
	private String _contextVariable = null;
	
	public SpeechActHandler () {
		_speechAct = new SpeechAct();
	}
	
	public void SpeechAct(Attributes attributes)
	{
		_speechAct.setActionType(attributes.getValue("type"));
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
	
	public SpeechAct getSpeechAct() {
		return _speechAct; 
	}
	
	public void ReceiverCharacters(String receiver) {
		_speechAct.setReceiver(receiver);
	}
	
	public void SenderCharacters(String sender) {
		_speechAct.setSender(sender);
	}
	
	public void TypeCharacters(String type) {
		_speechAct.setMeaning(type);
	}
	
	public void ParamCharacters(String param)
	{
		_speechAct.AddParameter(param);
	}
	
	public void Utterance(Attributes attributes) {
	}
	
	public void UtteranceCharacters(String utterance) {
		_speechAct.setUtterance(utterance);
	}
	
	public void Context(Attributes attributes)
	{
		_contextVariable = attributes.getValue("id");
	}
	
	public void ContextCharacters(String value)
	{
		_speechAct.AddContextVariable(_contextVariable, value);
	}
}