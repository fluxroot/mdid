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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Phokham Nonava
 */
public class ExceptionDatabaseTest {

    private static Path exceptionFile = null;

    @BeforeClass
    public static void beforeClass() throws IOException {
        exceptionFile = Files.createTempFile("mdid", null);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(exceptionFile, Charset.defaultCharset())) {
            bufferedWriter.write("a/path");
            bufferedWriter.newLine();
        }
    }

    @AfterClass
    public static void afterClass() throws IOException {
        Files.delete(exceptionFile);
    }

    @Test
    public void testExceptionDatabase() throws IOException {
        ExceptionDatabase database = new ExceptionDatabase(exceptionFile);
        Assert.assertTrue(database.contains("a/path"));
    }

}
