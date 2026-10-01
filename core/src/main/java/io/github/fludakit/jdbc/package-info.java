/**
 * Fluent JDBC client for simplified database access.
 *
 * <p>This package provides a modern, type-safe API for executing SQL queries and updates
 * against a {@link javax.sql.DataSource}. The main entry point is {@link io.github.fludakit.jdbc.JdbcClient},
 * which supports parameterized SQL, automatic result mapping, and fluent method chaining.</p>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * DataSource dataSource = // ... your DataSource
 * JdbcClient client = JdbcClient.create(dataSource);
 *
 * // Query with automatic mapping
 * List<User> users = client
 *     .sql("SELECT id, name, email FROM users WHERE active = :active")
 *     .params(Map.of("active", true))
 *     .query(User.class)
 *     .list();
 *
 * // Update with generated keys
 * KeyHolder keys = client
 *     .sql("INSERT INTO users (name, email) VALUES (:name, :email)")
 *     .params(Map.of("name", "John", "email", "john@example.com"))
 *     .updateAndReturnKeys();
 * }</pre>
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link io.github.fludakit.jdbc.JdbcClient} — Main API for executing SQL statements</li>
 *   <li>{@link io.github.fludakit.jdbc.RowMapper} — Custom result set mapping</li>
 *   <li>{@link io.github.fludakit.jdbc.JdbcConfig} — Configuration for timeouts, fetch sizes, and placeholder syntax</li>
 *   <li>{@link io.github.fludakit.jdbc.converter.Converter} — Type conversion for query parameters and results</li>
 *   <li>{@link io.github.fludakit.jdbc.support.KeyHolder} — Access to auto-generated keys</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Named and positional parameter binding</li>
 *   <li>Automatic mapping to records, classes, and Maps</li>
 *   <li>Custom {@link io.github.fludakit.jdbc.RowMapper} for complex mappings</li>
 *   <li>Type conversion via {@link io.github.fludakit.jdbc.converter.ConverterRegistry}</li>
 *   <li>Streaming results for large datasets</li>
 *   <li>Batch operations</li>
 *   <li>Generated key retrieval</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>All JDBC exceptions are wrapped in {@link io.github.fludakit.jdbc.JdbcClientException},
 * which provides error codes to distinguish between JDBC errors, mapping failures, and
 * empty results. Use {@link io.github.fludakit.jdbc.JdbcClientException#getCode()} to
 * identify the failure type.</p>
 *
 * @see io.github.fludakit.jdbc.JdbcClient
 * @see io.github.fludakit.jdbc.RowMapper
 * @see io.github.fludakit.jdbc.JdbcConfig
 */
package io.github.fludakit.jdbc;
