# Order System

JEE-based order management system with Hibernate and PostgreSQL.

## Project Structure

```
order-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── entity/         # JPA Entities
│   │   │       ├── repository/     # Data Access Layer
│   │   │       ├── service/        # Business Logic
│   │   │       └── rest/           # REST Endpoints
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml # JPA Configuration
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── beans.xml       # CDI Configuration
│   └── test/
│       ├── java/                   # Test Classes
│       └── resources/              # Test Resources
├── pom.xml                         # Maven Configuration
└── README.md
```

## Prerequisites

- JDK 17 or higher (test run succeeded with JDK 17 and JDK 21)
- Maven 3.8+
- PostgreSQL 14+
- WildFly 27+ (or other Jakarta EE 10 compatible server)

## PostgreSQL Setup

```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib -y

# Start PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Setup database and user
sudo -i -u postgres
psql

# In the psql console:
CREATE USER dbuser WITH PASSWORD 'dbpassword';
CREATE DATABASE bestellsystem;
GRANT ALL PRIVILEGES ON DATABASE bestellsystem TO dbuser;
\c bestellsystem
GRANT ALL ON SCHEMA public TO dbuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dbuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dbuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO dbuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO dbuser;
\q
exit

# Test connection
psql -h localhost -U dbuser -d bestellsystem
```

## WildFly Setup

```bash
# Download and install WildFly
cd ~
wget https://github.com/wildfly/wildfly/releases/download/31.0.0.Final/wildfly-31.0.0.Final.tar.gz
tar -xzf wildfly-31.0.0.Final.tar.gz
sudo mv wildfly-31.0.0.Final /opt/wildfly
sudo chown -R $USER:$USER /opt/wildfly

# Download PostgreSQL JDBC Driver
wget https://jdbc.postgresql.org/download/postgresql-42.7.1.jar -P ~/

# Start WildFly
/opt/wildfly/bin/standalone.sh &

# Configure DataSource (in new terminal)
/opt/wildfly/bin/jboss-cli.sh --connect

# In CLI:
module add --name=org.postgresql --resources=~/postgresql-42.7.1.jar --dependencies=javax.api,javax.transaction.api

/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)

data-source add --name=PostgresDS --jndi-name=java:jboss/datasources/PostgresDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/bestellsystem --user-name=dbuser --password=dbpassword --enabled=true

# Test DataSource
/subsystem=datasources/data-source=PostgresDS:test-connection-in-pool

# Exit
exit
```

## Build

```bash
mvn clean package
```

## Deploy

```bash
# Deploy application
cp target/order-system.war /opt/wildfly/standalone/deployments/

# Check deployment status
ls -l /opt/wildfly/standalone/deployments/

# Watch logs
tail -f /opt/wildfly/standalone/log/server.log
```

## REST API Testing

```bash
# Create test data
curl -X POST http://localhost:8080/order-system/api/testdaten/erstellen

# Get all customers
curl http://localhost:8080/order-system/api/kunden

# Get all products
curl http://localhost:8080/order-system/api/produkte

# Create new customer
curl -X POST http://localhost:8080/order-system/api/kunden \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Thomas Müller",
    "email": "thomas@example.com",
    "adresse": "Hauptstraße 10, 80331 München"
  }'

# Create order
curl -X POST http://localhost:8080/order-system/api/bestellungen \
  -H "Content-Type: application/json" \
  -d '{"kundeId": 1, "positionen": [{"produktId": 1, "menge": 1}, {"produktId": 2, "menge": 2}]}'

# Get customer orders
curl http://localhost:8080/order-system/api/bestellungen/kunde/1

# Update order status
curl -X PUT http://localhost:8080/order-system/api/bestellungen/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_BEARBEITUNG"}'

# Search products
curl http://localhost:8080/order-system/api/produkte/suche?name=laptop
```

## Database Queries

```bash
# Open PostgreSQL console
psql -h localhost -U dbuser -d bestellsystem

# In psql:
\dt                    # Show all tables
\d kunde              # Show table structure
SELECT * FROM kunde;  # Show all customers
SELECT * FROM produkt;
SELECT * FROM bestellung;
\q                    # Exit
```

## Development Workflow

```bash
# After code changes:
cd ~/Git/order-system
mvn clean package
cp target/order-system.war /opt/wildfly/standalone/deployments/

# WildFly automatically detects changes and redeploys
tail -f /opt/wildfly/standalone/log/server.log
```

## Entities

- **Kunde** (Customer)
- **Produkt** (Product)
- **Bestellung** (Order)
- **BestellPosition** (Order Item)
