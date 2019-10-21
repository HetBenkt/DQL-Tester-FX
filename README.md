[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=HetBenkt_DQL-Tester-FX)](https://sonarcloud.io/dashboard?id=HetBenkt_DQL-Tester-FX)

# DQL Tester FX
#### With new technologies, new tools will be created!
This slogan made me start this project where we want to create a new version of the good old 'DQL Tester' tool.

'DQL Tester' supports the administration tasks on a Documentum environment. With this project a new version of 'DQL Tester' is under construction and made available for everyone who wants to join our development team.

The 'FX' postfix in the title makes it clear we use the new JavaFX technology to create standalone applications. 

### Feature list
* Execute DQL queries with the result in a table view
* Browse the repository
* Dump and modify properties
* Administration tasks
    * Running jobs
    * User/Group management
    * Updates in security
* Execute API and DQL scripts
     

### Requirements for this application
* A Java Runtime Environment (JRE) (e.g. [OpenJDK Runtime Environment 18.9 (build 11.0.1+13)](https://jdk.java.net/))
* JavaFX libraries (as they are not included in OpenJDK!). Downloadable fom [JavaFX SDK](https://gluonhq.com/products/javafx/)
* A runnable OpenText Documentum environment that can be pinged by hostname or IP-address from the machine where this application will be executed
* DFC and BPM/PE libraries. These can be downloaded from [OpenText My Support](https://mysupport.opentext.com)

### How to run the application:
1. Download the latest [release](https://github.com/HetBenkt/DQL-Tester-FX/releases) to the local drive from
2. In the download directory create a sub directory named: `lib`
3. Copy the following DFC (minimal) jar files in this `lib` directory
    * aspectjrt.jar
    * certj.jar
    * commons-lang-2.4.jar
    * cryptoj.jar
    * dfc.jar
    * log4j.jar
    * jaxb-api.jar
    * bpm_infra.jar (BPM / Process Engine)
4. Copy the following jar files in this `lib` directory from the download locations
    * [json-20180813.jar](http://central.maven.org/maven2/org/json/json/20180813/json-20180813.jar)
    * [richtextfx-fat-0.10.1.jar](https://github.com/FXMisc/RichTextFX/releases/download/v0.10.1/richtextfx-fat-0.10.1.jar)    
5. In the download directory create a sub directory named: `config`
6. Create a new file called `dfc.properties` in the `config` directory with this content
    ```
    dfc.docbroker.host[0]=<host; hostname or ip-address>
    dfc.docbroker.port[0]=<port; default 1489>
    dfc.globalregistry.repository=<repository_name>
    dfc.globalregistry.password=<dm_bof_registry (encrypted) password>
    dfc.globalregistry.username=dm_bof_registry
    dfc.session.secure_connect_default=native
    dfc.date_format=dd/mm/yyyy hh:mi:ss
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
9. Start a CMD prompt and give these commands (point to your own path as these are samples)
* `cd "c:\DQL Tester FX"`
>It's the location of the downloaded `DQLTesterFX-xxx.jar` and just created `lib` and `config` directory
* `set PATH_TO_FX="c:\javafx-sdk-11.0.1\lib"`
>Sets the path to the extracted JavaFX runtime libraries
* `"c:\jdk-11.0.1\bin\java.exe" --module-path %PATH_TO_FX% --add-modules=javafx.controls,javafx.fxml -cp DQLTesterFX-xxx.jar;lib\*;config nl.bos.Main`
>For a direct login, pass 3 parameters: 
* `"c:\jdk-11.0.1\bin\java.exe" --module-path %PATH_TO_FX% --add-modules=javafx.controls,javafx.fxml -cp DQLTesterFX-xxx.jar;lib\*;config nl.bos.Main <REPOSITORY_NAME> <USERNAME> <PASSWORD>`
