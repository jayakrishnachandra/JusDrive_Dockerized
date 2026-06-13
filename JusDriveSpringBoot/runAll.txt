@echo off
REM Batch script to run all Spring Boot apps

set apps=api-gateway auth-service booking-service car-service enquiry-service notification-service 

for %%A in (%apps%) do (
    echo Starting %%A...
    start "%%A" cmd /k "cd %%A && mvn spring-boot:run"
)

echo All Spring Boot apps are launching in separate windows.
