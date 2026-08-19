@echo off
setlocal enabledelayedexpansion

echo ================================================
echo Resume Parser - Java Application
echo ================================================
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting application...
echo Backend URL: http://localhost:8080
echo.

REM Run with Maven directly (skip compilation step)
set MAVEN_OPTS=--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED
.\mvnw.cmd compile exec:java -Dexec.mainClass="com.talentfor.resumeparser.ResumeParserApplication"

pause
