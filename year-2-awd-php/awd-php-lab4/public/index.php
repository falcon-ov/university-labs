<?php
// Подключаем файл с вспомогательными функциями
require_once __DIR__ . '/../src/helpers.php';

// Путь к файлу с задачами
$storageFile = __DIR__ . '/../storage/tasks.txt';

// Получаем 2 последние задачи
$latestTasks = getLatestTasks($storageFile, 2);
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ToDo List - Главная</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <h1>ToDo List - Главная</h1>
    
    <nav>
        <ul>
            <li><a href="/task/index.php">Все задачи</a></li>
            <li><a href="/task/create.php">Добавить задачу</a></li>
        </ul>
    </nav>
    
    <h2>Последние задачи</h2>
    
    <?php if (empty($latestTasks)): ?>
        <p>Пока нет ни одной задачи. <a href="/task/create.php">Добавьте первую задачу</a>!</p>
    <?php else: ?>
        <ul>
            <?php foreach ($latestTasks as $task): ?>
                <li>
                    <h3><?php echo $task->title; ?></h3>
                    <p>Приоритет: <?php echo $task->priority; ?></p>
                    <?php if (!empty($task->description)): ?>
                        <p>Описание: <?php echo $task->description; ?></p>
                    <?php endif; ?>
                    <?php if (!empty($task->due_date)): ?>
                        <p>Срок выполнения: <?php echo $task->due_date; ?></p>
                    <?php endif; ?>
                    <p><a href="/task/index.php">Показать все задачи</a></p>
                </li>
            <?php endforeach; ?>
        </ul>
    <?php endif; ?>
</body>
</html>