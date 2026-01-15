<?php
/**
 * Шаблон страницы просмотра задачи
 *
 * @var array $task Данные задачи
 */
$title = 'Просмотр задачи: ' . htmlspecialchars($task['title']);
?>
<h2><?php echo htmlspecialchars($task['title']); ?></h2>
<p>Приоритет: <?php echo htmlspecialchars($task['priority']); ?></p>
<?php if (!empty($task['description'])): ?>
    <p>Описание: <?php echo htmlspecialchars($task['description']); ?></p>
<?php endif; ?>
<?php if (!empty($task['due_date'])): ?>
    <p>Срок выполнения: <?php echo htmlspecialchars($task['due_date']); ?></p>
<?php endif; ?>
<?php if (!empty($task['category_name'])): ?>
    <p>Категория: <?php echo htmlspecialchars($task['category_name']); ?></p>
<?php endif; ?>
<?php if (!empty($task['tags'])): ?>
    <p>Теги: <?php echo htmlspecialchars(implode(', ', $task['tags'])); ?></p>
<?php endif; ?>
<?php if (!empty($task['steps'])): ?>
    <p>Шаги выполнения:</p>
    <ol>
        <?php foreach ($task['steps'] as $step): ?>
            <li><?php echo htmlspecialchars($step['step_text']); ?></li>
        <?php endforeach; ?>
    </ol>
<?php endif; ?>
<p>Создано: <?php echo htmlspecialchars($task['created_at']); ?></p>
<p>
    <a href="/task/edit?id=<?php echo $task['id']; ?>">Редактировать</a> |
    <form action="/task/delete" method="post" style="display:inline;">
        <input type="hidden" name="id" value="<?php echo $task['id']; ?>">
        <button type="submit" onclick="return confirm('Вы уверены, что хотите удалить задачу?');">Удалить</button>
    </form>
</p>