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
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * @author Phokham Nonava
 */
public class Configuration {

    private static final Configuration instance = new Configuration();
    
    public AbstractOperationMode mode = null;
    public Path path = null;
    
    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }
    
    public void parseArgumens(String[] args) throws NoSuchAlgorithmException, IOException {
        // Build our parser
        OptionParser parser = new OptionParser();
        OptionSpec<Path> hashDatabaseArg = parser.accepts("f").withRequiredArg().withValuesConvertedBy(new PathConverter()).defaultsTo(Paths.get(HashDatabase.FILENAME));
        OptionSpec<Path> exceptionDatabaseArg = parser.accepts("e").withRequiredArg().withValuesConvertedBy(new PathConverter());
        
        // Parse arguments
        OptionSet options = parser.parse(args);

        // Get the non-option arguments
        List<String> nonOptionArgs = options.nonOptionArguments();

        // Check the non-option arguments
        if (nonOptionArgs.size() < 1) {
            throw new OptionException("Please specify an operation mode");
        } else if (nonOptionArgs.size() > 2) {
            throw new OptionException("Unknown option: " + nonOptionArgs.get(2));
        }

        // Get the hash database
        Path hashDatabase = hashDatabaseArg.value(options).normalize();

        // Get the exception database
        Path exceptionDatabase = null;
        if (options.hasArgument(exceptionDatabaseArg)) {
            exceptionDatabase = exceptionDatabaseArg.value(options).normalize();
            if (!Files.exists(exceptionDatabaseArg.value(options))) {
                throw new OptionException("Exception database does not exist: " + exceptionDatabase.toString());
            }
        }

        // Get the operation mode
        if (nonOptionArgs.get(0).equalsIgnoreCase("index")) {
            mode = new IndexingMode(hashDatabase, exceptionDatabase);
        } else if (nonOptionArgs.get(0).equalsIgnoreCase("update")) {
            mode = new UpdateMode(hashDatabase, exceptionDatabase);
        } else if (nonOptionArgs.get(0).equalsIgnoreCase("analyze")) {
            mode = new AnalysisMode(hashDatabase, exceptionDatabase);
        } else {
            throw new OptionException("Unknown operation mode: " + nonOptionArgs.get(0));
        }
        
        // Get the path
        if (nonOptionArgs.size() == 2) {
            path = Paths.get(nonOptionArgs.get(1)).normalize();
            if (!Files.exists(path)) {
                throw new OptionException("File does not exist: " + path.toString());
            }
        } else {
            path = Paths.get(".").normalize();
        }
    }

}
