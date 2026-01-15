# IWNO8: Непрерывная интеграция с помощью Github Actions

---

## Цель работы

В рамках данной работы я научился настраивать непрерывную интеграцию с помощью Github Actions.

---

## Задание

Создать веб-приложение, написать тесты для него и настроить непрерывную интеграцию с помощью Github Actions на базе контейнеров.

---

## Описание выполнения работы

### Подготовка

☑ Для выполнения работы у меня был установлен Docker на компьютере.

### Выполнение

☑ Я создал репозиторий `containers08` и склонировал его на свой компьютер.

☑ В директории `containers08` я создал директорию `./site`, в которой разместил веб-приложение на базе PHP.

#### Создание веб-приложения

Я создал в директории `./site` веб-приложение со следующей структурой:

```
site
├── modules/
│   ├── database.php
│   └── page.php
├── templates/
│   └── index.tpl
├── styles/
│   └── style.css
├── config.php
└── index.php
```

![img](/images/image_1.png)

- **Файл `modules/database.php`** содержит класс `Database` для работы с базой данных SQLite. Класс реализует методы:
  - `__construct($path)` — инициализирует подключение к базе данных по указанному пути.
  - `Execute($sql)` — выполняет SQL-запрос.
  - `Fetch($sql)` — выполняет SQL-запрос и возвращает результат в виде ассоциативного массива.
  - `Create($table, $data)` — создает запись в таблице и возвращает её идентификатор.
  - `Read($table, $id)` — возвращает запись по идентификатору.
  - `Update($table, $id, $data)` — обновляет запись по идентификатору.
  - `Delete($table, $id)` — удаляет запись по идентификатору.
  - `Count($table)` — возвращает количество записей в таблице.

- **Файл `modules/page.php`** содержит класс `Page` для работы с страницами. Класс реализует методы:
  - `__construct($template)` — инициализирует шаблон страницы.
  - `Render($data)` — отображает страницу, подставляя данные в шаблон.

- **Файл `templates/index.tpl`** содержит шаблон страницы.

- **Файл `styles/style.css`** содержит стили для страницы.

- **Файл `index.php`** отображает страницу, используя классы `Database` и `Page`.:

```php
<?php
require_once __DIR__ . '/modules/database.php';
require_once __DIR__ . '/modules/page.php';
require_once __DIR__ . '/config.php';

$db = new Database($config["db"]["path"]);
$page = new Page(__DIR__ . '/templates/index.tpl');

// Получаем ID страницы из GET-запроса, по умолчанию 1
$pageId = isset($_GET['page']) ? (int)$_GET['page'] : 1;

// Читаем данные из базы
$data = $db->Read("page", $pageId);

// Если данные не найдены, возвращаем заглушку
if (!$data) {
    $data = [
        'title' => 'Page Not Found',
        'content' => 'The requested page does not exist.'
    ];
}

// Рендерим страницу
echo $page->Render($data);
```

- **Файл `config.php`** содержит настройки подключения к базе данных.

#### Подготовка SQL файла для базы данных

Я создал директорию `./sql` в корневом каталоге и в ней файл `schema.sql` со следующим содержимым:

```sql
CREATE TABLE page (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    content TEXT
);

INSERT INTO page (title, content) VALUES ('Page 1', 'Content 1');
INSERT INTO page (title, content) VALUES ('Page 2', 'Content 2');
INSERT INTO page (title, content) VALUES ('Page 3', 'Content 3');
```

#### Создание тестов

Я создал директорию `./tests` и в ней файлы `testframework.php` и `tests.php`.

- **Файл `testframework.php`** содержит функции для логирования и класс `TestFramework` для запуска тестов.
- **Файл `tests.php`** включает тесты для всех методов классов `Database` и `Page`. Я дополнил файл тестами, проверяющими:
  - Подключение к базе данных.
  - Метод `Count` для подсчета записей.
  - Метод `Create` для создания записи.
  - Метод `Read` для чтения записи.
  - Метод `Update` для обновления записи.
  - Метод `Delete` для удаления записи.
  - Методы класса `Page` для рендеринга страницы.

#### Создание Dockerfile

Я создал файл `Dockerfile` в корневом каталоге со следующим содержимым:

```dockerfile
FROM php:7.4-fpm as base

RUN apt-get update && \
    apt-get install -y sqlite3 libsqlite3-dev && \
    docker-php-ext-install pdo_sqlite

VOLUME ["/var/www/db"]

COPY sql/schema.sql /var/www/db/schema.sql

RUN echo "prepare database" && \
    cat /var/www/db/schema.sql | sqlite3 /var/www/db/db.sqlite && \
    chmod 777 /var/www/db/db.sqlite && \
    rm -rf /var/www/db/schema.sql && \
    echo "database is ready"

COPY site /var/www/html
```

#### Настройка Github Actions

Я создал файл `.github/workflows/main.yml` со следующим содержимым:

```yaml
name: CI

on:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      - name: Build the Docker image
        run: docker build -t containers08 .
      - name: Create `container`
        run: docker create --name container --volume database:/var/www/db containers08
      - name: Copy tests to the container
        run: docker cp ./tests container:/var/www/html
      - name: Up the container
        run: docker start container
      - name: Run tests
        run: docker exec container php /var/www/html/tests/tests.php
      - name: Stop the container
        run: docker stop container
      - name: Remove the container
        run: docker rm container
```

#### Запуск и тестирование

Я отправил изменения в репозиторий и проверил выполнение тестов во вкладке **Actions** репозитория. Все тесты прошли успешно, но не сразу, историю неудачных попыток можно увидеть в истории `actions` в репозитории на github, вначале я столкнулся с ошибками из-за неправильного пути к файлам в php файлах в контейнере.

![img](/images/image_5.png)

---

## Ответы на вопросы

1. **Что такое непрерывная интеграция?**  
   Непрерывная интеграция (CI) — это практика автоматической сборки, тестирования и интеграции изменений кода в общий репозиторий при каждом коммите или запросе на включение изменений. Это позволяет выявлять ошибки на ранних этапах разработки.

2. **Для чего нужны юнит-тесты? Как часто их нужно запускать?**  
   Юнит-тесты проверяют корректность работы отдельных компонентов (функций, методов) приложения. Они помогают убедиться, что код работает как ожидалось, и предотвращают регрессии при внесении изменений. Запускать юнит-тесты следует автоматически при каждом коммите или создании Pull Request, а также локально перед отправкой изменений.

3. **Что нужно изменить в файле `.github/workflows/main.yml`, чтобы тесты запускались при каждом создании запроса на слияние (Pull Request)?**  
   Для запуска тестов при создании Pull Request нужно добавить событие `pull_request` в секцию `on`. Измененный фрагмент файла:

   ```yaml
   on:
     push:
       branches:
         - main
     pull_request:
       branches:
         - main
   ```

4. **Что нужно добавить в файл `.github/workflows/main.yml`, чтобы удалять созданные образы после выполнения тестов?**  
   Для удаления Docker-образа после выполнения тестов можно добавить шаг с командой `docker image rm`. Пример:

   ```yaml
   - name: Remove Docker image
     run: docker image rm containers08
   ```

   Этот шаг нужно добавить после шага `Remove the container`.

---

## Выводы

В ходе выполнения работы я освоил создание веб-приложения на PHP, разработку юнит-тестов и настройку непрерывной интеграции с использованием Github Actions. Я научился работать с Docker для контейнеризации приложения и понял, как автоматизировать тестирование при каждом коммите. Опыт настройки CI/CD помог мне лучше понять процесс интеграции и тестирования кода в реальных проектах.