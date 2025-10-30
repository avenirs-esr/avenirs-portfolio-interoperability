\connect template1;
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'avenirs_api' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS avenirs_interoperability;
DROP ROLE IF EXISTS avenirs_interoperability_admin;
DROP ROLE IF EXISTS avenirs_interoperability_admin_role;
