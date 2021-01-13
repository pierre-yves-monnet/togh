# Installation

Tomcat server : D:\bonita\tomcat\Tomcat-9.0.41, port 7080
http://localhost:7080/Together

npm install
npm install -S carbon-components carbon-components-react carbon-icons

copier configuration/paths.js dans node_modules/react-scripts/config/path

mvn clean install


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


13 date picker
https://blog.bitsrc.io/13-react-time-and-date-pickers-for-2020-d52d88d1ca0b

Carbon:
https://www.carbondesignsystem.com/developing/react-tutorial/overview
install carbon
https://www.npmjs.com/package/carbon-components
npm install -S carbon-components carbon-components-react carbon-icons


Install react in tomcat
https://frugalisminds.com/deploy-react-js-in-tomcat/
npm install
mvn clean install



## Collects 
fetch("/login")
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
          
          
          
          
          
