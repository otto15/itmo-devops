FROM node:20-alpine AS build
RUN apk add --no-cache openjdk17-jre-headless
WORKDIR /app/garage-frontend
COPY garage-frontend/package*.json ./
COPY garage-backend/openapi/ /app/garage-backend/openapi/
RUN npm ci --ignore-scripts
COPY garage-frontend/ .
RUN npm run generate-api
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/garage-frontend/dist /usr/share/nginx/html
COPY garage-frontend/nginx.conf /etc/nginx/conf.d/default.conf