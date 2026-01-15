# Laboratory Work №3: Configuring the Task Scheduler (cron) for Script Automation

Completed by: Socolov Daniil
Group: I2302

## Goal
I learned how to configure the task scheduler (cron) to automate the execution of the `currency_exchange_rate.py` script.

## Preparation
This laboratory work is based on Laboratory Work №2. I copied all files from the `lab02` directory into a new directory `lab03` for further work.

## Task Execution

### 1. Project Creation
1. I created a new branch `lab03` in the repository.
2. I created the `lab03` directory and copied the following files from `lab02`:
   - `currency_exchange_rate.py` — script for retrieving currency exchange rates via API.
   - `.env` — file containing the `API_KEY` environment variable.
   - `docker-compose.yml` and `Dockerfile` from `lab02` for the `web` (API server) service.
3. I created additional files in the `lab03` directory:
   - `cronjob` — file with scheduled tasks for cron.
   - `Dockerfile` — to build a container image with cron.
   - `entrypoint.sh` — script to configure and start cron.
   - `readme.md` — this report.
   - ![img](/images/img_1.png)

### 2. Configuring cron Jobs
I configured the `cronjob` file, which defines two scheduled tasks:
- **Daily at 6:00 AM**: retrieve the MDL to EUR rate for the previous day.
- **Weekly on Friday at 5:00 PM**: retrieve the MDL to USD rate for the previous week (7 days).

Content of `cronjob`:
```plaintext
# Set PATH for cron
PATH=/usr/local/bin:/usr/bin:/bin

# Daily at 6:00 AM: MDL to EUR for the previous day (test: every minute)
* * * * * python3 /app/currency_exchange_rate.py MDL EUR $(date -d "yesterday" +\%Y-\%m-\%d) >> /var/log/cron.log 2>&1

# Weekly on Friday at 5:00 PM: MDL to USD for the previous week (test: every minute)
* * * * * for day in $(seq 1 7); do date=$(date -d "$day days ago" +\%Y-\%m-\%d); python3 /app/currency_exchange_rate.py MDL USD $date >> /var/log/cron.log 2>&1; done
```

For testing, I used the schedule `* * * * *` (every minute) to verify task execution.

### 3. Creating Dockerfile
I created a Dockerfile based on the `python:3.12-slim` image for the `lab03-cron` container. I performed the following actions:

- Installed dependencies: cron, curl (for diagnostics), requests (for Python).
- Copied files: `currency_exchange_rate.py`, `cronjob`, `entrypoint.sh`.
- Set permissions and added cron tasks to crontab.
- Created log files and the `/app/data` directory.
- Ran `entrypoint.sh` to start cron.

Content of Dockerfile:
![img](/images/img_2.png)

1. Configuring `entrypoint.sh`
I set up the `entrypoint.sh` script to configure and run cron:

- Saved environment variables to `/etc/environment`.
- Created the log file `/var/log/cron.log`.
- Started log monitoring (`tail -f`) in the background.
- Started the cron daemon in the background.

Content of `entrypoint.sh`:
![img](/images/img_3.png)

2. Configuring `docker-compose.yml`
I configured the `docker-compose.yml` file to define two services:

- `web`: API server (from lab02).
- `lab03-cron`: container with cron using the created Dockerfile.

Content of `docker-compose.yml`:
![img](/images/img_4.png)

## How to Build and Run the Container with cron

I went to the `lab03` directory:
```bash
cd `root`\lab03
```

I rebuilt the images and started the containers:
```bash
docker-compose down # to clear
docker-compose build --no-cache
docker-compose up -d
```
![img](/images/img_5.png)

I checked the status of the containers:
```bash
docker ps
```
![img](/images/img_6.png)

#### How to Check cron Job Execution
To verify cron tasks are running:

I entered the container:
```bash
docker exec -it lab03-cron sh
```

Checked the cron logs:
```bash
cat /var/log/cron.log
```
![img](/images/img_7.png)

Checked the created files:
```bash
ls /app/data
```
![img](/images/img_8.png)

Checked for errors:
```bash
cat /app/error.log
```
I saw the errors encountered during development.  
![img](/images/img_9.png)

Checked the API:
```bash
curl -X POST "http://web:80/?from=MDL&to=EUR&date=2025-10-09" -d "key=EXAMPLE_API_KEY"
```
![img](/images/img_10.png)

### Project Structure and File Purpose

- **currency_exchange_rate.py**: I wrote a script that retrieves exchange rates via API and saves data in JSON files inside `/app/data`.
- **cronjob**: I created a file with cron tasks for daily and weekly script execution.
- **Dockerfile**: I set up how to build the image for the `lab03-cron` container.
- **entrypoint.sh**: Script for configuring environment, creating logs, and launching cron.
- **docker-compose.yml**: I described the `web` (API) and `lab03-cron` services, their dependencies, and mounted volumes.
- **.env**: Contains `API_KEY` for API access.
- **data/**: Directory on the host for saving JSON files (mounted to `/app/data`).
- **logs/**: Directory on the host for log files (`cron.log`, `error.log`).

### Results

- Manual script execution (`python3 /app/currency_exchange_rate.py MDL EUR 2025-10-09`) successfully creates JSON files.
- Cron jobs execute according to schedule (`* * * * *`), logs are written to `/var/log/cron.log`, and data is saved to `/app/data`.
- The API at `http://web:80` is accessible, and scheduled tasks successfully retrieve data.

Example of a JSON file:
![img](/images/img_result.png)

