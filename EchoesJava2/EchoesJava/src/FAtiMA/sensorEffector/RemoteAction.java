/** 
 * RemoteAction.java - Represents an Action that is ready to be sent 
 * 					   to the remote virtual world for execution 
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
 * Created: 29/08/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 29/08/2006 - File created
 * João Dias: 18/09/2006 - Added the Setter setActionType
 * 						 - Added the AddParameter method
 * João Dias: 28/09/2006 - Added the GetParameter method
 * João Dias: 05/10/2006 - Added parsing for RemoteActions
 * 						 - The empty constructor is now public, which is needed for parsing
 * 						 - Added Setters and Getters for subject and target
 * 						 - Added method ToEvent()
 * Joao Dias: 08/10/2006 - I was forgetting to add "</Action>" at the end of the xml string
 * 						   returned by the method toXml()
 */


package FAtiMA.sensorEffector;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import FAtiMA.ValuedAction;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.util.parsers.RemoteActionHandler;


public class RemoteAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String _subject;
	protected String _actionType;
	protected String _target;
	protected ArrayList _parameters;
	protected ActiveEmotion _emotion;
	
	//camera and perspective fields
	protected String _intensity = null;
	protected Integer _cameraTarget = null;
	protected String _cameraShot = null;
	protected String _cameraAngle = null;
	
	/**
	 * Parses a RemoteAction from a XML formatted String
	 * @param xml - the XML string to be parsed
	 * @return the parsed RemoteAction
	 */
	public static RemoteAction ParseFromXml(String xml) {
		RemoteActionHandler sh = new RemoteActionHandler();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())), sh);
			return sh.getRemoteAction();
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * Creates a new empty RemoteAction
	 *
	 */
	public RemoteAction()
	{
		_parameters = new ArrayList();
	}
	
	public RemoteAction(ValuedAction va)
	{
		ListIterator li = va.GetAction().GetLiteralList().listIterator();

		_actionType = li.next().toString();
		_subject = AutobiographicalMemory.GetInstance().getSelf();
		_parameters = new ArrayList();
		_target = null;
		if(li.hasNext())
		{
			_target = li.next().toString(); 	
			while(li.hasNext())
			{
				_parameters.add(li.next().toString());
			}
		}
		_emotion = va.getEmotion();
	}
	
	public void AddParameter(String param)
	{
		_parameters.add(param);
	}
	
	public ArrayList GetParameters()
	{
		return _parameters;
	}
	
	public String getSubject()
	{
		return this._subject;
	}
	
	public void setSubject(String subject)
	{
		this._subject = subject;
	}
	
	public String getActionType()
	{
		return this._actionType;
	}
	
	public void setActionType(String actionType)
	{
		this._actionType = actionType; 
	}
	
	public String getTarget()
	{
		return this._target;
	}
	
	public void setTarget(String target)
	{
		this._target = target;
	}
	
	/**
	 * Sets the CameraAngle (only used because of the camera module). This information
	 * is used to decide where to put the camera when the character is acting.
	 * @param cameraAngle - the angle used for the camera
	 */
	public void setCameraAngle(String cameraAngle) {
        _cameraAngle = cameraAngle;
    }
	
	/**
	 * Sets the CameraShot (only used because of the camera module). This information
	 * is used to decide where to put the camera when the character is acting.
	 * @param cameraShot - the type of shot used for the camera
	 */
    public void setCameraShot(String cameraShot) {
        _cameraShot = cameraShot;
    }
    
    /**
     * Sets the CameraTarget (only used because of the camera module). This information
	 * is used to decide where to point the camera when the character is acting. Should
	 * we film the subject or the target of the action?
     * @param cameraTarget - the character that we will film while the action is performed
     */
    public void setCameraTarget(Integer cameraTarget) {
        _cameraTarget = cameraTarget;
    }
    
    /**
     * Sets the intensity of the Action
     * @param intensity - the intensity value to store
     */
    public void setIntensity(String intensity) {
        _intensity = intensity;
    }
    
    protected String cameraToXMl()
    {
    	String aux;
    	//camera arguments
	    aux = "<Camera>";
	    if(_intensity != null) {
	        aux = aux + "<Intensity>" + _intensity + "</Intensity>";
	    }
	    if(_cameraTarget != null) {
	        aux = aux + "<CameraTarget>" + _cameraTarget + "</CameraTarget>";
	    }
	    if(_cameraShot != null) {
	        aux = aux + "<CameraShot>" + _cameraShot + "</CameraShot>";
	    }
	    if(_cameraAngle != null) {
	        aux = aux + "<CameraAngle>" + _cameraAngle + "</CameraAngle>";
	    }
	    aux = aux + "</Camera>";
	    
	    return aux;
    }
    
    /**
     * Converts the RemoteAction to an Event
     * @return the converted Event
     */
	public Event toEvent() {
	    Event event;
	    event = new Event(_subject,_actionType,_target);
		
		for(ListIterator li = _parameters.listIterator(); li.hasNext();)
		{
			event.AddParameter(new Parameter("param",li.next()));
		}
		
		return event;
	}
    
	public String toXML()
    {
    	String xmlAction;
    	
    	xmlAction = "<Action><Subject>" + this._subject + "</Subject><Type>" +
    		_actionType + "</Type>";
    	
    	if(_target != null)
    	{
    		xmlAction = xmlAction + "<Target>" + _target + "</Target>";
    	}
    	
    	xmlAction = xmlAction + "<Parameters>";
    	
    	ListIterator li = _parameters.listIterator();
    	while(li.hasNext())
    	{
    		xmlAction = xmlAction + "<Param>" + li.next() + "</Param>";
    	}
    	
    	xmlAction = xmlAction + "</Parameters>";
    	
    	if(_emotion != null)
    	{
    		xmlAction = xmlAction + _emotion.toXml();
    	}
    	
    	xmlAction = xmlAction + cameraToXMl();
    	
    	xmlAction = xmlAction + "</Action>";
    	
    	return xmlAction;
    }
    
    /**
     * @deprecated - you should try to send the message in xml and not
     * in plain text
     * @return
     */
    public String toPlainStringMessage()
    {
    	String msg;
    	msg = _actionType;
    	if(_target != null)
    	{
    		msg = msg + " " + _target;
    	}
    	if(_parameters.size() > 0)
    	{
    		ListIterator li = _parameters.listIterator();
    		while(li.hasNext())
    		{
    			msg = msg + " " + li.next();
    		}
    	}
    	
    	if(_intensity != null)
    	{
		    msg = msg + " intensity:" + _intensity;
		}
		if(_cameraTarget != null) {
		    msg = msg + " cameraTarget:" + _cameraTarget;
		}
		if(_cameraShot != null) {
		    msg = msg + " cameraShot:" + _cameraShot;
		}
		if(_cameraAngle != null) {
		    msg = msg + " cameraAngle:" + _cameraAngle;
		}
		
		return msg;
    }
}
