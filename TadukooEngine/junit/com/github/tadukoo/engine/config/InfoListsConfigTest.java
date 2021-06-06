package com.github.tadukoo.engine.config;

import com.github.tadukoo.engine.info.InfoType;
import com.github.tadukoo.engine.info.ShortInfo;
import com.github.tadukoo.parsing.json.JSONArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InfoListsConfigTest{
	private InfoListsConfig config;
	
	@BeforeEach
	public void setup() throws Throwable{
		config = InfoListsConfig.builder()
				.build();
	}
	
	@Test
	public void testBuilderDefaultInfoList()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		List<ShortInfo> infos = config.getInfoLists();
		assertTrue(infos instanceof JSONArrayList);
		assertTrue(infos.isEmpty());
	}
	
	@Test
	public void testBuilderSetInfoListsJSONArrayList() throws Throwable{
		JSONArrayList<ShortInfo> infos = new JSONArrayList<>();
		infos.add(new ShortInfo(InfoType.LIST, "A List", "List.json",
				"https://dummy.url/List.json"));
		config = InfoListsConfig.builder()
				.infoLists(infos)
				.build();
		assertEquals(infos, config.getInfoLists());
	}
	
	@Test
	public void testBuilderSetInfoListsNotJSONArrayList() throws Throwable{
		List<ShortInfo> infos = new ArrayList<>();
		ShortInfo info = new ShortInfo(InfoType.LIST, "A List", "List.json",
				"https://dummy.url/List.json");
		infos.add(info);
		config = InfoListsConfig.builder()
				.infoLists(infos)
				.build();
		List<ShortInfo> theInfos = config.getInfoLists();
		assertTrue(theInfos instanceof JSONArrayList);
		assertEquals(1, theInfos.size());
		assertEquals(info, theInfos.get(0));
	}
	
	@Test
	public void testBuilderSetSingleInfoList() throws Throwable{
		ShortInfo info = new ShortInfo(InfoType.LIST, "A List", "List.json",
				"https://dummy.url/List.json");
		config = InfoListsConfig.builder()
				.infoList(info)
				.build();
		List<ShortInfo> theInfos = config.getInfoLists();
		assertTrue(theInfos instanceof JSONArrayList);
		assertEquals(1, theInfos.size());
		assertEquals(info, theInfos.get(0));
	}
	
	@Test
	public void testMappedPojoConstructor() throws Throwable{
		ShortInfo info = new ShortInfo(InfoType.LIST, "A List", "List.json",
				"https://dummy.url/List.json");
		InfoListsConfig otherConfig = InfoListsConfig.builder()
				.infoList(info)
				.build();
		config = new InfoListsConfig(otherConfig);
		List<ShortInfo> theInfos = config.getInfoLists();
		assertTrue(theInfos instanceof JSONArrayList);
		assertEquals(1, theInfos.size());
		assertEquals(info, theInfos.get(0));
	}
	
	@Test
	public void testGetKeyOrder(){
		List<String> keys = config.getKeyOrder();
		assertEquals(1, keys.size());
		assertEquals("info-lists", keys.get(0));
	}
}
