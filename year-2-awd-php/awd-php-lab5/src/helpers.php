<?php
/**
 * Файл с вспомогательными функциями для работы с данными
 */

/**
 * Фильтрует входные данные
 *
 * @param string $data Данные для фильтрации
 * @return string Отфильтрованные данные
 */
function filterInput($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

/**
 * Валидирует название задачи
 *
 * @param string $title Название задачи
 * @return string|false Результат валидации (ошибка или false)
 */
function validateTitle($title) {
    if (empty($title)) {
        return "Название задачи обязательно для заполнения";
    }
    if (strlen($title) < 3) {
        return "Название задачи должно содержать минимум 3 символа";
    }
    if (strlen($title) > 100) {
        return "Название задачи не должно превышать 100 символов";
    }
    return false;
}

/**
 * Валидирует описание задачи
 *
 * @param string $description Описание задачи
 * @return string|false Результат валидации
 */
function validateDescription($description) {
    if (strlen($description) > 500) {
        return "Описание задачи не должно превышать 500 символов";
    }
    return false;
}

/**
 * Валидирует приоритет задачи
 *
 * @param string $priority Приоритет задачи
 * @return string|false Результат валидации
 */
function validatePriority($priority) {
    $validPriorities = ['low', 'medium', 'high'];
    if (!in_array($priority, $validPriorities)) {
        return "Выберите корректный приоритет задачи";
    }
    return false;
}

/**
 * Валидирует дату выполнения
 *
 * @param string $dueDate Дата выполнения
 * @return string|false Результат валидации
 */
function validateDueDate($dueDate) {
    if (!empty($dueDate)) {
        $date = date_create($dueDate);
        if (!$date) {
            return "Неверный формат даты";
        }
        $today = date_create('today');
        if ($date < $today) {
            return "Дата выполнения не может быть в прошлом";
        }
    }
    return false;
}

/**
 * Получает задачи с пагинацией
 *
 * @param PDO $pdo Экземпляр PDO
 * @param int $limit Количество задач на страницу
 * @param int $page Номер страницы
 * @return array Список задач
 * @throws PDOException
 */
function getAllTasks($pdo, $limit, $page = 1) {
    if (!$pdo instanceof PDO) {
        throw new InvalidArgumentException("Ожидается экземпляр PDO");
    }
    $offset = ($page - 1) * $limit;
    $stmt = $pdo->prepare("
        SELECT t.*, c.name as category_name
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        ORDER BY t.created_at DESC
        LIMIT :limit OFFSET :offset
    ");
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
    $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
    $stmt->execute();
    $tasks = $stmt->fetchAll();

    foreach ($tasks as &$task) {
        // Получаем теги
        $stmt = $pdo->prepare("
            SELECT t.name
            FROM tags t
            JOIN task_tags tt ON t.id = tt.tag_id
            WHERE tt.task_id = :task_id
        ");
        $stmt->execute(['task_id' => $task['id']]);
        $task['tags'] = array_column($stmt->fetchAll(), 'name');

        // Получаем шаги
        $stmt = $pdo->prepare("SELECT step_text FROM steps WHERE task_id = :task_id");
        $stmt->execute(['task_id' => $task['id']]);
        $task['steps'] = $stmt->fetchAll();
    }

    return $tasks;
}

/**
 * Получает общее количество задач
 *
 * @param PDO $pdo Экземпляр PDO
 * @return int Общее количество задач
 * @throws PDOException
 */
function getTotalTasks($pdo) {
    if (!$pdo instanceof PDO) {
        throw new InvalidArgumentException("Ожидается экземпляр PDO");
    }
    $stmt = $pdo->query("SELECT COUNT(*) FROM tasks");
    return $stmt->fetchColumn();
}

/**
 * Получает задачу по ID
 *
 * @param PDO $pdo Экземпляр PDO
 * @param int $id ID задачи
 * @return array|null Данные задачи или null
 */
function getTaskById($pdo, $id) {
    $stmt = $pdo->prepare("
        SELECT t.*, c.name as category_name
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.id = :id
    ");
    $stmt->execute(['id' => $id]);
    $task = $stmt->fetch();
    if ($task) {
        $stmt = $pdo->prepare("SELECT name FROM tags JOIN task_tags ON tags.id = task_tags.tag_id WHERE task_tags.task_id = :task_id");
        $stmt->execute(['task_id' => $task['id']]);
        $task['tags'] = array_column($stmt->fetchAll(), 'name');

        $stmt = $pdo->prepare("SELECT step_text FROM steps WHERE task_id = :task_id");
        $stmt->execute(['task_id' => $task['id']]);
        $task['steps'] = $stmt->fetchAll();
    }
    return $task ?: null;
}

/**
 * Получает все категории
 *
 * @param PDO $pdo Экземпляр PDO
 * @return array Список категорий
 */
function getAllCategories($pdo) {
    $stmt = $pdo->query("SELECT * FROM categories");
    return $stmt->fetchAll();
}

/**
 * Рендерит шаблон с данными
 *
 * @param string $template Путь к шаблону (без .php)
 * @param array $data Данные для шаблона
 * @return void
 */
function renderTemplate($template, $data = []) {
    extract($data);
    ob_start();
    require __DIR__ . '/../templates/' . $template . '.php';
    $content = ob_get_clean();
    require __DIR__ . '/../templates/layout.php';
}

/**
 * Получает последние задачи
 *
 * @param PDO $pdo Экземпляр PDO
 * @param int $limit Количество задач
 * @return array Список задач
 * @throws PDOException
 */
function getRecentTasks($pdo, $limit) {
    if (!$pdo instanceof PDO) {
        throw new InvalidArgumentException("Ожидается экземпляр PDO");
    }
    $stmt = $pdo->prepare("
        SELECT t.*, c.name as category_name
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        ORDER BY t.created_at DESC
        LIMIT :limit
    ");
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
    $stmt->execute();
    $tasks = $stmt->fetchAll();

    foreach ($tasks as &$task) {
        // Получаем теги
        $stmt = $pdo->prepare("
            SELECT t.name
            FROM tags t
            JOIN task_tags tt ON t.id = tt.tag_id
            WHERE tt.task_id = :task_id
        ");
        $stmt->execute(['task_id' => $task['id']]);
        $task['tags'] = array_column($stmt->fetchAll(), 'name');

        // Получаем шаги
        $stmt = $pdo->prepare("SELECT step_text FROM steps WHERE task_id = :task_id");
        $stmt->execute(['task_id' => $task['id']]);
        $task['steps'] = $stmt->fetchAll();
    }

    return $tasks;
}