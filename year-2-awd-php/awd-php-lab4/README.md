# Отчет по лабораторной работе №4: Обработка и валидация форм

## Инструкции по запуску проекта

1. Установите PHP (если не установлен).
2. Скачайте файлы проекта.
3. Откройте терминал в папке `public` и запустите сервер:
   ```sh
   cd public
   php -S localhost:8000
   ```
4. Перейдите в браузере на `http://localhost:8000/task/create.php`.

## Описание лабораторной работы

**Цель работы**: Освоить работу с HTML-формами в PHP, включая отправку, обработку и валидацию данных.

**Тема проекта**: ToDo-лист — приложение для управления задачами.

## Структура проекта
- `public/` — публичные файлы
  - `index.php` — главная страница (2 последние задачи)
  - `task/create.php` — форма добавления задачи
  - `task/index.php` — список всех задач без пагинации
  - `css/style.css` — CSS
  - `js/script.js` — JS
- `src/` — исходный код
  - `handlers/create-task.php` — обработчик формы
  - `helpers.php` — вспомогательные функции
- `storage/tasks.txt` — файл для хранения задач

## Фрагменты кода, описание выполнения заданий

### Задание 1. Создание проекта
Создана структура проекта с разделением на публичные файлы, исходный код и данные. Публичные файлы находятся в папке `public/`, обработчики и функции — в `src/`, а данные хранятся в `storage/tasks.txt`.

### Задание 2. Создание формы добавления задачи
Реализована форма в `task/create.php` с полями: название, описание, приоритет, срок выполнения, тэги и шаги. Форма использует метод `POST`. Добавлена динамическая работа с шагами через JavaScript.

**Фрагмент кода формы (`task/create.php`):**
```php
<form action="/task/create.php" method="post">
    <div>
        <label for="title">Название задачи:</label>
        <input type="text" id="title" name="title" value="<?php echo $_POST['title'] ?? ''; ?>">
        <?php if (isset($errors['title'])): ?>
            <p><?php echo $errors['title']; ?></p>
        <?php endif; ?>
    </div>
    <div>
        <label for="priority">Приоритет:</label>
        <select id="priority" name="priority">
            <option value="low">Низкий</option>
            <option value="medium">Средний</option>
            <option value="high">Высокий</option>
        </select>
    </div>
    <div id="steps-container">
        <label>Шаги для выполнения:</label>
        <div id="steps-list"></div>
        <button type="button" id="add-step">Добавить шаг</button>
    </div>
    <button type="submit">Добавить задачу</button>
</form>
```

**Фрагмент JavaScript (`script.js`):**
```javascript
addStepBtn.addEventListener("click", function () {
    let stepDiv = document.createElement("div");
    stepDiv.classList.add("step");
    let removeBtn = document.createElement("span");
    removeBtn.textContent = "❌";
    removeBtn.classList.add("remove-btn");
    removeBtn.addEventListener("click", function () {
        stepDiv.remove();
    });
    let input = document.createElement("input");
    input.type = "text";
    input.name = "steps[]";
    input.placeholder = "Введите шаг";
    stepDiv.appendChild(removeBtn);
    stepDiv.appendChild(input);
    stepContainer.appendChild(stepDiv);
});
```

### Задание 3. Обработка формы
Обработчик в `handlers/create-task.php` фильтрует данные с помощью `filterInput()` и валидирует их. При успехе данные сохраняются в JSON в `tasks.txt`.

**Фрагмент кода обработчика (`task_handler.php`):**
```php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = filterInput($_POST['title'] ?? '');
    $priority = filterInput($_POST['priority'] ?? '');
    $steps = isset($_POST['steps']) ? array_map('filterInput', $_POST['steps']) : [];
    
    $titleError = validateTitle($title);
    if ($titleError) $errors['title'] = $titleError;
    
    $priorityError = validatePriority($priority);
    if ($priorityError) $errors['priority'] = $priorityError;

    if (empty($errors)) {
        $taskData = [
            'title' => $title,
            'priority' => $priority,
            'steps' => $steps,
            'created_at' => date('Y-m-d H:i:s')
        ];
        if (saveTask($storageFile, $taskData)) {
            header('Location: /index.php');
            exit;
        }
    }
}
```

### Задание 4. Отображение задач
- В `index.php` выводятся 2 последние задачи с использованием `getLatestTasks()`.  
- В `task/index.php` — список всех задач с пагинацией.

**Фрагмент вывода последних задач (`index.php`):**
```php
$latestTasks = getLatestTasks($storageFile, 2);
if (empty($latestTasks)): ?>
    <p>Пока нет задач. <a href="/task/create.php">Добавьте задачу</a>!</p>
<?php else: ?>
    <ul>
        <?php foreach ($latestTasks as $task): ?>
            <li>
                <h3><?php echo htmlspecialchars($task->title); ?></h3>
                <p>Приоритет: <?php echo htmlspecialchars($task->priority); ?></p>
            </li>
        <?php endforeach; ?>
    </ul>
<?php endif; ?>
```

