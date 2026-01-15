# IWNO6: Взаимодействие контейнеров

## Цель работы
Выполнив данную работу студент сможет управлять взаимодействием нескольких контейнеров.

## Задание
Создать php приложение на базе двух контейнеров: nginx, php-fpm.

## Подготовка
Для выполнения данной работы необходимо иметь установленный на компьютере Docker.

Для выполнения работы необходимо иметь опыт выполнения лабораторной работы №3.

## Выполнение
☑ Создал репозиторий containers06 и скопировал его себе на компьютер.

В директории containers06 создал директорию mounts/site. 

![image](/images/image_01.png)

В данную директорию переписал сайт на php, созданный в рамках предмета по php.
Я выбрал первую часть второй лабораторной по php.

![image](/images/image_02.png)

Создал файл .gitignore в корне проекта и добавил в него строки:
```
# Ignore files and directories
mounts/site/*
```

![image](/images/image_03.png)

Создал в директории *containers06 файл nginx/default.conf со следующим содержимым:

![image](/images/image_04.png)

## Запуск и тестирование
Создал сеть internal для контейнеров.
```bash
docker network create internal
```

![image](/images/image_05.png)

Создал контейнер backend со следующими свойствами:
```
на базе образа php:7.4-fpm;
к контейнеру примонтирована директория mounts/site в /var/www/html;
работает в сети internal.
```

```bash
docker run -d --name backend --network internal -v D:/git/cv-labs/containers06/mounts/site:/var/www/html php:7.4-fpm
```

![image](/images/image_06.png)

Создал контейнер frontend со следующими свойствами:

```
на базе образа nginx:1.23-alpine;
с примонтированной директорией mounts/site в /var/www/html;
с примонтированным файлом nginx/default.conf в /etc/nginx/conf.d/default.conf;
порт 80 контейнера проброшен на порт 80 хоста;
работает в сети internal.
```

```bash
docker run -d --name frontend --network internal -p 80:80 -v D:/git/cv-labs/containers06/mounts/site:/var/www/html -v D:/git/cv-labs/containers06/nginx/default.conf:/etc/nginx/conf.d/default.conf nginx:1.23-alpine
```

![image](/images/image_07.png)

Проверил работу сайта в браузере, перейдя по адресу http://localhost.

![image](/images/image_08.png)

## Ответы на вопросы:

1. **Каким образом контейнеры взаимодействуют?**  
   Контейнеры `frontend` (nginx) и `backend` (php-fpm) общаются через сеть `internal`, где nginx перенаправляет PHP-запросы на php-fpm по имени контейнера.

2. **Как видят друг друга в сети internal?**  
   В сети `internal` контейнеры видят друг друга по именам (`frontend`, `backend`), которые Docker автоматически разрешает в IP-адреса.

3. **Почему переопределяли конфигурацию nginx?**  
   Стандартная конфигурация nginx не поддерживает PHP и php-fpm, поэтому `default.conf` настроили для обработки PHP-файлов и указания пути к сайту.

## Вывод:  
Создано PHP-приложение на двух контейнерах с использованием сети `internal`. Настройка nginx и Docker обеспечила их взаимодействие и работу сайта.