# Setup your vscode to start development

1. Install Maven

2. Update Maven Mirrors

3. Install VSCode Java Extensions

## Building 

run the command `mvn install` under the folder `fugitivec/fugitivec`. It will read the `pom.xml` and build ths source. 

## Debugging 

If you want to debug your java application in vscode, you can modify the launch settings in `.vscode/launch.json`. 

Before you start to debug your application, you should set your breakpoints in the java source file(like CreateChannel.java) and then run the following command at first. 

``` bash
mvnDebug exec:java -Dexec.mainClass="com.vancir.network.CreateChannel"
```

`mainClass` is the class file you want to debug. 

Then switch to the `Debug` section in VSCode and click the green triangle button, which will attach the maven debug program. 

After you did the above operation, you can find that the program have paused in the breakpoint you set before. 

## Unit Test

If you want to run the unit test, you can run the command `mvn test` to do that.