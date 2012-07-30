DinghyProp
==========


Building
--------
Building requires Java 1.6 or newer and Apache Ant 1.6 or newer.
In the project directory, run:
    ant


Running the Server
------------------
Locate the JAR titled 'Server.jar'.  Immediately after a build, it is located
in out/artifacts.  Use that path in the commands below if you have moved the
JAR.  Use the resolvable name of the server or its IP address as the argument
to '-Djava.rmi.server.hostname='.

on Linux (here, IP address is 131.230.6.172), run:
    CLASSPATH=out/artifacts/Server.jar rmiregistry &
    java -Djava.rmi.server.hostname=131.230.6.172 -jar out/artifacts/Server.jar

on Windows (here, IP address is 131.230.6.172), run:
    set CLASSPATH=out/artifacts/Server.jar
    start rmiregistry
    java -Djava.rmi.server.hostname=131.230.6.172 -jar out/artifacts/Server.jar


Setting Up Java WebStart
------------------------
For WebStart to work, the server machine must also run a webserver.  The client
JAR (out/artifacts/Client.jar) should be moved to a directory served by the web
server.  Next, in the project directory, locate the JNLP file (dgp.jnlp).  Edit
line 8 of the JNLP file to reflect the location of the client JAR on the web
server (here, IP address is 131.230.6.72, web server directory is dgp):

    <jnlp spec="6.0+" href="dgp.jnlp" version="1.0" codebase="http://131.230.6.172/dgp">

The web server should be configure to report a JNLP file's MIMEtype as
    'application/x-java-jnlp-file'
On Apache, this is done by adding the line
    application/x-java-jnlp-file JNLP
to Apache's mime.types file.

Finally, the JNLP file should be copied to the same directory on the web server
as Client.jar was.


Running a WebStart Client
-------------------------
A client computer can navigate a web browser to the address of the JNLP file on
the web server and Java WebStart should launch the client.  If, instead, the
browser downloads the JNLP, the program can be run by enter a command at the
commandline:
    javaws path/to/dgp.jnlp


Running a Client from the Command Line
--------------------------------------
Given the build location of the client JAR and a server address of
131.230.6.172, the client is started by entering the following on the command
line:
    java -jar out/artifacts/Client.jar 131.230.6.172


Starting the Monitor Animation
------------------------------
Given the build location of the monitors JAR and a server address of
131.230.6.172, the a monitor client is started by entering the following on the
command line:
    java -jar out/artifacts/Monitors.jar 131.230.6.172
