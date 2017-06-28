# This directory should contain server core modules.

Modules may be placed in this directory as jar files:

```
core/m1.jar
core/m2.jar
```

or grouped to feature directories:

```
core/core-feature-1/m1.jar
core/core-feature-1/m2.jar
core/core-feature-2/m1.jar
```

All non-jar files are ignored (_**including feature configuration files**_).

All files in child directories of feature directories are ignored.
