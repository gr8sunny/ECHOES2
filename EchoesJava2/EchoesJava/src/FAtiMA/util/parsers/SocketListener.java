/** 
 * SocketListener.java - Implements a socket listener
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
 * Created: 01/04/2002 
 * @author: Rui Prada
 * Email to: rui.prada@tagus.ist.utl.pt
 * 
 * History: 
 * Rui Prada: 01/04/2002 - File created
 */

package FAtiMA.util.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public abstract class SocketListener extends Thread {
    protected int maxSize = 256;

    protected Socket socket;
    byte[] buffer = new byte[maxSize];
    BufferedReader reader; 

    protected boolean stoped = false;

    public SocketListener() {
    }
    
    /** Creates new SocketListener */
    public SocketListener(Socket socket) {
        this.socket = socket;
    }
    
    public void initialize()
    {
    	InputStream is = null;
        InputStreamReader in = null;
        if(this.socket != null)
        {
        	try
        	{
        		is = this.socket.getInputStream();
        	}		
        	catch(IOException e)
        	{
        		stoped = true;
        		return;
        	}
        	try
        	{
        		in = new InputStreamReader(is,"UTF-8");
        	}
        	catch(UnsupportedEncodingException ex)
            {
            	in = new InputStreamReader(is);
            }
        
        	this.reader = new BufferedReader(in);
        }
        else
        {
        	stoped = true;
        }
    }

    public void close () {
        stoped = true;
        try {
        	reader.close();
            socket.close();
        }
        catch(java.io.IOException ex) {
        }
    }

    public abstract void processMessage(String msg);

    public void run() {

    	String msg;
    	
        while(!stoped) {
            try {
                sleep(100);
            }
            catch(InterruptedException ex) {
            }
            
            if(reader!= null)
            {
            	try
            	{
            		msg = reader.readLine();
            		processMessage(msg);
            	}
                catch (java.io.IOException ex) {
                    ex.printStackTrace();
                    stoped = true;
                }
            }
        }
    }
}