<?php
/**
 * Подключение к базе данных SQLite
 *
 * @return PDO Экземпляр PDO
 * @throws PDOException
 */
function getDbConnection() {
    $config = require __DIR__ . '/../config/db.php';
    return new PDO($config['dsn'], $config['username'], $config['password'], $config['options']);
}
