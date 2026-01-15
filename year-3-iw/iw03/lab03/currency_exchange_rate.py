import requests
import os
import json
import sys
from datetime import datetime

def get_exchange_rate(from_currency, to_currency, date, api_key):
    url = f"http://web:80/?from={from_currency}&to={to_currency}&date={date}"
    try:
        response = requests.post(url, data={'key': api_key})
        data = response.json()
        if data.get('error'):
            raise ValueError(data['error'])
        return data['data']
    except Exception as e:
        log_error(str(e))
        print(f"Ошибка: {e}")
        return None

def save_data(data, from_currency, to_currency, date):
    # абсолютный путь внутри контейнера
    output_dir = "/app/data"
    os.makedirs(output_dir, exist_ok=True)

    filename = os.path.join(output_dir, f"{from_currency}_{to_currency}_{date}.json")
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"Данные сохранены: {filename}")

def log_error(message):
    # тоже абсолютный путь
    error_log = "/app/error.log"
    with open(error_log, 'a', encoding='utf-8') as f:
        f.write(f"{datetime.now()} - {message}\n")

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Использование: python currency_exchange_rate.py <FROM> <TO> <DATE>")
        sys.exit(1)
    
    from_currency = sys.argv[1]
    to_currency = sys.argv[2]
    date = sys.argv[3]

    try:
        datetime.strptime(date, "%Y-%m-%d")
    except ValueError:
        print("Неверный формат даты. Используйте YYYY-MM-DD")
        sys.exit(1)

    api_key = os.getenv('API_KEY', 'EXAMPLE_API_KEY')
    
    data = get_exchange_rate(from_currency, to_currency, date, api_key)
    if data:
        save_data(data, from_currency, to_currency, date)
