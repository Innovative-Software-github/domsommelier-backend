package com.innovativesoftware.domsommelier_backend.filter_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Правки конфигурации фильтров для уже существующих БД. data/filters.sql выполняется
 * только при первичной инициализации (db-init.mode=true), на проде он выключен, а
 * ручной скрипт после деплоя легко забыть. Каждый шаг идемпотентен: на исправленной
 * базе (и на новой, засеянной уже исправленным filters.sql) ничего не меняет.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FilterConfigMigration {

    /** Кириллическая «С» в середине — так поле попало в filters.sql. */
    private static final String BROKEN_SUGAR_FIELD = "sugar\u0421ontent";

    private final JdbcTemplate jdbc;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrate() {
        fixSugarContentField();
        convertSpiritStrengthToRange();
    }

    /**
     * Фронт слал ключ с кириллической «С», бэкенд ждал латинский sugarContent, а
     * автообновление вариантов не находило поле — фильтр по сахару был пустым.
     */
    private void fixSugarContentField() {
        int updated = jdbc.update("update filter set field = 'sugarContent' where field = ?", BROKEN_SUGAR_FIELD);
        if (updated > 0) {
            log.info("Фильтры: поле sugarContent исправлено ({} шт.)", updated);
        }
    }

    /** Крепость крепкого была списком галочек «38.0», «40.0» — делаем диапазоном, как у слабоалкогольных. */
    private void convertSpiritStrengthToRange() {
        List<UUID> ids = jdbc.queryForList(
                "select id from filter where product_category = 'spirit' and field = 'strength' and type = 'multi_select'",
                UUID.class);

        for (UUID id : ids) {
            jdbc.update("delete from multi_select_filter_option where multi_select_filter_id = ?", id);
            jdbc.update("delete from multi_select_filter where id = ?", id);
            jdbc.update("update filter set type = 'range' where id = ?", id);
            jdbc.update("insert into range_filter (id, min, max, unit) values (?, 0, 100, '%')", id);
            jdbc.update("insert into range_filter_step (range_filter_id, min, max, label) values (?, 0, 39.9, 'до 40%')", id);
            jdbc.update("insert into range_filter_step (range_filter_id, min, max, label) values (?, 40, 45, '40% - 45%')", id);
            jdbc.update("insert into range_filter_step (range_filter_id, min, max, label) values (?, 45.1, 100, 'Свыше 45%')", id);
            log.info("Фильтры: крепость крепкого переведена в диапазон ({})", id);
        }
    }
}
