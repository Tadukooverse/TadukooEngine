package com.github.tadukoo.engine.info;

import com.github.tadukoo.engine.ProgramHandler;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.util.pojo.MappedPojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ProgramInfoTest{
	private ProgramInfo programInfo;
	private final String title = "A Title";
	private final String description = "Some description";
	private final String programJarName = "ATitle.jar";
	
	@BeforeEach
	public void setup() throws Throwable{
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.build();
	}
	
	@Test
	public void testBuilderTitle(){
		assertEquals(title, programInfo.getTitle());
	}
	
	@Test
	public void testBuilderDescription(){
		assertEquals(description, programInfo.getDescription());
	}
	
	@Test
	public void testBuilderDefaultProgramJarName(){
		assertEquals(programJarName, programInfo.getProgramJarName());
	}
	
	@Test
	public void testBuilderProgramJarNameMissingJar() throws Throwable{
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.programJarName("ATitle")
				.build();
		assertEquals(programJarName, programInfo.getProgramJarName());
	}
	
	@Test
	public void testBuilderSetProgramJarName() throws Throwable{
		String jarName = "SomeJarName.jar";
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.programJarName(jarName)
				.build();
		assertEquals(jarName, programInfo.getProgramJarName());
	}
	
	@Test
	public void testBuilderDefaultLibJarNames(){
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(0, libJarNames.size());
	}
	
	@Test
	public void testBuilderSetLibJarNamesJSONArrayList() throws Throwable{
		JSONArrayList<String> jarNames = new JSONArrayList<>();
		jarNames.add("test.jar");
		jarNames.add("test2.jar");
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.libJarNames(jarNames)
				.build();
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(jarNames, libJarNames);
	}
	
	@Test
	public void testBuilderSetLibJarNamesList() throws Throwable{
		List<String> jarNames = new ArrayList<>();
		jarNames.add("test.jar");
		jarNames.add("test2.jar");
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.libJarNames(jarNames)
				.build();
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(2, libJarNames.size());
		assertEquals(jarNames.get(0), libJarNames.get(0));
		assertEquals(jarNames.get(1), libJarNames.get(1));
	}
	
	@Test
	public void testBuilderSingleLibJarName() throws Throwable{
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.libJarName("test.jar")
				.build();
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(1, libJarNames.size());
		assertEquals("test.jar", libJarNames.get(0));
	}
	
	@Test
	public void testBuilderSetProgramHandler() throws Throwable{
		ProgramHandler progHand = programInfo -> {
			// Do nothing
		};
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.programHandler(progHand)
				.build();
		assertEquals(progHand, programInfo.getItem(ProgramInfo.PROGRAM_HANDLER));
	}
	
	@Test
	public void testBuilderSetEverything() throws Throwable{
		ProgramHandler progHand = programInfo -> {
			// Do nothing
		};
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.programJarName("testJarName.jar")
				.libJarName("test.jar").libJarName("test2.jar")
				.programHandler(progHand)
				.build();
		assertEquals(title, programInfo.getTitle());
		assertEquals(description, programInfo.getDescription());
		assertEquals("testJarName.jar", programInfo.getProgramJarName());
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(2, libJarNames.size());
		assertEquals("test.jar", libJarNames.get(0));
		assertEquals("test2.jar", libJarNames.get(1));
		assertEquals(progHand, programInfo.getItem(ProgramInfo.PROGRAM_HANDLER));
	}
	
	@Test
	public void testBuilderMissingTitle() throws Throwable{
		try{
			programInfo = ProgramInfo.builder()
					.description(description)
					.build();
			fail();
		}catch(IllegalArgumentException e){
			assertEquals("The following errors happened in building a ProgramInfo: " +
							"\ntitle is required",
					e.getMessage());
		}
	}
	
	@Test
	public void testBuilderMissingDescription() throws Throwable{
		try{
			programInfo = ProgramInfo.builder()
					.title(title)
					.build();
			fail();
		}catch(IllegalArgumentException e){
			assertEquals("The following errors happened in building a ProgramInfo: " +
							"\ndescription is required",
					e.getMessage());
		}
	}
	
	@Test
	public void testBuilderMissingEverything() throws Throwable{
		try{
			programInfo = ProgramInfo.builder().build();
			fail();
		}catch(IllegalArgumentException e){
			assertEquals("""
							The following errors happened in building a ProgramInfo:\s
							title is required
							description is required""",
					e.getMessage());
		}
	}
	
	@Test
	public void testMappedPojoConstructor() throws Throwable{
		ProgramHandler progHand = programInfo -> {
			// Do nothing
		};
		MappedPojo pojo = ProgramInfo.builder()
				.title(title).description(description)
				.programJarName("testJarName.jar")
				.libJarName("test.jar").libJarName("test2.jar")
				.programHandler(progHand)
				.build();
		programInfo = new ProgramInfo(pojo);
		assertEquals(title, programInfo.getTitle());
		assertEquals(description, programInfo.getDescription());
		assertEquals("testJarName.jar", programInfo.getProgramJarName());
		List<String> libJarNames = programInfo.getLibJarNames();
		assertTrue(libJarNames instanceof JSONArrayList);
		assertEquals(2, libJarNames.size());
		assertEquals("test.jar", libJarNames.get(0));
		assertEquals("test2.jar", libJarNames.get(1));
		assertEquals(progHand, programInfo.getItem(ProgramInfo.PROGRAM_HANDLER));
	}
	
	@Test
	public void testGetKeyOrder(){
		List<String> keys = programInfo.getKeyOrder();
		assertEquals(4, keys.size());
		assertEquals("title", keys.get(0));
		assertEquals("description", keys.get(1));
		assertEquals("program-jar-name", keys.get(2));
		assertEquals("library-jar-names", keys.get(3));
	}
}
