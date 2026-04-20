## Development

### Prerequisites

* JDK 21 or later
* Maven 3.9+

### Building

```bash
mvn clean install
```

### Running the test/demo server

The project includes a Spring Boot based test server that hosts example views
for all components. It lives in the *test* sources, so the normal
`spring-boot:run` goal does not work.

**From your IDE:** run the `main` method of
`src/test/java/org/vaadin/firitin/TestServer.java`.

**From the command line:**

```bash
mvn org.springframework.boot:spring-boot-maven-plugin:test-run
```

The server starts at **<http://localhost:9998>**. Vaadin runs in development
mode, so the first request triggers a frontend build (takes a moment).

### Project structure

```
src/main/java      Production code (the add-on itself)
src/test/java      Test/demo views and TestServer
src/test/resources Test resources (e.g. translation files)
src/site/          Maven site sources (this documentation)
```

### Generating the documentation site

```bash
mvn site
```

The generated site is written to `target/site/`. Open `target/site/index.html`
in a browser to preview.

### Running tests

```bash
mvn test
```

### Releasing

Releases are automated via the Maven release plugin. The parent POM
(`viritin-addon-project-parent`) configures signing and deployment to
Maven Central.

### Contributing

Check out the project alongside your own Vaadin application and develop helpers
directly in Viritin. Pull requests are welcome!

See the [GitHub repository](https://github.com/viritin/flow-viritin) for
open issues and contribution guidelines.
