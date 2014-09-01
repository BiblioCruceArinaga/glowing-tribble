package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class StaticMethods 
{
	private StaticMethods() {}
	
	public static String convertByteArrayListToString(
			final ArrayList<Byte> bytes, int start)
	{
		byte[] bytesArray = new byte[bytes.size()];
        for (int i=start; i<bytesArray.length; i++) 
        {
        	bytesArray[i] = bytes.get(i);
        }
        
        try {
            return new String(bytesArray, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
	}
	
	public static String sanitizeString(final String oldString) 
	{
		String newString = oldString;
		
    	final int index = oldString.indexOf('&');
    	if (index > -1) {
    		switch (oldString.charAt(index - 1)) {
	    		case 'a':
	    			newString = oldString.replace("a&", "á");
	    			break;
	    		case 'e':
	    			newString = oldString.replace("e&", "é");
	    			break;
	    		case 'i':
	    			newString = oldString.replace("i&", "í");
	    			break;
	    		case 'o':
	    			newString = oldString.replace("o&", "ó");
	    			break;
	    		case 'u':
	    			newString = oldString.replace("u&", "ú");
	    			break;
	    		case 'n':
	    			newString = oldString.replace("n&", "ñ");
	    			break;
    			default:
    				break;
    		}
    	}
    	
    	return newString;
    }
}