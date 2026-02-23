#!/usr/bin/env bash

mkdir -p deployment
echo "services:" > docker-compose.yaml

generate_app_service() {
  cat <<EOF >> docker-compose.yaml
  app:
    build: 
      context: .
      dockerfile: Dockerfile.backend
    depends_on:
      - db
    environment:
      - DB_URL=jdbc:postgresql://${APP_DB_HOST}/${APP_DB}
      - DB_USER=${APP_DB_USER}
      - DB_PASSWORD=\${APP_DB_PW}
      - APP_DOMAIN=${APP_DOMAIN}
      - OIDC_ISSUER=${OIDC_ISSUER}
      - OIDC_ID=${OIDC_ID}
      - OIDC_SECRET=${OIDC_SECRET}
      - USERNAME_ATTRIBUTE=${OIDC_ATTRIBUTE}
EOF
  if [ "$use_caddy" = false ]; then
    cat <<EOF >> docker-compose.yaml
    ports:
      - "${APP_PORT}:8080"
EOF
  fi
}

generate_db_service() {
  cat <<EOF >> docker-compose.yaml
  db:
    image: docker.io/postgres:18
    environment:
      - POSTGRES_DB=${APP_DB}
      - POSTGRES_USER=${APP_DB_USER}
      - POSTGRES_PASSWORD=\${APP_DB_PW}
    volumes:
      - pgdata:/var/lib/postgresql
EOF
}

generate_dex_service() {
  cat <<EOF >> docker-compose.yaml
  dex:
    image: dexidp/dex:latest-alpine
    restart: always
    configs:
      - source: dex
        target: /etc/dex/config.docker.yaml
    volumes:
      - dex:/var/dex/
EOF
  if [ "$use_caddy" = false ]; then
    cat <<EOF >> docker-compose.yaml
    ports:
      - "${DEX_PORT}:5556"
EOF
  fi
}

generate_caddy_service() {
  cat <<EOF >> docker-compose.yaml
  caddy:
    image: dock.tilera.xyz/tilera/caddy:latest
    command:
      - caddy 
      - run
      - --config
      - /conf/Caddyfile
    volumes:
      - caddy:/data
    ports:
      - "80:80"
      - "443:443"
    configs:
      - source: caddy
        target: /conf/Caddyfile
EOF
}

generate_volumes() {
  if [ "$1" = true ] || [ "$2" = true ] || [ "$3" = true ]; then
    cat <<EOF >> docker-compose.yaml
volumes:
EOF
  fi
  if [ "$1" = true ]; then
    cat <<EOF >> docker-compose.yaml
  pgdata:
EOF
  fi

  if [ "$2" = true ]; then
    cat <<EOF >> docker-compose.yaml
  dex:
EOF
  fi

  if [ "$3" = true ]; then
    cat <<EOF >> docker-compose.yaml
  caddy:
EOF
  fi
}

generate_configs() {
  if [ "$1" = true ] || [ "$2" = true ]; then
    cat <<EOF >> docker-compose.yaml
configs:
EOF
  fi
  if [ "$1" = true ]; then
    cat <<EOF >> docker-compose.yaml
  dex:
    file: "./deployment/dex.yaml"
EOF
  fi

  if [ "$2" = true ]; then
    cat <<EOF >> docker-compose.yaml
  caddy:
    file: "./deployment/Caddyfile"
EOF
  fi
}

ask_user() {
  read -p "Please input the domain of your instance: " APP_DOMAIN

  read -p "Do you have already have a PostgreSQL database? [y/n] " choice
  case "$choice" in
    y|Y ) use_db=false;;
    * ) use_db=true;;
  esac

  if [ "$use_db" = true ]; then
    APP_DB_PW=$(openssl rand -hex 32)
    APP_DB_HOST="db:5432"
    APP_DB_USER=app
    APP_DB=app
  else
    read -p "Database host (including port): " APP_DB_HOST
    read -p "Database name: " APP_DB
    read -p "Database username: " APP_DB_USER
    read -p "Database password: " APP_DB_PW
  fi

  read -p "Do you have already have a OpenID Connect provider? [y/n] " choice
  case "$choice" in
    y|Y ) use_dex=false;;
    * ) use_dex=true;;
  esac

  if [ "$use_dex" = true ]; then
    OIDC_SECRET=$(openssl rand -hex 32)
    OIDC_ID=chessfed
    OIDC_ISSUER="https://${APP_DOMAIN}/dex"
    OIDC_ATTRIBUTE=name
  else
    read -p "OIDC issuer: " OIDC_ISSUER
    read -p "OIDC id: " OIDC_ID
    read -p "OIDC secret: " OIDC_SECRET
    OIDC_ATTRIBUTE=preferred_username
  fi

  read -p "Do you have already have a reverse proxy? [y/n] " choice
  case "$choice" in
    y|Y ) use_caddy=false;;
    * ) use_caddy=true;;
  esac

  if [ "$use_caddy" = false ]; then
    read -p "Enter port for the backend [0-65535]: " APP_PORT
    if [ "$use_dex" = true ]; then
      read -p "Enter port for Dex [0-65535]: " DEX_PORT
      echo "Please configure your reverse proxy to pass requests to https://${APP_DOMAIN}/* to port ${APP_PORT} and to https://${APP_DOMAIN}/dex/* to port ${DEX_PORT}"
    else
      echo "Please configure your reverse proxy to pass requests to https://${APP_DOMAIN}/* to port ${APP_PORT}."
    fi
  fi

}

generate_dex_config() {
  cat <<EOF > deployment/dex.yaml
issuer: ${OIDC_ISSUER}
storage:
  type: sqlite3
  config:
    file: /var/dex/dex.db
web:
  http: 0.0.0.0:5556
telemetry:
  http: 0.0.0.0:5558
frontend:
  issuer: "Chessfed Login"
oauth2:
  skipApprovalScreen: true
enablePasswordDB: true
staticClients:
- id: ${OIDC_ID}
  name: 'Chessfed'
  secret: ${OIDC_SECRET}
  redirectURIs:
  - 'https://${APP_DOMAIN}/login/oauth2/code/openid'
staticPasswords:
EOF
}

generate_caddyfile() {
  cat <<EOF > deployment/Caddyfile
${APP_DOMAIN} {
    handle /dex/* {
        reverse_proxy http://dex:5556
    }
EOF
  if [ "$use_dex" = true ]; then
    cat <<EOF >> deployment/Caddyfile
    handle {
        reverse_proxy http://app:8080
    }
EOF
  fi
  cat <<EOF >> deployment/Caddyfile
}
EOF
}

generate_dotenv() {
  if [ ! -f ".env" ]; then
    cat <<EOF > .env
APP_DB_PW=${APP_DB_PW}
EOF
  fi
}

ask_user
generate_dotenv
generate_app_service

if [ "$use_db" = true ]; then
  generate_db_service
fi

if [ "$use_dex" = true ]; then
  generate_dex_service
  generate_dex_config
fi

if [ "$use_caddy" = true ]; then
  generate_caddy_service
  generate_caddyfile
fi

generate_configs $use_dex $use_caddy
generate_volumes $use_db $use_dex $use_caddy

echo "Compose file generated. Use 'docker compose up' to start."
