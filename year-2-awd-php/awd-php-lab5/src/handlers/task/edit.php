<?php
/**
 * Обработчик редактирования задачи
 */

require_once __DIR__ . '/../../helpers.php';

$errors = [];
$post = $_POST;
$taskId = $_GET['id'];

$task = getTaskById($pdo, $taskId);
if (!$task) {
    http_response_code(404);
    echo "Задача не найдена";
    exit;
}

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
            $pdo = getDbConnection();
            $pdo->beginTransaction();

            // Обновляем задачу
            $stmt = $pdo->prepare("
                UPDATE tasks
                SET title = :title, description = :description, priority = :priority, due_date = :due_date, category_id = :category_id
                WHERE id = :id
            ");
            $stmt->execute([
                'title' => $title,
                'description' => $description,
                'priority' => $priority,
                'due_date' => $dueDate ?: null,
                'category_id' => $categoryId ?: null,
                'id' => $taskId,
            ]);

            // Удаляем старые шаги
            $pdo->prepare("DELETE FROM steps WHERE task_id = :task_id")->execute(['task_id' => $taskId]);

            // Сохраняем новые шаги
            foreach ($steps as $step) {
                if (!empty($step)) {
                    $stmt = $pdo->prepare("INSERT INTO steps (task_id, step_text) VALUES (:task_id, :step_text)");
                    $stmt->execute(['task_id' => $taskId, 'step_text' => $step]);
                }
            }

            // Удаляем старые теги
            $pdo->prepare("DELETE FROM task_tags WHERE task_id = :task_id")->execute(['task_id' => $taskId]);

            // Сохраняем новые теги
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
            header('Location: /task/show?id=' . $taskId);
            exit;
        } catch (PDOException $e) {
            $pdo->rollBack();
            $errors['general'] = "Ошибка при обновлении задачи: " . $e->getMessage();
        }
    }
}

renderTemplate('task/edit', ['task' => $task, 'errors' => $errors, 'post' => $post]);