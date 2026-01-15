#!/bin/bash

# --- Аргументы ---
PATH_TO_CHECK=$1
THRESHOLD=${2:-80}   # если второй аргумент не передан, берём 80

# --- Проверка пути ---
if [ -z "$PATH_TO_CHECK" ]; then
    echo "Ошибка: не указан путь."
    exit 2
fi

if [ ! -d "$PATH_TO_CHECK" ]; then
    echo "Ошибка: путь '$PATH_TO_CHECK' не существует."
    exit 2
fi

# --- Получение процента использования ---
USAGE=$(df -h "$PATH_TO_CHECK" 2>/dev/null | awk 'NR==2 {gsub("%","",$5); print $5}')

if [ -z "$USAGE" ]; then
    echo "Ошибка: не удалось получить данные о диске."
    exit 2
fi

# --- Вывод информации ---
DATE=$(date +"%Y-%m-%d %H:%M:%S")
echo "$DATE"
echo "Путь: $PATH_TO_CHECK"
echo "Использовано: ${USAGE}%"

# --- Проверка порога ---
if [ "$USAGE" -lt "$THRESHOLD" ]; then
    echo "Статус: OK"
    exit 0
else
    echo "Статус: WARNING: диск почти заполнен!"
    exit 1
fi
