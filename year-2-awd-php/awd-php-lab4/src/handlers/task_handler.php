<?php
/**
 * Обработчик формы добавления задачи
 */

// Подключаем файл с вспомогательными функциями
require_once __DIR__ . '/../helpers.php';

// Путь к файлу для хранения задач
$storageFile = __DIR__ . '/../../storage/tasks.txt';

// Массив для хранения ошибок валидации
$errors = [];

// Обрабатываем форму только при POST-запросе
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Фильтруем входные данные
    $title = filterInput($_POST['title'] ?? '');
    $description = filterInput($_POST['description'] ?? '');
    $priority = filterInput($_POST['priority'] ?? '');
    $dueDate = filterInput($_POST['due_date'] ?? '');
    $tags = $_POST['tags'] ?? [];
    //*
    $steps = isset($_POST['steps']) && is_array($_POST['steps']) ? array_map('filterInput', $_POST['steps']) : [];
    
    // Валидируем данные
    $titleError = validateTitle($title);
    if ($titleError) {
        $errors['title'] = $titleError;
    }
    
    $descriptionError = validateDescription($description);
    if ($descriptionError) {
        $errors['description'] = $descriptionError;
    }

    $priorityError = validatePriority($priority);
    if ($priorityError) {
        $errors['priority'] = $priorityError;
    }

    $dueDateError = validateDueDate($dueDate);
    if ($dueDateError) {
        $errors['due_date'] = $dueDateError;
    }

    
    // Если ошибок нет, сохраняем данные
    if (empty($errors)) {
        // Подготавливаем массив с данными задачи
        $taskData = [
            'title' => $title,
            'description' => $description,
            'priority' => $priority,
            'due_date' => $dueDate,
            'tags' => $tags,
            'steps' => $steps
        ];
        
        // Сохраняем данные в файл
        if (saveTask($storageFile, $taskData)) {
            // Перенаправляем на главную страницу
            header('Location: /index.php');
            exit;
        } else {
            $errors['general'] = "Ошибка при сохранении задачи";
        }
    }
}
?>