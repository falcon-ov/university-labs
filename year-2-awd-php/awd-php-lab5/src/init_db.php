<?php
/**
 * Инициализация базы данных MySQL
 */

try {
    $config = require __DIR__ . '/../config/db.php';
    $pdo = new PDO(
        $config['dsn'],
        $config['username'],
        $config['password'],
        $config['options']
    );

    // Создание таблиц
    $pdo->exec("
        CREATE TABLE IF NOT EXISTS categories (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS tasks (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            description TEXT,
            priority VARCHAR(50) NOT NULL,
            due_date DATE,
            category_id INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
        );

        CREATE TABLE IF NOT EXISTS steps (
            id INT AUTO_INCREMENT PRIMARY KEY,
            task_id INT NOT NULL,
            step_text TEXT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS tags (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS task_tags (
            task_id INT,
            tag_id INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (task_id, tag_id),
            FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
            FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
        );
    ");

    // Добавление тестовых категорий
    $pdo->exec("INSERT IGNORE INTO categories (name) VALUES ('Работа'), ('Личное'), ('Учеба')");

    echo "База данных успешно инициализирована.\n";
} catch (PDOException $e) {
    echo "Ошибка: " . $e->getMessage();
}
