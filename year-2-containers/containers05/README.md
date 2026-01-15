# Лабораторная работа №5: Запуск сайта в контейнере

## Цель работы
Выполнив данную работу студент сможет подготовить образ контейнера для запуска веб-сайта на базе Apache HTTP Server + PHP (mod_php) + MariaDB.

## Задание
Создать Dockerfile для сборки образа контейнера, который будет содержать веб-сайт на базе Apache HTTP Server + PHP (mod_php) + MariaDB. База данных MariaDB должна храниться в монтируемом томе. Сервер должен быть доступен по порту 8000.

Установить сайт WordPress. Проверить работоспособность сайта.

# Подготовка
☑ Для выполнения данной работы необходимо иметь установленный на компьютере Docker.

☑ Для выполнения работы необходимо иметь опыт выполнения лабораторной работы №3.

# Выполнение
Открыл VSCode, открыл папку с лабораторными, открыл терминал.
Создал репозиторий containers05 на сайте GitHub и скопировал его себе на компьютер.
```bash
git clone https://github.com/falcon-ov/containers05
```

# Извлечение конфигурационных файлов apache2, php, mariadb из контейнера
Создал в папке containers05 папку files, а также

папку files/apache2 - для файлов конфигурации apache2;
папку files/php - для файлов конфигурации php;
папку files/mariadb - для файлов конфигурации mariadb.

![image](/images/image_1.png)

Создал в папке containers05 файл Dockerfile со следующим содержимым представленном на скриншоте.
И построил образ контейнера с именем apache2-php-mariadb с помощью следующей команды:
```bash
docker build -t apache2-php-mariadb .
```

![image](/images/image_2.png)

Создал контейнер apache2-php-mariadb из образа apache2-php-mariadb и запустил его в фоновом режиме с командой запуска bash.

![image](/images/image_3.png)

Скопировал из контейнера файлы конфигурации apache2, php, mariadb в папку files/ на компьютере. Для этого, в контексте проекта, выполнил команды:

![image](/images/image_4.png)

После выполнения команд в папке files/ должны появиться файлы конфигурации apache2, php, mariadb. Проверил их наличие. 

![image](/images/image_5.png)

Остановил и удалил контейнер apache2-php-mariadb одной командой.

`-f (force) принудительно останавливает и удаляет контейнер, даже если он запущен.`

![image](/images/image_6.png)

# Настройка конфигурационных файлов
# Конфигурационный файл apache2
Открыл файл files/apache2/000-default.conf, нашёл строку #ServerName www.example.com и заменил её на ServerName localhost.

![image](/images/image_7.png)

![image](/images/image_8.png)

Нашёл строку ServerAdmin webmaster@localhost и заменил в ней почтовый адрес на свой.

![image](/images/image_9.png)

![image](/images/image_10.png)

После строки DocumentRoot /var/www/html добавил следующие строки:

```bash
DirectoryIndex index.php index.html
```

![image](/images/image_11.png)

Сохранил файл и закрыл.

В конце файла files/apache2/apache2.conf добавил следующую строку:

```bash
ServerName localhost
```

![image](/images/image_12.png)

# Конфигурационный файл php

Открыл файл files/php/php.ini, нашёл строку ;error_log = php_errors.log и заменил её на error_log = /var/log/php_errors.log.

![image](/images/image_13.png)

![image](/images/image_14.png)

Настроил параметры memory_limit, upload_max_filesize, post_max_size и max_execution_time следующим образом:

memory_limit = 128M
upload_max_filesize = 128M
post_max_size = 128M
max_execution_time = 120

![image](/images/image_15.png)

![image](/images/image_16.png)

![image](/images/image_17.png)

![image](/images/image_18.png)

Сохранил файл и закрыл.

# Конфигурационный файл mariadb

Открыл файл files/mariadb/50-server.cnf, нашёл строку #log_error = /var/log/mysql/error.log и раскомментировал её.

![image](/images/image_19.png)

![image](/images/image_20.png)

Сохранил файл и закрыл.

# Создание скрипта запуска

Создал в папке files папку supervisor и файл supervisord.conf со следующим содержимым:

![image](/images/image_21.png)

# Создание Dockerfile

Открыл файл Dockerfile и добавил в него следующие строки:

![image](/images/image_22.png)

- Добавил монтирование томов VOLUME сразу после FROM
- В инструкцию RUN добавил установку пакета supervisor
- Добавил загрузку WordPress через ADD
- Добавил распаковку архива
- Добавил копирование всех конфигурационных файлов через COPY
- Добавил создание директории для сокета MySQL и установку прав
- Добавил открытие порта 80 через EXPOSE
- Добавил команду запуска supervisord через CMD

Собрал образ контейнера с именем apache2-php-mariadb
```bash
docker build -t apache2-php-mariadb .
```

и запустил контейнер apache2-php-mariadb из образа apache2-php-mariadb.
```bash
docker run -d --name apache2-php-mariadb -p 8000:80 apache2-php-mariadb
```

![image](/images/image_23.png)

Подключился к контейнеру
```bash
docker exec -it apache2-php-mariadb bash
```

Проверил наличие сайта WordPress в папке /var/www/html/.

