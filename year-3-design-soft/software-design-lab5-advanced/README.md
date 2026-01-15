# Как работает система с Dependency Injection

## 1. Общая идея

Система устроена как конвейер обработки данных. Есть четыре шага: читаем данные, обрабатываем, сериализуем (переводим в нужный формат) и записываем куда-то. Проще это представить так:

Чтение данных → Обработка → Форматирование → Вывод

То есть сначала получаем данные, потом меняем их как нужно, потом переводим в JSON или CSV, а потом выводим в консоль или пишем в файл.

---

## 2. Основные компоненты

### Модель данных

Есть простой класс `Record`, который хранит три поля: `id`, `name` и `value`. Например:

`Record("1", "Test", 100)`

Этот объект — это одна запись данных.

---

### Форматы сериализации

Есть интерфейс `SerializationFormat` и разные реализации, например `JsonFormat` и `CsvFormat`. Это нужно, чтобы легко менять формат вывода без изменения остального кода.

Пример использования:

```java
List<Record> data = [...];
SerializationFormat format = new JsonFormat();
String result = format.serialize(data);
```

В результате получаем JSON-строку с нашими данными.

---

### Источники чтения (Reader)

Есть интерфейс `SourceReader` с методом `read()`, который возвращает список записей.

Реализации могут быть разными:

* `FileSourceReader` — читает данные из файла
* `RandomSourceReader` — генерирует случайные данные
* `HttpSourceReader` — получает данные по HTTP

Главное, что код, который использует Reader, не знает откуда данные. Можно легко заменить источник.

---

### Источники записи (Writer)

Есть интерфейс `SourceWriter` с методом `write()`, который получает данные и формат.

Реализации:

* `FileSourceWriter` — пишет в файл
* `ConsoleSourceWriter` — выводит в консоль

Так же легко менять способ записи, не меняя остальной код.

---

### Обработчик данных (Processor)

Класс `DataProcessor` меняет данные по правилам. В примере он делает имена заглавными и прибавляет 10 к числовому значению.

---

## 3. Сервис (PipelineService)

Это главный класс, который связывает все компоненты:

```java
PipelineService {
    SourceReader reader;
    SourceWriter writer;
    DataProcessor processor;
    SerializationFormat format;

    void execute() {
        // читаем данные
        // обрабатываем
        // записываем
    }
}
```

Важно, что PipelineService сам не создает Reader, Writer и Format — их ему дают извне. Это и есть **Dependency Injection**.

---

## 4. Dependency Injection (DI)

### Принцип Inversion of Control (IoC)

Плохой способ — когда сервис сам создаёт все объекты:

```java
SourceReader reader = new FileSourceReader("file.txt");
SourceWriter writer = new ConsoleSourceWriter();
```

Тогда сервис сильно привязан к конкретным классам.

Хороший способ — когда зависимости передаются через конструктор:

```java
PipelineService(SourceReader reader, SourceWriter writer, ...) {
    this.reader = reader;
}
```

Так сервис становится гибким и тестируемым.

---

### Варианты DI

**1. Контейнер зависимостей (DIContainer)**

Мы создаём контейнер, регистрируем в нём объекты, а потом получаем готовый сервис:

```java
DIContainer container = new DIContainer();
container.register(SourceReader.class, new FileSourceReader("input.txt"));
container.register(SourceWriter.class, new ConsoleSourceWriter());
...
PipelineService pipeline = container.createPipeline();
pipeline.execute();
```

Контейнер хранит объекты и отдаёт их сервису.

**2. Фабрика (Factory)**

Фабрика создаёт pipeline по параметрам, например:

```java
PipelineFactory factory = new PipelineFactory();
PipelineService pipeline = factory.createPipeline("file", "console", "csv");
pipeline.execute();
```

Внутри фабрика решает, какой Reader, Writer и Format использовать, и собирает сервис.

---

## 5. Принцип единой ответственности (SRP)

Каждый класс делает только одну вещь:

* Reader читает данные
* Writer пишет данные
* Processor обрабатывает данные
* Format сериализует данные
* PipelineService соединяет всё вместе

Если один класс делает всё сразу, его сложно тестировать и менять.

---

## 6. Dependency Inversion Principle

Сервис должен зависеть от интерфейсов, а не от конкретных классов.

Правильно:

```java
PipelineService {
    SourceReader reader; // интерфейс
}
```

Неправильно:

```java
PipelineService {
    FileSourceReader reader; // конкретный класс
}
```

Так мы можем подставить любую реализацию Reader или Writer.

---

## 7. Пример работы

1. Создаем фабрику
2. Фабрика создаёт pipeline: Random → Console, JSON
3. Фабрика внутри создаёт Reader, Writer, Processor, Format и PipelineService
4. Вызываем `execute()`
5. Последовательно выполняются шаги: читаем, обрабатываем, сериализуем, выводим

---

## 8. Как расширять систему

Чтобы добавить новый формат (например XML), нужно:

1. Сделать новый класс `XmlFormat`, который реализует `SerializationFormat`
2. Добавить его в фабрику

Чтобы добавить новый источник (например базу данных):

1. Сделать новый `DatabaseSourceReader`, который реализует `SourceReader`
2. Добавить его в фабрику

