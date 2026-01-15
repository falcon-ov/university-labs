<?php
/**
 * Обработчик создания задачи
 */

require_once __DIR__ . '/../../helpers.php';

$errors = [];
$post = $_POST;
$pdo = getDbConnection(); // Получаем PDO из db.php

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = filterInput($post['title'] ?? '');
    $description = filterInput($post['description'] ?? '');
    $priority = filterInput($post['priority'] ?? '');
    $dueDate = filterInput($post['due_date'] ?? '');
    $categoryId = filterInput($post['category_id'] ?? '');
    $tags = $post['tags'] ?? [];
    $steps = array_map('filterInput', $post['steps'] ?? []);

    // Валидация
    if ($titleError = validateTitle($title)) {
        $errors['title'] = $titleError;
    }
    if ($descriptionError = validateDescription($description)) {
        $errors['description'] = $descriptionError;
    }
    if ($priorityError = validatePriority($priority)) {
        $errors['priority'] = $priorityError;
    }
    if ($dueDateError = validateDueDate($dueDate)) {
        $errors['due_date'] = $dueDateError;
    }

    if (empty($errors)) {
        try {
            $pdo->beginTransaction();

            // Сохраняем задачу
            $stmt = $pdo->prepare("
                INSERT INTO tasks (title, description, priority, due_date, category_id)
                VALUES (:title, :description, :priority, :due_date, :category_id)
            ");
            $stmt->execute([
                'title' => $title,
                'description' => $description,
                'priority' => $priority,
                'due_date' => $dueDate ?: null,
                'category_id' => $categoryId ?: null,
            ]);
            $taskId = $pdo->lastInsertId();

            // Сохраняем шаги
            foreach ($steps as $step) {
                if (!empty($step)) {
                    $stmt = $pdo->prepare("INSERT INTO steps (task_id, step_text) VALUES (:task_id, :step_text)");
                    $stmt->execute(['task_id' => $taskId, 'step_text' => $step]);
                }
            }

            // Сохраняем теги
            foreach ($tags as $tag) {
                $stmt = $pdo->prepare("INSERT OR IGNORE INTO tags (name) VALUES (:name)");
                $stmt->execute(['name' => $tag]);
                $stmt = $pdo->prepare("SELECT id FROM tags WHERE name = :name");
                $stmt->execute(['name' => $tag]);
                $tagId = $stmt->fetchColumn();

                $stmt = $pdo->prepare("INSERT INTO task_tags (task_id, tag_id) VALUES (:task_id, :tag_id)");
                $stmt->execute(['task_id' => $taskId, 'tag_id' => $tagId]);
            }

            $pdo->commit();
            header('Location: /index');
            exit;
        } catch (PDOException $e) {
            $pdo->rollBack();
            $errors['general'] = "Ошибка при сохранении задачи: " . $e->getMessage();
        }
    }
}

// Рендерим форму с ошибками и данными
renderTemplate('task/create', [
    'errors' => $errors,
    'post' => $post,
    'pdo' => $pdo // Передаем PDO для получения категорий
]);