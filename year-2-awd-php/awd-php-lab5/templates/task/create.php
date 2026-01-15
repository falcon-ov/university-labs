<?php
/**
 * Шаблон формы добавления задачи
 *
 * @var array $errors Ошибки валидации
 * @var array $post Введенные данные
 * @var PDO $pdo Экземпляр PDO
 */
$title = 'Добавление новой задачи';
?>
<h2>Добавление новой задачи</h2>
<?php if (!empty($errors['general'])): ?>
    <p class="error"><?php echo htmlspecialchars($errors['general']); ?></p>
<?php endif; ?>
<form action="/task/create" method="post">
    <div>
        <label for="title">Название задачи:</label>
        <input type="text" id="title" name="title" value="<?php echo htmlspecialchars($post['title'] ?? ''); ?>">
        <?php if (!empty($errors['title'])): ?>
            <p class="error"><?php echo htmlspecialchars($errors['title']); ?></p>
        <?php endif; ?>
    </div>
    <div>
        <label for="description">Описание задачи:</label>
        <textarea id="description" name="description"><?php echo htmlspecialchars($post['description'] ?? ''); ?></textarea>
        <?php if (!empty($errors['description'])): ?>
            <p class="error"><?php echo htmlspecialchars($errors['description']); ?></p>
        <?php endif; ?>
    </div>
    <div>
        <label for="priority">Приоритет:</label>
        <select id="priority" name="priority">
            <option value="">-- Выберите приоритет --</option>
            <option value="low" <?php echo ($post['priority'] ?? '') === 'low' ? 'selected' : ''; ?>>Низкий</option>
            <option value="medium" <?php echo ($post['priority'] ?? '') === 'medium' ? 'selected' : ''; ?>>Средний</option>
            <option value="high" <?php echo ($post['priority'] ?? '') === 'high' ? 'selected' : ''; ?>>Высокий</option>
        </select>
        <?php if (!empty($errors['priority'])): ?>
            <p class="error"><?php echo htmlspecialchars($errors['priority']); ?></p>
        <?php endif; ?>
    </div>
    <div>
        <label for="due_date">Срок выполнения:</label>
        <input type="date" id="due_date" name="due_date" value="<?php echo htmlspecialchars($post['due_date'] ?? ''); ?>">
        <?php if (!empty($errors['due_date'])): ?>
            <p class="error"><?php echo htmlspecialchars($errors['due_date']); ?></p>
        <?php endif; ?>
    </div>
    <div>
        <label for="category_id">Категория:</label>
        <select id="category_id" name="category_id">
            <option value="">-- Выберите категорию --</option>
            <?php
            $categories = getAllCategories($pdo);
            foreach ($categories as $category):
            ?>
                <option value="<?php echo $category['id']; ?>" <?php echo ($post['category_id'] ?? '') == $category['id'] ? 'selected' : ''; ?>>
                    <?php echo htmlspecialchars($category['name']); ?>
                </option>
            <?php endforeach; ?>
        </select>
    </div>
    <div>
        <label for="tags">Теги:</label>
        <select id="tags" name="tags[]" multiple>
            <option value="работа" <?php echo in_array('работа', $post['tags'] ?? []) ? 'selected' : ''; ?>>Работа</option>
            <option value="личное" <?php echo in_array('личное', $post['tags'] ?? []) ? 'selected' : ''; ?>>Личное</option>
            <option value="учеба" <?php echo in_array('учеба', $post['tags'] ?? []) ? 'selected' : ''; ?>>Учеба</option>
            <option value="срочно" <?php echo in_array('срочно', $post['tags'] ?? []) ? 'selected' : ''; ?>>Срочно</option>
            <option value="важно" <?php echo in_array('важно', $post['tags'] ?? []) ? 'selected' : ''; ?>>Важно</option>
        </select>
    </div>
    <div id="steps-container">
        <label>Шаги для выполнения:</label>
        <div id="steps-list">
            <?php if (!empty($post['steps']) && is_array($post['steps'])): ?>
                <?php foreach ($post['steps'] as $step): ?>
                    <div class="step">
                        <span class="remove-btn">❌</span>
                        <input type="text" name="steps[]" value="<?php echo htmlspecialchars($step); ?>">
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