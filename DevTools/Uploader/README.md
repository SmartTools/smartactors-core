# Uploader

SmartActors tool for uploading features and their artifacts to remote repository.

## Usage
```shell
java -jar uploader.jar -fp /path/to/feature -u username -p password
```

* `-fp`, `-featurePath` - path to feature artifacts
* `-u`, `-username` - username in remote repository
* `-p`, `-password` - password for remote repository

### Additional parameters
* `-c`, `-command` - name of the command to execute. As of 0.1.0, there's only one command - `upload`, which is set as default
* `-rId`, `-repositoryId` - ID of remote repository, see *Overriding remote repository info* for more details
* `-rUrl`,`-repositoryUrl` - URL of remote repository, see *Overriding remote repository info* for more details

### Overriding remote repository info
In most cases, features already have info about remote repository in their `config.json` (`"repository"` object).
In some cases, if there's a need to override this behavior, user can provide their own URL and ID for the remote repository.
Then uploader will ignore info about remote repository in feature and will use URL and ID provided by the user.
