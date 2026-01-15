# Лабораторная работа №6. Балансирование нагрузки в облаке и авто-масштабирование

## Цель работы

Закрепить навыки работы с AWS EC2, Elastic Load Balancer, Auto Scaling и CloudWatch, создав отказоустойчивую и автоматически масштабируемую архитектуру.

Я развернул:

- VPC с публичными и приватными подсетями;
- Виртуальную машину с веб-сервером (nginx);
- Application Load Balancer;
- Auto Scaling Group (на основе AMI);
- нагрузочный тест с использованием CloudWatch.

## Выполнение работы

### Шаг 1. Создание VPC и подсетей 

1. Создал VPC
![img](/images/img_1.png)

2. Создал _2 публичные подсети_ и _2 приватные подсети_ в _разных зонах доступности_ (`us-east-1a` и `us-east-1b`):
   - CIDR-блок: `10.0.1.0/24` и `10.0.2.0/24`
![img](/images/img_2.png)

3. Создал Internet Gateway и прикрепил его к VPC.
![img](/images/img_3.png)

4. В Route Table прописал маршрут для публичных подсетей:
   - Destination: `0.0.0.0/0` → Target: Internet Gateway
![img](/images/img_4.png)

### Шаг 2. Создание и настройка виртуальной машины

1. Запустил виртуальную машину в созданной подсети:
   - AMI: `Amazon Linux 2`
   - Тип: `t3.micro`
   - В настройках сети выбрал созданную VPC и подсеть
   - Назначил публичный IP-адрес (Enable auto-assign public IP)
   - В настройках безопасности создал новую группу безопасности с правилами:
     - Входящие: SSH (порт 22) — мой IP, HTTP (порт 80) — 0.0.0.0/0
     - Исходящие: Все трафики — 0.0.0.0/0
   - В `Advanced Details` -> `Detailed CloudWatch monitoring` выбрал `Enable`
   - В настройках `UserData` указал скрипт init.sh

![img](/images/img_5.png)

2. Дождался, пока `Status Checks` виртуальной машины стали зелёными (`3/3 checks passed`).

3. Убедился, что веб-сервер работает, подключившись к публичному IP-адресу через браузер.
![img](/images/img_6.png)

### Шаг 3. Создание AMI

**Что такое image и чем он отличается от snapshot?**
Image (AMI) — это полный образ виртуальной машины, включающий операционную систему, приложения и конфигурацию. Snapshot — это точечная копия только диска (volume) в определённый момент времени. AMI использует snapshots как основу, но также содержит метаданные о конфигурации инстанса.

**Какие есть варианты использования AMI?**
- Создание идентичных инстансов с предустановленным ПО
- Резервное копирование конфигураций
- Масштабирование приложений через Auto Scaling
- Распространение настроенных окружений между регионами

1. В EC2 выбрал `Instance` → `Actions` → `Image and templates` → `Create image`.
2. Назвал AMI: `project-web-server-ami`.
3. Дождался появления AMI в разделе AMIs.

![img](/images/img_7.png)
![img](/images/img_8.png)

### Шаг 4. Создание Launch Template

**Что такое Launch Template и зачем он нужен?**
Launch Template — это шаблон для запуска EC2 инстансов, содержащий все параметры конфигурации (AMI, тип инстанса, security groups и т.д.). Он упрощает создание новых инстансов и необходим для Auto Scaling Groups.

**Чем он отличается от Launch Configuration?**
Launch Template — более новая и функциональная версия. Поддерживает версионирование, больше параметров (например, spot instances), может использоваться напрямую для запуска инстансов. Launch Configuration — устаревший вариант без версионирования.

1. В разделе EC2 выбрал `Launch Templates` → `Create launch template`.
2. Указал параметры:
   - Название: `project-launch-template`
   - AMI: `project-web-server-ami`
   - Тип инстанса: `t3.micro`
   - Security groups: та же группа, что и для VM
   - В `Advanced details` -> `Detailed CloudWatch monitoring` выбрал `Enable`
3. Нажал `Create launch template`.

![img](/images/img_9.png)

### Шаг 5. Создание Target Group

**Зачем необходим и какую роль выполняет Target Group?**
Target Group — это группа ресурсов (EC2 инстансов), на которые Load Balancer распределяет входящий трафик. Он проверяет работоспособность инстансов (health checks) и направляет запросы только на здоровые цели.

1. В разделе EC2 выбрал `Target Groups` → `Create target group`.
2. Указал параметры:
   - Название: `project-target-group`
   - Тип: `Instances`
   - Протокол: `HTTP`
   - Порт: `80`
   - VPC: созданная VPC
3. Нажал `Next` -> `Next`, затем `Create target group`.

![img](/images/img_10.png)

### Шаг 6. Создание Application Load Balancer

**В чем разница между Internet-facing и Internal?**
Internet-facing — балансировщик доступен из интернета, имеет публичный IP. Internal — балансировщик доступен только внутри VPC, используется для внутренней балансировки между сервисами.

**Что такое Default action и какие есть типы Default action?**
Default action — действие, выполняемое когда запрос не соответствует ни одному правилу маршрутизации. Типы: forward (перенаправление в target group), redirect (HTTP редирект), fixed-response (статичный ответ), authenticate (аутентификация через Cognito/OIDC).

