/*
** Copyright 2012 Phokham Nonava
**
** This file is part of Message Digest Integrity Database.
**
** Message Digest Integrity Database is free software: you can redistribute it and/or modify
** it under the terms of the GNU Lesser General Public License as published by
** the Free Software Foundation, either version 3 of the License, or
** (at your option) any later version.
**
** Message Digest Integrity Database is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with Message Digest Integrity Database.  If not, see <http://www.gnu.org/licenses/>.
*/
package mdid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Phokham Nonava
 */
public class HashDatabaseTest {

	private static Path tempDirectory = null;
	private Path hashFile = null;
	
	@BeforeClass
	public static void beforeClass() throws IOException {
		tempDirectory = Files.createTempDirectory("mdid");
	}
	
	@AfterClass
	public static void afterClass() throws IOException {
		Files.delete(tempDirectory);
	}

	@Before
	public void before() {
		hashFile = tempDirectory.resolve("mdid.db");
	}
	
	@After
	public void after() throws IOException {
		Files.deleteIfExists(hashFile);
	}
	
	@Test
	public void testReadOnly() throws IOException {
		// Initialize database
		try (HashDatabase database = new HashDatabase(hashFile)) {
			database.putAndMark("a/path", "1234");
		}

		// Reopen database read only
		try (HashDatabase database = new HashDatabase(hashFile, false)) {
			database.putAndMark("another/path", "5678");
		}

		// Check database
		try (HashDatabase database = new HashDatabase(hashFile, false)) {
			Assert.assertNull(database.get("another/path"));
		}
	}
	
	@Test
	public void testWritable() throws IOException {
		// Initialize database
		try (HashDatabase database = new HashDatabase(hashFile)) {
			database.putAndMark("a/path", "1234");
		}

		// Reopen database writable
		try (HashDatabase database = new HashDatabase(hashFile, true)) {
			database.putAndMark("another/path", "5678");
		}

		// Check database
		try (HashDatabase database = new HashDatabase(hashFile, false)) {
			Assert.assertNotNull(database.get("another/path"));
		}
	}

	@Test
	public void testHashDatabase() throws IOException {
		try (HashDatabase database = new HashDatabase(hashFile)) {
			database.putAndMark("a/path", "1234");
			database.putAndMark("another/path", "5678");
		}

		try (HashDatabase database = new HashDatabase(hashFile, false)) {
			Assert.assertEquals("1234", database.get("a/path"));
			
			database.mark("a/path");
			database.removeUnmarked();
			Assert.assertNull(database.get("another/path"));
			
			database.remove("a/path");
			Assert.assertNull(database.get("a/path"));
		}
	}

}
