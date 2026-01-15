<?php
// Подключаем файл с вспомогательными функциями
require_once __DIR__ . '/../../src/helpers.php';

// Путь к файлу с задачами
$storageFile = __DIR__ . '/../../storage/tasks.txt';

// Получаем все задачи
$allTasks = getAllTasks($storageFile);

// Настройки пагинации
$tasksPerPage = 5; // Количество задач на страницу
$totalTasks = count($allTasks); // Общее количество задач
$totalPages = ceil($totalTasks / $tasksPerPage); // Общее количество страниц

// Определяем текущую страницу из GET-параметра
$currentPage = isset($_GET['page']) ? (int)$_GET['page'] : 1;
if ($currentPage < 1) $currentPage = 1; // Страница не может быть меньше 1
if ($currentPage > $totalPages) $currentPage = $totalPages; // Страница не может быть больше максимальной

// Вычисляем смещение для выборки задач
$offset = ($currentPage - 1) * $tasksPerPage;

// Получаем задачи для текущей страницы
$tasks = array_slice($allTasks, $offset, $tasksPerPage);
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Все задачи</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <h1>Все задачи</h1>
    
    <nav>
        <ul>
            <li><a href="/index.php">Главная</a></li>
            <li><a href="/task/create.php">Добавить задачу</a></li>
        </ul>
    </nav>
    
    <?php if (empty($allTasks)): ?>
        <p>Пока нет ни одной задачи. <a href="/task/create.php">Добавьте первую задачу</a>!</p>
    <?php else: ?>
        <ul>
            <?php foreach ($tasks as $task): ?>
                <li>
                    <h3><?php echo htmlspecialchars($task->title); ?></h3>
                    <p>Приоритет: <?php echo htmlspecialchars($task->priority); ?></p>
                    <?php if (!empty($task->description)): ?>
                        <p>Описание: <?php echo htmlspecialchars($task->description); ?></p>
                    <?php endif; ?>
                    <?php if (!empty($task->due_date)): ?>
                        <p>Срок выполнения: <?php echo htmlspecialchars($task->due_date); ?></p>
                    <?php endif; ?>
                    <?php if (!empty($task->tags)): ?>
                        <p>Теги: <?php echo htmlspecialchars(implode(', ', $task->tags)); ?></p>
                    <?php endif; ?>
                    <?php if (!empty($task->steps)): ?>
                        <p>Шаги выполнения:</p>
                        <ol>
                            <?php foreach ($task->steps as $step): ?>
                                <li><?php echo htmlspecialchars($step); ?></li>
                            <?php endforeach; ?>
                        </ol>
                    <?php endif; ?>
                    <p>Создано: <?php echo htmlspecialchars($task->created_at); ?></p>
                </li>
            <?php endforeach; ?>
        </ul>

        <!-- Навигация по страницам -->
        <div class="pagination">
            <?php if ($currentPage > 1): ?>
                <a href="/task/index.php?page=<?php echo $currentPage - 1; ?>">Предыдущая</a>
            <?php endif; ?>

            <?php for ($i = 1; $i <= $totalPages; $i++): ?>
                <?php if ($i == $currentPage): ?>
                    <span><?php echo $i; ?></span>
                <?php else: ?>
                    <a href="/task/index.php?page=<?php echo $i; ?>"><?php echo $i; ?></a>
                <?php endif; ?>
            <?php endfor; ?>

            <?php if ($currentPage < $totalPages): ?>
                <a href="/task/index.php?page=<?php echo $currentPage + 1; ?>">Следующая</a>
            <?php endif; ?>
        </div>
    <?php endif; ?>
</body>
</html> 