package com.github.tadukoo.engine.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InfoTypeTest{
	
	@Test
	public void testKEY(){
		assertEquals("type", InfoType.KEY);
	}
	
	@Test
	public void testLIB(){
		assertEquals("lib", InfoType.LIB.getType());
	}
	
	@Test
	public void testPROGRAM(){
		assertEquals("program", InfoType.PROGRAM.getType());
	}
	
	@Test
	public void testLIST(){
		assertEquals("list", InfoType.LIST.getType());
	}
	
	@Test
	public void testParseFromStringLIB(){
		assertEquals(InfoType.LIB, InfoType.parseFromString("lib"));
	}
	
	@Test
	public void testParseFromStringPROGRAM(){
		assertEquals(InfoType.PROGRAM, InfoType.parseFromString("program"));
	}
	
	@Test
	public void testParseFromStringLIST(){
		assertEquals(InfoType.LIST, InfoType.parseFromString("list"));
	}
	
	@Test
	public void testParseFromStringInvalid(){
		assertNull(InfoType.parseFromString("garbage-text"));
	}
}
