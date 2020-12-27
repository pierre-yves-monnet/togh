# Installation

Tomcat server : D:\bonita\tomcat\Tomcat-9.0.41, port 7080
http://localhost:7080/Together

## base de donnée

Une base de donnée Postgres est crée. Le fichier applications.ressources défini cela:

spring.datasource.hikari.connectionTimeout=20000
spring.datasource.hikari.maximumPoolSize=5

spring.datasource.url=jdbc:postgresql://localhost:5432/together
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

## Eclipse

https://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/

Puis faire sur le projet un "Debug in server"
==> Le war va demarrer



# Architecture

com.together.spring.SprintApplication : this is the REST call
com.together.service : Service to access information and data

https://start.spring.io/



# Avancée

## Créer la table et sauver des données:

***************************
APPLICATION FAILED TO START
***************************

Description:

Field eventRepository in com.together.spring.Application required a bean of type 'com.together.data.entity.EventRepository' that could not be found.

The injection point has the following annotations:
	- @org.springframework.beans.factory.annotation.Autowired(required=true)


Action:

Consider defining a bean of type 'com.together.data.entity.EventRepository' in your configuration.


## Les services
Bon, je ne vois pas comment on crée des services. L'avantage que je vois des services comme Bonita les utilisent, c'est qu'on peut avoir 2 implementation du service, et pouvoir basculer via la configuration d'une implementation à l'autre.
Comment faire cela?


## React or Angular ?
Quand je vois ca 
https://stackoverflow.com/questions/43927144/react-equivalent-to-ng-model
et ca
https://reactjsnews.com/NgRepeat-Equivalent-in-React#:~:text=React%20Alternative%20to%20ng%2Drepeat,native%20iterator%20directly%20in%20React.
je me dit qu'Angular est vraiment mieux.

ES6 + Angular ?

