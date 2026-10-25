package io.github.fludakit.jdbc.support;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyHolderTest {

    @Test
    void emptyHolderReportsNullAndNoKeys() {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();

        assertNull(holder.getKey());
        assertEquals(Map.of(), holder.getKeys());
        assertTrue(holder.getKeyList().isEmpty());
    }

    @Test
    void singleRowSingleColumnKey() {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        holder.getKeyList().add(Map.of("id", 5L));

        assertEquals(5L, holder.getKey());
        assertEquals(Map.of("id", 5L), holder.getKeys());
    }

    @Test
    void singleRowMultipleColumns() {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", 5L);
        row.put("name", "Duke");
        holder.getKeyList().add(row);

        assertEquals(5L, holder.getKey(), "getKey returns the first column's value");
        assertEquals(row, holder.getKeys());
        assertEquals(1, holder.getKeyList().size());
    }

    @Test
    void emptyFirstRowReportsNull() {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        holder.getKeyList().add(Map.of());

        assertNull(holder.getKey());
    }

    @Test
    void multipleRowsKeepAllButKeyReadsTheFirst() {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        holder.getKeyList().add(Map.of("id", 1L));
        holder.getKeyList().add(Map.of("id", 2L));

        assertEquals(2, holder.getKeyList().size());
        assertEquals(1L, holder.getKey());
        assertEquals(2L, holder.getKeyList().get(1).get("id"));
    }
}
