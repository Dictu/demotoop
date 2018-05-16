# Contents #
Demo application for TOOP 
For cross border login a connection to an EidasNode Connector is needed.

# First of all #
This project depends on:
- JDK 9 (http://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase9-3934878.html)
- Apache Maven 3x (https://maven.apache.org/)
- Lombok (https://projectlombok.org)
All three need to be installed, configured and running properly.

# Check out #
Use GIT clone to get a local copy of this repo.

# Configuration #
Before running check server.port of webserver, eidas configuration and key configuration inside ./src/main/resources/application.properties.
For outgoing network connections a proxy configuration might be needed. Running locally this is not necessary.
When running the jar, the application.properties can be placed next to the jar and it will be picked up automatically.

# Building #
You will need to build the toopapi project first!
Build using:
mvn clean install

# Running #
Run the application inside ./target/:
java -jar demotoop<version>.jar

# OR #
mvn spring-boot:run

# Browse to # 
http://localhost:<server.port>/demotoop/ and log in using your eIDAS credentials.