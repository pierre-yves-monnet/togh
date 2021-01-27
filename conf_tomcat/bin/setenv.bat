@echo off

rem Set some JVM system properties required by Bonita


rem Optional JAAS configuration. Usually used when delegating authentication to LDAP / Active Directory server
rem set SECURITY_OPTS="-Djava.security.auth.login.config=%CATALINA_HOME%\conf\jaas-standard.cfg"

rem Pass the JVM system properties to Tomcat JVM using CATALINA_OPTS variable
set CATALINA_OPTS=-D -Dfile.encoding=UTF-8 -Xms1024m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -Dspring.profiles.active=h2

set CATALINA_PID=%CATALINA_BASE%\catalina.pid

