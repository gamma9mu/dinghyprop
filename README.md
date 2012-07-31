DinghyProp
==========


DinghyProp is comprised of three layers: the GP layer, the interpreter, and the
simulation environment.  The network architecture is a master-slave
configuration.  The master hosts most of the GP layer, breeding generations of
programs.  These programs are sent to the slaves which host the interpreter and
simulation environment.  These sections are used to evaluate the fitness of the
programs, which is returned to the master for use in selecting programs for
reproduction.

Genetic Programming Overview
----------------------------

### Objective
Find a program that will navigate a boat through a simulated environment.  It
should be capable of avoiding hazards and reaching a goal location known by its
position.

### Function Set
The function set includes the arithmetic functions (+, -, \*, /, and ^ for
exponentiation), an if-then-else construct, comaprison functions (<, >, <=, >=,
==, and !=), and navigation functions (move, turn-left, and turn-right).

### Terminal Set
The terminal set includes integers as well as the following variables:

variables representing distances to a hazard:

  * front
  * short-left (45&deg; left of front)
  * short-right (45&deg; right of front)
  * left
  * right
  * rear

positional variables:

  * position-x
  * position-y
  * goal-position-x
  * goal-position-y
  * heading

### Fitness
The fitness of the program will be determined using 3 combined metrics.  The
first is the distance traveled (maximum of 100 points).  The second metric is
the ratio of the distance to the goal divided by the initial distance to the
goal in the simulation scaled to an integer percentage (called the percent
improvement).  The third metric is a 100 point reward for reaching the goal.

### Selection
Programs will be selected by tournament selection for any among crossover, point
mutation, subtree mutation, and reproduction.

### Initial Population
Ramped half-and-half (depth: 3, terminals: 10% constants)

### Initial Parameters
  * 90% Crossover
  * 8% Mutation (evenly split between point and subtree mutations)
  * 2% Reproduction

### Termination
Individual with fitness 300 for each test case (assumes a simulation duration
of 100 iterations) or after 100,000 generations.

### Interpreter Overview
The purpose of the interpreter is the execution of the evolved programs.  The
features of the interpreter will be limited to supporting the features of the
language listed above.  The language will use S-expressions and prefix notation
for function calls, as in Lisp, because this offers an easy to parse format.
navigation functions and variables are handled by the simulator while branching,
arithmetic operators, and comparison operators are handled directly within the
interpreter.

### Simulation Overview
The simulation environment serves to support the interpreter by tracking
information regarding the dinghy and its environment.  While the interpreter
evaluates the programs, function calls and variable references are forwarded to
the simulation environment.  The simulation handles tracking the boat and
providing the requested information to the interpreter.  In addition, the
simulator determines the fitness of the program.


## Building
Building requires Java 1.6 or newer and Apache Ant 1.6 or newer.
In the project directory, run:

    ant


## Running DinghyProp
Because of issue with Java RMI and callback objects, many forms of firewalling
or NAT will cause the clients to hang when connecting to the server.  Because of
this, DinghyProp must be used in a network where the clients may connect to the
server without passing through firewalls and NAT.

### Running the Server
Locate the JAR titled 'Server.jar'.  Immediately after a build, it is located
in out/artifacts.  Use that path in the commands below if you have moved the
JAR.  Use the resolvable name of the server or its IP address as the argument
to `-Djava.rmi.server.hostname=`.

on Linux (here, IP address is 131.230.6.172), run:

    CLASSPATH=out/artifacts/Server.jar rmiregistry &
    java -Djava.rmi.server.hostname=131.230.6.172 -jar out/artifacts/Server.jar

on Windows (here, IP address is 131.230.6.172), run:

    set CLASSPATH=out/artifacts/Server.jar
    start rmiregistry
    java -Djava.rmi.server.hostname=131.230.6.172 -jar out/artifacts/Server.jar


### Setting Up Java WebStart
For WebStart to work, the server machine must also run a webserver.  The client
JAR, `out/artifacts/Client.jar`, should be moved to a directory served by the web
server.  Next, in the project directory, locate the JNLP file, `dgp.jnlp`.  Edit
line 8 of the JNLP file to reflect the location of the client JAR on the web
server (here, IP address is 131.230.6.72, web server directory is dgp):

    <jnlp spec="6.0+" href="dgp.jnlp" version="1.0" codebase="http://131.230.6.172/dgp">

The web server should be configure to report a JNLP file's MIMEtype as

    'application/x-java-jnlp-file'

On Apache, this is done by adding the line

    application/x-java-jnlp-file JNLP

to Apache's `mime.types` file.

Finally, the JNLP file should be copied to the same directory on the web server
as `Client.jar` was.


### Running a WebStart Client
A client computer can navigate a web browser to the address of the JNLP file on
the web server and Java WebStart should launch the client.  If, instead, the
browser downloads the JNLP, the program can be run by enter a command at the
commandline:

    javaws path/to/dgp.jnlp


### Running a Client from the Command Line
Given the build location of the client JAR and a server address of
131.230.6.172, the client is started by entering the following on the command
line:

    java -jar out/artifacts/Client.jar 131.230.6.172


### Starting the Monitor Animation
Given the build location of the monitors JAR and a server address of
131.230.6.172, the a monitor client is started by entering the following on the
command line:

    java -jar out/artifacts/Monitors.jar 131.230.6.172
