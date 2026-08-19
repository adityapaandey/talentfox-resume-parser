@echo off
echo Building with Java directly (no Maven)...
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Download dependencies
echo Downloading dependencies...
if not exist "lib" mkdir lib
cd lib

REM Download Spring Boot JARs
curl -O https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/3.2.1/spring-boot-3.2.1.jar
curl -O https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.2.1/spring-boot-autoconfigure-3.2.1.jar
curl -O https://repo1.maven.org/maven2/org/springframework/spring-web/6.1.2/spring-web-6.1.2.jar
curl -O https://repo1.maven.org/maven2/org/springframework/spring-webmvc/6.1.2/spring-webmvc-6.1.2.jar
curl -O https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.30/pdfbox-2.0.30.jar
curl -O https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.5/poi-ooxml-5.2.5.jar

cd ..

echo Compiling Java files...
javac -cp "lib/*" -d target/classes src/main/java/com/talentfor/resumeparser/**/*.java

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Running application...
java -cp "target/classes;lib/*" com.talentfor.resumeparser.ResumeParserApplication

pause
