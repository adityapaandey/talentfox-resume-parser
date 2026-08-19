@echo off
echo Starting Resume Parser (Java)...
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Build with Gradle using JDK 21 compatible version
.\gradle-8.5\bin\gradle.bat clean bootJar -x test --warning-mode=none

if errorlevel 1 (
    echo Build failed. Trying with Maven...
    call mvnw.cmd spring-boot:run -DskipTests
) else (
    echo Starting application...
    java -jar build\libs\resume-parser-1.0.0.jar
)

pause
