# Feature Generator

SmartActors tool for creating feature templates.

## Usage
```shell
java -jar feature_gen.jar -pPath path/to/project -n feature_name -gId com.example
```

* `-pPath`, `-projectPath` - path to folder with SmartActors project
* `-n`, `-name` - name of the feature
* `-gId`, `-groupId` - group ID of the project
* `-v`, `-version` - version of the feature

## Feature structure
After executing this tool, the following folder structure will be generated:

```
feature_name
├── bin.xml
├── config.json
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── example
    │               └── feature_name
    └── test
        └── java
            └── com
                └── example
                    └── feature_name
```

Developer will have to create actors, plugins manually. It's recommended something similar to [live templates in IntelliJ IDEA](https://www.jetbrains.com/help/idea/using-live-templates.html) to speed up the process.
