# pnAutoMine

`pnAutoMine` - плагин для автоматических шахт на Paper-сервере.

Он позволяет создавать шахты из выделения WorldEdit, автоматически сбрасывать их по таймеру и показывать голограмму с текущим состоянием шахты.

## Возможности

- создание шахты по выделению WorldEdit
- автоматический сброс шахты по интервалу
- набор типов шахт с отдельным составом блоков
- автоматическая смена типа шахты по порядку
- голограмма над шахтой с названием, типом, блоками и временем до сброса
- команда просмотра списка и информации по шахтам
- поддержка PlaceholderAPI
- настраиваемый sidebar scoreboard через TAB и PlaceholderAPI
- поддержка FancyHolograms и DecentHolograms

## Команды

- `/pnautomine help` - показать помощь
- `/pnautomine create <id> <type>` - создать шахту из выделения WorldEdit
- `/pnautomine delete <id>` - удалить шахту
- `/pnautomine reset <id>` - сбросить шахту
- `/pnautomine resetall` - сбросить все шахты
- `/pnautomine list` - список шахт
- `/pnautomine info <id>` - информация о шахте
- `/pnautomine setspawn <id>` - установить точку телепорта
- `/pnautomine sethologram <id>` - установить позицию голограммы
- `/pnautomine reload` - перезагрузить плагин
- `/pnautomine types` - список типов шахт

Алиасы:

- `/mine`
- `/mines`
- `/am`

## Права

- `pnautomine.admin` - доступ к административным командам
- `pnautomine.use` - базовый доступ

## Требования

- Paper-сервер
- WorldEdit
- Java 17

Опционально:

- FancyHolograms
- DecentHolograms
- PlaceholderAPI
- TAB (для sidebar scoreboard)

## PlaceholderAPI и scoreboard TAB

pnAutoMine не заменяет scoreboard TAB: он предоставляет значения, а название,
строки, цвета и условия показа владелец сервера настраивает в
`plugins/TAB/config.yml`. После первого запуска пример создаётся в
`plugins/pnAutoMine/tab-scoreboard-example.yml`.

Placeholder текущей шахты определяется по позиции игрока:

- `%pnautomine_current_exists%` — находится ли игрок в шахте (`true`/`false`)
- `%pnautomine_current_id%` — ID текущей шахты
- `%pnautomine_current_name%` — отображаемое имя
- `%pnautomine_current_type%` — ID типа
- `%pnautomine_current_type_display%` — отображаемое имя типа
- `%pnautomine_current_blocks_total%` — всего блоков
- `%pnautomine_current_blocks_remaining%` — осталось блоков
- `%pnautomine_current_blocks_mined%` — добыто блоков
- `%pnautomine_current_percentage%` — процент оставшихся блоков
- `%pnautomine_current_percentage_mined%` — процент добытых блоков
- `%pnautomine_current_reset_time%` — время до сброса
- `%pnautomine_current_reset_seconds%` — время до сброса в секундах
- `%pnautomine_current_reset_interval%` — интервал сброса в секундах
- `%pnautomine_current_world%` — мир шахты
- `%pnautomine_mine_count%` — число загруженных шахт

Для конкретной шахты вместо `current` указывается её ID:
`%pnautomine_<mine_id>_<value>%`, например
`%pnautomine_spawn_blocks_remaining%`.

Требуются установленные PlaceholderAPI и TAB. Проверить результат можно
командой TAB: `/tab parse <игрок> %pnautomine_current_name%`.

## Настройка

Основные параметры находятся в `config.yml`:

- `defaults` - интервалы и поведение сброса
- `mine-types` - типы шахт и состав блоков
- `mine-type-progression` - порядок автоматической смены типов
- `hologram` - текст и позиция голограммы

## Установка

1. Положите `pnAutoMine.jar` в папку `plugins`.
2. Установите WorldEdit.
3. Запустите сервер один раз.
4. Настройте `config.yml`.
5. Создайте шахту через WorldEdit и команду `/pnautomine create`.
