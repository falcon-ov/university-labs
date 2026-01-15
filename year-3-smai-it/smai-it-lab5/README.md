# Лабораторная работа №5: Использование
Grafana для визуализации данных из Zabbix
# Цель работы
Ознакомление студентов с платформой Grafana, используемой для визуализации данных,
собираемых системой Zabbix. Студенты изучат установку Grafana, настройку плагина Zabbix и
создание интерактивных dashboards для анализа метрик мониторинга.
# Задачи
1. Установить и запустить сервис Grafana.
2. Установить и активировать официальный плагин Zabbix для Grafana.
3. Настроить подключение между Grafana и Zabbix API.
4. Создать персональный dashboard с панелями, использующими данные из Zabbix.
5. Работать с переменными, фильтрацией и различными видами визуализации.
6. Создать правило оповещения (alert) в Grafana на основе данных Zabbix.
# Необходимые ресурсы
- Сервер Linux (Ubuntu/Debian) с доступом по SSH.
- Установленный и работающий сервер Zabbix (Zabbix Server + Zabbix Agent).
- URL/API-адрес сервера Zabbix и пользователь с правами чтения.
- Веб-браузер.
# Практические задания
0. Создал репозиторий на Github и создал Codespace в Github для этого репозитория.
    1. Создал файл конфигурации окружения `.devcontainer/devcontainer.json:`
    2. Создал docker-compose.yml
    3. Создайте скрипт запуска setup.sh
    4. Запустил окружение:
    ```bash
    bashchmod +x setup.sh
    docker-compose up -d
    ```
    ![img](/images/img_1.png)

1. Установка Grafana(через Docker)
2. Установка плагина Zabbix для Grafana(через Docker)
3. Настройка источника данных Zabbix

В веб-интерфейсе Grafana:
Configuration → Data sources → Add data source → Zabbix
![img](/images/img_2.png)
Указать:
- API URL:
http://<IP_ZABBIX>/api_jsonrpc.php
![img](/images/img_3.png)
- Данные авторизации пользователя Zabbix
- Test & Save
![img](/images/img_4.png)

4. Создание Dashboard в Grafana
Создайте новый dashboard и добавьте панели на основе данных Zabbix.
Рекомендуемые панели:
    1. CPU Load
        - Query Mode: Metrics
        - Group: Linux servers
        - Host: выбрать нужный host
        - Application: CPU
        - Item: Load average (1 min)
        ![img](/images/img_5.png)
    2. Использование памяти
        - Application: Memory
        - Item: Available memory / Used memory
        ![img](/images/img_6.png)
    3. Сетевой трафик
        - Application: Network interfaces
        - Items:
        - Incoming bandwidth
        - Outgoing bandwidth
        ![img](/images/img_7.png)
    4. Дисковое пространство
        - Application: Filesystem
        - Items:
        - Free disk space
        - Used disk space
        ![img](/images/img_8.png)
    5. Статус Zabbix Agent
        - Application: Zabbix agent
        - Item: Agent ping
        - Тип визуализации: Stat panel
        ![img](/images/img_9.png)
        ![img](/images/img_10.png)
5. Создание переменных
Переменные позволяют динамически выбирать хосты и группы.
Создание переменной «host»:
    1. Dashboard → Settings → Variables → Add variable
    2. Name: host
    3. Type: Query
    4. Data source: Zabbix
    5. Query:
    `group.get`
    6. Использование переменной в панелях:
    `$host`
    ![img](/images/img_11.png)
6. Создание оповещений (Alerts) в Grafana на основе данных Zabbix
    1. Выберите панель → Alert → Create Alert Rule
    2. Пример оповещения CPU:
        - Условие: CPU Load > 1.5 в течение 5 минут 
    3. Выберите канал уведомлений (email, Telegram, Slack).(выбрал)
    4. Сохраните и протестируйте alert.
    ![img](/images/img_12.png)
    ![img](/images/img_13.png)
# Задания для студента
Студент должен:
1. Установить Grafana на предоставленный сервер. V
2. Установить плагин Zabbix для Grafana. V
3. Настроить источник данных Zabbix через API. V
4. Создать dashboard минимум с 5 панелями, включая: V
    - 1 график с CPU Load V
    - 1 график с RAM usage V
    - 1 график с сетевым трафиком V
    - 1 таблицу с системной информацией V
    - 1 Stat panel для Agent ping V
5. Создать переменную для фильтрации по host. V
6. Настроить alert для CPU, памяти или диска. V
7. Экспортировать dashboard в формате JSON и приложить к отчёту. V
![img](/images/img_14.png)
# Контрольные вопросы

1. Для чего необходим плагин Zabbix в Grafana?
2. Какую роль играет Zabbix API при настройке источника данных?
3. Чем отличается «Graph panel» от «Stat panel»?
4. Что такое переменная dashboard и как она используется?
5. Как настраивается alert на основе данных Zabbix?

Ответы:
1. Плагин нужен, чтобы Grafana могла подключаться к Zabbix и брать метрики для графиков.
2. Zabbix API дает доступ к данным сервера, через него Grafana получает метрики и хосты.
3. Graph panel показывает графики по времени, а Stat panel выводит одно числовое значение.
4. Переменная dashboard это параметр, который позволяет менять данные на панели без редактирования.
5. Настройка alert делается в панели, выбираем метрику из Zabbix, задаем условие и настраиваем уведомления.