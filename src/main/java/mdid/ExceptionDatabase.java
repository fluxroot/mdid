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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Phokham Nonava
 */
public class ExceptionDatabase {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionDatabase.class);

    private ArrayList<String> table = new ArrayList<>();

    public ExceptionDatabase() {
        // Create an empty database
    }

    public ExceptionDatabase(Path exceptionFile) throws IOException {
        Objects.requireNonNull(exceptionFile);

        logger.info("Reading exception file {}", exceptionFile.toString());
        try (BufferedReader bufferedReader = Files.newBufferedReader(exceptionFile, Charset.defaultCharset())) {
            int count = 0;

            String line = bufferedReader.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.equalsIgnoreCase("")) {
                    table.add(line);
                    ++count;
                }

                line = bufferedReader.readLine();
            }

            logger.info("Read {} entries from exception file", count);
        }
    }

    public boolean contains(String file) {
        Objects.requireNonNull(file);

        return table.contains(file);
    }

    public void put(String file) {
        Objects.requireNonNull(file);

        table.add(file);
    }

}
