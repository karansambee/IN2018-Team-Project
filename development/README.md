# The IN2018 module code

Compilation Instructions:

Import the source into IntelliJ (src) (Unfortunately, as IntelliJs form designer was used, you must use this IDE to build the project).

Now you can build the project.

A MANIFEST.MF file is provided in the META-INF folder within the source folder (src) which is used within a JAR file.

After building within IntelliJ, navigate into the build folder (out/production/<The name of your project>) and zip this into a file.

Make sure the file extention is .jar and not .zip .

Grab the dependancies from the DEP folder.

Deployment will require the created jar file to be located with the two dependancy jars.

You will also need a MySQL compatible database setup with at least a DDL authorized user.

Running the jar is simple, from the directory, run java -jar <name of your deployed jar file> <database host> <database port> <database name> <database username> <database password> [UNLOCK|PURGE]

Where UNLOCK and PURGE are administrative functions (The first unlocks all rows in order to bypass concurrency protection, while the other, CLEARS all the tables).

When run with DDL permissions the program will setup its database on first start with an Administrator account called admin@localhost with an ID of 1 and a password of Administrator.
The program will also create a USD currency with an exchange rate with itself of 1. The Default Administrator account can be disabled through the staff management tab.
