# Astrology Application

A modern web application for generating and analyzing astrological charts, built with React and Spring Boot.

## 🌟 Features

- User authentication and authorization
- Astrological chart generation
- Interactive chart visualization
- Astronomical calculations using SwissEph library
- RESTful API architecture

## 🚀 Technology Stack

### Frontend
- React 18
- Material-UI (MUI)
- Ant Design
- React Router
- Axios
- D3.js (Data Visualization)

### Backend
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI
- Resilience4j
- JWT Authentication
- SwissEph Astronomical Ephemeris Library

## 📋 Prerequisites

### Frontend Requirements
- Node.js (v16 or higher recommended)
- npm or yarn package manager

### Backend Requirements
- JDK 17 or higher
- Maven 3.6 or higher
- PostgreSQL database server

## 🛠️ Installation & Setup

### Backend Setup

1. Configure Database
```bash
# Create PostgreSQL database and update application.properties with:
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
```

2. Build and Run Backend
```bash
cd backend
./mvnw clean package
java -jar target/astrology-api-0.0.1-SNAPSHOT.jar
```

### Frontend Setup

1. Install Dependencies
```bash
cd frontend
npm install
```

2. Start Development Server
```bash
npm start
```

3. Build for Production
```bash
npm run build
```

## 🚀 Deployment

### 1. Docker Deployment

#### Prerequisites
- Docker and Docker Compose installed
- Configured environment variables

#### Environment Configuration
Create a `.env` file in the root directory with the following variables:
```env
POSTGRES_DB=your_database_name
POSTGRES_USER=your_database_user
POSTGRES_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret
SPRING_PROFILES_ACTIVE=prod
```

#### Docker Services
- Frontend: React application
- Backend: Spring Boot application
- Database: PostgreSQL
- Nginx: Reverse proxy (optional)

#### Commands
```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# Stop all services
docker-compose down

# Production deployment
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d

# Monitoring
docker-compose logs -f
docker-compose ps
docker stats
```

### 2. Local Deployment

#### Prerequisites
- Node.js (v16 or higher)
- JDK 17 or higher
- Maven 3.6 or higher
- PostgreSQL database server

#### Backend Setup
1. Configure Database
```bash
# Create PostgreSQL database and update application.properties with:
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
```

2. Build and Run
```bash
cd backend
./mvnw clean package
java -jar target/astrology-api-0.0.1-SNAPSHOT.jar
```

#### Frontend Setup
1. Install Dependencies
```bash
cd frontend
npm install
```

2. Start Development Server
```bash
npm start
```

3. Build for Production
```bash
npm run build
```

#### Security Checklist
- Update JWT secret key
- Configure CORS policies
- Enable HTTPS
- Secure database credentials
- Configure production properties
- Set up monitoring
- Configure logging

## 🔍 Verification Steps

1. Access frontend application (default: http://localhost:3000)
2. Test user registration and login
3. Verify backend API connectivity
4. Check astronomical calculations

## 📝 Common Issues & Solutions

1. Database Connection Issues
   - Verify database service status
   - Check connection credentials
   - Confirm user permissions

2. API Connection Issues
   - Verify backend service status
   - Check API endpoint configuration
   - Verify CORS settings

3. SwissEph Issues
   - Verify swisseph.jar presence
   - Check JNA dependency

## 🔧 Monitoring & Maintenance

- Use Spring Boot Actuator for backend monitoring
- Regular log review
- Database performance monitoring
- Scheduled backups
- Regular dependency updates

## 👥 Contributing
- Jinghao Mo
- Hongyu Jin
- Qian Tang

---

For detailed documentation and API references, please visit our [Wiki](add_wiki_link_here).
