package com.github.tadukoo.engine.config;

import com.github.tadukoo.engine.info.ShortInfo;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.parsing.json.OrderedJSONClass;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.map.MapUtil;
import com.github.tadukoo.util.pojo.MappedPojo;
import com.github.tadukoo.util.pojo.OrderedMappedPojo;
import com.github.tadukoo.util.tuple.Pair;
import com.github.tadukoo.view.form.AbstractSimpleForm;
import com.github.tadukoo.view.form.field.TableFormField;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Info Lists Config represents the info lists part of the config file for the engine/launcher.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class InfoListsConfig extends AbstractSimpleForm implements OrderedJSONClass{
	
	/**
	 * Builder used to create a {@link InfoListsConfig}. It takes the following parameters:
	 *
	 * <table>
	 *     <caption>Info Lists Config Parameters</caption>
	 *     <tr>
	 *         <th>Parameter</th>
	 *         <th>Description</th>
	 *         <th>Default or Required</th>
	 *     </tr>
	 *     <tr>
	 *         <td>infoLists</td>
	 *         <td>{@link ShortInfo}s for info lists the launcher/engine should use</td>
	 *         <td>Defaults to an empty list</td>
	 *     </tr>
	 * </table>
	 *
	 * @author Logan Ferree (Tadukoo)
	 * @version Alpha v.0.1
	 */
	public static class ItemListsConfigBuilder{
		/** {@link ShortInfo}s for info lists the launcher/engine should use */
		private JSONArrayList<ShortInfo> infoLists = new JSONArrayList<>();
		
		// Can't create Config Builder outside of Config
		private ItemListsConfigBuilder(){ }
		
		/**
		 * @param infoLists {@link ShortInfo}s for info lists the launcher/engine should use
		 * @return this, to continue building
		 */
		public ItemListsConfigBuilder infoLists(JSONArrayList<ShortInfo> infoLists){
			this.infoLists = infoLists;
			return this;
		}
		
		/**
		 * @param infoLists {@link ShortInfo}s for info lists the launcher/engine should use
		 * @return this, to continue building
		 */
		public ItemListsConfigBuilder infoLists(List<ShortInfo> infoLists){
			this.infoLists = new JSONArrayList<>(infoLists);
			return this;
		}
		
		/**
		 * @param infoList A {@link ShortInfo} for an info lists the launcher/engine should use
		 * @return this, to continue building
		 */
		public ItemListsConfigBuilder infoList(ShortInfo infoList){
			infoLists.add(infoList);
			return this;
		}
		
		/**
		 * Builds a new {@link InfoListsConfig} using the set parameters
		 *
		 * @return The newly built {@link InfoListsConfig}
		 * @throws Throwable If anything goes wrong
		 */
		public InfoListsConfig build() throws Throwable{
			return new InfoListsConfig(infoLists);
		}
	}
	
	/** The key to be used for the {@link ShortInfo}s for info lists the launcher/engine should use */
	private static final String INFO_LISTS = "info-lists";
	
	/**
	 * Creates a new Info Lists Config with the given parameters.
	 *
	 * @param infoLists {@link ShortInfo}s for info lists the launcher/engine should use
	 * @throws Throwable If anything goes wrong in creating components
	 */
	private InfoListsConfig(JSONArrayList<ShortInfo> infoLists) throws Throwable{
		super(MapUtil.createMap(Pair.of(INFO_LISTS, infoLists)));
	}
	
	/**
	 * Creates a new Info Lists Config using the given pojo for values
	 *
	 * @param pojo The {@link MappedPojo} to use for values
	 * @throws Throwable If anything goes wrong in creating components
	 */
	public InfoListsConfig(MappedPojo pojo) throws Throwable{
		super(pojo);
	}
	
	/**
	 * @return A new {@link ItemListsConfigBuilder} to use to build an Info Lists Config
	 */
	public static ItemListsConfigBuilder builder(){
		return new ItemListsConfigBuilder();
	}
	
	/** {@inheritDoc} */
	@Override
	public List<String> getKeyOrder(){
		return ListUtil.createList(INFO_LISTS);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDefaultFields() throws Throwable{
		// Grab List of info lists and cast to List of OrderedMappedPojos for Table use
		List<OrderedMappedPojo> infoLists = new JSONArrayList<>();
		infoLists.addAll(getInfoLists());
		
		// Create Table field for Info Lists
		addField(TableFormField.builder()
				.key(INFO_LISTS).defaultValue(infoLists)
				.rowPos(0).colPos(0)
				.build());
	}
	
	/**
	 * @return {@link ShortInfo}s for info lists the launcher/engine should use
	 */
	public List<ShortInfo> getInfoLists()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		return getJSONArrayItem(INFO_LISTS, ShortInfo.class);
	}
}
