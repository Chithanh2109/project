@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set MAVEN_HOME=C:\Program Files\Java\Apache\apache-maven-3.9.9
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%
cd /d "%~dp0"
mvn spring-boot:run 