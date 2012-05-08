package FAtiMA.util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import FAtiMA.autobiographicalMemory.AutobiographicalMemory;

public abstract class ApplicationLogger {
	

    private static final String LOG_FILE = "-errorlog.txt";
    
    public static void Write(String msg)
    {
    	try 
    	{
    		String logFile = AutobiographicalMemory.GetInstance().getSelf() + LOG_FILE; 
    		FileOutputStream out = new FileOutputStream(logFile,true);
        	ObjectOutputStream s = new ObjectOutputStream(out);
        	
        	s.writeChars(msg);
        	s.flush();
        	s.close();
        	out.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

}
