package com.github.tadukoo.engine.info;

import com.github.tadukoo.util.ListUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfoListTest{
	private InfoList infoList;
	private List<ShortInfo> theList;
	private ShortInfo info;
	private ShortInfo info2;
	
	@BeforeEach
	public void setup(){
		info = new ShortInfo(InfoType.LIB, "Tadukoo Util", "TadukooUtil.jar",
				"https://dummy.url/TadukooUtil.jar");
		info2 = new ShortInfo(InfoType.PROGRAM, "Tadukoo Pojo Maker", "TadukooPojoMaker.jar",
				"https://dummy.url/TadukooPojoMaker.jar");
		theList = ListUtil.createList(info, info2);
		infoList = new InfoList(theList);
	}
	
	@Test
	public void testListConstructor()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		List<ShortInfo> list = infoList.getList();
		assertEquals(2, list.size());
		assertEquals(info, list.get(0));
		assertEquals(info2, list.get(1));
	}
	
	@Test
	public void testMappedPojoConstructor()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		InfoList otherInfoList = new InfoList(theList);
		infoList = new InfoList(otherInfoList);
		List<ShortInfo> list = infoList.getList();
		assertEquals(2, list.size());
		assertEquals(info, list.get(0));
		assertEquals(info2, list.get(1));
	}
	
	@Test
	public void testGetKeyOrder(){
		List<String> keys = infoList.getKeyOrder();
		assertEquals(2, keys.size());
		assertEquals(InfoType.KEY, keys.get(0));
		assertEquals("list", keys.get(1));
	}
}
