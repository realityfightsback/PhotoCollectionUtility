package test;

import static org.junit.Assert.*;
import impl.Comparison;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class ComparisonTest {
	File f;
	String testDir = "C:\\Users\\Standard\\Desktop\\JUnitTestDir";

	@Before
	public void setUp() throws Exception {
		f = new File(testDir + "\\Test.txt");
		f.mkdirs();
		f.createNewFile();
		Calendar cal = Calendar.getInstance();

		cal.set(2003, 5, 12);
		f.setLastModified(cal.getTimeInMillis());

		Comparison.setRepoLocation(testDir);
		Comparison.setSubLocation(testDir);

	}

	@Test
	public void createUniqueFilePathTest() throws IOException {

		String res = Comparison.createFilePath(false, f);
		assertEquals("Improper unique file filepath", testDir
				+ "\\unique\\2003-5\\Test.txt", res);
	}

	@Test
	public void createDupeFilePathTest() throws IOException {

		String res = Comparison.createFilePath(true, f);
		assertEquals("Improper dupe filepath", testDir
				+ "\\likelyDupe\\2003-5\\Test.txt", res);
	}

	@Test
	public void dateFormatCreationTest() throws IOException {

		StringBuilder sBuild = new StringBuilder();
		Comparison.createDataFormattedPath(f, sBuild);

		assertEquals("Date formatting is incorrect", "\\2003-5\\Test.txt",
				sBuild.toString());
	}
}
