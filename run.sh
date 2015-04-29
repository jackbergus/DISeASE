#!/bin/bash
unzip -o errorData.zip
#java -jar target/DISeASE-1.0-SNAPSHOT.jar
mvn exec:java -Dexec.mainClass="disease.DISeASEMain"

