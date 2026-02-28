# Servlet, Jsp and Jdbc training

## TLDR How do I run the web site?

Download tomcat 11 and maven 3 if you haven't already.
I'll assume you have tomcat installed here: `c:\Workspace\opt\apache-tomcat-11.0.18`.
```
cd /d c:\Workspace
git clone (insert url here)
cd /d c:\Workspace\opt\apache-tomcat-11.0.18\bin
makebase.bat c:\Workspace\calzoneshack\tomcat
cd /d c:\Workspace\calzoneshack
mvn clean package
```

Intellij > Hamburger > Open > c:\Workspace\calzoneshack\pom.xml

Create run configuration:
* Tomcat Server Local
* Tomcat home: c:\Workspace\opt\apache-tomcat-11.0.18
* Tomcat base directory: c:\Workspace\calzoneshack\tomcat
* Deployment > Deploy at the server startup > + > web:war exploded
* Application context: /training

When running the selenium tests in intellij I recommend adding these VM options to your run configuration: `-Dtraining.baseUrl=http://localhost:8889/training`

## What you need for this training

* Java 25  
https://adoptium.net
* Maven 3  
https://www.apache.org
* Tomcat 11  
You need the one under Binary Distributions - Core  
https://tomcat.apache.org
* A copy of the specs
  * Servlet 6.1  
  https://jakarta.ee/specifications/servlet/6.1/
  * JSP 3.1  
  https://jakarta.ee/specifications/pages/3.1/
  * JSTL 3.0  
  https://jakarta.ee/specifications/tags/3.0/
  * Expression language 6.0
  https://jakarta.ee/specifications/expression-language/6.0/

## Structure of a maven war project

Familiarize yourself with maven before you continue.
* https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
* https://maven.apache.org/guides/getting-started/index.html
* (optional) https://maven.apache.org/guides/mini/guide-naming-conventions.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-the-pom.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-profiles.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
* (optional) https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html
* (optional) https://maven.apache.org/settings.html
* (optional) https://maven.apache.org/pom.html
* (optional) https://maven.apache.org/general.html

| Directory/File     | Description                   |
|--------------------|-------------------------------|
| pom.xml            | Project Object Model          |
| src/main/java      | Application/Library sources   |
| src/main/resources | Application/Library resources |
| src/main/webapp    | Web application sources       |
| src/test/java      | Test sources                  |
| src/test/resources | Test resources                |

## Structure of a war file

You'll find the war file in the target folder after running `mvn package`.
A war file is a zip file with a different extension.
If you rename it to `.zip` and look inside the file you'll find this structure.

| Directory/File               | Description                                                                          |
|------------------------------|--------------------------------------------------------------------------------------|
| index.html, favicon.ico, ... | Directly accessible static files are outside the META-INF and WEB-INF folder         |
| META-INF/context.xml         | Tomcat specific configuration file. Allows the context path to be configured.        |
| WEB-INF/web.xml              | Deployment descriptor. This where we configure filters, servlets, authentication ... |
| WEB-INF/classes              | Java sources are compiled and placed here as class files. Resources are copied here. |
| WEB-INF/lib                  | Dependencies are copied here. These are jar files.                                   |

Example of META-INF/context.xml

the application will be available at
http://localhost/my-context-path/index.html

```
<?xml version="1.0" encoding="UTF-8"?>
<Context path="/my-context-path"/>
```

Example of WEB-INF/web.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
  version="6.0">
    <context-param>
        <param-name>javax.servlet.jsp.jstl.fmt.localizationContext</param-name>
        <param-value>messages</param-value>
    </context-param>
    <servlet>
       <servlet-name>catalog</servlet-name>
       <servlet-class>com.example.CatalogServlet</servlet-class>
       <init-param>
           <param-name>catalog</param-name>
           <param-value>Spring</param-value>
       </init-param>
    </servlet>
    <servlet-mapping>
       <servlet-name>catalog</servlet-name>
       <url-pattern>/catalog/*</url-pattern>
    </servlet-mapping>
    <session-config>
        <session-timeout>30</session-timeout>
        <cookie-config>
            <http-only>true</http-only>
        </cookie-config>
        <tracking-mode>COOKIE</tracking-mode>
    </session-config>
    <jsp-config>
        <jsp-property-group>
            <url-pattern>*.jsp</url-pattern>
            <scripting-invalid>true</scripting-invalid>
            <trim-directive-whitespaces>true</trim-directive-whitespaces>
        </jsp-property-group>
    </jsp-config>
    <error-page>
        <location>/error-page.html</location>
    </error-page>
    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
    <security-constraint>
    	<web-resource-collection>
    	    <url-pattern>/admin/*</url-pattern>
   		</web-resource-collection>
		<auth-constraint>
        	<role-name>Admin</role-name>
	    </auth-constraint>
	</security-constraint>
	<login-config>
		<auth-method>FORM</auth-method>
		<form-login-page>/loginPage.html<form-login-page>
		<form-error-page>/loginError.html<form-error-page>
	</login-config>
    <error-page>
    	<error-code>404</error-code>
		<location>/404.html</location>
    </error-page>
</web-app>
```

Example of loginPage.html
```
<form method="POST" action="j_security_check">
	<input type="text" name="j_username"/>
	<input type="password" name="j_password"/>
	<button type="submit">Enter</button>
</form>
```

Example of loginError.html
```
<p>Wrong password.</p>
```

