package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Bunch of helper methods used by different classes.
 * These methods allow us to avoid duplicity
 */

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
	
	@SuppressWarnings("rawtypes")
	public static Object[] joinToArrayListsIntoOneArray(ArrayList array1, ArrayList array2)
	{
		final int lengthArray1 = array1.size();
		final int totalLength = lengthArray1 + array2.size();
		Object[] joinedArrays = new Object[totalLength];
		
		int i = 0;
		for (; i<lengthArray1; i++) {
			joinedArrays[i] = array1.get(i);
		}
		for (int j=0; i<totalLength; i++) {
			joinedArrays[i] = array2.get(j++);
		}
		
		return joinedArrays;
	}
}