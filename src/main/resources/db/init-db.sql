SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'template1'
  AND pid <> pg_backend_pid();

CREATE ROLE avenirs_interoperability_admin_role SUPERUSER;
CREATE ROLE avenirs_interoperability_admin PASSWORD 'ENC(SqPcSCTJpp/z0ipY/oXC/Y1SqO9GB/D+)' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_interoperability_admin_role to avenirs_interoperability_admin;

CREATE DATABASE avenirs_interoperability OWNER avenirs_interoperability_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_interoperability TO avenirs_interoperability_admin_role;
\c avenirs_interoperability
CREATE SCHEMA IF NOT EXISTS dev AUTHORIZATION avenirs_interoperability_admin;
ALTER USER avenirs_interoperability_admin SET search_path TO dev, public;
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
