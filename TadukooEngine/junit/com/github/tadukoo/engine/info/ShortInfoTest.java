package com.github.tadukoo.engine.info;

import com.github.tadukoo.parsing.json.JSONArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ShortInfoTest{
	private ShortInfo info;
	private final InfoType type = InfoType.PROGRAM;
	private final String title = "Test Program";
	private final String name = "TestName.json";
	private final String location = "https://dummy.url/TestName.json";
	
	@BeforeEach
	public void setup(){
		info = new ShortInfo(type, title, name, location);
	}
	
	@Test
	public void testGetType(){
		assertEquals(type, info.getType());
	}
	
	@Test
	public void testGetTitle(){
		assertEquals(title, info.getTitle());
	}
	
	@Test
	public void testGetInfoName(){
		assertEquals(name, info.getInfoName());
	}
	
	@Test
	public void testGetInfoLocation(){
		assertEquals(location, info.getInfoLocation());
	}
	
	@Test
	public void testMappedPojoConstructor(){
		ShortInfo otherInfo = new ShortInfo(type, title, name, location);
		info = new ShortInfo(otherInfo);
		assertEquals(type, info.getType());
		assertEquals(title, info.getTitle());
		assertEquals(name, info.getInfoName());
		assertEquals(location, info.getInfoLocation());
	}
	
	@Test
	public void testGetKeyOrder(){
		List<String> keys = info.getKeyOrder();
		assertEquals(4, keys.size());
		assertEquals(InfoType.KEY, keys.get(0));
		assertEquals("title", keys.get(1));
		assertEquals("info-name", keys.get(2));
		assertEquals("info-location", keys.get(3));
	}
	
	@Test
	public void testEquals(){
		assertEquals(info, new ShortInfo(type, title, name, location));
	}
	
	@SuppressWarnings("AssertBetweenInconvertibleTypes")
	@Test
	public void testEqualsNotShortInfo(){
		assertNotEquals(info, new JSONArrayList<>());
	}
	
	@Test
	public void testEqualsDifferentType(){
		assertNotEquals(info, new ShortInfo(InfoType.LIB, title, name, location));
	}
	
	@Test
	public void testEqualsDifferentTitle(){
		assertNotEquals(info, new ShortInfo(type, "Other Title", name, location));
	}
	
	@Test
	public void testEqualsDifferentName(){
		assertNotEquals(info, new ShortInfo(type, title, "Other Name", location));
	}
	
	@Test
	public void testEqualsDifferentLocation(){
		assertNotEquals(info, new ShortInfo(type, title, name, "Other Location"));
	}
	
	@Test
	public void testEqualsDifferentEverything(){
		assertNotEquals(info, new ShortInfo(InfoType.LIB, "Other Title", "Other Name",
				"Other Location"));
	}
}
