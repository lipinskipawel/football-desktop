## This is the plugin for the game

The propose of this plugin is to create the installers for the following platforms:
 - Windows
 - Linux

### How to build it from source
To build plugin from source the following command have to me executed
 - `mvn clean install`

<strong>Remember</strong> to be in the same folder as this README.md file.

### How to use it 
In order to bundle this plugin to your product you have to add this into your plugin section. Example in maven
```xml
<plugin>
    <groupId>com.github.lipinskipawel</groupId>
    <artifactId>prod-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <configuration>
        <builtFinalNameOfJar>${build.finalName}</builtFinalNameOfJar>
        <directory>football-game</directory>
        <jdkOfNeed>jdks/jdk</jdkOfNeed>
    </configuration>
</plugin>
```
The configuration is necessary for the proper working of a plugin. `buildFinalNameOfJar` is the final name of jar which
will be included as a part of installer. `directory` is the installer and this is the place where all files will be 
stored. `jdkOfNeed` is the path to the JDK/JRE which will be bundled into installer `directory` along with the 
`buildFinalNameOfJar`. This version of JDK/JRE will be used to run the program. In the installer directory will be a 
file called `play.sh` or `play.bat` depends on which platform you are building the product and this file has to be used to
run the product.

If you want to build installer for Windows you have to put JRE under the `jdkOfNeed` and run plugin
 - `mvn prod:windows`

For Linux you have to put JDK under the `jdkOfNeed` and run plugin
 - `mvn prod:linux`

Those commands will create installer (`directory`) only when there is already built jar file from source. If the jar
file hasn't been built you can add configuration to your build life cycle and execute 

`mvn clean package`
```xml
<executions>
    <execution>
        <phase>package</phase>
        <goals>
            <goal>linux</goal>
        </goals>
    </execution>
</executions>
```
