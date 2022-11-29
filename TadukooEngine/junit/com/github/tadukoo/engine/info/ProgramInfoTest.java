package com.github.tadukoo.engine.info;

import com.github.tadukoo.engine.ProgramHandler;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.util.junit.logger.JUnitEasyLogger;
import com.github.tadukoo.util.pojo.MappedPojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
	public void testBuilderDefaultLogger(){
		assertNull(programInfo.getLogger());
	}
	
	@Test
	public void testBuilderSetLogger() throws Throwable{
		JUnitEasyLogger logger = new JUnitEasyLogger();
		programInfo = ProgramInfo.builder()
				.logger(logger)
				.title(title).description(description)
				.build();
		assertEquals(logger, programInfo.getLogger());
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
	public void testBuilderDefaultLibraries(){
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(0, libraries.size());
	}
	
	@Test
	public void testBuilderSetLibrariesJSONArrayList() throws Throwable{
		JSONArrayList<ShortInfo> librariesList = new JSONArrayList<>();
		librariesList.add(new ShortInfo(InfoType.LIB, "Test", "test.jar", "nowhere"));
		librariesList.add(new ShortInfo(InfoType.LIB, "Test 2", "test2.jar", "nowhere2"));
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.libraries(librariesList)
				.build();
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(librariesList, libraries);
	}
	
	@Test
	public void testBuilderSetLibrariesList() throws Throwable{
		List<ShortInfo> librariesList = new ArrayList<>();
		librariesList.add(new ShortInfo(InfoType.LIB, "Test", "test.jar", "nowhere"));
		librariesList.add(new ShortInfo(InfoType.LIB, "Test 2", "test2.jar", "nowhere2"));
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.libraries(librariesList)
				.build();
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(2, libraries.size());
		assertEquals(libraries.get(0), libraries.get(0));
		assertEquals(libraries.get(1), libraries.get(1));
	}
	
	@Test
	public void testBuilderSingleLibrary() throws Throwable{
		ShortInfo library = new ShortInfo(InfoType.LIB, "Test", "test.jar", "nowhere");
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.library(library)
				.build();
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(1, libraries.size());
		assertEquals(library, libraries.get(0));
	}
	
	@Test
	public void testBuilderSingleLibraryPieces() throws Throwable{
		String title = "Test";
		String infoName = "test.jar";
		String infoLocation = "nowhere";
		programInfo = ProgramInfo.builder()
				.title(title).description(description)
				.library(title, infoName, infoLocation)
				.build();
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(1, libraries.size());
		ShortInfo library = libraries.get(0);
		assertEquals(InfoType.LIB, library.getType());
		assertEquals(title, library.getTitle());
		assertEquals(infoName, library.getInfoName());
		assertEquals(infoLocation, library.getInfoLocation());
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
		JUnitEasyLogger logger = new JUnitEasyLogger();
		ProgramHandler progHand = programInfo -> {
			// Do nothing
		};
		String libTitle1 = "Test";
		String infoName1 = "test.jar";
		String infoLocation1 = "nowhere";
		String libTitle2 = "Test 2";
		String infoName2 = "test2.jar";
		String infoLocation2 = "nowhere2";
		programInfo = ProgramInfo.builder()
				.logger(logger)
				.title(title).description(description)
				.programJarName("testJarName.jar")
				.library(libTitle1, infoName1, infoLocation1)
				.library(new ShortInfo(InfoType.LIB, libTitle2, infoName2, infoLocation2))
				.programHandler(progHand)
				.build();
		assertEquals(logger, programInfo.getLogger());
		assertEquals(title, programInfo.getTitle());
		assertEquals(description, programInfo.getDescription());
		assertEquals("testJarName.jar", programInfo.getProgramJarName());
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(2, libraries.size());
		ShortInfo library = libraries.get(0);
		assertEquals(InfoType.LIB, library.getType());
		assertEquals(libTitle1, library.getTitle());
		assertEquals(infoName1, library.getInfoName());
		assertEquals(infoLocation1, library.getInfoLocation());
		ShortInfo library2 = libraries.get(1);
		assertEquals(InfoType.LIB, library2.getType());
		assertEquals(libTitle2, library2.getTitle());
		assertEquals(infoName2, library2.getInfoName());
		assertEquals(infoLocation2, library2.getInfoLocation());
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
		JUnitEasyLogger logger = new JUnitEasyLogger();
		ProgramHandler progHand = programInfo -> {
			// Do nothing
		};
		String libTitle1 = "Test";
		String infoName1 = "test.jar";
		String infoLocation1 = "nowhere";
		String libTitle2 = "Test 2";
		String infoName2 = "test2.jar";
		String infoLocation2 = "nowhere2";
		MappedPojo pojo = ProgramInfo.builder()
				.logger(logger)
				.title(title).description(description)
				.programJarName("testJarName.jar")
				.library(libTitle1, infoName1, infoLocation1)
				.library(new ShortInfo(InfoType.LIB, libTitle2, infoName2, infoLocation2))
				.programHandler(progHand)
				.build();
		programInfo = new ProgramInfo(pojo);
		assertEquals(logger, programInfo.getLogger());
		assertEquals(title, programInfo.getTitle());
		assertEquals(description, programInfo.getDescription());
		assertEquals("testJarName.jar", programInfo.getProgramJarName());
		List<ShortInfo> libraries = programInfo.getLibraries();
		assertTrue(libraries instanceof JSONArrayList);
		assertEquals(2, libraries.size());
		ShortInfo library = libraries.get(0);
		assertEquals(InfoType.LIB, library.getType());
		assertEquals(libTitle1, library.getTitle());
		assertEquals(infoName1, library.getInfoName());
		assertEquals(infoLocation1, library.getInfoLocation());
		ShortInfo library2 = libraries.get(1);
		assertEquals(InfoType.LIB, library2.getType());
		assertEquals(libTitle2, library2.getTitle());
		assertEquals(infoName2, library2.getInfoName());
		assertEquals(infoLocation2, library2.getInfoLocation());
		assertEquals(progHand, programInfo.getItem(ProgramInfo.PROGRAM_HANDLER));
	}
	
	@Test
	public void testGetKeyOrder(){
		List<String> keys = programInfo.getKeyOrder();
		assertEquals(4, keys.size());
		assertEquals("title", keys.get(0));
		assertEquals("description", keys.get(1));
		assertEquals("program-jar-name", keys.get(2));
		assertEquals("libraries", keys.get(3));
	}
	
	@Test
	public void testSetLogger(){
		JUnitEasyLogger logger = new JUnitEasyLogger();
		assertNull(programInfo.getLogger());
		programInfo.setLogger(logger);
		assertEquals(logger, programInfo.getLogger());
	}
}
