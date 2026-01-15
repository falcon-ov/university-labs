<?php
/**
 * Шаблон главной страницы
 *
 * @var array $tasks Список задач
 * @var PDO $pdo Экземпляр PDO
 * @var int|null $currentPage Номер текущей страницы (для пагинации)
 * @var int|null $totalPages Общее количество страниц (для пагинации)
 * @var int|null $tasksPerPage Количество задач на страницу (для пагинации)
 */
$title = isset($currentPage) ? 'Все задачи' : 'Главная';
?>
<h2><?php echo isset($currentPage) ? 'Все задачи' : 'Последние задачи'; ?></h2>
<?php if (empty($tasks)): ?>
    <p>Пока нет задач. <a href="/task/create">Добавьте задачу</a>!</p>
<?php else: ?>
    <ul>
        <?php foreach ($tasks as $task): ?>
            <li>
                <h3><?php echo htmlspecialchars($task['title']); ?></h3>
                <p>Приоритет: <?php echo htmlspecialchars($task['priority']); ?></p>
                <p><a href="/task/show?id=<?php echo $task['id']; ?>">Подробнее</a></p>
            </li>
        <?php endforeach; ?>
    </ul>
    <?php if (isset($currentPage) && isset($totalPages)): ?>
        <div class="pagination">
            <?php if ($currentPage > 1): ?>
                <a href="/index?page=<?php echo $currentPage - 1; ?>">Предыдущая</a>
            <?php endif; ?>
            <?php for ($i = 1; $i <= $totalPages; $i++): ?>
                <?php if ($i == $currentPage): ?>
                    <span><?php echo $i; ?></span>
                <?php else: ?>
                    <a href="/index?page=<?php echo $i; ?>"><?php echo $i; ?></a>
                <?php endif; ?>
            <?php endfor; ?>
            <?php if ($currentPage < $totalPages): ?>
                <a href="/index?page=<?php echo $currentPage + 1; ?>">Следующая</a>
            <?php endif; ?>
        </div>
    <?php endif; ?>
<?php endif; ?>