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
|   |       └── index.html          # HTML web application
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

## Test

```bash
mvn clean verify
```

## Deploy

```bash
# Build application
mvn clean package

# Deploy application
cp target/order-system.war /opt/wildfly/standalone/deployments/

# Check deployment status
ls -l /opt/wildfly/standalone/deployments/

# Watch logs
tail -f /opt/wildfly/standalone/log/server.log
```

## HTML Web Application
```bash
# Access via web application
http://localhost:8080/order-system/
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
    "name": "Müller",
    "vorname": "Anna",
    "email": "mueller@example.com",
    "geburtstag": "1978-08-08",
    "strasse": "Am Anger",
    "hausnummer": 33,
    "postleitzahl": 33332,
    "ort": "Gütersloh"
  }'

# Create order
curl -X POST http://localhost:8080/order-system/api/bestellungen \
  -H "Content-Type: application/json" \
  -d '{"kundeId": 1, "positionen": [{"produktId": 1, "menge": 1}, {"produktId": 2, "menge": 2}]}'

# Get customer orders
curl http://localhost:8080/order-system/api/bestellungen/kunde/1

# Create bill for order with ID 1
curl -X POST http://localhost:8080/order-system/api/rechnungen/erstellen/1

# Get all bills
curl http://localhost:8080/order-system/api/rechnungen

# Download bill with ID 1 as PDF
curl -OJ http://localhost:8080/order-system/api/rechnungen/1/pdf

# Mark bill with ID 1 as payed
curl -X PUT http://localhost:8080/order-system/api/rechnungen/1/bezahlen \
  -H "Content-Type: application/json" \
  -d '{"bearbeiter": "Stephan"}'

# Check bill with ID 1 (status should be completed)
curl http://localhost:8080/order-system/api/bestellungen/1
```

## Database Queries

```bash
# Open PostgreSQL console
psql -h localhost -U dbuser -d bestellsystem

# Describe or show all tables
\dt

# Describe table kunde
\d kunde

# Query all customers in SQL syntax
SELECT * FROM kunde;

# Query all products in SQL syntax
SELECT * FROM produkt;

# Query all orders in SQL syntax
SELECT * FROM bestellung;

# Exit PostgreSQL console
\q                
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

## Cyclomatic Complexity Overview
```bash
# Run PMD code analysis
mvn pmd:pmd

# Generate cyclomatic complexity overview as TXT
python3 cc.py
```

## SonarQube Analysis
```bash
# Run with SonarQube analysis
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=order-system \
  -Dsonar.projectName='order-system' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_6387765bba30d3a250661a9edb69d82e6eb16f51 \
  -Dsonar.coverage.jacoco.xmlReportPaths=doc/jacoco/jacoco.xml

# Export SQL SonarQube analysis results
sudo -u postgres pg_dump sonarqube \
  -t issues \
  -t project_measures \
  -t metrics \
  -t snapshots \
  -t projects \
  -t project_branches \
  > doc/SQL/sonarqube_metrics_export.sql

# Get the current quality gate status with details
curl -s -u admin:admin "http://localhost:9000/api/qualitygates/project_status?projectKey=order-system" | jq .
{
  "projectStatus": {
    "status": "OK",
    "conditions": [
      {
        "status": "OK",
        "metricKey": "new_coverage",
        "comparator": "LT",
        "errorThreshold": "80"
      },
      {
        "status": "OK",
        "metricKey": "new_duplicated_lines_density",
        "comparator": "GT",
        "errorThreshold": "3",
        "actualValue": "0.0"
      },
      {
        "status": "OK",
        "metricKey": "new_security_hotspots_reviewed",
        "comparator": "LT",
        "errorThreshold": "100"
      },
      {
        "status": "OK",
        "metricKey": "new_violations",
        "comparator": "GT",
        "errorThreshold": "0",
        "actualValue": "0"
      }
    ],
    "ignoredConditions": false,
    "period": {
      "mode": "PREVIOUS_VERSION",
      "date": "2025-12-12T09:08:08+0100"
    },
    "caycStatus": "compliant"
  }
}
```

## Read Database Dump
```bash
# Create database
createdb -U dbuser -h localhost bestellsystem

# Read database dump
psql -U dbuser -d bestellsystem -h localhost -p 5432 < doc/SQL/bestellsystem_db_export.sql
```

## Database Schema Overview
```bash
# Download schemaspy JAR from GitHub
curl -L https://github.com/schemaspy/schemaspy/releases/download/v7.0.2/schemaspy-app.jar -o schemaspy-app.jar  

# Generate schema overview as HTML
java -jar schemaspy-app.jar -t pgsql -host localhost:5432 -db bestellsystem -s public -u dbuser -p dbpassword -dp ~/.m2/repository/org/postgresql/postgresql/42.7.8/postgresql-42.7.8.jar -o doc/schemaspy -vizjs
```

## Entities

- **Kunde** (Customer)
- **Produkt** (Product)
- **Bestellung** (Order)
- **BestellPosition** (Order Item)
