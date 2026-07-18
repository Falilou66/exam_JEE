# syntax=docker/dockerfile:1

# ---------- Étape build ----------
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---------- Étape runtime (Nginx) ----------
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/samabank-frontend/browser /usr/share/nginx/html
EXPOSE 80
