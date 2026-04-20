#!/bin/bash

PROJECT="/Users/mezbahuddin/Capstone Design/Student Brigde /StudentBridge"
TOMCAT="/Users/mezbahuddin/Desktop/apache-tomcat-10.1.54/webapps/StudentBridge"

mkdir -p "$TOMCAT/WEB-INF/classes"
mkdir -p "$TOMCAT/WEB-INF/lib"

rsync -av --exclude ".git" --exclude ".vscode" "$PROJECT/" "$TOMCAT/"

javac -cp "$PROJECT/docs/mysql-connector-j-9.3.0.jar:$PROJECT/docs/servlet-api.jar" -d "$PROJECT/bin" "$PROJECT"/Backend/*.java

cp "$PROJECT"/bin/*.class "$TOMCAT/WEB-INF/classes/"
cp "$PROJECT/server/mysql-connector-j-9.3.0.jar" "$TOMCAT/WEB-INF/lib/"

echo "Deployment Complete!"