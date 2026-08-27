# Память проекта

## Состояние проекта

- Название: pnAutoMine
- Текущая версия: 1.0.2
- Поддерживаемые версии: Paper; компиляция против Paper API 1.21.11, исходная ошибка получена на Paper 1.21.4
- Версия Java: байткод Java 17; проверочная сборка выполнена JDK 21.0.11
- Основные зависимости: WorldEdit и pnLibrary; опционально FancyHolograms, DecentHolograms, PlaceholderAPI и TAB
- Последний стабильный commit: `a6f0a5608a756eab9856e4db664bdf433929ee18`
- Последний опубликованный release: `v1.0.2` — https://github.com/Dy6HiLa/pnAutoMine/releases/tag/v1.0.2

## Постоянные архитектурные правила

- Все встраиваемые библиотеки, которым нужна изоляция, должны попадать в `ru.privatenull.pnautomine.libs`.
- Метрики и обновления запускаются через актуальный `pnLibrary` API: `PluginBanner.Identity` и `PluginRuntime`. Не добавлять отдельную зависимость `org.bstats:bstats-bukkit`, потому что pnLibrary уже содержит vendored bStats.
- В production-JAR не должно быть классов `org/bstats/**` или `ru/privatenull/pnlibrary/**`; pnLibrary должна быть relocated в `ru/privatenull/pnautomine/libs/pnlibrary`.
- Composite build подключает pnLibrary из `../../Plugins/pnLibrary`; каталога `../pnLibrary` в текущей структуре workspace нет.
- Sidebar шахты настраивает сам администратор в TAB. pnAutoMine не управляет scoreboard через TAB API, а предоставляет значения через PlaceholderAPI.
- Статистика добычи хранится в памяти отдельно для шахты и UUID игрока только в пределах текущего цикла шахты. Она очищается при reset, reload и перезапуске и не является долговременной статистикой игрока.
- Группы материалов и `value-per-block` задаются в `mining-statistics.groups`; один материал не следует включать в несколько групп с разной ценой.

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

### Статистика добычи для TAB

- **Статус:** АКТУАЛЬНО
- **Дата:** 2026-08-28
- **Компонент:** BlockBreakListener / Mine / PlaceholderAPI
- **Контекст:** scoreboard должен показывать количество добытых игроком ресурсов и расчётную зарплату без отдельного плагина статистики.
- **Принятое решение:** на `MONITOR` учитываются только неотменённые `BlockBreakEvent` внутри региона; сохраняются общий счёт шахты, счёт по `Material` и счёт по UUID игрока. Placeholder может обращаться к группе из конфига либо к точному Bukkit Material.
- **Почему выбран этот подход:** счётчик отражает фактически разрешённые ломания в шахте, не зависит от содержимого инвентаря и не требует базы данных для краткоживущего scoreboard текущего reset-цикла.
- **Не использовать:** подсчёт отменённых событий, сканирование инвентаря как замену статистике или долговременное накопление этих счётчиков без отдельного запроса на формат хранения и миграцию.
- **Совместимость:** существующий `%pnautomine_*_blocks_mined%` остаётся общим счётчиком шахты. Новые player-placeholders требуют контекст игрока; вне текущей шахты current-placeholders возвращают пустую строку.
- **Как проверить:** сломать блоки двух материалов двумя игроками, проверить player/global placeholders, выполнить reset и убедиться, что все значения стали нулевыми.

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
- **Commit или release:** исправление `fab9973cb6f6eee68c76c9a4b44aebebdabac2c4`; опубликовано в `v1.0.1`.

## Форматы и совместимость

