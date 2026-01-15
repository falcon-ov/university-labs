# Лабораторная работа №3. Мониторинг ИТ-инфраструктуры с использованием Zabbix
Цель работы:
Ознакомление студентов с системой Zabbix для мониторинга серверов, сервисов и сетевого оборудования.

### Задачи:

- Установить и настроить сервер Zabbix.
- Установить агент Zabbix на контролируемую машину.
- Добавить и настроить хост в системе.
- Просмотреть собираемые метрики и создать оповещения.

### Необходимое оборудование и программное обеспечение:
- Две виртуальные машины (например, Ubuntu Server 22.04 - в моем случае Debian 12)
- Доступ в Интернет
- Zabbix Server + MySQL(MariaDB) + Apache/Nginx
- Zabbix Agent

## Ход работы:
0. В качестве среды выполнение лабораторной работы я выбрал Google Cloud
в котором создал два инстанса: 
    - instance-lab - e2-medium(Debian 12) для Zabbix Server + (MariaDB)MySQL + Apache/Nginx,
    - instance-lab-small - e2-small(Debian 12) для Zabbix Agent
    ![img](/images/img_1.png)

1. Установка и настройка сервера Zabbix.
    - Добавил репозиторий Zabbix
    ```bash
    wget https://repo.zabbix.com/zabbix/6.4/debian/pool/main/z/zabbix-release/zabbix-release_6.4-1+debian12_all.deb
    ```
    ![img](/images/img_2.png)
    - Установил Zabbix Server + Frontend + Agent + MariaDB:
    ```bash
    sudo apt install zabbix-server-mysql zabbix-frontend-php zabbix-apache-conf zabbix-agent mariadb-server -y
    ```

2. Настройка базы данных и веб-интерфейса.

    - Зашел в MariaDB
    ![img](/images/img_3.png)

    - Создал базу и пользователя для Zabbix(пароль password):
    ![img](/images/img_4.png)

    - Импортировал начальные таблицы Zabbix, сначала скачал sql скрипты
        ```
        cd /tmp

        wget https://repo.zabbix.com/zabbix/6.4/debian/pool/main/z/zabbix/zabbix-sql-scripts_6.4.18-1+debian12_all.deb

        dpkg -x zabbix-sql-scripts_6.4.18-1+debian12_all.deb ./zabbix-sql

        zcat ./zabbix-sql/usr/share/zabbix-sql-scripts/mysql/server.sql.gz | mysql -uzabbix -ppassword zabbix

        mysql -uzabbix -ppassword zabbix -e "SHOW TABLES;"
        ```

    В архиве create.sql.gz лежат таблицы, индексы и базовые данные, которые нужны Zabbix для работы:

    - Открыл конфигурационный файл `sudo nano /etc/zabbix/zabbix_server.conf`, нашел строку `# DBPassword=` и заменил на  пароль пользователя базы, который я создал `DBPassword=password`, также разкоментировал `# DBHost=localhost`: \
    Сохранил файл (Ctrl+O, Enter) и закрыл (Ctrl+X).
    ![img](/images/img_6.png)
    ![img](/images/img_7.png)

    - Перезапустил Zabbix server и проверил работу -> работает
    ```
    sudo systemctl daemon-reload
    sudo systemctl restart zabbix-server
    sudo systemctl status zabbix-server
    ```
    ![img](/images/img_8.png)

    - Зашел через Google Cloud Shell на `localhost/zabbix` -
    `gcloud compute ssh instance-lab --zone=europe-north2-b -- -L 8080:localhost:80` - SSH-подключение к виртуальной машине и одновременно пробрасывание порта (port forwarding)
    ![img](/images/img_11.png)

    - Настройка веб-интерфейса Zabbix
    ![img](/images/img_12.png)
    ![img](/images/img_13.png)
    ![img](/images/img_14.png)
    ![img](/images/img_15.png)
    ![img](/images/img_16.png)
    ![img](/images/img_17.png)\
    user: Admin password: zabbix \
    ![img](/images/img_18.png)

