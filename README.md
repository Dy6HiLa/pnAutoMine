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
