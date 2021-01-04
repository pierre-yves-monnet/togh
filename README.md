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



# Avancée - question avec Christophe

## Créer la table et sauver des données:

1/
***************************
APPLICATION FAILED TO START
***************************

Description:

Field eventRepository in com.together.spring.Application required a bean of type 'com.together.data.entity.EventRepository' that could not be found.

The injection point has the following annotations:
	- @org.springframework.beans.factory.annotation.Autowired(required=true)


Action:

Consider defining a bean of type 'com.together.data.entity.EventRepository' in your configuration.
==> Jamais trouvé la root cause, ca ne se produit plus

2/
Mais quand je demande un create, j'ai un

java.lang.NullPointerException: null
	at com.together.service.EventService.createEvent(EventService.java:35) ~[classes/:1.0.0]
	at com.together.spring.Application.newevent(Application.java:69) ~[classes/:1.0.0]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_144]


==> C'est la facon de Spring de dire que la table n'est pas crée?
En effet, la table n'est pas créée. Rien dans les tutorial que je vois indique qu'il faut que je crée la table

Et pourquoi je vois ca 
2020-12-27 10:52:17.218  INFO 21352 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFERRED mode.
2020-12-27 10:52:17.300  INFO 21352 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 51ms. Found 0 JPA repository interfaces.

puis ca
org.springframework.beans.factory.BeanDefinitionStoreException: Failed to read candidate component class: file [D:\bonita\tomcat\Tomcat-9.0.41\wtpwebapps\togh\WEB-INF\classes\com\together\repository\spring\EventSpringRepository.class]; nested exception is java.nio.channels.ClosedByInterruptException


3/ Script de creation
En suivant https://www.baeldung.com/spring-data-jpa-generate-db-schema
 le fichier est bien crée, mais vide !
 





## Les services
Bon, je ne vois pas comment on crée des services. L'avantage que je vois des services comme Bonita les utilisent, c'est qu'on peut avoir 2 implementation du service, et pouvoir basculer via la configuration d'une implementation à l'autre.
Comment faire cela?

Example : comment depuis ma classe LoginService, j'accede a ma classe UserService ? 
Example 2 : j'ai 2 implementation de EventRepository ( EventMemRepository & EventSpringRepository). Comment puis passer la bonne classe a mon EventService?
Actuel :
	celui qui pilote est Application.java. J'ai donc un "serviceAccessor" avec 2 implementations : MemoryServiceAccessor a SpringServiceAccessor. Et Application donne un serviceAccessor a chaque service
	Mais justement Spring fait ca : avec Bonita, on precise dans un XML quel est l'implementation des services : ici, je voudrais donner l'implementation des EventRepository.
	


# React or Angular ?
Quand je vois ca 
https://stackoverflow.com/questions/43927144/react-equivalent-to-ng-model
et ca
https://reactjsnews.com/NgRepeat-Equivalent-in-React#:~:text=React%20Alternative%20to%20ng%2Drepeat,native%20iterator%20directly%20in%20React.
je me dit qu'Angular est vraiment mieux.

ES6 + Angular ?



https://daveceddia.com/angular-directives-mapped-to-react/
https://fr.reactjs.org/docs/getting-started.html


https://www.taniarascia.com/getting-started-with-react/


Spring;
https://www.baeldung.com/spring-new-requestmapping-shortcuts

## Collects 
fetch("/login")
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
          
