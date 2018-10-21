DQL Tester 16.4

Requirements for this application
- A Java Runtime Environment (JRE) version 8 (e.g. jre1.8.0_161)
- A runnable OpenText Documentum 16.4 environment that can be pinged by hostname or IP-address from the machine where this application will be executed
- DFC libraries from version 16.4. These can be downloaded from the opentext support site (https://mysupport.opentext.com/)

Steps on how to run the application:
1. Download the latest Maven packaged JAR file to the local drive
2. In the download directory create a sub directory named: 'lib'
2a. copy the following downloaded DFC jar files in this subdirectory;
    - certj-6.2.1.jar
    - commons-lang-2.4.jar
    - cryptoj-6.2.2.jar
    - dfc-16.4.0000.0185.jar
    - log4j-1.2.13.jar
    - tools-1.8.10.jar (= aspectj)
2b. copy the following jar files from the download location
    - json-simple-1.1.1.jar (https://code.google.com/archive/p/json-simple/)
    - lombok-1.16.0.jar (https://projectlombok.org/all-versions)
3. In the download directory create a sub directory named: 'config'
3a. create a new file called 'dfc.properties' with this content
    dfc.docbroker.host[0]=<hostname or IP>
    dfc.docbroker.port[0]=<connection broker port; default 1489>
    dfc.globalregistry.repository=<repository name>
    dfc.globalregistry.password=<dm_bof_registry (encrypted) password>
    dfc.globalregistry.username=dm_bof_registry
    dfc.session.secure_connect_default=native
3b. create a new file called 'log4j.properties' with this content
    log4j.rootCategory=INFO, A1, F1
    log4j.category.MUTE=OFF
    log4j.additivity.tracing=false
    
    #------------------- CONSOLE --------------------------
    log4j.appender.A1=org.apache.log4j.ConsoleAppender
    log4j.appender.A1.threshold=ERROR
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%d{ABSOLUTE} %5p [%t] %c - %m%n
    
    #------------------- FILE --------------------------
    log4j.appender.F1=org.apache.log4j.RollingFileAppender
    log4j.appender.F1.File=C\:/Documentum/logs/log4j.log
    log4j.appender.F1.MaxFileSize=10MB
    log4j.appender.F1.layout=org.apache.log4j.PatternLayout
    log4j.appender.F1.layout.ConversionPattern=%d{ABSOLUTE} %5p [%t] %c - %m%n
4. Start a CMD prompt and give these commands:
4a. cd "c:\DQL Tester 16.4"
    - location of DQLTester-1.0-SNAPSHOT.jar
4b. "C:\Program Files\Java\jre1.8.0_161\bin\java.exe" -jar DQLTester-1.0-SNAPSHOT.jar
    - For a direct login pass 3 parameters: "C:\Program Files\Java\jre1.8.0_161\bin\java.exe" -jar DQLTester-1.0-SNAPSHOT.jar <REPOSITORY_NAME> <USERNAME> <PASSWORD>