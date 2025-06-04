# Stage 1: Build
FROM node:latest AS builder
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all project files
COPY . .

# Expose the port Vite runs on
EXPOSE 5173

# Run the Vite development server
CMD ["npm", "run", "dev"]



# # Stage 1: Build
# FROM node:latest AS builder
# WORKDIR /app

# # Copy package.json and package-lock.json
# COPY package*.json ./

# # Install dependencies
# RUN npm install

# # Copy all project files
# COPY . .

# # Build the project
# RUN npm run build

# # Stage 2: Serve
# FROM node:latest
# WORKDIR /app

# # Copy build artifacts from the builder stage
# COPY --from=builder /app/dist /app/dist

# # Install a simple HTTP server to serve the built files
# RUN npm install -g serve

# # Expose the port the app runs on
# EXPOSE 3000

# # Serve the built files
# CMD ["serve", "-s", "dist", "-l", "3000"]