- Существующий формат `%pnautomine_<mine_id>_<value>%` сохранён. Поддерживаются `id`, `name`, `type`, `type_display`, `blocks_total`, `blocks_remaining`, `blocks_mined`, `percentage`, `percentage_mined`, `reset_time`, `reset_seconds`, `reset_interval`, `world`.
- Добавлены player-aware placeholders `%pnautomine_current_<value>%`; шахта определяется по текущей позиции игрока. `%pnautomine_current_exists%` возвращает `true` или `false`, остальные current-placeholders вне шахты возвращают пустую строку.
- Добавлен `%pnautomine_mine_count%`.
- Добавлены `%pnautomine_next_mine_<value>%` для ближайшей по времени сброса шахты и `<mine>_next_type` / `<mine>_next_type_display` для следующего типа прогрессии.
- Добавлены `<mine>_player_mined_total`, `<mine>_player_mined_<group-or-material>`, `<mine>_player_earnings` (алиас `player_salary`), а также общие `<mine>_mined_<group-or-material>` и `<mine>_blocks_mined_<group-or-material>`. Префикс `<mine>` может быть `current` или ID шахты.
- `player_earnings` суммирует добытые блоки по `value-per-block`; число знаков задаёт `mining-statistics.salary-decimals` от 0 до 4.
- Проценты форматируются с точкой независимо от системной Locale. При пересекающихся ID выбирается самое длинное совпадение.
- Форматы `config.yml`, файлов шахт и команд не изменены.
- Публичный Java-метод `getUpdateChecker()` теперь возвращает `PluginUpdateService`, потому что старого типа `UpdateChecker` в актуальной pnLibrary нет.

## Последние выполненные изменения

### 2026-08-28 — Следующая шахта и статистика добычи

- **Изменено:** добавлены placeholders ближайшего reset, следующего типа, персональной и общей добычи по материалам, расчётной зарплаты; добавлен конфиг групп и новый TAB-пример в стиле предоставленного scoreboard.
- **Причина:** вывести данные шахты и добычи в TAB без отдельного плагина статистики.
- **Результат:** версия 1.0.2 собирается; счётчики очищаются вместе с шахтой и поддерживают настраиваемые группы либо точные Material.
- **Проверено:** `gradlew.bat clean test build` — успешно; 2 теста, 0 failures/errors; JAR версии 1.0.2 содержит новый конфиг/пример, 0 `org/bstats/**` и 0 нерелоцированных `ru/privatenull/pnlibrary/**`; SHA-256 `fab2590bd72c94cf06fbee8551d37eb981c7f0c12c348ece5d3264c931d89184`.
- **Commit/release:** `a6f0a5608a756eab9856e4db664bdf433929ee18`; `v1.0.2`.

### 2026-08-24 — Публикация pnAutoMine 1.0.1

- **Изменено:** версия повышена до 1.0.1, добавлены release notes, создан GitHub Release и загружен production-JAR.
- **Причина:** существующий `v1.0.0` уже содержал старый JAR; подмена asset без повышения версии не позволила бы автообновлению обнаружить исправление.
- **Результат:** `pnAutoMine-1.0.1.jar` доступен в GitHub Release `v1.0.1`.
- **Проверено:** release не draft и не prerelease; asset имеет размер 24 213 743 байта и SHA-256 `79dfbc8b68edbc61f5d171086752fa4753a72d43e490e8f97cf375c8ad924dd5`, совпадающий с локальной сборкой.
- **Commit/release:** release commit `eb4ce5d5a174ed5f155d94298ddd1b2119f367f1`; `v1.0.1`.

### 2026-08-24 — Исправление bStats и scoreboard через TAB

- **Изменено:** метрики и обновления мигрированы на `PluginRuntime`; удалён прямой bStats; исправлен composite-build путь; добавлены current/static placeholders, TAB softdepend, README и сохраняемый пример scoreboard.
- **Причина:** плагин не включался из-за bStats relocation-check; владелец сервера запросил настраиваемый sidebar через TAB.
- **Результат:** production-JAR успешно собирается, не содержит нерелоцированных bStats/pnLibrary и включает пример TAB.
- **Проверено:** `gradlew.bat clean build` — `BUILD SUCCESSFUL` (Gradle 8.8, JDK 21.0.11, Windows 11); `git diff --check` без ошибок; структура JAR проверена через `System.IO.Compression`.
- **Commit/release:** `fab9973cb6f6eee68c76c9a4b44aebebdabac2c4`; опубликовано в `v1.0.1`.

## Незавершённая работа

- Нужна фактическая проверка включения JAR на Paper с установленным WorldEdit, PlaceholderAPI и TAB; в текущей задаче сервер не запускался.
