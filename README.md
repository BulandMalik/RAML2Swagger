# raml2swagger
A utility to generate Swagger JSON from RAML specification/documentation.

# Dependencies

- Maven
- App Container (Tomcat etc.)
- RAML Parser
- Jersey
- Spring MVC
- JDK 1.6 or higher version

# Usage

raml2swagger is a rest service so you can compile it using maven, it will generates the war file and you can deploy it to tomcat.

Below are the steps to have it up & running.

- hope you have all the necessary dependencies installed
- go to the folder where you have this project and run mvn clean install, it should generate a war file under target folder
- deploy target/raml2swagger.war to your tomcat/webapps folder
- run tomcat
- access the tool by using the following url (i assume your tomcat is running on 8080)
- - http://localhost:8080/raml2swagger/v1?filePath={location to raml file}
- - - filePath, its the pointer to where the raml file exists either on file system (classpath) or at different server

# Contributing to this project

# License