3. Установка Zabbix Agent на другой машине.

    - Подключился через Сloud Shell ко второму инстансу
    ![img](/images/img_19.png)

    - Добавил репозиторий Zabbix
    ```bash
    wget https://repo.zabbix.com/zabbix/6.4/debian/pool/main/z/zabbix-release/zabbix-release_6.4-1+debian12_all.deb

    sudo dpkg -i zabbix-release_6.4-1+debian12_all.deb
    
    sudo apt update
    ```

    - Устанавил агента
    ```bash
    sudo apt install zabbix-agent -y
    ```
    ![img](/images/img_20.png)

    - Настройка агента
    ```bash
    sudo nano /etc/zabbix/zabbix_agentd.conf
    ```
    
    Нашел и изменил данные строки:
    ```
    Server=10.226.0.2       # IP сервера Zabbix (instance-lab)
    ServerActive=10.226.0.2
    Hostname=instance-lab-small
    ```
    ![img](/images/img_21.png)
    ![img](/images/img_22.png)
    ![img](/images/img_23.png)

    - Перезапустил агента
    ```bash
    sudo systemctl restart zabbix-agent
    sudo systemctl enable zabbix-agent
    sudo systemctl status zabbix-agent
    ```

    ![img](/images/img_24.png)


4. Добавление хоста в веб-интерфейсе Zabbix.

    ![img](/images/img_25.png)
    ![img](/images/img_26.png)
    ![img](/images/img_27.png)

5. Проверка сбора данных (CPU, RAM, сеть).

    ![img](/images/img_28.png)

6. Настройка оповещения (триггер + действие).
    (триггер высокая загрузка CPU)

Data collection -> Hosts -> instance-lab-small -> Triggers -> Create trigger
    ![img](/images/img_30.png)
    ![img](/images/img_31.png)
    ![img](/images/img_32.png)

6. Создание действия
Alerts -> Actions -> Trigger actions -> create action

    
    ![img](/images/img_33.png)
    ![img](/images/img_34.png)
    ![img](/images/img_35.png)
    ![img](/images/img_36.png)

    Когда триггер CPU сработает ->
    Zabbix отправит уведомление внутри веб-интерфейса, которое можно посмотреть 
    Reports -> Notifications или
    иконка колокольчика справа вверху.

7. Создание персональной панели мониторинга (dashboard).

    Monitoring -> Dashboards -> справа сверху Create dashboard

    ![img](/images/img_37.png)

    CPU Load
    → Add widget
    Тип: Graph
    Data set → Add

    ![img](/images/img_38.png)

    CPU Utilization (%)

    ![img](/images/img_39.png)

    Memory usage

    ![img](/images/img_40.png)

    Network traffic

    ![img](/images/img_41.png)
    
    Disk space

    ![img](/images/img_42.png)

    Problems / Alerts

    ![img](/images/img_43.png)

    System info block(Host availability)

    ![img](/images/img_44.png)

## Контрольные вопросы:
1. Какова роль основных компонентов Zabbix (Server, Agent, Proxy, Database, Frontend)?

    Zabbix Server – центральный компонент, собирает данные с агентов, хранит события и обрабатывает триггеры.
    Zabbix Agent – устанавливается на контролируемые машины, собирает метрики (CPU, RAM, диск, сеть) и отправляет их на сервер.
    Zabbix Proxy – опциональный компонент, собирает данные с агентов в удалённых сетях и передаёт их серверу (используется для масштабирования и экономии трафика).
    Database – хранит всю информацию о хостах, метриках, триггерах, событиях и конфигурации.
    Frontend – веб-интерфейс, через который администратор управляет системой, просматривает метрики, настраивает триггеры и оповещения.

2. Как осуществляется связь между Zabbix Server и Zabbix Agent?

    Passive mode: агент ждёт запросов от сервера и отправляет данные по запросу.
    Active mode: агент сам периодически отправляет данные на сервер.
    Связь идёт по порту 10050/10051 (TCP), сервер опрашивает агента или принимает данные от него.

3. Что такое «items» и «triggers» в Zabbix?

    Items – отдельные метрики или показатели, которые собирает агент (например, загрузка CPU, использование памяти, свободное место на диске).

    Triggers – правила или условия, которые проверяют значения items и создают события при нарушении порога (например, если CPU > 2 → триггер срабатывает).