**Фрагмент списка задач с пагинацией (`task/index.php`):**
```php
$tasksPerPage = 5;
$totalTasks = count($allTasks);
$totalPages = ceil($totalTasks / $tasksPerPage);
$currentPage = isset($_GET['page']) ? (int)$_GET['page'] : 1;
$offset = ($currentPage - 1) * $tasksPerPage;
$tasks = array_slice($allTasks, $offset, $tasksPerPage);

foreach ($tasks as $task): ?>
    <li>
        <h3><?php echo htmlspecialchars($task->title); ?></h3>
        <p>Приоритет: <?php echo htmlspecialchars($task->priority); ?></p>
    </li>
<?php endforeach; ?>

<div class="pagination">
    <?php if ($currentPage > 1): ?>
        <a href="/task/index.php?page=<?php echo $currentPage - 1; ?>">Предыдущая</a>
    <?php endif; ?>
    <?php for ($i = 1; $i <= $totalPages; $i++): ?>
        <a href="/task/index.php?page=<?php echo $i; ?>"><?php echo $i; ?></a>
    <?php endfor; ?>
</div>
```

### Дополнительное задание
Добавлена пагинация в `task/index.php` (см. фрагмент выше). Логика:  
- 5 задач на страницу.  
- Навигация по страницам через GET-параметр `page`.

## Ответы на контрольные вопросы

1. **Какие методы HTTP применяются для отправки данных формы?**  
   Методы HTTP определяют, как данные формы передаются от клиента (браузера) к серверу:  
   - **`GET`** — данные формы добавляются в URL в виде строки запроса (например, `?title=Задача&priority=low`). Используется для запросов, которые не изменяют состояние сервера, например, для поиска или фильтрации данных. Ограничение: длина URL (около 2000 символов), что делает его неподходящим для больших объемов данных. В текущем проекте `GET` используется для пагинации в `task/index.php` через параметр `page`.  
   - **`POST`** — данные формы отправляются в теле HTTP-запроса, скрыты от пользователя и не имеют строгих ограничений по объему. Используется для отправки форм, изменяющих состояние сервера, например, добавления задачи в `task/create.php`. В проекте `POST` применяется для передачи данных формы в обработчик `handlers/create-task.php`.  

   **Пример**:  
   - `GET`: `http://localhost:8000/task/index.php?page=2`  
   - `POST`: данные формы (`title`, `priority`, `steps[]`) передаются скрыто в теле запроса.

2. **Что такое валидация данных, и чем она отличается от фильтрации?**  
   - **Валидация** — это процесс проверки данных на соответствие заданным правилам или ожиданиям. Цель — убедиться, что данные корректны и пригодны для дальнейшей обработки. Например, проверка, что название задачи не пустое и содержит от 3 до 100 символов. В проекте валидация реализована в функциях вроде `validateTitle()` и `validateDueDate()` в `helpers.php`. Если данные не проходят валидацию, возвращается сообщение об ошибке, и обработка прерывается.  
   - **Фильтрация** — это преобразование или очистка данных для удаления нежелательных символов или приведения их к безопасному виду. Цель — подготовить данные к использованию, даже если они изначально некорректны. Например, удаление пробелов с помощью `trim()` или экранирование HTML-тегов через `htmlspecialchars()`. В проекте фильтрация применяется в функции `filterInput()`.  
   - **Разница**: Валидация отвечает на вопрос "правильны ли данные?" и может отклонить их, а фильтрация отвечает на вопрос "как сделать данные безопасными или удобными?" и изменяет их. Валидация — это проверка, фильтрация — это обработка.  

   **Пример из проекта**:  
   - Валидация: `if (strlen($title) < 3) return "Название слишком короткое";` — отклоняет данные.  
   - Фильтрация: `$title = htmlspecialchars($title);` — преобразует `<script>` в `&lt;script&gt;`.

3. **Какие функции PHP используются для фильтрации данных?**  
   PHP предоставляет множество функций для фильтрации данных, которые используются в проекте для обеспечения безопасности и корректности:  
   - **`filter_var($data, FILTER_SANITIZE_STRING)`** — универсальная функция для очистки данных. Может удалять или экранировать специальные символы в зависимости от фильтра. Например, `filter_var($email, FILTER_SANITIZE_EMAIL)` очищает email. В проекте можно было бы использовать для дополнительных проверок.  
   - **`strip_tags($data)`** — удаляет HTML- и PHP-теги из строки, оставляя только текст. Полезно для предотвращения XSS-атак. Например, `<b>Текст</b>` станет `Текст`.  
   - **`htmlspecialchars($data)`** — преобразует специальные HTML-символы в их сущности (`<` → `&lt;`, `>` → `&gt;`). Используется в проекте для вывода данных в `index.php` и `task/index.php`, чтобы избежать выполнения вредоносного кода.  
   - **`trim($data)`** — удаляет пробелы в начале и конце строки. Применяется в `filterInput()` для очистки пользовательского ввода, например, `"  текст  " → "текст"`.  

   **Пример из проекта (`helpers.php`)**:  
   ```php
   function filterInput($data) {
       $data = trim($data);           // Удаляем пробелы
       $data = stripslashes($data);   // Удаляем экранирование
       $data = htmlspecialchars($data); // Экранируем HTML
       return $data;
   }
   ```
   Эти функции вместе обеспечивают базовую очистку данных перед сохранением или выводом.

## Список использованных источников
- [www.php.net](https://www.php.net/manual/en/)  
- [www.w3schools.com](https://www.w3schools.com/)  
- [github.com/MSU-Courses/advanced-web-programming/](https://github.com/MSU-Courses/advanced-web-programming/)

## Дополнительные важные аспекты
- Код безопасен благодаря `htmlspecialchars()` для вывода данных.  
- Возможное улучшение: стилизация пагинации в `style.css`.
