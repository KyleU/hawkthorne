# Hawkthorne

[https://hawkthorne.net](https://hawkthorne.net)

Hawkthorne is a web game based on the Community episode "[Digital Estate Planning](https://github.com/KyleU/hawkthorne/wiki/EpisodeS03E20)".

The project is written in pure Scala, using assets and logic from [Digital Estate Planning: The Game](https://github.com/hawkthorne/hawkthorne-journey), an open source project.

## Running the app

The legacy Play 2.6 build currently requires Java 11. On macOS, install the
prerequisites with `brew install openjdk@11 sbt node`, then build with:

```shell
$ ./bin/build.sh compile
```

First, change the database section of application.conf to use your existing database credentials.

You'll either need Node.js available as "node" on the path, or change project/Server.scala's EngineType to Rhino.

Now, finally,
```shell
$ ./bin/build.sh
> run
$ open http://127.0.0.1:9000
```

As the application starts, it will create database tables and seed data.

The first account to sign up is created as an Admin, all subsequent users will have a normal user role.


## Projects

* `server` Main web application
* `core` Minimal shared classes, compiled to JVM, Scala.js, and native
* `shared` Core Scala logic and rules definitions, for JVM projects, compiled to JVM, Scala.js, and native
* `client` The main game logic, a Scala.js app
* `doc` Paradox documentation, published via `ghpages`
* `adminClient` Scala.js app for the admin site
* `pipeline` Creates all of the Scala files in `models.data`
* `wikiExport` Generates the Github wiki from shared definitions
