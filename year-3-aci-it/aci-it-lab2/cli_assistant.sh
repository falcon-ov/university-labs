#!/bin/bash

MAX_TRIES=3

prompt_nonempty() {
  local var_name="$1"
  local prompt_text="$2"
  local tries=0
  local value

  while [ $tries -lt $MAX_TRIES ]; do
    read -p "$prompt_text" value
    if [ -n "$value" ]; then
      printf -v "$var_name" '%s' "$value"
      return 0
    fi
    tries=$((tries + 1))
    echo "Ввод не должен быть пустым. Попытка $tries из $MAX_TRIES."
  done

  echo "Слишком много пустых вводов. Выход."
  exit 1
}

# Запрашиваем имя (обязательно)
prompt_nonempty user_name "Введите ваше имя: "

# Запрашиваем отдел/группу (необязательно; пустой ввод считается \"не указан\")
read -p "Введите отдел или группу (оставьте пустым, если не хотите указывать): " user_dept
if [ -z "$user_dept" ]; then
  user_dept="не указан"
fi

# Мини-отчёт
current_date="$(date '+%Y-%m-%d %H:%M:%S')"
host_name="$(hostname)"
uptime_info="$(uptime -p 2>/dev/null || uptime | sed 's/^/uptime: /')"
root_free="$(df -h / --output=avail,target 2>/dev/null | tail -n1 | awk '{print $1}')"
logged_users_count="$(who | wc -l | tr -d '[:space:]')"

echo "------------------------------"
echo "Мини-отчёт"
echo "------------------------------"
echo "Текущая дата: $current_date"
echo "Имя хоста: $host_name"
echo "Время аптайма: $uptime_info"
echo "Свободное место на / : $root_free"
echo "Пользователей в системе: $logged_users_count"
echo "------------------------------"
echo "Здравствуйте, $user_name ($user_dept)!"
