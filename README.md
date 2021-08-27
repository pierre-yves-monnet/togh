# Development Installation
/* ******************************************************************************** */
/*                                                                                  */
/*  Installation Developement environnment                                          */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

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

## Lombok 
The https://projectlombok.org/setup/eclipse has to be installed in eclipse.

## mobile
https://flutter.dev/



## install npm
npx create-react-app togh

### Install carbon
https://medium.com/carbondesign/up-running-with-carbon-react-in-less-than-5-minutes-25d43cca059e

npm add carbon-components carbon-components-react carbon-icons
npm add node-sass@4.14.1

npm start



# Architecture
/* ******************************************************************************** */
/*                                                                                  */
/*  Architecture							                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


com.together.spring.ServletInitializer : this is the REST call

-----------------------------------------
com.togh.Application
package com.togh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
-----------------------------------------



com.together.service : Service to access information and data

https://start.spring.io/

## Date management
There is two king of date : Date+Time (Zoned) and Date
 *  Date+Time (Zoned) : this is an Instant. When I give for the event date, Feb 1, 14:00 ==> in Pacific time. A user in Paris time shoud see Feb 1, 23:00 (depends of the date, there is 8 or 9 hours delay)
 * Date : an absolute date. In an itinerary I give "Feb 3", that's mean it's Feb 3 and that it.
 
In Database, a Date+Time is saved as a LocalDateTime, UTC ( SPRING use a string: 2021-03-31 07:00:00), 
a Date in LocalDate. SPRING use a string : 2021-03-31

Browser return in all case a UTC Date, whatever the widget 
	"2026-08-07T07:00:00.000Z"
	+ the timezoneoffset: 480

So:
- for a LocalDateTime, there is nothing to do, it's already a UTC date
- for a LocalDateTime, calculation must be 1/ calculate the local time (date - offset), the get the date 	
     For example, if I give "Feb 3" in my widget at 20:00 in California, browser send "2021-02-04T05:00:00Z", timeoffset= 480. Then calculation get Feb3



 
Example : date of the event is a Date + Time (Zoned)
  That's mean 
 

# Development
/* ******************************************************************************** */
/*                                                                                  */
/*  Development  							                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


## React

Explication sur React:
https://fr.reactjs.org/docs/state-and-lifecycle.html

https://daveceddia.com/angular-directives-mapped-to-react/
https://fr.reactjs.org/docs/getting-started.html


https://www.taniarascia.com/getting-started-with-react/


https://www.pluralsight.com/guides/how-to-use-react-bootstrap's-popover
Mais ca ne marche pas car il faut du JS Voici comme il fait 
https://www.codeply.com/p/p5euzBO22C



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
https://react.semantic-ui.com/modules/dropdown/#types-search-selection


Comment in HTML/React : 
{/*this is a comment */}


## CSS et Color
https://colorbrewer2.org/#type=qualitative&scheme=Pastel1&n=7
http://colrd.com/color/0xffff6666/



## Spring;
https://www.baeldung.com/spring-new-requestmapping-shortcuts


## icon
<a href='https://www.freepik.com/vectors/icons'>Icons vector created by freepik - www.freepik.com</a>

<div>Icons made by <a href="https://www.flaticon.com/authors/dinosoftlabs" title="DinosoftLabs">DinosoftLabs</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

<div>Icons made by <a href="https://www.flaticon.com/authors/retinaicons" title="Retinaicons">Retinaicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

https://www.flaticon.com/

Rendre une image fond transparent
https://www.remove.bg/fr/upload 


