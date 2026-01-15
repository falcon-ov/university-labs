<?php
// Подключаем обработчик формы
require_once __DIR__ . '/../../src/handlers/task_handler.php';
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Добавление новой задачи</title>
    <link rel="stylesheet" href="/css/style.css">
    <script src="/js/script.js"></script>

</head>
<body>
    <h1>Добавление новой задачи</h1>
    
    <nav>
        <ul>
            <li><a href="/index.php">Главная</a></li>
            <li><a href="/task/index.php">Все задачи</a></li>
        </ul>
    </nav>
    
    <?php if (isset($errors['general'])): ?>
        <p><?php echo $errors['general']; ?></p>
    <?php endif; ?>
    
    <form action="/task/create.php" method="post">
        <div>
            <label for="title">Название задачи:</label>
            <input type="text" id="title" name="title" value="<?php echo $_POST['title'] ?? ''; ?>">
            <?php if (isset($errors['title'])): ?>
                <p><?php echo $errors['title']; ?></p>
            <?php endif; ?>
        </div>
        
        <div>
            <label for="description">Описание задачи:</label>
            <textarea id="description" name="description"><?php echo $_POST['description'] ?? ''; ?></textarea>
            <?php if (isset($errors['description'])): ?>
                <p><?php echo $errors['description']; ?></p>
            <?php endif; ?>
        </div>
        
        <div>
            <label for="priority">Приоритет:</label>
            <select id="priority" name="priority">
                <option value="">-- Выберите приоритет --</option>
                <option value="low" <?php echo (isset($_POST['priority']) && $_POST['priority'] === 'low') ? 'selected' : ''; ?>>Низкий</option>
                <option value="medium" <?php echo (isset($_POST['priority']) && $_POST['priority'] === 'medium') ? 'selected' : ''; ?>>Средний</option>
                <option value="high" <?php echo (isset($_POST['priority']) && $_POST['priority'] === 'high') ? 'selected' : ''; ?>>Высокий</option>
            </select>
            <?php if (isset($errors['priority'])): ?>
                <p><?php echo $errors['priority']; ?></p>
            <?php endif; ?>
        </div>
        
        <div>
            <label for="due_date">Срок выполнения:</label>
            <input type="date" id="due_date" name="due_date" value="<?php echo $_POST['due_date'] ?? ''; ?>">
            <?php if (isset($errors['due_date'])): ?>
                <p><?php echo $errors['due_date']; ?></p>
            <?php endif; ?>
        </div>
        
        <div>
            <label for="tags">Теги:</label>
            <select id="tags" name="tags[]" multiple>
                <option value="работа">Работа</option>
                <option value="личное">Личное</option>
                <option value="учеба">Учеба</option>
                <option value="срочно">Срочно</option>
                <option value="важно">Важно</option>
            </select>
        </div>
        
    <!-- Дополнительное задание -->
        <div id="steps-container">
            <label>Шаги для выполнения:</label>
            <div id="steps-list">
                <?php if (!empty($_POST['steps'])): ?>
                    <?php foreach ($_POST['steps'] as $step): ?>
                        <div class="step">
                        <span class="remove-btn">❌</span> <input type="text" name="steps[]" value="<?php echo htmlspecialchars($step); ?>">
                        </div>
                    <?php endforeach; ?>
                <?php endif; ?>
            </div>
            <button type="button" id="add-step">Добавить шаг</button>
        </div>
        
        <div>
            <button type="submit">Добавить задачу</button>
        </div>
    </form>
</body>
</html>