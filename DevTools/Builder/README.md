# Builder

SmartActors tool for assembling feature in a single `.jar` file.

## Usage
```shell
java -jar builder.jar -fp path/to/feature
```

For now, it requires presence of `pom.xml` in the feature, since SmartActors feature are built using Maven.

### Support for different config types
As of 0.2.0, builder tool can assemble features with the following config types:
- `.json`
- `.yml`

Feature should contain config of one type only. If there are config files of multiple types, then tool will not assemble feature.

## Configuration
### Maven
`M2_HOME` environment variable should be set, otherwise builder would not know where Maven instance is located.
