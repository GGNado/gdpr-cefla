# ---- Stage 1: Build ----
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npx ng build --configuration=production

# ---- Stage 2: Serve ----
FROM nginx:1.27-alpine
# Rimuovi la config di default di NGINX
RUN rm /etc/nginx/conf.d/default.conf
# Copia la nostra config personalizzata
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copia i file compilati di Angular
COPY --from=build /app/dist/gdpr-fe /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
