package io.github.fludakit.jdbc;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The named-parameter rewriting matrix, verified by capturing the SQL actually prepared and the
 * parameter binding order. The {@code $}/{@code :}/{@code @} styles cannot run against H2, and
 * before this test no other test executed them at all.
 */
class NamedParameterRewriteTest {

    private final List<String> preparedSql = new ArrayList<>();
    private final List<Object> bound = new ArrayList<>();

    private JdbcClient client(String placeholder) {
        DataSource dataSource = (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(),
                new Class<?>[] { DataSource.class },
                (proxy, method, args) -> "getConnection".equals(method.getName()) ? connectionProxy() : null);
        return JdbcClient.builder(dataSource).placeholder(placeholder).build();
    }

    private Connection connectionProxy() {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                (proxy, method, args) -> {
                    if ("prepareStatement".equals(method.getName())) {
                        preparedSql.add((String) args[0]);
                        return statementProxy();
                    }
                    return null;
                });
    }

    private PreparedStatement statementProxy() {
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class<?>[] { PreparedStatement.class },
                (proxy, method, args) -> {
                    return switch (method.getName()) {
                        case "setObject" -> {
                            bound.add(args[1]);
                            yield null;
                        }
                        case "executeUpdate" -> 1;
                        default -> null;
                    };
                });
    }

    @Test
    void questionMarkPassesThroughPositionally() {
        client("?").sql("INSERT INTO t (a, b) VALUES (?, ?)").params(1, "x").update();

        assertEquals(List.of("INSERT INTO t (a, b) VALUES (?, ?)"), preparedSql);
        assertEquals(List.of(1, "x"), bound);
    }

    @Test
    void dollarNumberedRewritesInOrder() {
        client("$").sql("UPDATE t SET a = :a, b = :b WHERE id = :id")
                .param("a", 1).param("b", "x").param("id", 7L).update();

        assertEquals(List.of("UPDATE t SET a = $1, b = $2 WHERE id = $3"), preparedSql);
        assertEquals(List.of(1, "x", 7L), bound);
    }

    @Test
    void colonNumberedRewritesInOrder() {
        client(":").sql("UPDATE t SET a = :a WHERE id = :id").param("a", 1).param("id", 7L).update();

        assertEquals(List.of("UPDATE t SET a = :1 WHERE id = :2"), preparedSql);
        assertEquals(List.of(1, 7L), bound);
    }

    @Test
    void atNamedPreservesNames() {
        client("@").sql("UPDATE t SET a = :a WHERE id = :id").param("a", 1).param("id", 7L).update();

        assertEquals(List.of("UPDATE t SET a = @a WHERE id = @id"), preparedSql);
        assertEquals(List.of(1, 7L), bound);
    }

    @Test
    void reusedNameBindsOncePerOccurrence() {
        client("$").sql("UPDATE t SET a = :x WHERE (b = :x OR c = :x)").param("x", 9).update();

        assertEquals(List.of("UPDATE t SET a = $1 WHERE (b = $2 OR c = $3)"), preparedSql);
        assertEquals(List.of(9, 9, 9), bound);
    }

    @Test
    void doubleColonCastIsNotAParameter() {
        client("?").sql("UPDATE t SET flag = 1 WHERE v::INT = :v").param("v", 1).update();

        assertEquals(List.of("UPDATE t SET flag = 1 WHERE v::INT = ?"), preparedSql);
        assertEquals(List.of(1), bound);
    }
}
