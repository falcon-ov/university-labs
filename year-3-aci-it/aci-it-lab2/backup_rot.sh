#!/bin/bash
set -u

SOURCE_DIR="${1:-}"
BACKUP_DIR="${2:-$HOME/backups}"

if [ -z "$SOURCE_DIR" ] || [ ! -d "$SOURCE_DIR" ]; then
    echo "Ошибка: укажите существующий каталог-источник" >&2
    exit 1
fi

mkdir -p "$BACKUP_DIR" || exit 2

BASENAME=$(basename "$SOURCE_DIR")
TIMESTAMP=$(date '+%Y%m%d_%H%M%S')
ARCHIVE="backup_${BASENAME}_${TIMESTAMP}.tar.gz"
ARCHIVE_PATH="$BACKUP_DIR/$ARCHIVE"

if tar -czf "$ARCHIVE_PATH" -C "$(dirname "$SOURCE_DIR")" "$BASENAME"; then
    SIZE=$(stat -c%s "$ARCHIVE_PATH" 2>/dev/null || stat -f%z "$ARCHIVE_PATH")
    echo "$(date '+%Y-%m-%dT%H:%M:%S') FILE=$ARCHIVE SIZE=$SIZE STATUS=0" >> "$BACKUP_DIR/backup.log"
    exit 0
else
    echo "$(date '+%Y-%m-%dT%H:%M:%S') FILE=$ARCHIVE SIZE=0 STATUS=1" >> "$BACKUP_DIR/backup.log"
    exit 1
fi