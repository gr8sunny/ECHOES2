/** 
 * SearchKey.java - 
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
 * João Dias: 02/10/2006 - Now its possible to have SearchKeys with objects instead
 * 						   of Strings
 * **/

package FAtiMA.autobiographicalMemory;

public class SearchKey {
	
	public static short PEOPLE = 1;
	public static short LOCATION = 2;
	public static short OBJECTS = 3;
	public static short ACTION = 4;
	public static short SUBJECT = 5;
	public static short TARGET = 6;
	public static short PARAMETERS = 7;
	
	private short _field;
	private Object _key;
	
	public SearchKey(short field, Object key)
	{
		this._field = field;
		this._key = key;
	}
	
	public short getField()
	{
		return this._field;
	}
	
	public Object getKey()
	{
		return this._key;
	}

}
