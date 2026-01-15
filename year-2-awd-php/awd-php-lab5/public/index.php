<?php
/**
 * Единая точка входа для приложения
 */

require_once __DIR__ . '/../src/helpers.php';
require_once __DIR__ . '/../src/db.php';

// Инициализируем подключение к базе данных
try {
    $pdo = getDbConnection();
} catch (PDOException $e) {
    http_response_code(500);
    die("Ошибка подключения к базе данных: " . $e->getMessage());
}

// Получаем путь запроса
$requestUri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$method = $_SERVER['REQUEST_METHOD'];

// Простая маршрутизация
switch ($requestUri) {
    case '/':
        // Главная страница: последние 3 задачи без пагинации
        $tasks = getRecentTasks($pdo, 3); // Новая функция для 3 задач
        renderTemplate('index', ['tasks' => $tasks, 'pdo' => $pdo]);
        break;

    case '/index':
        // Страница со всеми задачами: пагинация
        $tasksPerPage = 5;
        $currentPage = isset($_GET['page']) ? (int)$_GET['page'] : 1;
        if ($currentPage < 1) $currentPage = 1;
        $tasks = getAllTasks($pdo, $tasksPerPage, $currentPage);
        $totalTasks = getTotalTasks($pdo);
        $totalPages = ceil($totalTasks / $tasksPerPage);
        renderTemplate('index', [
            'tasks' => $tasks,
            'pdo' => $pdo,
            'currentPage' => $currentPage,
            'totalPages' => $totalPages,
            'tasksPerPage' => $tasksPerPage
        ]);
        break;

    case '/task/create':
        if ($method === 'POST') {
            require __DIR__ . '/../src/handlers/task/create.php';
        } else {
            renderTemplate('task/create', ['errors' => [], 'post' => [], 'pdo' => $pdo]);
        }
        break;

    case '/task/edit':
        $id = $_GET['id'] ?? null;
        if ($id && $method === 'POST') {
            require __DIR__ . '/../src/handlers/task/edit.php';
        } elseif ($id) {
            $task = getTaskById($pdo, $id);
            if ($task) {
                renderTemplate('task/edit', ['task' => $task, 'errors' => [], 'post' => [], 'pdo' => $pdo]);
            } else {
                http_response_code(404);
                echo "Задача не найдена";
            }
        } else {
            http_response_code(400);
            echo "ID задачи не указан";
        }
        break;

    case '/task/delete':
        if ($method === 'POST') {
            require __DIR__ . '/../src/handlers/task/delete.php';
        } else {
            http_response_code(405);
            echo "Метод не разрешен";
        }
        break;

    case '/task/show':
        $id = $_GET['id'] ?? null;
        if ($id) {
            $task = getTaskById($pdo, $id);
            if ($task) {
                renderTemplate('task/show', ['task' => $task, 'pdo' => $pdo]);
            } else {
                http_response_code(404);
                echo "Задача не найдена";
            }
        } else {
            http_response_code(400);
            return "ID задачи не указан";
        }
        break;

    default:
        http_response_code(404);
        echo "Страница не найдена";
        break;
}