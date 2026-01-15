import requests
import os
import json
import sys
from datetime import datetime

def get_exchange_rate(from_currency, to_currency, date, api_key):
    url = f"http://localhost:8080/?from={from_currency}&to={to_currency}&date={date}"
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
    os.makedirs('data', exist_ok=True)
    filename = f"data/{from_currency}_{to_currency}_{date}.json"
    with open(filename, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"Данные сохранены: {filename}")

def log_error(message):
    with open('error.log', 'a') as f:
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

    api_key = "EXAMPLE_API_KEY"  # замените на свой ключ из .env
    
    data = get_exchange_rate(from_currency, to_currency, date, api_key)
    if data:
        save_data(data, from_currency, to_currency, date)