##DQL Tester FX

####Requirements for this application
- A Java Runtime Environment (JRE) (e.g. jre1.8.0_161)
- A runnable OpenText Documentum environment that can be pinged by hostname or IP-address from the machine where this application will be executed
- DFC libraries. These can be downloaded from the opentext support site (https://mysupport.opentext.com)

####How to run the application:
1. Download the latest release to the local drive (https://help.github.com/articles/creating-releases)
2. In the download directory create a sub directory named: `lib`
3. Copy the following downloaded DFC jar files in this `lib` directory
    - aspectjrt.jar
    - certj.jar
    - commons-lang-2.4.jar
    - cryptoj.jar
    - dfc.jar
    - log4j.jar
4. Copy the following jar files in this `lib` directory from the download locations
    - json-simple-1.1.1.jar (https://code.google.com/archive/p/json-simple/)
    - lombok-1.16.0.jar (https://projectlombok.org/all-versions)
5. In the download directory create a sub directory named: `config`
6. Create a new file called `dfc.properties` in the `config` directory with this content
    ```
    dfc.docbroker.host[0]=<host; hostname or ip-address>
    dfc.docbroker.port[0]=<port; default 1489>
    dfc.globalregistry.repository=<repository_name>
    dfc.globalregistry.password=<dm_bof_registry (encrypted) password>
    dfc.globalregistry.username=dm_bof_registry
    dfc.session.secure_connect_default=native
    ```
7. Create a new file called `log4j.properties` with this content in the `config` directory
    ```
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
    ```
4. Start a CMD prompt and give these commands (point to your own path as these are samples)
- `cd "c:\DQL Tester FX"`
>It's the location of the downloaded `DQLTesterFX-1.0-SNAPSHOT.jar` and just created `lib` and `config` directory
- `"c:\jre1.8.0_161\bin\java.exe" -cp DQLTesterFX-1.0-SNAPSHOT.jar;lib/*;config nl.bos.Main`
>For a direct login, pass 3 parameters: 
- `"c:\jre1.8.0_161\bin\java.exe" -cp DQLTesterFX-1.0-SNAPSHOT.jar;lib/*;config nl.bos.Main <REPOSITORY_NAME> <USERNAME> <PASSWORD>`