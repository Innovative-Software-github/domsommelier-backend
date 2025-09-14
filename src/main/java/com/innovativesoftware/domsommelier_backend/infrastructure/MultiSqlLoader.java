package com.innovativesoftware.domsommelier_backend.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Component
public class MultiSqlLoader {

    private final DataSource dataSource;

    @Autowired
    public MultiSqlLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadSqlScripts() throws Exception {
        List<String> files = List.of(
                "data/init.sql",
                "data/accessories.sql",
                "data/wine.sql",
                "data/snack.sql",
                "data/spirit.sql",
                "data/lowalcohol.sql",
                "data/shampaigne.sql",
                "data/filters.sql"
        );
        try (Connection connection = dataSource.getConnection()) {
            for (String file : files) {
                InputStream is = getClass().getClassLoader().getResourceAsStream(file);
                if (is == null) {
                    throw new RuntimeException("File not found: " + file);
                }
                String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Arrays.stream(sql.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(query -> {
                            try (Statement stmt = connection.createStatement()) {
                                stmt.execute(query);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }
}
