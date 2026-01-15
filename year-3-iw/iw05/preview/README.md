# Лабораторная работа 04: Настройка Jenkins для автоматизации DevOps задач

## Описание проекта

В рамках данной лабораторной работы я настроил Jenkins Controller и SSH Agent для автоматизации процессов CI/CD. Был создан pipeline для автоматического тестирования PHP-проекта с использованием контейнеризации через Docker и Docker Compose.

Проект включает в себя:
- Контейнер Jenkins Controller для управления задачами
- SSH Agent для выполнения задач в изолированном окружении
- CI/CD pipeline для автоматической установки зависимостей и запуска тестов
- Интеграцию с GitHub репозиторием

## Шаг 1: Настройка Jenkins Controller

### 1.1. Создание структуры проекта

Создал папку `lab04` в корне GitHub репозитория:

```bash
mkdir lab04
cd lab04
```

![img](/images/img_1.png)

### 1.2. Создание docker-compose.yml

Создал файл `docker-compose.yml` со следующим содержимым:

```yaml
services:
  jenkins-controller:
    image: jenkins/jenkins:lts
    container_name: jenkins-controller
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
    networks:
      - jenkins-network

volumes:
  jenkins_home:
  jenkins_agent_volume:

networks:
  jenkins-network:
    driver: bridge
```

### 1.3. Запуск Jenkins Controller

Запустил контейнер Jenkins Controller:

```bash
docker-compose up -d
```

![img](/images/img_2.png)

![img](/images/img_3.png)

### 1.4. Первоначальная настройка Jenkins

1. Открыл браузер и перешел по адресу `http://localhost:8080`
![img](/images/img_4.png)

2. Получил начальный пароль администратора из логов контейнера:

```bash
docker logs jenkins-controller
```

![img](/images/img_5.png)

3. Скопировал пароль из секции "Jenkins initial setup is required"
4. Вставил пароль в веб-интерфейс Jenkins

![img](/images/img_6.png)

5. Выбрал опцию "Install suggested plugins" для установки рекомендуемых плагинов
6. Создал учетную запись администратора с необходимыми учетными данными
![img](/images/img_7.png)

7. Подтвердил URL Jenkins (оставил по умолчанию `http://localhost:8080/`)
![img](/images/img_8.png)

8. Завершил начальную настройку
![img](/images/img_9.png)

## Шаг 2: Настройка SSH Agent

### 2.1. Создание SSH ключей

Создал папку `secrets` для хранения SSH ключей:

```bash
mkdir secrets
cd secrets
ssh-keygen -f jenkins_agent_ssh_key -t rsa -b 4096 -N ""
cd ..
```
![img](/images/img_10.png)

![img](/images/img_11.png)

При генерации ключа оставил пароль пустым для автоматического подключения.

### 2.2. Создание Dockerfile для SSH Agent

Создал файл `Dockerfile` в корне проекта:

```dockerfile
FROM jenkins/ssh-agent

# install PHP-CLI
RUN apt-get update && apt-get install -y php-cli
```
![img](/images/img_12.png)

Этот Dockerfile устанавливает PHP-CLI в контейнер SSH Agent для возможности запуска PHP-скриптов и тестов.

### 2.3. Добавление SSH Agent в docker-compose.yml

Обновил файл `docker-compose.yml`, добавив сервис SSH Agent:

```yaml
services:
  jenkins-controller:
    image: jenkins/jenkins:lts
    container_name: jenkins-controller
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
    networks:
      - jenkins-network

  ssh-agent:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: ssh-agent
    environment:
      - JENKINS_AGENT_SSH_PUBKEY=${JENKINS_AGENT_SSH_PUBKEY}
    volumes:
      - jenkins_agent_volume:/home/jenkins/agent
    depends_on:
      - jenkins-controller
    networks:
      - jenkins-network

volumes:
  jenkins_home:
  jenkins_agent_volume:

networks:
  jenkins-network:
    driver: bridge
```

### 2.4. Создание .env файла

Создал файл `.env` в корне проекта и добавил публичный SSH ключ:

```bash
type secrets\jenkins_agent_ssh_key.pub
```

