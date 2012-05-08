/** 
 * SpeechAct.java - Represents a SpeechAct
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
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 29/08/2006 - Part of the class functionality was moved to
 * 						   the new RemoteAction class. This class is now
 * 						   a subclass of RemoteAction. 
 * João Dias: 12/09/2006 - The type of the SpeechAct is no longer a junction of
 * 						   the type and the answer type (ex: "invitepositiveanser") 
 * 						   in the case of answer SpeechActs
 * 						 - Added the method toLanguageEngine that converts the SpeechAct
 * 						   into the appropriate XML that is going to be sent to the LanguageEngine
 * 						   so that it generates the utterance. In this method we join the type
 * 						   of speechAct with the answer, so an "invite,positiveanswer" speechAct
 * 						   becomes a "invitepositiveanswer" speechAct
 * João Dias: 18/09/2006 - Changes to SpeechAct structure so that it can model several SpeechAct
 * 						   types (ex: Question, Reply, Support).  
 * 					     - Renamed the attribute "type" and corresponding methods to "meaning" (that
 * 						   (ex: insult, greeting, mock) to avoid confusion with the new SpeechAct type 
 * 					       attribute (ex: Question, Reply, Support)
 * João Dias: 02/10/2006 - Support SpeechActs renamed to Reinforce SpeechActs to maintain consistency
 * 					       with the SpeechActs defined in the LanguageEngine
 *						 - When Reinforce SpeechActs are searched in the LanguageEngine, we append
 *						   reinforce to the speecAct name. For instance, if the SpeechAct corresponds
 *						   to a Reinforce of type mock, we search in the LE for mockreinforce in order
 *						   to retrieve the appropriate utterance
 * João Dias: 15/11/2006 - Added new types of SpeechActs: UserSpeech, Confirmation, Suggestion, etc.
 * João Dias: 27/11/2006 - Added method getContextVariables
 * João Dias: 07/11/2006 - Changed the return type of the method ParseFromXML so that it is compatible with
 * 						   same method (newly introduced) from the superclass (RemoteAction).
 * 						     
 */

package FAtiMA.sensorEffector;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import FAtiMA.ValuedAction;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.util.parsers.SpeechActHandler;
import FAtiMA.wellFormedNames.Name;


/**
 * Represents a SpeechAct
 * @author João Dias
 */
public class SpeechAct extends RemoteAction {

	private static final long serialVersionUID = 1L;
	
	public static final String Question = "Question";
	public static final String Reply = "Reply";
	public static final String Reinforce = "Reinforce";
	public static final String Speech = "SpeechAct";
	public static final String Confrontation = "Confrontation";
	public static final String UserSpeech = "UserSpeech";
	//public static final String ConfirmationRequest = "Confirmation";
	//public static final String CopingSuggestion = "Suggestion";
	public static final String CopingSpeech = "CopingSpeech";
	
	private String _meaning;
	private String _utterance;
	private String _AMsummary;
	private ArrayList _contextVariables;
	
