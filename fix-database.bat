@echo off
echo Fixing database constraint for DISEASE_TREATMENT message type...
echo.

echo Please run the following SQL commands in your database:
echo.
echo 1. Connect to your PostgreSQL database
echo 2. Run the SQL commands from fix-message-type-constraint.sql
echo.

echo Alternative: If you're using Hibernate with auto-ddl, you can:
echo 1. Stop the backend
echo 2. Delete the database tables (or drop the database)
echo 3. Restart the backend (it will recreate tables with new constraints)
echo.

echo The constraint needs to be updated to include 'DISEASE_TREATMENT' as a valid message type.
pause
