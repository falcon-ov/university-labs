# IW02: Creating a Python Script to Interact with an API

## Goal

Completed by: Socolov Daniil  
Group: I2302

Learn to interact with a Web API using a Python script.

## Project Setup

1. Unpack `lab02prep.zip` in a convenient location. ☑
2. Install Docker and Docker Compose if they are not installed. ☑
3. Navigate to the project directory. ☑
4. Create a `.env` file: ☑

```bash
cp sample.env .env ☑
```

5. Start the service:

```bash
docker-compose up --build ☑
```
![img](/images/img_1.png)

6. The service will be available at `http://localhost:8080`

Test the API:

```bash
curl "http://localhost:8080/?currencies" -X POST -d "key=EXAMPLE_API_KEY"
```

![img](/images/img_2.png)

---

## Creating a Branch and Project Structure for lab02

```bash
git checkout -b lab02
mkdir lab02
cd lab02
touch currency_exchange_rate.py
```
![img](/images/img_3.png)
---

## Script `currency_exchange_rate.py`

### Script Structure

- `get_exchange_rate()` – sends a request to the API and handles errors  
- `save_data()` – saves the result to a JSON file  
- `log_error()` – writes errors to `error.log`  

### Script Functions

- Retrieves currency exchange rates from the API for a specified date
- Saves the received data as JSON files in the `data/` folder
- Logs errors into `error.log`

```python
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
        print(f"Error: {e}")
        return None

def save_data(data, from_currency, to_currency, date):
    os.makedirs('data', exist_ok=True)
    filename = f"data/{from_currency}_{to_currency}_{date}.json"
    with open(filename, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"Data saved: {filename}")

def log_error(message):
    with open('error.log', 'a') as f:
        f.write(f"{datetime.now()} - {message}\n")

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python currency_exchange_rate.py <FROM> <TO> <DATE>")
        sys.exit(1)
    
    from_currency = sys.argv[1]
    to_currency = sys.argv[2]
    date = sys.argv[3]

    try:
        datetime.strptime(date, "%Y-%m-%d")
    except ValueError:
        print("Invalid date format. Use YYYY-MM-DD")
        sys.exit(1)

    api_key = "EXAMPLE_API_KEY"  # replace with your key from .env
    
    data = get_exchange_rate(from_currency, to_currency, date, api_key)
    if data:
        save_data(data, from_currency, to_currency, date)
```

### Running the Script

```bash
python3 lab02/currency_exchange_rate.py <FROM> <TO> <DATE>
```

Example:

```bash
python3 lab02/currency_exchange_rate.py USD EUR 2025-01-01
```

## Testing

Running the script for 5 dates with equal intervals:

```bash
python3 lab02/currency_exchange_rate.py USD EUR 2025-01-01
python3 lab02/currency_exchange_rate.py USD EUR 2025-03-01
python3 lab02/currency_exchange_rate.py USD EUR 2025-05-01
python3 lab02/currency_exchange_rate.py USD EUR 2025-07-01
python3 lab02/currency_exchange_rate.py USD ABC 2025-01-01
```
![img](/images/img_4.png)

- Check JSON files saved in the `data/` folder
- Check error logging in `error.log`

![img](/images/img_5.png)
![img](/images/img_6.png)

## Conclusion

- The script allows automatic retrieval of currency exchange rates from the local PHP API.
- Data is saved as JSON files for further analysis.
- Errors are logged in a separate file, which is convenient for debugging.
- The script can be extended to work with other currencies and dates.