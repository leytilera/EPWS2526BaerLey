#!/usr/bin/env bash

if [ ! -f "deployment/dex.yaml" ]; then
    echo "Error: Dex config does not exist."
    exit 1
fi

command -v htpasswd >/dev/null 2>&1 || { echo "Error: htpasswd is not installed. This script requires htpasswd."; exit 1; }

read -p "Please enter the domain of your instance: " APP_DOMAIN
read -p "Please enter the username of the new user: " APP_USER
APP_PASSWD=$(htpasswd -BnC 10 ${APP_USER} | cut -d: -f2)
UUID=$(cat /proc/sys/kernel/random/uuid)

cat <<EOF >> deployment/dex.yaml 
- email: "${APP_USER}@${APP_DOMAIN}"
  hash: "${APP_PASSWD}"
  username: "${APP_USER}"
  userID: "${UUID}"
EOF