Скопировал содержимое публичного ключа и добавил в `.env`:

```env
JENKINS_AGENT_SSH_PUBKEY=ssh-rsa AAAAB3NzaC1yc2E...
```
![img](/images/img_13.png)

### 2.5. Перезапуск Docker Compose

Пересобрал и перезапустил все сервисы:

```bash
docker-compose down
docker-compose up -d --build
```
![img](/images/img_14.png)

## Шаг 3: Подключение SSH Agent к Jenkins

### 3.1. Установка SSH Agents Plugin

1. Открыл Jenkins веб-интерфейс `http://localhost:8080`
2. Перешел в **Manage Jenkins** → **Manage Plugins**
3. Во вкладке **Available** нашел "SSH Build Agents plugin"
4. Установил плагин и перезапустил Jenkins
![img](/images/img_15.png)
![img](/images/img_16.png)

### 3.2. Регистрация SSH ключей в Jenkins

1. Перешел в **Manage Jenkins** → **Manage Credentials**
2. Выбрал **(global)** domain
3. Нажал **Add Credentials**
4. Заполнил форму:
   - **Kind**: SSH Username with private key
   - **ID**: jenkins-ssh-key
   - **Description**: SSH key for jenkins agent
   - **Username**: jenkins
   - **Private Key**: выбрал "Enter directly" и вставил содержимое файла `secrets/jenkins_agent_ssh_key`
5. Нажал **Create**

![img](/images/img_17.png)
![img](/images/img_18.png   )


### 3.3. Добавление нового Jenkins Agent Node

1. Перешел в **Manage Jenkins** → **Manage Nodes and Clouds**
2. Нажал **New Node**
3. Заполнил параметры:
   - **Node name**: ssh-agent1
   - **Type**: Permanent Agent
4. Нажал **Create**
5. Настроил параметры узла:
   - **Number of executors**: 2
   - **Remote root directory**: `/home/jenkins/agent`
   - **Labels**: `php-agent`
   - **Usage**: "Use this node as much as possible"
   - **Launch method**: "Launch agents via SSH"
   - **Host**: `ssh-agent`
   - **Credentials**: выбрал созданные ранее `jenkins-ssh-key`
   - **Host Key Verification Strategy**: "Non verifying Verification Strategy"
6. Нажал **Save**

![img](/images/img_19.png)
![img](/images/img_20.png)
![img](/images/img_21.png)

### 3.4. Проверка подключения

После сохранения конфигурации, Jenkins автоматически попытался подключиться к агенту. Проверил статус в списке узлов - агент `ssh-agent1` успешно подключился и готов к работе.
![img](/images/img_22.png)

## Шаг 4: Создание Jenkins Pipeline

### 4.1. Выбор PHP проекта

Для демонстрации выбрал PHP проект из предыдущих курсов, который содержит unit тесты на PHPUnit.
Точнее сгенерировал с помощью Claude Sonnet php проект для демонстрации.

### 4.2. Создание Jenkinsfile

Создал файл `Jenkinsfile` в корне проекта:

```groovy
pipeline {
    agent {
        label 'php-agent'
    }
    
    stages {        
        stage('Install Dependencies') {
            steps {
                echo 'Preparing project...'
                dir('php-project') { 
                    sh 'composer install --no-interaction --prefer-dist'
                }
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                dir('php-project') { 
                    sh './vendor/bin/phpunit'
                }
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo 'All stages completed successfully!'
        }
        failure {
            echo 'Errors detected in the pipeline.'
        }
    }
}
```

### 4.3. Создание Pipeline в Jenkins

1. В Jenkins перешел на главную страницу
2. Нажал **New Item**
3. Ввел имя: "PHP-Project-Pipeline"
4. Выбрал тип **Pipeline**
5. Нажал **OK**
![img](/images/img_23.png)

6. В разделе **Pipeline** настроил:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: указал URL моего GitHub репозитория
   - **Branch Specifier**: */main
   - **Script Path**: Jenkinsfile
7. Нажал **Save**

![img](/images/img_24.png)

