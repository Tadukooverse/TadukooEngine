package com.github.tadukoo.engine.info;

import com.github.tadukoo.parsing.json.AbstractOrderedJSONClass;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.StringUtil;
import com.github.tadukoo.util.pojo.MappedPojo;

import java.util.List;

/**
 * Short Info is used in lists of info to provide basic information for finding program or library info files.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class ShortInfo extends AbstractOrderedJSONClass{
	/** The key for the title of the info */
	private static final String TITLE = "title";
	/** The key for the name of the info file */
	private static final String INFO_NAME = "info-name";
	/** The key for the location (online) of the info file */
	private static final String INFO_LOCATION = "info-location";
	
	/**
	 * Creates a new Short Info object with the given parameters.
	 *
	 * @param type The type of Short Info
	 * @param title The title of the info
	 * @param infoName The name of the info file
	 * @param infoLocation The location (online) of the info file, if it needs downloaded
	 */
	public ShortInfo(InfoType type, String title, String infoName, String infoLocation){
		super();
		setItem(InfoType.KEY, type.getType());
		setItem(TITLE, title);
		setItem(INFO_NAME, infoName);
		setItem(INFO_LOCATION, infoLocation);
	}
	
	/**
	 * Creates a new Short Info object with the values of the given pojo.
	 *
	 * @param pojo The {@link MappedPojo} to use the values of
	 */
	public ShortInfo(MappedPojo pojo){
		super(pojo);
	}
	
	/** {@inheritDoc} */
	@Override
	public List<String> getKeyOrder(){
		return ListUtil.createList(InfoType.KEY, TITLE, INFO_NAME, INFO_LOCATION);
	}
	
	/**
	 * @return The type of Short Info
	 */
	public InfoType getType(){
		return InfoType.parseFromString((String) getItem(InfoType.KEY));
	}
	
	/**
	 * @return The title of the info
	 */
	public String getTitle(){
		return (String) getItem(TITLE);
	}
	
	/**
	 * @return The name of the info file
	 */
	public String getInfoName(){
		return (String) getItem(INFO_NAME);
	}
	
	/**
	 * @return The location (online) of the info file, if it needs downloaded
	 */
	public String getInfoLocation(){
		return (String) getItem(INFO_LOCATION);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof ShortInfo info){
			return info.getType().equals(getType()) && StringUtil.equals(info.getTitle(), getTitle()) &&
					StringUtil.equals(info.getInfoName(), getInfoName()) &&
					StringUtil.equals(info.getInfoLocation(), getInfoLocation());
		}else{
			return false;
		}
	}
}
