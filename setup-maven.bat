@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "M2_HOME=C:\Program Files\Java\Apache\apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%"
mvn -v
