<?php
// {/**
//  * Конфигурация подключения к базе данных
//  *
//  * @ ###### array Параметры подключения
//  */
// return [
//     'dsn' => 'sqlite:' . __DIR__ . '/../storage/todo_list.db',
//     'options' => [
//         PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
//         PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
//     ]
// ];}

/**
 * Конфигурация подключения к базе данных MySQL
 *
 * @return array Параметры подключения
 */
return [
    'dsn' => 'mysql:host=mysql;dbname=todo;charset=utf8mb4', // host=mysql — это имя сервиса из docker-compose.yml
    'username' => 'user',      
    'password' => 'password', 
    'options' => [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]
];
