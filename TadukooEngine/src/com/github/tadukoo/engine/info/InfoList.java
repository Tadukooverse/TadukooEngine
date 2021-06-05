package com.github.tadukoo.engine.info;

import com.github.tadukoo.parsing.json.AbstractOrderedJSONClass;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.pojo.MappedPojo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Info List stores a collection of {@link ShortInfo} objects, which provide info about program and library info files.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class InfoList extends AbstractOrderedJSONClass{
	/** The key used for the list of info files */
	private static final String LIST = "list";
	
	/**
	 * Creates a new Info List with the given parameters.
	 *
	 * @param infoList The list of {@link ShortInfo} objects
	 */
	public InfoList(List<ShortInfo> infoList){
		super();
		setItem(InfoType.KEY, InfoType.LIST.getType());
		setItem(LIST, new JSONArrayList<>(infoList));
	}
	
	/**
	 * Creates a new Info List using the given pojo's values.
	 *
	 * @param pojo The {@link MappedPojo} to use values from
	 */
	public InfoList(MappedPojo pojo){
		super(pojo);
	}
	
	/** {@inheritDoc} */
	@Override
	public List<String> getKeyOrder(){
		return ListUtil.createList(InfoType.KEY, LIST);
	}
	
	/**
	 * @return The list of info files
	 */
	public List<ShortInfo> getList()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		return getJSONArrayItem(LIST, ShortInfo.class);
	}
}
