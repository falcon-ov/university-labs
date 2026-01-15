<?php
/**
 * Обработчик удаления задачи
 */

require_once __DIR__ . '/../../helpers.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $taskId = filterInput($_POST['id'] ?? '');

    if ($taskId) {
        try {
            $pdo = getDbConnection();
            $stmt = $pdo->prepare("DELETE FROM tasks WHERE id = :id");
            $stmt->execute(['id' => $taskId]);
            header('Location: /index');
            exit;
        } catch (PDOException $e) {
            http_response_code(500);
            echo "Ошибка при удалении задачи: " . $e->getMessage();
        }
    } else {
        http_response_code(400);
        echo "ID задачи не указан";
    }
} else {
    http_response_code(405);
    echo "Метод не разрешен";
}