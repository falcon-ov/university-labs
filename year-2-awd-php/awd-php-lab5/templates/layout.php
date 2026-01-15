<?php
/**
 * Базовый шаблон для всех страниц
 *
 * @param string $content Содержимое страницы
 * @param string $title Заголовок страницы
 */
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?php echo htmlspecialchars($title ?? 'ToDo List'); ?></title>
    <link rel="stylesheet" href="/css/style.css">
    <script src="/js/script.js"></script>
</head>
<body>
    <header>
        <h1>ToDo List</h1>
        <nav>
            <ul>
                <li><a href="/">Главная</a></li>
                <li><a href="/task/create">Добавить задачу</a></li>
                <li><a href="/index?page=1">Все задачи</a></li>
            </ul>
        </nav>
    </header>
    <main>
        <?php echo $content; ?>
    </main>
</body>
</html>