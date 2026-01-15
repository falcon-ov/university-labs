# Лабораторная работа 4
## Динамические свойства объектов в Java

---

### Задание 1: Примеры из библиотек

**Пример:** Spring Framework (Spring MVC) - `RequestAttributes`

```java
@GetMapping("/profile")
public String profile() {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

    // допустим, получили ID текущего пользователя из токена
    int currentUserId = 123;
    attrs.setAttribute("userId", currentUserId, RequestAttributes.SCOPE_REQUEST);

    // теперь где-то в другом компоненте мы можем достать этот userId
    return "ok";
}
```
А потом, например, в сервисе:
```java
public int getCurrentUserId() {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
    return (Integer) attrs.getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
}
```
В этом случае, лучше, конечно, передавать userId через аргумент метода, но код приведен в качестве демонстрации.

**Контекст:** Хранит данные, специфичные для HTTP-запроса. Библиотека не знает, какие данные будут добавлены пользователем.

---

Spring Framework реализует `RequestAttributes` как контекст, привязанный к текущему потоку исполнения (thread).
То есть он работает по принципу `ThreadLocal + словарь атрибутов (Map)`.

Механизм примерно такой:
```java
class RequestContextHolder {
private static final ThreadLocal<RequestAttributes> requestAttributesHolder = new ThreadLocal<>();

    public static RequestAttributes getRequestAttributes() {
        return requestAttributesHolder.get();
    }

    public static void setRequestAttributes(RequestAttributes attributes) {
        requestAttributesHolder.set(attributes);
    }
}
```

То есть:
когда начинается новый HTTP-запрос, Spring создаёт объект RequestAttributes и помещает его в ThreadLocal;\
этот объект хранит Map ключ–значение, где ключ — имя атрибута, а значение — произвольный объект (Object): (Map<String, Object>);\
в течение выполнения запроса все классы (фильтры, сервисы и т.д.) могут вытащить тот же контекст из RequestContextHolder.getRequestAttributes().


### Задание 2: Почему FatStruct не подходит для библиотек

**FatStruct** - это структура со всеми возможными полями сразу:

```java
class FatStruct {
    String name;
    Integer age;
    Double salary;
    // ... еще 50 полей
}
```

**Проблема:** Библиотека не знает, какие поля понадобятся пользователю. Если добавить все возможные поля:
- Трата памяти на неиспользуемые поля
- Невозможно добавить новые поля без изменения библиотеки
- Пользователь не может расширить структуру своими данными

---

### Задание 3: Система с динамическими свойствами

**Цель:** Создать библиотеку с контекстом, где пользователь может добавлять свои данные и операции.

#### Библиотека (`library` модуль)

**Типизированный ключ:**

```java
public class PropertyKey<T> {
    private final int id;
    private final String name;
    
    PropertyKey(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() { return id; }
}
```

**Реестр ключей:**

```java
public class PropertyRegistry {
    private static final Map<String, Integer> keys = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    public static <T> PropertyKey<T> register(String name) {
        Integer existing = keys.putIfAbsent(name, counter.get());
        if (existing != null) {
            throw new IllegalStateException("Ключ '" + name + "' уже зарегистрирован");
        }
        return new PropertyKey<>(counter.getAndIncrement(), name);
    }
}
```

**Контекст с динамическими свойствами:**

```java
public class Context {
    private final Map<Integer, Object> properties = new HashMap<>();
    
    public <T> void set(PropertyKey<T> key, T value) {
        properties.put(key.getId(), value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(PropertyKey<T> key) {
        return (T) properties.get(key.getId());
    }
}
```

**Обработчик операций:**

```java
public interface Operation {
    void execute(Context context);
}

public class OperationExecutor {
    public void run(Context context, Operation operation) {
        operation.execute(context);
    }
}
```

---

#### Пользовательский проект (`client` модуль)

**Регистрация ключей:**

```java
public class UserKeys {
    public static final PropertyKey<String> USERNAME = 
        PropertyRegistry.register("username");
    public static final PropertyKey<Integer> AGE = 
        PropertyRegistry.register("age");
}
```

**Использование:**

```java
Context ctx = new Context();
ctx.set(UserKeys.USERNAME, "Alice");
ctx.set(UserKeys.AGE, 25);

Operation op = context -> {
    String name = context.get(UserKeys.USERNAME);
    Integer age = context.get(UserKeys.AGE);
    System.out.println(name + " is " + age);
};

new OperationExecutor().run(ctx, op);
```

---

### Преимущества решения

1. **Типобезопасность** - Generic гарантирует правильный тип
2. **Централизованный реестр** - Предотвращает конфликты ключей
3. **Расширяемость** - Пользователи добавляют свои ключи
4. **Изоляция** - Библиотека не зависит от пользовательских данных

---

### Выбор подхода

**Словарь с int-ключом** выбран потому что:
- Быстрый доступ O(1)
- Экономия памяти (int vs String)
- Контроль уникальности через реестр