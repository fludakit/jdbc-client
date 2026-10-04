# FluDa JDBC Client

[![Build](https://github.com/fludakit/jdbc-client/actions/workflows/build.yml/badge.svg)](https://github.com/fludakit/jdbc-client/actions/workflows/build.yml)

A lightweight, type-safe, fluent query engine built directly over JDBC for Jakarta EE / CDI applications.

## Modules

- `fluda-jdbc-client-core`: the core `JdbcClient` API and supporting types.
- `fluda-jdbc-client-config`: optional MicroProfile Config integration.
- `fluda-jdbc-client-cdi`: CDI producers for `JdbcClient` and converters.

## Usage

Create a `JdbcClient` from any `DataSource` and run fluent queries:

```java
import io.github.fludakit.jdbc.JdbcClient;
import javax.sql.DataSource;

DataSource dataSource = ...; // obtain a DataSource from your application or runtime
JdbcClient client = new JdbcClient(dataSource);

List<Engineer> engineers = client.sql("SELECT id, name FROM engineers WHERE department = :dept")
        .param("dept", "Engineering")
        .query(Engineer.class)
        .list();
```

For CDI integration, updates and generated keys, result mapping, converters, and configuration, see the [JDBC Client documentation](https://fludakit.github.io/documentation/jdbc-client/getting-started/).

## Building

```bash
./mvnw clean install
```

## Documentation

See the [reference documentation site](https://fludakit.github.io/documentation/jdbc-client/getting-started/) for getting started, querying, updates, and full API reference.

## Contributing

Contributions are welcome — issues, pull requests, and feature suggestions are all encouraged. See [CONTRIBUTING.md](CONTRIBUTING.md) for how to build the project and submit changes.
