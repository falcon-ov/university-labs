FROM python:3.12-slim

# Установка зависимостей
RUN apt-get update && apt-get install -y cron curl && \
    pip install requests

# Рабочая директория
WORKDIR /app

# Копирование файлов
COPY currency_exchange_rate.py /app/
COPY cronjob /etc/cron.d/cronjob
COPY entrypoint.sh /app/entrypoint.sh

# Исправление endings, права и установка crontab
RUN sed -i 's/\r$//' /etc/cron.d/cronjob && \
    sed -i 's/\r$//' /app/entrypoint.sh && \
    sed -i 's/\r$//' /app/currency_exchange_rate.py && \
    chmod 0644 /etc/cron.d/cronjob && \
    chmod +x /app/entrypoint.sh && \
    /usr/bin/crontab /etc/cron.d/cronjob && \
    mkdir /app/data && \
    touch /var/log/cron.log /app/error.log && \
    chmod 666 /var/log/cron.log /app/error.log

# ENV для API_KEY (если нужно fallback)
ENV API_KEY=EXAMPLE_API_KEY

ENTRYPOINT ["/app/entrypoint.sh"]