# Maintaining

Run the application:

```shell
./mvnw -pl :web package org.codehaus.cargo:cargo-maven3-plugin:run -Dcargo.maven.containerId=tomcat11x -Dcargo.servlet.port=8889
```
* open the homepage in your browser: http://localhost:8889/training/
* Hit `Ctrl-C` to stop the application.

Compile, lint and unit- and acceptance-test:

```shell
./mvnw -P SAST clean verify
```

Update dependencies:

```shell
./mvnw -U -Dmaven.version.ignore='(?i).*-(alpha|beta|m|rc)([-.]?\d+)?' versions:display-dependency-updates
```

Update plugins:

```shell
./mvnw -U -Dmaven.version.ignore='(?i).*-(alpha|beta|m|rc)([-.]?\d+)?' versions:display-plugin-updates 
```
