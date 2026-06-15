#!/bin/bash

PROJECT="$(cd "$(dirname "$0")" && pwd)"
TOMCAT="/Users/mezbahuddin/Desktop/apache-tomcat-10.1.54/webapps/StudentBridge"

echo "🚀 Deploying StudentBridge..."
echo "Project path: $PROJECT"

mkdir -p "$TOMCAT"
mkdir -p "$TOMCAT/WEB-INF/classes"
mkdir -p "$TOMCAT/WEB-INF/lib"

# Copy only needed frontend/project files
cp "$PROJECT/index.html" "$TOMCAT/"
cp -R "$PROJECT/frontend" "$TOMCAT/"

# Compile Java backend
javac -cp "$PROJECT/server/mysql-connector-j-9.3.0.jar:$PROJECT/server/servlet-api.jar" \
  -d "$PROJECT/bin" \
  "$PROJECT"/Backend/*.java

# Copy compiled servlet classes
cp "$PROJECT"/bin/*.class "$TOMCAT/WEB-INF/classes/"

# Copy MySQL driver
cp "$PROJECT/server/mysql-connector-j-9.3.0.jar" "$TOMCAT/WEB-INF/lib/"

echo "✅ Deployment Complete!"
echo "Open: http://localhost:8080/StudentBridge/"
