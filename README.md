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

Note: 
le projet doit etre not� "Dynamic web application" sinon Spring ne veut pas d�marr� ( ? )
Click droit => Properties => Project Facet => Dynamic Web Application

## mobile
https://flutter.dev/

# Architecture

com.together.spring.SprintApplication : this is the REST call
com.together.service : Service to access information and data

https://start.spring.io/

 
# google 
API Key AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es

https://console.cloud.google.com/apis

https://code.google.com/archive/p/google-translate-api-v2-java/ ==> 2011

https://cloud.google.com/translate/docs/quickstarts
API KEY Google Translate : AIzaSyAqzQn9yET9inp9RmDm7to4unFO3df82Fg
 	

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
 
## cloud

Deployer une image docker: 	
https://cloud.google.com/kubernetes-engine/docs/tutorials/hello-app
 
Deployer Postgres
https://cloud.google.com/solutions/deploying-highly-available-postgresql-with-gke
 
## Sauvegarder le container
 
## importer le container
  
 
## Arret de docker
  wsl --shutdown
  
# CI
https://tomgregory.com/building-a-spring-boot-application-in-jenkins/

# React

Explication sur React:
https://fr.reactjs.org/docs/state-and-lifecycle.html

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


# icon
<a href='https://www.freepik.com/vectors/icons'>Icons vector created by freepik - www.freepik.com</a>

<div>Icons made by <a href="https://www.flaticon.com/authors/dinosoftlabs" title="DinosoftLabs">DinosoftLabs</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

<div>Icons made by <a href="https://www.flaticon.com/authors/retinaicons" title="Retinaicons">Retinaicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

https://www.flaticon.com/

Rendre une image fond transparent
https://www.remove.bg/fr/upload 

Participant : 3 personnages          
Itinerary : a revoir
Chat : OK
Task : OK
Shopping List : OK
Survey : Revoir un peu la ligne pour ne pas faire 3 zones identiques 
Geolocalisation : a revoir
Photo : a revoir
Frais : a revoir

Starter 		: todo
Ender 			: todo
Default Boy		: todo
Default girl	: todo


const ITINERARYITEM_POI 		= "POI";
const ITINERARYITEM_BEGIN		= "BEGIN";
const ITINERARYITEM_END			= "END";
const ITINERARYITEM_SHOPPING	= "SHOPPING";
const ITINERARYITEM_AIRPORT		= "AIRPORT";
const ITINERARYITEM_BUS			= "BUS";
const ITINERARYITEM_TRAIN		= "TRAIN";
const ITINERARYITEM_BOAT		= "BOAT";
const ITINERARYITEM_NIGHT		= "NIGHT";
const ITINERARYITEM_VISITE		= "VISITE";
const ITINERARYITEM_RESTAURANT	= "RESTAURANT";
const ITINERARYITEM_ENTERTAINMENT = "ENTERTAINMENT"









## React structure
 {names.map(function(name, index){
                    return <li key={ index }>{name}</li>;
                  })}
                  

## Carbon
import { DatePicker } from 'carbon-components-react';
import { DatePickerInput } from 'carbon-components-react';
import { TimePicker } from 'carbon-components-react';
import { TimePickerSelect } from 'carbon-components-react';
import { RadioButtonGroup } from 'carbon-components-react';
import { RadioButton } from 'carbon-components-react';
import { TextInput } from 'carbon-components-react';
import { TextArea } from 'carbon-components-react';
import { Select } from 'carbon-components-react';
import { SelectItem } from 'carbon-components-react';
import { Tag } from 'carbon-components-react';


					<RadioButtonGroup
							valueSelected={this.state.panelVisible}
							legend=""
							name="type"
							onChange={(event) => {
								console.log("Invitation.Change type="+event);        					
								this.setState( {"panelVisible": event})}
								}
							>
							<RadioButton value="INVITATION" id="invitation_r1" labelText={<FormattedMessage id="Invitation.ByEmail" defaultMessage="Send an Email"/>} labelPosition="right" />
							<RadioButton value="SEARCH" id="invitation_r2"  labelText={<FormattedMessage id="Invitation.SearchAUser" defaultMessage="Search a user"/>} labelPosition="right"/>
						</RadioButtonGroup>     

## Translation
https://phrase.com/blog/posts/react-i18n-best-libraries/

https://www.freecodecamp.org/news/setting-up-internationalization-in-react-from-start-to-finish-6cb94a7af725/


https://lokalise.com/blog/react-i18n-intl/
{
 "app.channel.plug": "Tutorial brought to you by {blogName}"
}

