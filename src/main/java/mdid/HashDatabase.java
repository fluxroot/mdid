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
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Phokham Nonava
 */
public class HashDatabase implements Closeable {

    public static final String FILENAME = "sha1sum";
    public static final String MESSAGEDIGEST = "SHA-1";

    private static final Logger logger = LoggerFactory.getLogger(HashDatabase.class);

    private final Path hashFile;
    private final boolean writable;

    private Hashtable<String, String> table = new Hashtable<>();
    private Hashtable<String, String> marks = new Hashtable<>();

    public HashDatabase(Path hashFile) throws IOException {
        Objects.requireNonNull(hashFile);

        this.hashFile = hashFile;
        this.writable = true;

        logger.info("Opening database in writable mode");

        Files.deleteIfExists(hashFile);
        Files.createFile(hashFile);
        logger.info("Created hash file {}", hashFile.toString());
    }

    public HashDatabase(Path hashFile, boolean writable) throws IOException {
        Objects.requireNonNull(hashFile);

        this.hashFile = hashFile;
        this.writable = writable;

        if (writable) {
            logger.info("Opening database in writable mode");
        } else {
            logger.info("Opening database in read only mode");
        }

        logger.info("Reading hash file {}", hashFile.toString());
        try (BufferedReader bufferedReader = Files.newBufferedReader(hashFile, Charset.defaultCharset())) {
            int count = 0;

            String line = bufferedReader.readLine();
            while (line != null) {
                // A hash line consists of <hash value> <path>
                // Search for the first space
                int index = line.indexOf(" ");
                if (index != -1) {
                    String hash = line.substring(0, index).trim();
                    String path = line.substring(index).trim();

                    table.put(path, hash);
                    ++count;
                } else {
                    logger.warn("Invalid line format {}", line);
                }

                line = bufferedReader.readLine();
            }

            logger.info("Read {} entries from hash file", count);
        }
    }

    public void close() throws IOException {
        if (writable) {
            logger.info("Writing hash file {}", hashFile.toString());
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(hashFile, Charset.defaultCharset())) {
                int count = 0;

                for (Entry<String, String> entry : table.entrySet()) {
                    String line = entry.getValue() + " " + entry.getKey();

                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    ++count;
                }

                for (Entry<String, String> entry : marks.entrySet()) {
                    String line = entry.getValue() + " " + entry.getKey();

                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    ++count;
                }

                logger.info("Wrote {} entries to the hash file", count);
            }
        }
    }

    public String get(String file) {
        Objects.requireNonNull(file);

        String hash = table.get(file);
        if (hash == null) {
            hash = marks.get(file);
        }

        return hash;
    }

    public List<String> getUnmarked() {
        return new ArrayList<>(table.keySet());
    }

    public String putAndMark(String path, String hash) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(hash);

        String oldTableHash = table.remove(path);
        String oldMarksHash = marks.put(path, hash);
        assert !(oldTableHash != null && oldMarksHash != null);

        if (oldTableHash != null) {
            return oldTableHash;
        } else {
            return oldMarksHash;
        }
    }

    public String mark(String file) {
        Objects.requireNonNull(file);

        String hash = table.remove(file);
        if (hash != null) {
            marks.put(file, hash);
            return hash;
        } else {
            return marks.get(file);
        }
    }

    public String remove(String file) {
        Objects.requireNonNull(file);

        String hash = table.remove(file);
        if (hash == null) {
            hash = marks.remove(file);
        }

        return hash;
    }

    public void removeUnmarked() {
        table.clear();
    }

}
