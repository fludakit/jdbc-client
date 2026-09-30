# FluDa JDBC Client

A lightweight, type-safe, fluent query engine built directly over JDBC for Jakarta EE / CDI applications.

## Modules

- `fluda-jdbc-client-core`: the core `JdbcClient` API and supporting types.
- `fluda-jdbc-client-config`: optional MicroProfile Config integration.
- `fluda-jdbc-client-cdi`: CDI producers for `JdbcClient` and converters.

## Usage

### Creating a `JdbcClient`

```java
import io.github.fludakit.jdbc.JdbcClient;

import javax.sql.DataSource;

DataSource dataSource = ...; // obtain a DataSource from your application or runtime

JdbcClient client = new JdbcClient(dataSource);
```

### Performing a query

```java
List<Engineer> engineers = client.sql("SELECT id, name FROM engineers WHERE id = :id")
        .param("id", 1L)
        .query(Engineer.class)
        .list();
```

### Updating existing data

```java
int rows = client.sql("UPDATE engineers SET name = :name WHERE id = :id")
        .param("name", "Ada Lovelace")
        .param("id", 1L)
        .update();
```

## Building

```bash
./mvnw clean install
```

## Documentation

See the [reference documentation site](https://fludakit.github.io/) for installation, quickstart, and full API reference.

## Contributing

Contributions are welcome — issues, pull requests, and feature suggestions are all encouraged.
