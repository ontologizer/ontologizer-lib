[![Build Status](https://travis-ci.org/ontologizer/ontologizerlib.svg?branch=master)](https://travis-ci.org/ontologizer/ontologizerlib)

# OntologizerLib

Core components from [Ontologizer](http://ontologizer.de) for

- Ontology representation
- I/O of OBO files

## Use Library

Use the following snippet for your `pom.xml` for the current release version:

```
<!-- https://mvnrepository.com/artifact/de.ontologizer/ontologizerlib-core -->
<dependency>
    <groupId>de.ontologizer</groupId>
    <artifactId>ontologizerlib-core</artifactId>
    <version>0.1</version>
</dependency>
<!-- https://mvnrepository.com/artifact/de.ontologizer/ontologizerlib-io -->
<dependency>
    <groupId>de.ontologizer</groupId>
    <artifactId>ontologizerlib-io</artifactId>
    <version>0.1</version>
</dependency>
```

Snapshots are available via [JFrog OSS](https://oss.jfrog.org). Use the following
snippet in your `pom.xml` to configure Maven to resolve them:

```
<repositories>
    ...
    <repository>
        <id>jfrog-oss-snapshots</id>
        <url>https://oss.jfrog.org/artifactory/oss-snapshot-local</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    ...
</repositories>
```

Current snapshot version is `0.2-SNAPSHOT`.

## Development

See the [OntologizerLib Manual](http://ontologizerlib.rtfd.io) on how to get started with using or developing OntoligizerLib.