1. В разделе EC2 выбрал `Load Balancers` → `Create Load Balancer` → `Application Load Balancer`.
2. Указал параметры:
   - Название: `project-alb`
   - Scheme: `Internet-facing`
   - Subnets: две публичные подсети
   - Security Groups: та же группа безопасности
   - Listener: протокол `HTTP`, порт `80`
   - Default action: `project-target-group`
3. Нажал `Create load balancer`.

![img](/images/img_11.png)

3. Перешёл в раздел `Resource map` и убедился в существовании связей между `Listeners`, `Rules` и `Target groups`.

![img](/images/img_12.png)

### Шаг 7. Создание Auto Scaling Group

**Почему для Auto Scaling Group выбираются приватные подсети?**
Инстансы в приватных подсетях не имеют прямого доступа из интернета, что повышает безопасность. Доступ к ним осуществляется через Load Balancer, который находится в публичных подсетях. Это классическая архитектура с разделением уровней доступа.

**Зачем нужна настройка: Availability Zone distribution?**
Эта настройка определяет стратегию распределения инстансов по зонам доступности. Balanced best effort обеспечивает равномерное распределение для высокой доступности — если одна зона выйдет из строя, сервис продолжит работать в других зонах.

**Что такое Instance warm-up period и зачем он нужен?**
Это время, необходимое инстансу для полной инициализации и начала обработки запросов. В течение этого периода метрики инстанса не учитываются при принятии решений о масштабировании, что предотвращает преждевременное создание новых инстансов.

1. В разделе EC2 выбрал `Auto Scaling Groups` → `Create Auto Scaling group`.
2. Указал параметры:
   - Название: `project-auto-scaling-group`
   - Launch template: `project-launch-template`
   - Network: созданная VPC и две приватные подсети
   - Availability Zone distribution: `Balanced best effort`
   - Attach to an existing load balancer: `project-target-group`
   - Group size: min `2`, max `4`, desired `2`
   - Target tracking scaling policy: CPU `50%`, warm-up `60 seconds`
   - Enable group metrics collection within CloudWatch
3. Нажал `Create Auto Scaling group`.

![img](/images/img_13.png)

### Шаг 8. Тестирование Application Load Balancer

**Какие IP-адреса вы видите и почему?**
Видны приватные IP-адреса инстансов из Auto Scaling Group (например, 10.0.x.x). Load Balancer распределяет запросы между инстансами, поэтому при обновлении страницы IP меняется, показывая разные инстансы за балансировщиком.

1. Перешёл в раздел EC2 -> `Load Balancers`, выбрал созданный Load Balancer и скопировал его DNS-имя.
2. Вставил DNS-имя в браузер и убедился, что вижу страницу веб-сервера.
3. Обновил страницу несколько раз и посмотрел на IP-адреса в ответах.

![img](/images/img_14.png)

Модифицировал index.html для отображения IP-адреса:

```bash
sudo nano /usr/share/nginx/html/index.html
# добавил:
Hello from EC2 instance with IP: 
```

![img](/images/img_15.png)
![img](/images/img_16.png)

### Шаг 9. Тестирование Auto Scaling

**Какую роль в этом процессе сыграл Auto Scaling?**
Auto Scaling отслеживал метрики CPU через CloudWatch и при превышении порога 50% автоматически запустил дополнительные инстансы (до максимума 4) для распределения нагрузки. Это обеспечило масштабирование приложения без ручного вмешательства.

1. Перешёл в CloudWatch -> `Alarms`, где увидел автоматические оповещения для Auto Scaling Group.
2. Выбрал оповещение `TargetTracking-XX-AlarmHigh-...` и посмотрел на график CPU Utilization (около 0-1%).
3. Открыл 6-7 вкладок браузера с адресом:
   ```
   http://<DNS-имя Load Balancer-а>/load?seconds=60
   ```
4. Вернулся в CloudWatch и увидел рост нагрузки на графике CPU Utilization.
5. Подождал 2-3 минуты, пока CloudWatch зафиксировал высокую нагрузку и создал `Alarm` (красный цвет).

![img](/images/img_17.png)

6. Перешёл в раздел `EC2` -> `Instances` и увидел увеличение количества запущенных инстансов.

![img](/images/img_18.png)

### Шаг 10. Завершение работы и очистка ресурсов

1. Остановил нагрузочный тест (закрыл вкладки браузера).
2. Удалил Load Balancer (`EC2` -> `Load Balancers` -> `Delete`).
3. Удалил Target Group (`EC2` -> `Target Groups` -> `Delete`).
4. Удалил Auto Scaling Group (`EC2` -> `Auto Scaling Groups` -> `Delete`).
5. Завершил все запущенные инстансы (`EC2` -> `Instances` -> `Terminate`).
6. Удалил AMI (`EC2` -> `AMIs` -> `Deregister`) с удалением связанных snapshots.
7. Удалил Launch Template (`EC2` -> `Launch Templates` -> `Delete`).
8. Удалил VPC и подсети (раздел `VPC`).
