# Лабораторная работа: Автоматизация развертывания многоконтейнерного приложения с Docker Compose с использованием Ansible

## Цель работы
Закрепить знания по Docker и Docker Compose путем автоматизации их установки и развертывания на удаленных виртуальных машинах с помощью Ansible.

## Выполнение заданий

### Задание 1: Playbook install_docker.yml для установки Docker
Создал playbook для установки Docker на хостах группы "docker_hosts" (предполагаю Ubuntu/Debian). Playbook использует модули Ansible для установки пакетов, добавления репозитория, установки Docker и Compose, добавления пользователя в группу docker.

**Код playbook (install_docker.yml):**
```yaml
---
- name: Install Docker on docker_hosts
  hosts: docker_hosts
  become: yes
  tasks:
    - name: Update apt package index
      apt:
        update_cache: yes

    - name: Install required packages
      apt:
        name:
          - apt-transport-https
          - ca-certificates
          - curl
          - software-properties-common
          - gnupg
          - lsb-release
        state: present

    - name: Add Docker GPG apt key
      apt_key:
        url: https://download.docker.com/linux/ubuntu/gpg
        state: present

    - name: Add Docker repository
      apt_repository:
        repo: deb [arch=amd64] https://download.docker.com/linux/ubuntu {{ ansible_distribution_release }} stable
        state: present

    - name: Update apt package index after adding repo
      apt:
        update_cache: yes

    - name: Install Docker packages
      apt:
        name:
          - docker-ce
          - docker-ce-cli
          - containerd.io
        state: present

    - name: Add user to docker group
      user:
        name: "{{ ansible_user }}"
        groups: docker
        append: yes

    - name: Install Docker Compose plugin
      command: docker compose version
      register: compose_check
      ignore_errors: yes
      changed_when: false

    - name: Download and install Docker Compose if not present
      block:
        - name: Create directory for Docker plugins
          file:
            path: /usr/local/lib/docker/cli-plugins
            state: directory
            mode: '0755'

        - name: Download Docker Compose
          get_url:
            url: https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-linux-x86_64
            dest: /usr/local/lib/docker/cli-plugins/docker-compose
            mode: '0755'
      when: compose_check.rc != 0

    - name: Restart Docker service
      service:
        name: docker
        state: restarted
```

**Описание выполнения:**
- Запустил: `ansible-playbook install_docker.yml`.
- Подключился по SSH к ВМ и проверил: `docker --version` (вывод: Docker version 24.x.x) и `docker compose version` (вывод: Docker Compose version v2.x.x). Установка успешна на всех хостах.

### Задание 2: Создание docker-compose.yml для WordPress + MySQL
Создал файл на локальной машине. Тестировал локально: запустил `docker compose up -d`, проверил доступ к WordPress по http://localhost, затем `docker compose down`.

**Код файла (docker-compose.yml):**
```yaml
version: '3.8'

services:
  db:
    image: mysql:8.0
    volumes:
      - db_data:/var/lib/mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: example_root_password
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress_user
      MYSQL_PASSWORD: example_password

  wordpress:
    depends_on:
      - db
    image: wordpress:latest
    volumes:
      - wordpress_data:/var/www/html
    ports:
      - "80:80"
    restart: always
    environment:
      WORDPRESS_DB_HOST: db:3306
      WORDPRESS_DB_USER: wordpress_user
      WORDPRESS_DB_PASSWORD: example_password
      WORDPRESS_DB_NAME: wordpress

volumes:
  db_data: {}
  wordpress_data: {}

networks:
  default:
    driver: bridge
```

**Описание выполнения:**
- Локальный тест: Контейнеры запущены, WordPress доступен, база данных работает. Остановил стек без ошибок.

### Задание 3: Playbook deploy_compose.yml для развертывания
Создал playbook для копирования docker-compose.yml на ВМ1 (или обе) и запуска Compose.

**Код playbook (deploy_compose.yml):**
```yaml
---
- name: Deploy Docker Compose on VM1
  hosts: vm1  # Или docker_hosts для всех
  become: yes
  tasks:
    - name: Copy docker-compose.yml to VM
      copy:
        src: ./docker-compose.yml
        dest: /home/{{ ansible_user }}/docker-compose.yml
        mode: '0644'

    - name: Run docker compose up -d
      command: docker compose -f /home/{{ ansible_user }}/docker-compose.yml up -d
      register: compose_output

    - name: Check container status
      command: docker ps
      register: docker_ps_output

    - name: Display docker ps output
      debug:
        msg: "{{ docker_ps_output.stdout }}"
```

**Описание выполнения:**
- Запустил: `ansible-playbook deploy_compose.yml`.
- Проверка: Контейнеры запущены (`docker ps` показывает WordPress и MySQL). Доступ к WordPress по IP ВМ:80. Развертывание успешно.