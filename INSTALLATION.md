# 🚀 Installation Guide

This project is packaged as a ZIP archive and uses **Docker Compose** for setup and deployment.

## 🧱 Prerequisites

Make sure you have the following installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## 📦 Installation Steps

1. **Unzip the project:**

   Unpack the ZIP archive to any directory on your machine.

2. **Navigate to the project root:**

   ```bash
   cd path/to/unzipped/soft_project 2
   ```

3. **Build and start the application:**

   From the root directory, run:

   ```bash
   docker-compose up --build
   ```

4. **Access the application:**

   Open your browser and go to:

   ```
   http://localhost:3000
   ```


    http://localhost:3000 - Frontend
    http://localhost:8081 - Keycloak
    http://localhost:8081 - Backend
## 🛑 Stopping the Application

To stop and remove all running containers:

```bash
docker-compose down
```

