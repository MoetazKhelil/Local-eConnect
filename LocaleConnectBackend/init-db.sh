#!/bin/bash

#credentials
DB_HOST="localeconnect-database.postgres.database.azure.com"
DB_PORT=*************
DB_ADMIN_USER= *************
DB_ADMIN_PASSWORD=*************


run_sql_on_db() {
    local db_name=$1
    local sql_command=$2
    PGPASSWORD=$DB_ADMIN_PASSWORD psql -h $DB_HOST -U $DB_ADMIN_USER -d "$db_name" -c "$sql_command"
}

# Create a new single database
NEW_DB_NAME="consolidatedDB"

echo "Creating new consolidated database: $NEW_DB_NAME"
run_sql_on_db "postgres" "DROP DATABASE IF EXISTS \"$NEW_DB_NAME\";"

sleep 10

run_sql_on_db "postgres" "CREATE DATABASE \"$NEW_DB_NAME\";"

# Add a delay
sleep 10

SCHEMA_FILES=(
    "data-scripts/02_userDB_schema.sql"
    "data-scripts/03_tripDB_schema.sql"
    "data-scripts/04_meetupDB_schema.sql"
    "data-scripts/05_itineraryDB_schema.sql"
    "data-scripts/06_feedDB_schema.sql"
    "data-scripts/07_notificationDB_schema.sql"
)

for file in "${SCHEMA_FILES[@]}"; do
    echo "Initializing $NEW_DB_NAME with $file"
    run_sql_on_db "$NEW_DB_NAME" "$(cat "$file")"
done

echo "Database initialization completed."
