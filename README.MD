Project provides support for parsing of the Outlook MSG files. Initially only two operations are supported:
1. Extracting of bare attachments (binary files)
2. Extracting of embedded MSG files

To start working please clone the project:
```
git clone https://github.com/ifedoroff/msg-parser
```

Checkout sub module responsible for parsing Compound Binary Format files:
```
git submodule init
git submodule update
```

Now you can work on a project and build it. To build a <i>jar</i> file use:
```
    gradlew jar
``` 
The below command will download Gradle distribution and run the build.

Usage examples are stored in <i>src/test/java/samples</i>.