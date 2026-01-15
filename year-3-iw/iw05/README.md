# Lab05 --- Автоматизация конфигурации сервера с помощью Ansible

В этой работе я настроил полную цепочку автоматизации: **Jenkins → SSH
Agent → Ansible Agent → Test Server**. Также созданы три pipeline'а и
Ansible playbook для конфигурации сервера.

------------------------------------------------------------------------

## 1. Настройка Jenkins Controller

Создан сервис `jenkins-controller` в `compose.yaml`:

``` yaml
services:
  jenkins-controller:
    image: jenkins/jenkins:lts
    ports:
      - "8080:8080"
    volumes:
      - jenkins_home:/var/jenkins_home
```

После запуска выполнена первоначальная настройка и установлены плагины:

-   Docker\
-   Docker Pipeline\
-   GitHub Integration\
-   SSH Agent

------------------------------------------------------------------------

## 2. Настройка SSH Agent

Создан `Dockerfile.ssh_agent`:

``` dockerfile
FROM ubuntu:22.04

RUN apt update && apt install -y     php-cli php-xml php-mbstring php-curl php-zip     openssh-server git curl

RUN mkdir /var/run/sshd
```

Созданы SSH‑ключи и добавлены Jenkins‑credentials.

Добавлен сервис:

``` yaml
ssh-agent:
  build:
    context: .
    dockerfile: Dockerfile.ssh_agent
  volumes:
    - ssh_agent_home:/home/jenkins
```

------------------------------------------------------------------------

## 3. Создание Ansible Agent

Создан `Dockerfile.ansible_agent`:

``` dockerfile
FROM ubuntu:22.04

RUN apt update && apt install -y ansible openssh-client sshpass git
```

Добавлен сервис:

``` yaml
ansible-agent:
  build:
    context: .
    dockerfile: Dockerfile.ansible_agent
  volumes:
    - ansible_home:/home/ansible
```

------------------------------------------------------------------------

## 4. Создание Test Server

Создан `Dockerfile.test_server`:

``` dockerfile
FROM ubuntu:22.04

RUN apt update && apt install -y openssh-server sudo     && mkdir /var/run/sshd

RUN useradd -m ansible &&     mkdir -p /home/ansible/.ssh && chmod 700 /home/ansible/.ssh
```

Добавлен публичный ключ, включён SSH‑доступ.

------------------------------------------------------------------------

## 5. Ansible Playbook

Файл `hosts.ini`:

``` ini
[testserver]
test-server ansible_host=test-server ansible_user=ansible
```

Playbook `setup_test_server.yml`:

``` yaml
- hosts: testserver
  become: yes
  tasks:
    - name: install apache
      apt: name=apache2 state=present update_cache=yes

    - name: install php
      apt: name=php state=present

    - name: enable vhost
      copy:
        src: vhost.conf
        dest: /etc/apache2/sites-available/000-default.conf
```

------------------------------------------------------------------------

## 6. Pipeline сборки и тестирования PHP‑проекта

Файл `php_build_and_test_pipeline.groovy`:

``` groovy
node {
    sshagent(['ssh-agent-php']) {
        stage('Clone') {
            git 'https://github.com/user/php-project.git'
        }
        stage('Composer install') {
            sh 'composer install'
        }
        stage('Run tests') {
            sh './vendor/bin/phpunit --testdox'
        }
    }
}
```

------------------------------------------------------------------------

## 7. Pipeline настройки тестового сервера

``` groovy
node {
    sshagent(['ssh-ansible']) {
        stage('Clone') {
            git 'https://github.com/user/lab05.git'
        }
        stage('Run Ansible') {
            sh 'ansible-playbook -i ansible/hosts.ini ansible/setup_test_server.yml'
        }
    }
}
```

------------------------------------------------------------------------

## 8. Pipeline деплоя PHP‑проекта

``` groovy
node {
    sshagent(['ssh-ansible']) {
        stage('Clone') {
            git 'https://github.com/user/php-project.git'
        }
        stage('Deploy') {
            sh 'scp -r . ansible@test-server:/var/www/html/'
        }
    }
}
```

------------------------------------------------------------------------

## 9. Тестирование проекта

После деплоя проект работает в браузере.

------------------------------------------------------------------------

# Ответы на вопросы

### 1. Преимущества Ansible

-   Безагентная работа\
-   Простые YAML‑playbook'и\
-   SSH‑ориентированный подход\
-   Масштабируемость

### 2. Другие модули Ansible

-   `apt`, `yum` --- пакеты\

-   `copy`, `template` --- файлы

-   `service` --- сервисы\

-   `user` --- пользователи\

-   `git` --- репозитории

### 3. Проблемы и решения

**Проблема:** неправильные права `.ssh`\
**Решение:** `chmod 700` и `chmod 600`

**Проблема:** Ansible не подключался\
**Решение:** правильный `ansible_user` и ключ

**Проблема:** Apache не видел проект\
**Решение:** обновление vhost и рестарт службы
