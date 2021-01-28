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


I created a service

@Service
public class UserService {

Ok, fine, then now I want to access this service from 2 different classes.
Do I have to do that in my two classes:

public class RestEventControler {

    @Autowired
    private UserService userService;

---------------------
public class EventService {

    @Autowired
    private UserService userService;

S1: Is that what we expect? is the userService created is the same instance, or 2 different instance (can I use some private variable in my userService class?)
	IVAN ==> The default scope for a Service is singleton 
	PY ==> Perfect, that when I want. The point is in the second class, I got a userService == null 
	
	
	
S2: Additionnal question: I see in Spring it's possible to have multiple implementation for a service, and then in a XML, declare which implementation you can use. It's possible to do that in XML (that the way my company does).
How to do that in my application? I don't have any XML currently and it's working...
 IVAN: You can use @Primary to denote the default implementation if you have more than one available to the JVM.
 PY: Interresting. And how do you switch between them? Imagine that I have 2 Service "EventServiceBlue" and "EventServiceRed". 
 	- First, I should have maybe an interface or an abstract classe somewhere ? And this abstract class should have the tag @Service
 	- second, in which XML / or whatever file do I select the implementation Blue or Red ? Do you have a documentation somewhere about that?

S3: I will need a scheduler. Something to execute a method every hour (to send email, chech status...). Is a scheduler service/ class exists in Spring?
	 IVAN: try @Scheduled
	 PY => Thank you
	  


## JPA
I red a lot in Spring boot, https://www.baeldung.com/ website. I still have some question.

------------------
Update my database

I setup a datasource in my Spring Application (
## PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/together
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

and it's working : tables are created.
BUT... when I modify some information in my entity, I don't see any change in my database.
For Example, I did that
	@Column(name = "lastName", length = 100)
	private String lastName;
And I noticed that the database column is "last_name" that I don't like. So I rename my field with "lastname" but the database does not change (Sprint restarted).
JPA1: Why not?

JPA2: Second question: imagine that I have now my application in production. Then, I have a new version to deploy: how can I get BEFORE the SQL script to update my database?
In that situation, I don't want to drop column "last_name", then create "lastname" but I want to perform an "alter table ..." : how do I can detect changes, to prepare my SQL Script to update my database? I would like a tool which compare the existing schema with the new one and propose to me a SQL Script to update it.
	IVAN:  JPA2: I don't know of a tool to automatically convert your database schema.  How would it figure out what the old column name vs the new column name without you telling it?  So I expect you would need to manually convert your database.  There is probably a nice GUI out there to make it a little easier. 
	PY : I would expect a) a detection tool, b) a SQL script that I can review / then complete. That what I did in my previous company (we didn't use Hibernate). How do YOU do? You let Hibernate update for you your database?
	
	

------------------
* Composition 1=n
I have an object (EventEntity), which reference a subentity (ParticipantEntity). This is a composition, which is means this ParticipantEntity is referenced only un the EventEntity. It the same as a "Invoice/InvoiceLines".
When I create a EventEntity, I want to add Participants, and when I save/load the EventEntity, I want Hibernate to save/load Participants.
 
In the database, I'm expected to have fields 
- Participant.eventId foreign key Event.id
- Participant.eventOrder int
(second field to get an ordered list).

I declared that 
EventEntity:
   @OneToMany(fetch = FetchType.EAGER)
    private List<ParticipantEntity> participants;

ParticipantEntity:
   @ManyToOne
    @JoinColumn(name="eventid")
    private EventEntity event;

JPA3: Is that correct ?
	IVAN:  @OneToMany(
      mappedBy = "event",
      cascade = CascadeType.ALL,
      orphanRemoval = true
   ) 
   PYM: Perfect ! It's work fine!: Thank you !

To be honnest, I still not understand the role of the mappedof
----------------
Aggregation 1=>1
In Event,I link an Author, which is a ToghUser. This is a aggregation: a ToghUser is referenced in a lot of tables. And a ToghUser is created completely independately.
I don't want to load the User when I load an event, but when I call ThogUser user = event.getAuthorId() I want Hibernate to return to me the user.
In the database, I'm expected that field

Event.authorId foreignKey ThogUser.id

I declared that
   @Column(name="AUTHOR")
    private ThogUserEntity author;
    
   ==> Caused by: org.hibernate.MappingException: Could not determine type for: com.togh.entity.ToghUserEntity, at table: participant, for columns: [org.hibernate.mapping.Column(author)]
    
JPA4: Is that correct ?

BUT, as you know, in a application, it's very efficient to manipulate have only ID (specialy when the source is a REST call).
So, I balance between saving and manipulating only ID.
For example, instead to have a method
 setAuthor( ThogUserEntity author )
I imaging to have
 setAuthor( Long authorId )
 
My Java conception prefer the first method. My company use the second approach in the API and I have to say this is more confortable to use it (you saved a lot of 
ThogUser user = UserService.getFromId( authorId )
setAuthor( user )

JPA5: So, currently I'm 60/40. What do you thing? What is your approach with Spring?
 
 
----------------------
Manager enum
I do that
  public enum StatusEventEnum {
        INPREPAR, INPROG, CLOSED, CANCELLED;
        }

 @Column( name="status")
  private StatusEventEnum statusEvent;
  
JPA6: And then I discover that the database saved the value as an integer (INPREPAR =0 for example). Hum. The point is if my enum change (I change the order for example), I will completely "foutre le border" in my application. Is there is a way to ask Hibernate to save the value as a STRING?

--------------------
Set of attribut
IN general, I love to manipulate not attributes, but Map + constante
For example, to have this implementation
Map<String,Object> attributes = new HashMap<>();

public void setName( String name) {
 attributes.put(CST_NAME, name );
}
public String getName() {
	return (String) attributes.get( CST_NAME, name);
}

then, it's easiest to get at one shot all attributes, or to decide, for example, on a REST CALL, which attributes I want to return (all, or if I want to hide some attributes for a permission reason).
Is that possible to implemente that?
Can I do something like a list of 
@Column() 
and manipulate my attributes map? 
JPA7: I'm afraid if I do that the "userEntity.save()" does not works. Is that possible to ask this save() to use the getter?


JPA8: I got an error

   @OneToOne
    @JoinColumn(name = "author_id")
    private ToghUserEntity author;

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'eventRepository' defined in com.togh.repository.EventRepository defined in @EnableJpaRepositories 
declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Cannot resolve reference to bean 'jpaMappingContext' while setting bean property 'mappingContext'; 
nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jpaMappingContext': 
Invocation of init method failed; nested exception is javax.persistence.PersistenceException: 
[PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.MappingException: 
Could not determine type for: com.togh.entity.ToghUserEntity, at table: participant, for columns: [org.hibernate.mapping.Column(author)]

According https://www.baeldung.com/spring-data-rest-relationships, should be Ok

	
# docker
user togh: pass4togh

## Creation d'une image a partir de tomcat
pull tomcat ou qq chose comme ca

## Creation du container togh
docker run --name togh -h localhost -v /tmp/docker:/opt/togh -d -p 8080:8080 togh

Ajout des composants vi & postgres
apt-get update
apt-get install vi
apt-get install postgresql postgresql-contrib
 
 su - postgres
 pg_ctlcluster 11 main start
 
 creation de la la base de donnée
 \conninfo
 psql
 CREATE DATABASE together;
 
 Ajout de npm 
 A faire
 
 
 
 ## Sauvegarder le container
 
 ## importer le container
  
 
 Arret de docker
  wsl --shutdown
  

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


https://colorbrewer2.org/#type=qualitative&scheme=Pastel1&n=7
https://react.semantic-ui.com/modules/dropdown/#types-search-selection

Eleanora: epatricola@hotmail.com

# 2 servers
Comment avoir 2 servers?
J'ai donc maintenant npm sur 3000
Spring sur 7080

Comment faire un
    fetch('api/login?', requestOptions)
?
1er option : mettre en dur le serveur    

   	fetch('http://localhost:7080/togh/api/login?', requestOptions)
==> Fonctionne, mais ne passera pas pour la production. Comment mettre cette header en properties quelques part ?

2 eme option: mettre un proxy
https://create-react-app.dev/docs/proxying-api-requests-in-development/
Mais cela ne marche pas pour moi:
[HPM] Error occurred while trying to proxy request /api/login? from localhost:3000 to http://localhost:7080 (ECONNREFUSED) (https://nodejs.org/api/errors.html#errors_common_system_errors)


3eme option mettre dans le package.json
"proxy": {
    "/api/*":  {
      "target": "http://localhost:8000",
      "secure": false
    }
  },
==> Proxy doit etre une String


## install npm
npx create-react-app togh

### Install carbon
https://medium.com/carbondesign/up-running-with-carbon-react-in-less-than-5-minutes-25d43cca059e

npm add carbon-components carbon-components-react carbon-icons
npm add node-sass@4.14.1

npm start


# Spring profile
See https://www.baeldung.com/spring-profiles
-Dspring.profiles.active=dev
 
          
          
          
          
          
