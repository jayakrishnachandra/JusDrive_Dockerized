# Build stage
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

# Runtime stage
FROM nginx:alpine
COPY --from=build /app/dist/jus-drive/browser /usr/share/nginx/html
# Copy nginx config if needed, but for basic serving, default is okay.
# We might need to handle angular routing (SPA) in nginx.
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