8. Изменил Dockerfile на 
```Dockerfile
FROM jenkins/ssh-agent

# Install PHP-CLI and required dependencies
RUN apt-get update && apt-get install -y \
    php-cli \
    php-mbstring \
    php-xml \
    php-curl \
    unzip \
    wget \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install Composer
RUN wget https://getcomposer.org/installer -O composer-setup.php && \
    php composer-setup.php --install-dir=/usr/local/bin --filename=composer && \
    rm composer-setup.php

# Verify installations
RUN php --version && composer --version
```

### 4.4. Запуск Pipeline

1. Открыл созданный Pipeline
2. Нажал **Build Now**
3. Наблюдал выполнение в разделе **Build History**
4. Открыл **Console Output** для просмотра логов

![img](/images/img_25.png)

### 4.5. Результаты выполнения

Pipeline успешно выполнился:
- ✅ Stage "Install Dependencies": зависимости установлены через Composer
- ✅ Stage "Test": все unit тесты прошли успешно
- ✅ Post action "success": pipeline завершился без ошибок

## Ответы на вопросы

### Каковы преимущества использования Jenkins для автоматизации DevOps задач?

1. **Автоматизация процессов**: Jenkins автоматизирует рутинные задачи сборки, тестирования и развертывания, освобождая время разработчиков.

2. **Непрерывная интеграция (CI)**: Позволяет автоматически собирать и тестировать код при каждом коммите, выявляя ошибки на ранних этапах.

3. **Расширяемость**: Более 1800 плагинов позволяют интегрировать Jenkins практически с любыми инструментами разработки.

4. **Распределенные сборки**: Возможность использования агентов для распределения нагрузки и выполнения задач на разных платформах.

5. **Открытый исходный код**: Бесплатное решение с активным сообществом и регулярными обновлениями.

6. **Pipeline as Code**: Возможность описывать CI/CD процессы в виде кода (Jenkinsfile), который версионируется вместе с проектом.

7. **Визуализация процессов**: Наглядное отображение этапов сборки, истории выполнения и статистики.

### Какие еще типы Jenkins агентов существуют?

1. **Permanent Agents (Постоянные агенты)**: 
   - Статические агенты, которые всегда доступны
   - Используются для долгосрочных задач
   - Пример: SSH Agent, который я настроил в этой работе

2. **Cloud Agents (Облачные агенты)**:
   - Динамически создаются и удаляются по требованию
   - Amazon EC2, Azure VM, Google Cloud
   - Оптимизируют использование ресурсов

3. **Docker Agents**:
   - Агенты, работающие в Docker контейнерах
   - Создаются для каждой сборки и удаляются после завершения
   - Обеспечивают изолированную среду выполнения

4. **Kubernetes Agents**:
   - Агенты, запускаемые как поды в Kubernetes кластере
   - Масштабируются автоматически в зависимости от нагрузки

5. **JNLP Agents (Java Web Start)**:
   - Агенты, подключающиеся к мастеру через JNLP протокол
   - Полезны для агентов за NAT или файерволом

6. **Windows Agents**:
   - Специализированные агенты для Windows окружений
   - Используются для .NET проектов и Windows-специфичных задач

### Какие проблемы возникли при настройке Jenkins и как я их решил?

**Проблема 1: SSH Agent не подключался к Jenkins Controller**

*Решение*: Обнаружил, что забыл добавить публичный SSH ключ в переменную окружения `JENKINS_AGENT_SSH_PUBKEY` в файле `.env`. После добавления правильного значения и перезапуска контейнеров проблема была решена.

## Структура проекта

```
lab04/
├── docker-compose.yml
├── Dockerfile
├── .env
├── secrets/
│   ├── jenkins_agent_ssh_key
│   └── jenkins_agent_ssh_key.pub
├── Jenkinsfile
└── readme.md
```

## Заключение

В ходе выполнения лабораторной работы я успешно настроил Jenkins Controller и SSH Agent, создал работающий CI/CD pipeline для автоматического тестирования PHP проекта. Научился работать с Jenkins агентами, конфигурировать Pipeline as Code и решать проблемы, возникающие при настройке CI/CD инфраструктуры.

Все компоненты работают корректно, pipeline выполняется автоматически при изменениях в репозитории, что значительно упрощает процесс разработки и тестирования. 