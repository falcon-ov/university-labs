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
 * @return array Результат валидации message
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
 * @return array Результат валидации message
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
 * @return array Результат валидации [isValid, message]
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
 * @return array Результат валидации [isValid, message]
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
 * Получает все задачи из файла
 *
 * @param string $filename Путь к файлу с задачами
 * @return array Массив с задачами
 */
function getAllTasks($filename) {
    $tasks = file($filename, FILE_IGNORE_NEW_LINES);
    return array_map('json_decode', $tasks);
}

/**
 * Получает последние N задач из файла
 *
 * @param string $filename Путь к файлу с задачами
 * @param int $count Количество задач для получения
 * @return array Массив с последними задачами
 */
function getLatestTasks($filename, $count) {
    $tasks = getAllTasks($filename);
    return array_slice($tasks, -$count);
}

/**
 * Сохраняет задачу в файл
 *
 * @param string $filename Путь к файлу с задачами
 * @param array $taskData Данные задачи для сохранения
 * @return bool Результат сохранения
 */
function saveTask($filename, $taskData) {
    $lastTask = getLatestTasks($filename, 1);
    $newId = isset($lastTask[0]->id) ? (int)$lastTask[0]->id + 1 : 0;    
    $taskData['id'] = $newId;
    $taskData['created_at'] = date('Y-m-d H:i:s');
    return file_put_contents($filename, json_encode($taskData) . PHP_EOL, FILE_APPEND);
}
?>