	/**
	 * Parses a SpeechAct from a XML formatted String
	 * @param xml - the XML string to be parsed
	 * @return the parsed SpeechAct
	 */
	public static RemoteAction ParseFromXml(String xml) {
		SpeechActHandler sh = new SpeechActHandler();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))), sh);
			return sh.getSpeechAct();
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * Checks if the received action corresponds to a SpeechAct or 
	 * not (it may be a Question, a Reply, a Suppport, etc)
	 * @param action - the action to check
	 * @return true if the action actually corresponds to a SpeechAct,
	 * 		   false otherwise
	 */
	public static boolean isSpeechAct(String action)
	{
		return action.equals(SpeechAct.Speech) ||
			action.equals(SpeechAct.Question) ||
			action.equals(SpeechAct.Reply) ||
			action.equals(SpeechAct.Reinforce) ||
			action.equals(SpeechAct.Confrontation) ||
			action.equals(SpeechAct.UserSpeech) ||
			action.equals(SpeechAct.CopingSpeech);
			//action.equals(SpeechAct.ConfirmationRequest) ||
			//action.equals(SpeechAct.CopingSuggestion);
	}
    
	/**
	 * Creates a new empty SpeechAct
	 */
	public SpeechAct() {
		_contextVariables = new ArrayList();
		this._AMsummary = null;
	}
	
	/**
	 * Creates a new SpeechAct
	 * @param actType - the type of the SpeechAct (Question, Reply, Support)
	 * @param sender - the sender of the SpeechAct (who is saying the speech)
	 * @param receiver - the receiver of the SpeechAct 
	 * @param meaning - the SpeechAct's meaning (insult, greeting, etc)
	 */
	public SpeechAct(String actType, String sender, String receiver, String meaning) {
	    _target = receiver;
	    _subject = sender;
	    _meaning = meaning;
	    _actionType = actType;
	    this._AMsummary = null;
	    _contextVariables = new ArrayList();
	}
	
	/**
	 * Creates a new SpeechAct from a ValuedAction
	 * 
	 */
	public SpeechAct(ValuedAction speechAction)
	{
		_subject = AutobiographicalMemory.GetInstance().getSelf();
		
		Name action = speechAction.GetAction();
		ListIterator li = action.GetLiteralList().listIterator();
		_actionType = li.next().toString();
		
		if(!SpeechAct.isSpeechAct(_actionType))
		{
			return;
		}
		
		_target = li.next().toString();
		_meaning = li.next().toString();
		
		this._AMsummary = null;
		
		_contextVariables = new ArrayList();
		
		//third literal of a speech acts corresponds to a third person or object,
		if(li.hasNext())
		{
			String it = li.next().toString(); 
			if(!_actionType.equals(Reply))
			{
				_contextVariables.add(new Parameter("it",it));
			}
			_parameters.add(it);
		}
		
		while(li.hasNext())
		{
			_parameters.add(li.next().toString());
		}
		
		_emotion = speechAction.getEmotion();
	}
	
	/**
	 * Adds a context variable (name-value pair) to the SpeechAct
	 * @param name - the name of the context variable
	 * @param value - the value of the context variable
	 */
	public void AddContextVariable(String name, String value) 
	{
	    _contextVariables.add(new Parameter(name,value));
	}
	
	public ArrayList getContextVariables()
	{
		return _contextVariables;
	}

	/**
	 * Gets the SpeechAct's receiver
	 * @return the receiver
	 */
	public String getReceiver() {
		return _target;
	}

	/**
	 * Gets the SpeechAct's sender
	 * @return the sender of the SpeechAct
	 */
	public String getSender() {
		return _subject;
	}

	/**
	 * Gets the SpeechAct's meaning (ex: insult, greeting, etc)
	 * @return the meaning of the SpeechAct
	 */
	public String getMeaning() {
		return _meaning;
	}

	/**
	 * Gets the specific utterance of the SpeechAct (ex: "Hi John")
	 * @return the SpeechAct's utterance
	 */
	public String getUtterance() {
		return _utterance;
	}

	/**
	 * Sets the SpeechAct's receiver
	 * @param string - the receiver to store in the SpeechAct
	 */
	public void setReceiver(String string) {
		_target = string;
	}

	/**
	 * Sets the SpeechAct's sender
	 * @param string - the sender to store in the SpeechAct
	 */
	public void setSender(String string) {
		_subject = string;
	}

	/**
	 * Sets the SpeechAct's meaning (ex: insult, greeting)
	 * @param string - the type to store in the SpeechAct
	 */
	public void setMeaning(String string) {
		_meaning = string;
	}

	/**
	 * Sets the SpeechAct specific utterance (ex: "what a wimp", "hi Paul")
	 * @param string - the utterance to store in the SpeechAct
	 */
	public void setUtterance(String string) {
		_utterance = string;
	}
	
	public String getAMSummary()
	{
		return this._AMsummary;
	}
	
	public void setAMSummary(String summary)
	{
		this._AMsummary = summary;
	}
	
    /**
     * Converts the SpeechAct to an Event
     * @return the converted Event
     */
	public Event toEvent() {
	    Event event;
	    event = new Event(_subject,_actionType,_target);
		event.AddParameter(new Parameter("type", _meaning));
		
		for(ListIterator li = _parameters.listIterator(); li.hasNext();)
		{
			event.AddParameter(new Parameter("param",li.next()));
		}
		
		return event;
	}
	
	/**
	 * Converts the SpeechAct to XML (the inverse of the Parse method)
	 * @return a XML string that contains the SpeechAct information
	 */
	public String toXML() {
	    String aux;
	    Parameter p;
	    aux = "<SpeechAct type=\"" + _actionType + "\"><Sender>" + _subject 
	    	+ "</Sender><Receiver>" + _target
	    	+ "</Receiver><Type>" + _meaning + "</Type>";
	    
	    ListIterator li = _contextVariables.listIterator();
	    while(li.hasNext()) {
	        p = (Parameter) li.next();
	        aux = aux + "<Context id=\"" + p.GetName() + "\">" + p.GetValue() + "</Context>";
	    }
	   
	    aux = aux + "<Parameters>";
    	
    	li = _parameters.listIterator();
    	while(li.hasNext())
    	{
    		aux = aux + "<Param>" + li.next() + "</Param>";
    	}
    	
    	aux = aux + "</Parameters>";
	    
	    if(_utterance != null) {
	        aux = aux + "<Utterance>" + _utterance + "</Utterance>";
	    }
	    
	    if(_emotion != null)
	    {
	    	aux = aux + _emotion.toXml();
	    }
	    
	    if(_AMsummary != null)
	    {
	    	aux = aux + "<AMSummary>" + this._AMsummary + "</AMSummary>";
	    }
	    
	    aux = aux + cameraToXMl();
	  
	    aux = aux + "</SpeechAct>";
	    
	    return aux;
	}
	
	/**
	 * Converts the SpeechAct to XML that is ready to be sent to the 
	 * LanguageEngine for the generation of a specific utterance
	 * @return a XML string that contains the SpeechAct information
	 */
	public String toLanguageEngine() {
	    String aux;
	    Parameter p;
	    String speechType = _meaning;
	    
	    if(_actionType.equals(SpeechAct.Reply))
	    {
	    	speechType = speechType + _parameters.get(0);
	    }
	    else if(_actionType.equals(SpeechAct.Reinforce))
	    {
	    	speechType = speechType + "reinforce";
	    }
	    else if(_actionType.equals(SpeechAct.CopingSpeech))
	    {
	    	_contextVariables.add(new Parameter("copingstrategy",_parameters.get(0).toString()));
	    }
	    
	    aux = "<SpeechAct><Sender>" + _subject 
	    	+ "</Sender><Receiver>" + _target
	    	+ "</Receiver><Type>" + speechType + "</Type>";
	    
	    ListIterator li = _contextVariables.listIterator();
	    while(li.hasNext()) {
	        p = (Parameter) li.next();
	        aux = aux + "<Context id=\"" + p.GetName() + "\">" + p.GetValue() + "</Context>";
	    }
	    
	    if(_utterance != null) {
	        aux = aux + "<Utterance>" + _utterance + "</Utterance>";
	    }
	    
	    if(_emotion != null)
	    {
	    	aux = aux + _emotion.toXml();
	    }
	    
	    aux = aux + cameraToXMl();
	  
	    aux = aux + "</SpeechAct>";
	    
	    return aux;
	}
    
	public String toPlainStringMessage()
	{
		return "say " + toXML();
	}
}