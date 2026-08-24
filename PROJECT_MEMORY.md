# Память проекта

## Состояние проекта

- Название: pnAutoMine
- Текущая версия: 1.0.0
- Поддерживаемые версии: Paper; компиляция против Paper API 1.21.11, исходная ошибка получена на Paper 1.21.4
- Версия Java: байткод Java 17; проверочная сборка выполнена JDK 21.0.11
- Основные зависимости: WorldEdit и pnLibrary; опционально FancyHolograms, DecentHolograms, PlaceholderAPI и TAB
- Последний стабильный commit: не подтверждён; исходный HEAD задачи `f13e3996cd7934969f1dd346903eced6972e74d2` содержит ошибочную упаковку bStats
- Последний опубликованный release: не подтверждён

## Постоянные архитектурные правила

- Все встраиваемые библиотеки, которым нужна изоляция, должны попадать в `ru.privatenull.pnautomine.libs`.
- Метрики и обновления запускаются через актуальный `pnLibrary` API: `PluginBanner.Identity` и `PluginRuntime`. Не добавлять отдельную зависимость `org.bstats:bstats-bukkit`, потому что pnLibrary уже содержит vendored bStats.
- В production-JAR не должно быть классов `org/bstats/**` или `ru/privatenull/pnlibrary/**`; pnLibrary должна быть relocated в `ru/privatenull/pnautomine/libs/pnlibrary`.
- Composite build подключает pnLibrary из `../../Plugins/pnLibrary`; каталога `../pnLibrary` в текущей структуре workspace нет.
- Sidebar шахты настраивает сам администратор в TAB. pnAutoMine не управляет scoreboard через TAB API, а предоставляет значения через PlaceholderAPI.

## Принятые решения

### Метрики через PluginRuntime

- **Статус:** АКТУАЛЬНО
- **Дата:** 2026-08-24
- **Компонент:** pnLibrary / bStats / Gradle Shadow
- **Контекст:** pnAutoMine 1.0.0 отключался в `MetricsBase.checkRelocation` при создании `org.bstats.bukkit.Metrics`.
- **Принятое решение:** удалить прямую зависимость и прямой запуск bStats; использовать `.bStats(32828)` в `PluginBanner.Identity` и запускать инфраструктуру через `PluginRuntime`.
- **Почему выбран этот подход:** актуальная pnLibrary уже содержит bStats в приватном пакете и единообразно управляет метриками, обновлениями и lifecycle-баннером.
- **Не использовать:** прямой `new org.bstats.bukkit.Metrics(...)`, отключение relocation-check системным свойством или публикацию JAR с `org/bstats/**`.
- **Совместимость:** ID проекта bStats остаётся `32828`, GitHub-репозиторий обновлений — `Dy6HiLa/pnAutoMine`, интервал — 12 часов, permission — `pnautomine.admin`.
- **Как проверить:** `gradlew.bat clean build`; в основном JAR должно быть 0 классов `org/bstats/**`, а vendored Bukkit metrics должны находиться под relocated pnLibrary.

### Интеграция sidebar с TAB

- **Статус:** АКТУАЛЬНО
- **Дата:** 2026-08-24
- **Компонент:** PlaceholderAPI / TAB
- **Контекст:** строки и оформление scoreboard должны оставаться настраиваемыми владельцем сервера.
- **Принятое решение:** TAB читает placeholders pnAutoMine через PlaceholderAPI. Пример секции TAB сохраняется один раз в `plugins/pnAutoMine/tab-scoreboard-example.yml` и не перезаписывается при обновлениях.
- **Почему выбран этот подход:** TAB сам отвечает за sidebar, условия показа и обновление строк; pnAutoMine предоставляет только данные шахт.
- **Не использовать:** параллельный Bukkit-scoreboard или принудительный scoreboard через TAB API — это забирает контроль у конфигурации TAB и может конфликтовать с другими sidebar.
- **Совместимость:** без PlaceholderAPI placeholders не регистрируются; без TAB сама логика шахт продолжает работать.
- **Как проверить:** в игре выполнить `/tab parse <игрок> %pnautomine_current_name%` и зайти в регион шахты.