<FormattedMessage
 id = "app.channel.plug"
 defaultMessage="Tutorial brought to you by Lokalise"
 values = {{blogName: "Lokalise"}}
/>

--------- from JS
import { injectIntl, FormattedMessage } from "react-intl"; 
		const intl = this.props.intl;
 title={intl.formatMessage({id: "EventShoppingList.removeItem",defaultMessage: "Remove this item"})}
export default injectIntl(EventShoppingList);

Extraction : follow
https://formatjs.io/docs/getting-started/message-extraction/
> npm i -D @formatjs/cli
> npm run extract



## install npm
npx create-react-app togh

### Install carbon
https://medium.com/carbondesign/up-running-with-carbon-react-in-less-than-5-minutes-25d43cca059e

npm add carbon-components carbon-components-react carbon-icons
npm add node-sass@4.14.1

npm start


# Spring
 Comment faire marcher le @configuration ?
 
 Comment locker un record dans la table ?
 Voici mon cas d'uisage. Une evenement peut etre modifié en meme temps par 2 utilisateurs, donc 2 threads en meme temps.
 Il me faut donc, quand je veut modifier l'element, faire un "lock Event ID=444 / Read Event id=444/ My manipulation / Save Eventid=444 / Unlock ID=444
 Et si le thread n'arrive pas a faire le lock, je vais mettre en place une strategie tel "sleep 5 s / reesaaye
 Si j'en crois 
 https://www.baeldung.com/java-jpa-transaction-locks
 https://www.baeldung.com/jpa-pessimistic-locking
 
 SQL ACID Atomic Consistent Isolation...
 
 Je devrais mettre devant ma methode
 
 @Lock(LockModeType.PESSIMISTIC_WRITE)
mais tous les exemples sont pour des requetes en READ. 
De plus, moi je veux mettre cet @ dans ma classe d'application (qui est un @Service, et j'ai l'impression que c'est a mettre que devant les SELECT pour proteger les selects
je me trompe ? 

 
 * comment faire un update sur un champ?
 Ma REST API va etre du type "UPDATE / ROOT / DESCRIPTION" value="Ceci est ma nouvelle description"
 "UPDATE / ININERARY / Pid=111 / NAME" "Visite du musée
 Le serveur va faire / read event / Update description='Ceci est ma nouvelle description' / Save
 
 Comment je met a jour un seul champ de maniere dynamique ? Je voudrais avoir une methode "setAttribut( name, value)" ?
 	==> Beanutils.copy
 	PersistenceUtil.copyNonNullProperties(u, user);
https://github.com/chDame/fabulexie/blob/c7471210f2c6df6f7cf6d9022986a24aa11d48b0/[…]e-backend/src/main/java/org/fabulexie/util/PersistenceUtil.java
 	
 
 
## profile
See https://www.baeldung.com/spring-profiles
-Dspring.profiles.active=dev

## JPA
https://www.baeldung.com/spring-data-jpa-query
 
# Database 
update eventuser set name='birthday' where id=31;

insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(40,'local', 1, 1, 30,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(41,'local', 1, 1, 28,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(42,'local', 1, 1, 29,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(43,'local', 1, 1, 5,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(44,'local', 1, 0, 33,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(45,'local', 2, 2, 3,31);


## outils
          
               
# Tasks


	 
	Trouver un bon Radio box (Taches : Toutes taches / Tache planifiée / En cours / Realisées
	==> Utiliser le radio button de Bootstap
	

Simon ?	Google Geocodage d'une adresse
Simon ?	Affichage d'un itineraire
Simon ?	comment faire une saisie de currency en carbon : TextNumber => pas de virgule
Simon ?	Date Carbon: le format de date ne respecte pas la langue
	
	
	Access this in a iterator: example dans EventTaskList, je veux pouvoir afficher (ou pas) la liste des taches suivant des indicateurs dans this.state. mais le this n'est pas accessible dans la sous classe
	
	Comment surcharger une class CSS ? bx--content-switcher-btn bx--content-switcher--selected ==> changer le fond noir en fond plus doux
		.togh.bx--content-switcher--selected ==> Marche pas
		

	
Py	Sauvegarde
	faire marcher la requete JPA "user not register in the event"
	Survey
	
	Itineraire
	Depenses
	Integrer weather  : https://openweathermap.org/api
	My profile
	My Friend = recuperer les users de google
	Integrate in Google Calendar
	Integrate in Google Drive
	
          
          
          
