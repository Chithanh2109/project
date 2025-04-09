$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:MAVEN_HOME = "C:\Program Files\Java\Apache\apache-maven-3.9.9"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"
Set-Location $PSScriptRoot
mvn spring-boot:run 