package com.github.tadukoo.engine.info;

import com.github.tadukoo.util.StringUtil;

/**
 * Info Type is the type of info for info files being used by Tadukoo Engine. The type is stored in the info files
 * themselves to easily distinguish them.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public enum InfoType{
	/** Used for library files */
	LIB("lib"),
	/** Used for program files */
	PROGRAM("program"),
	/** Used for list files (that list info files) */
	LIST("list");
	
	/** The key to be used in files to tell their type */
	public static String KEY = "type";
	
	/** The type of info */
	private final String type;
	
	/**
	 * Creates a new InfoType with the given type.
	 *
	 * @param type The type of info
	 */
	InfoType(String type){
		this.type = type;
	}
	
	/**
	 * @return The type of info
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Finds an InfoType with the given type and returns it, to convert the given type String to an InfoType.
	 *
	 * @param type The type of info
	 * @return An InfoType with the given type, if one was found, otherwise null
	 */
	public static InfoType parseFromString(String type){
		for(InfoType infoType: values()){
			if(StringUtil.equalsIgnoreCase(infoType.getType(), type)){
				return infoType;
			}
		}
		return null;
	}
}
