package com.innovativesoftware.domsommelier_backend.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Slf4j
@Component
@ConditionalOnProperty(prefix = "db-init", name = "mode", havingValue = "true")
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
                "data/cities.sql",
                "data/stores.sql",
                "data/accessories.sql",
                "data/wine.sql",
                "data/snack.sql",
                "data/spirit.sql",
                "data/lowalcohol.sql",
                "data/shampaigne.sql",
                "data/stock.sql",
                "data/users.sql",
                "data/user_favorites.sql",
                "data/filters.sql"
        );
        try (Connection connection = dataSource.getConnection()) {
            log.info("db-init: старт выполнения {} sql-файлов", files.size());

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
