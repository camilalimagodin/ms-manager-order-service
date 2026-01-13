-- Initialization script for PostgreSQL
-- This script runs when the container is first created

-- Create database for SonarQube (if using same PostgreSQL instance)
CREATE DATABASE sonardb;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE orderdb TO order_user;
GRANT ALL PRIVILEGES ON DATABASE sonardb TO order_user;

-- Enable extensions
\c orderdb;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema (optional, using public)
-- CREATE SCHEMA IF NOT EXISTS orders;