![image](/images/image_24.png)

Проверил изменения конфигурационного файла apache2, файл действительно изменился.
```bash
cat /etc/apache2/sites-available/000-default.conf
```

![image](/images/image_25.png)

Проверил и apach2.conf с помощью следующей команды, где также заметил изменения.
```bash
cat /etc/apache2/apache2.conf
```

# Создал базу данных wordpress и пользователя wordpress с паролем wordpress в контейнере apache2-php-mariadb.

Подключаюсь к MariaDB

```bash
mysql
```

И выполняю команды построчно:

```sql
CREATE DATABASE wordpress;
CREATE USER 'wordpress'@'localhost' IDENTIFIED BY 'wordpress';
GRANT ALL PRIVILEGES ON wordpress.* TO 'wordpress'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

![image](/images/image_26.png)

# Создание файла конфигурации WordPress.  
Открыл в браузере сайт WordPress по адресу http://localhost/8000. Указал параметры подключения к базе данных:

![image](/images/image_28.png)

имя базы данных: wordpress;  
имя пользователя: wordpress;  
пароль: wordpress;  
адрес сервера базы данных: localhost;  
префикс таблиц: wp_.  
Скопировал содержимое файла конфигурации в файл files/wp-config.php на компьютере.

![image](/images/image_29.png)

![image](/images/image_30.png)

# Добавление файла конфигурации WordPress в Dockerfile
Добавьте в файл Dockerfile следующие строки:

```bash
# copy the configuration file for wordpress from files/ directory
COPY files/wp-config.php /var/www/html/wordpress/wp-config.php
```

![image](/images/image_31.png)

# Запуск и тестирование
Пересобрал образ контейнера с именем apache2-php-mariadb.
Удалил контенер.
```bash
docker rm apache2-php-mariadb
```
Создал образ.
```bash
docker build -t apache2-php-mariadb .
```

И запустил контейнер apache2-php-mariadb из образа apache2-php-mariadb

```bash
docker run -d --name apache2-php-mariadb -p 8000:80 apache2-php-mariadb
```

Проверил работоспособность сайта WordPress.

![image](/images/image_32.png)

Проанализировал структуру папок и пришел к выводу, что ошбику заключается в том,
что архив с wordpress я распаковал в папку html, следовательно и wp-config.php я должен также
скопировать в html.

Изменил строчку в dockerfile следующим образом.
```bash
COPY files/wp-config.php /var/www/html/wp-config.php
```

Также в dockerfile добавил код, который автоматически создает базу данных:
```bash
# initialize mariadb and create wordpress database
RUN /usr/sbin/mariadbd --user=mysql --datadir=/var/lib/mysql & \
    sleep 10 && \
    mysql -e "CREATE DATABASE wordpress;" && \
    mysql -e "CREATE USER 'wordpress'@'localhost' IDENTIFIED BY 'wordpress';" && \
    mysql -e "GRANT ALL PRIVILEGES ON wordpress.* TO 'wordpress'@'localhost';" && \
    mysql -e "FLUSH PRIVILEGES;" && \
    mysqladmin shutdown
```

![image](/images/image_33.png)

Все.

# Ответы на вопросы

1. **Какие файлы конфигурации были изменены?**  
   Изменены: `000-default.conf` и `apache2.conf` (Apache2), `php.ini` (PHP), `50-server.cnf` (MariaDB), а также создан `wp-config.php` (WordPress).

2. **За что отвечает инструкция DirectoryIndex в файле конфигурации Apache2?**  
   `DirectoryIndex` указывает, какие файлы Apache будет считать индексными (по умолчанию открывать) при обращении к директории, например, `index.php` или `index.html`.

3. **Зачем нужен файл wp-config.php?**  
   `wp-config.php` — это файл конфигурации WordPress, который содержит настройки подключения к базе данных (имя, пользователь, пароль, хост) и другие параметры сайта.

4. **За что отвечает параметр post_max_size в файле конфигурации PHP?**  
   `post_max_size` определяет максимальный размер данных, которые можно передать через HTTP-запрос методом POST, например, при загрузке файлов или отправке форм.

5. **Какие недостатки есть в созданном образе контейнера?**  
   - Все сервисы (Apache, PHP, MariaDB) запущены в одном контейнере, что противоречит принципу "один сервис — один контейнер".  
   - Отсутствует обработка ошибок при запуске сервисов через `supervisord`.

# Вывод:
  
В ходе выполнения лабораторной работы был создан и настроен образ контейнера на базе Docker, включающий Apache HTTP Server с модулем PHP, MariaDB и установленный WordPress. Были подготовлены и изменены конфигурационные файлы для Apache (`000-default.conf`, `apache2.conf`), PHP (`php.ini`) и MariaDB (`50-server.cnf`), а также создан файл `wp-config.php` для WordPress. Сайт успешно запущен и протестирован на порту 8000. Однако в процессе выявлены недостатки: нарушение принципа разделения сервисов, отсутствие персистентности данных и захардкоденные параметры, что указывает на необходимость дальнейшей оптимизации для реального использования. Работа позволила освоить базовые навыки создания и настройки контейнеров для веб-приложений.