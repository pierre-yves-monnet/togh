
Specifications

# Datas
## Event:
  An event is the base. it contains 
  	* a name
  	* a start event / event event
  	* a list of participants
    * a splitwyse
    * a google meet
    * mono day
    	* a location
    	* hotel|airbnb
    	 
    * a source
    * if this is a Trip multi day
    	List of "days" : Source, Destination, Hotel|airbnb
    * a list of surveys
    * a list of "what I bring"
    
    
## Function
	Register a split wyse
	Add to my calendar
	Invite
	
    
    
## participants
	Search by Email, then search if the participant already exist as a TogetherUser. if not, create one
	* participantId
	* ShareMyLocation (Boolean)
	
	
	
	
	
## TogetherUser
   * Email
   * userName	


# installation
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

