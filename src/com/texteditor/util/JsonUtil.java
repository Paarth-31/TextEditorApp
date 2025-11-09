package com.texteditor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

/**
 * JSON utility for serialization/deserialization.
 */
public class JsonUtil {
	private static final ObjectMapper mapper = new ObjectMapper();
	public static <T> T loadFromJson(String filePath, Class<T> clazz) {
        	try { return mapper.readValue(new File(filePath), clazz);}
        	catch (Exception e) {return null;}
	}

	public static boolean saveToJson(Object object, String filePath) {
        	try {
        		mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), object);
			return true;
		} 
		catch (Exception e) {return false;}
	}
}