## Известные проблемы и исправления

### bStats Metrics class has not been relocated correctly

- **Статус:** ИСПРАВЛЕНО
- **Дата:** 2026-08-24
- **Затронутые версии:** pnAutoMine 1.0.0 до исправленной пересборки
- **Симптом:** `IllegalStateException` из `org.bstats.MetricsBase.checkRelocation`, после чего Paper отключает плагин.
- **Причина:** `bstats-bukkit:3.0.3` встраивался под исходным пакетом `org.bstats` и запускался напрямую.
- **Исправление:** отдельная зависимость удалена; метрики переведены на vendored реализацию pnLibrary в приватном пакете. В Shadow оставлено защитное relocation-правило для `org.bstats`, если такой транзитивный пакет появится в будущем.
- **Неудачные подходы:** первая проверка сборки также выявила неверный путь `../pnLibrary`; после исправления пути обнаружилось, что исходный код использовал удалённые `UpdateChecker`, `UpdateSettings` и старый `lifecycle.PluginBanner`, поэтому код мигрирован на актуальный API вместо возврата устаревших классов.
- **Правило на будущее:** после изменений зависимостей проверять не только успешную сборку, но и фактические package-path внутри production-JAR.
- **Проверка:** `gradlew.bat clean build` успешен; JAR содержит 0 `org/bstats/**`, 0 `ru/privatenull/pnlibrary/**` и 13 классов vendored Bukkit metrics под `ru/privatenull/pnautomine/libs/pnlibrary/metrics/vendor/bukkit/**`. Запуск на полноценном Paper-сервере с WorldEdit в этой задаче не выполнялся.
- **Commit или release:** не создан.

## Форматы и совместимость

- Существующий формат `%pnautomine_<mine_id>_<value>%` сохранён. Поддерживаются `id`, `name`, `type`, `type_display`, `blocks_total`, `blocks_remaining`, `blocks_mined`, `percentage`, `percentage_mined`, `reset_time`, `reset_seconds`, `reset_interval`, `world`.
- Добавлены player-aware placeholders `%pnautomine_current_<value>%`; шахта определяется по текущей позиции игрока. `%pnautomine_current_exists%` возвращает `true` или `false`, остальные current-placeholders вне шахты возвращают пустую строку.
- Добавлен `%pnautomine_mine_count%`.
- Проценты форматируются с точкой независимо от системной Locale. При пересекающихся ID выбирается самое длинное совпадение.
- Форматы `config.yml`, файлов шахт и команд не изменены.
- Публичный Java-метод `getUpdateChecker()` теперь возвращает `PluginUpdateService`, потому что старого типа `UpdateChecker` в актуальной pnLibrary нет.

## Последние выполненные изменения

### 2026-08-24 — Исправление bStats и scoreboard через TAB

- **Изменено:** метрики и обновления мигрированы на `PluginRuntime`; удалён прямой bStats; исправлен composite-build путь; добавлены current/static placeholders, TAB softdepend, README и сохраняемый пример scoreboard.
- **Причина:** плагин не включался из-за bStats relocation-check; владелец сервера запросил настраиваемый sidebar через TAB.
- **Результат:** production-JAR успешно собирается, не содержит нерелоцированных bStats/pnLibrary и включает пример TAB.
- **Проверено:** `gradlew.bat clean build` — `BUILD SUCCESSFUL` (Gradle 8.8, JDK 21.0.11, Windows 11); `git diff --check` без ошибок; структура JAR проверена через `System.IO.Compression`.
- **Commit/release:** не создан.

## Незавершённая работа

- Нужна фактическая проверка включения JAR на Paper с установленным WorldEdit, PlaceholderAPI и TAB; в текущей задаче сервер не запускался.