import { PlusCircle, ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';


Marron : 210/165/108	#d2a66c
   		206/139/84		#ce8b54
		189/126/73		#bd7d49
		150/98/61		#96623d
		130/80/45		#82502d
		
Lounes : $5 / icons

Chat 				: OK
Task 				: OK
Shopping List 		: OK
Cypris				: OK
Participant 		: OK          
Itinerary 			: Ok
Survey 				: Revoir un peu la ligne pour ne pas faire 3 zones identiques 
Geolocalisation 	: a revoir
Photo 				: a revoir
Frais 				: a revoir
references			: todo / Engrenages
budget				: todo / sac ou tirelire
Default Boy			: ok (togh)
Default girl		: Ok (cypris)




Starter 		: todo
Ender 			: todo
Point d'interet	: todo
Visite			: todo
Achat 			: sac de courses
Divertissement	: todo
Aeroport		: todo
Stations de bus	: todo
gare			: todo
port			: todo
restaurant		: todo
nuit (hotel)	: todo



const ITINERARYITEM_POI 		= "POI";
const ITINERARYITEM_BEGIN		= "BEGIN";
const ITINERARYITEM_END			= "END";  
const ITINERARYITEM_SHOPPING	= "SHOPPING";  ok
const ITINERARYITEM_AIRPORT		= "AIRPORT";
const ITINERARYITEM_BUS			= "BUS";
const ITINERARYITEM_TRAIN		= "TRAIN";
const ITINERARYITEM_BOAT		= "BOAT";
const ITINERARYITEM_NIGHT		= "NIGHT";
const ITINERARYITEM_VISITE		= "VISITE";
const ITINERARYITEM_RESTAURANT	= "RESTAURANT";
const ITINERARYITEM_ENTERTAINMENT = "ENTERTAINMENT" 





##Structure
componentDidUpdate(prevProps) {
		 JSON.stringify(this.props.positions));
		if (prevProps.positions !== this.props.positions) {
			this.setState({ positions: this.props.positions });
		}
		

## React structure
 {names.map(function(name, index){
                    return <li key={ index }>{name}</li>;
                  })}
                  

Contexte
https://fr.reactjs.org/docs/context.html

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



# Open question
/* ******************************************************************************** */
/*                                                                                  */
/*  Open Question							                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

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
 //https://www.baeldung.com/apache-commons-beanutils
 	==> Beanutils.copy
 	PersistenceUtil.copyNonNullProperties(u, user);
https://github.com/chDame/fabulexie/blob/c7471210f2c6df6f7cf6d9022986a24aa11d48b0/[…]e-backend/src/main/java/org/fabulexie/util/PersistenceUtil.java
 	
 
 
## profile
See https://www.baeldung.com/spring-profiles
-Dspring.profiles.active=dev

## JPA
https://www.baeldung.com/spring-data-jpa-query
 


          
## Commentaire

/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

/* ******************************************************************************** */
/*                                                                                  */
/*  EventController,                                                                 */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

React:
// -----------------------------------------------------------
//
// EventExpense
//
// Display one event
//
// -----------------------------------------------------------


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------


	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------
	
## tool for react : 
  
  vscode
  react hooks


# API
/* ******************************************************************************** */
/*                                                                                  */
/*  API										                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

## google  
API Key AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es



https://console.cloud.google.com/apis

https://code.google.com/archive/p/google-translate-api-v2-java/ ==> 2011

https://cloud.google.com/translate/docs/quickstarts
 	
stocker les photo de l'API Key pour Flick


# Cloud & docker
/* ******************************************************************************** */
/*                                                                                  */
/*  Cloud									                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


# docker image tomcat:
docker pull tomcat
docker pull ubuntu
docker run --name togh -h localhost -v d:/tmp/docker:/opt/togh -d -p 8080:8080 tomcat

## Creation du container togh

docker run --name togh -h localhost -v d:/tmp/docker:/opt/togh -d -p 8080:8080 togh



-------------------------------------- container vide
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
  
  
## domain:

www.1and1.com

  
## CI
https://tomgregory.com/building-a-spring-boot-application-in-jenkins/


# Administration
/* ******************************************************************************** */
/*                                                                                  */
/*  Administration							                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

  private static final String TOGHADMIN_EMAIL = "toghadmin@togh.com";
    private static final String TOGHADMIN_USERNAME = "toghadmin";
    private static final String TOGHADMIN_PASSWORD = "togh";
 
# Database 
update eventuser set name='birthday' where id=31;

insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(40,'local', 1, 1, 30,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(41,'local', 1, 1, 28,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(42,'local', 1, 1, 29,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(43,'local', 1, 1, 5,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(44,'local', 1, 0, 33,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(45,'local', 2, 2, 3,31);

               
# Tasks

/* ******************************************************************************** */
/*                                                                                  */
/*  Tasks									                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

	 
	
	

Simon 	Anchor
Simon ? Comment afficher un itineraire avec google-map-react (attention, map sans S car une autre librairie existe google-maps-react)

Simon ?	Date Carbon: le format de date ne respecte pas la langue
	
	Comment surcharger une class CSS ? bx--content-switcher-btn bx--content-switcher--selected ==> changer le fond noir en fond plus doux
		.togh.bx--content-switcher--selected ==> Marche pas
		
CSS : le footer n'est pas tout a fait en bas et il est 1 pixel en haut
	
Py	

10.	Refresh sur plusieurs postes : Walter modifie une valeur, elle doit etre repercutée ailleurs
11. Revoir la deconnection, le feedback user quand on fait un save



12. Verifier : admintogh creation et login (admintogh d'un coté, et toghadmin@togh.com de l'autre et j'arrive plus a me connecter
13. Verifier : quand un user se connecte, on recupere bien dans toghentity ses propres tips preference?

	
		
V2		
	Possibilite de supprimer une invitation dans un event (et dans ce cas, l'user peut etre supprimer s'il n'était invoqué que dans un event)
	
	Event/Participant: mettre l'icone + un component qui donne toutes les coordonnées
 	
	MonitorService.registerErrorEvents() ==> Implementer
	
	Changer le niveau d'un utilisateur de FREE a PREMIUM => TOus les events dont c'est l'owner doit changer
	Expense
	Preference affichage
	Budget
	Administration Statistics usage
	
	Integrer weather  : https://openweathermap.org/api
	
	RestLoginController : google donne la langue preferer de l'utilisateur, quand on cree un user, on pourrais la stocker
	Permettre a un utilisateur de changer son username
	
	My Friend = recuperer les users de google
	Integrate in Google Calendar
	Integrate in Google Drive
	Coder whatismyip dans l'administration
		
    Survey : first, present the VIEW display if there is an survey      

	Admin gestion user
		- invited + password : c'est un probleme, l'utilisateur ne peut plus se connecter
		- invited depuis trop longtemps : a purger
		- inactif depuis trop longtemps : 

## bugs
	

Deconnection : ca ne renvoi plus sur la page de login      
          

EventEntity.getMap : normaliser les constante ici avec les SLAB_OPERATION (une seule constante)
EventSurvey : ca ne marche pas dajouter un nouveau choix de survey
  Et arriver par defaut sur les reponses, pas sur la definition
          

      