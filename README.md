# Development Installation
/* ******************************************************************************** */
/*                                                                                  */ /*  Installation Development
environnment                                          */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


## Database

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

## Intellij
Launch the class com.togh.ServerInitializer.

## Lombok

The https://projectlombok.org/setup/eclipse has to be installed in eclipse.

## mobile

https://flutter.dev/

## using h2

Using h2 database instead of postgres -Dspring.profiles.active=dev or --spring.profiles.active=h2

# Architecture

/* ******************************************************************************** */
/*                                                                                  */ /*
Architecture                                                                    */
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
/*                                                                                  */ /*
Development                                                                    */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

## Install React from scratch

```
npx create-react-app togh
```

Downgrade : modify package.json

```
"react": "17.0.2",
"react-dom": "17.0.2",
"react-scripts": "4.0.1",
````

change in index.js:

````
import ReactDOM from 'react-dom';
ReactDOM.render(<App />, document.getElementById('root'));
````

Create index.scss

````
@import 'carbon-components/scss/globals/scss/styles.scss';
````

Allow the indirect reference in import:
See https://saurabhshah23.medium.com/react-app-with-absolute-paths-using-jsconfig-json-2b07b1cb24d4
and create a jsonfig.js

````
cp configuration/paths.js node_modules/react-scripts/config/path
````

## Carbon:

https://medium.com/carbondesign/up-running-with-carbon-react-in-less-than-5-minutes-25d43cca059e
https://www.carbondesignsystem.com/developing/react-tutorial/step-1/
https://github.com/carbon-design-system/carbon-tutorial/tree/master/src

https://www.carbondesignsystem.com/developing/react-tutorial/overview

```
  
npm install carbon-components@10.25.0 carbon-components-react@7.25.0 @carbon/icons-react@10.22.0 carbon-icons@7.0.7
npm install sass@1.29.0
```

-------------------------------- OLD
"@carbon/react": "^1.0.3",
"react": "17.0.2",
"react-scripts": "5.0.0",
"@testing-library/jest-dom": "^5.16.4",
"@testing-library/react": "^13.0.1",
"@testing-library/user-event": "^13.5.0",
"babel-plugin-react-intl": "^8.2.25",
"google-map-react": "^2.1.10",
"react-autosuggest": "^10.1.0",
"react-axios": "^2.0.5",
"react-bootstrap-icons": "^1.8.1",
"react-chartjs-2": "^4.1.0",
"react-currency-input": "^1.3.6",
"react-custom-flag-select": "^3.0.7",
"react-dom": "17.0.2",
"react-geocode": "^0.2.3",
"react-google-login": "^5.2.2",
"react-intl": "^5.24.8",
"web-vitals": "^2.1.4"

-------------------------------- 

## other library

```
npm install axios
npm install react-axios --force
npm install chart.js
npm install react-chartjs-2 
npm install react-custom-flag-select
npm install react-bootstrap-icons
npm install react-google-login
npm install react-autosuggest
npm install react-geocode
npm install react-currency-input --force
npm install google-map-react
npm install react-intl --force
npm install babel-plugin-react-intl

npm install lodash@4.17.21

npm config set python D:\atelier\Python\Python310\python.exe 
npm install --global windows-build-tools

```

## Translation

1. Installation
   https://www.freecodecamp.org/news/setting-up-internationalization-in-react-from-start-to-finish-6cb94a7af725/

Extraction: visit
https://formatjs.io/docs/getting-started/message-extraction/

```
> npm install -D @formatjs/cli
```

Add in package.json

```
"extract": "formatjs extract src/**/*.jsx --out-file lang/en.json"
```

2. Usage

```
> cd npm
> npm run extract
```

https://phrase.com/blog/posts/react-i18n-best-libraries/

https://lokalise.com/blog/react-i18n-intl/
{
"app.channel.plug": "Tutorial brought to you by {blogName}"
}

<FormattedMessage id = "app.channel.plug"
defaultMessage="Tutorial brought to you by Lokalise"
values = {{blogName: "Lokalise"}} />

--------- from JS import { injectIntl, FormattedMessage } from "react-intl";

const intl = this.props.intl;

title={intl.formatMessage({id: "EventShoppingList.removeItem",defaultMessage: "Remove this item"})}

export default injectIntl(EventShoppingList);

Extraction : follow
https://formatjs.io/docs/getting-started/message-extraction/

## Explication sur React:

https://fr.reactjs.org/docs/state-and-lifecycle.html

https://daveceddia.com/angular-directives-mapped-to-react/
https://fr.reactjs.org/docs/getting-started.html

https://www.taniarascia.com/getting-started-with-react/

https://www.pluralsight.com/guides/how-to-use-react-bootstrap's-popover
Mais ca ne marche pas car il faut du JS Voici comme il fait
https://www.codeply.com/p/p5euzBO22C



13 date picker
https://blog.bitsrc.io/13-react-time-and-date-pickers-for-2020-d52d88d1ca0b

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

| Icon | Status |
|---|---|
|Chat                | OK/Paid|
|Task                | OK/Paid| 
|Shopping List       | OK/Paid |
|Participant        | OK/Paid   |  
|Itinerary            | Ok/Paid |
|Survey              |OK /Paid |
|Geolocalisation    | OK/|
|Paid Photo              |OK/Paid| 
|Frais                | OK/Paid |
|references            | todo / |
|Engrenages ||
|budget                |OK/Paid /| 
|sac ou tirelire | OK/Paid |
|Default Boy            | ok/Paid (togh)|
|Default girl        | Ok/Paid (cypris)|
|Starter        | OK/Paid |
|Ender            | OK/Paid| 
|Point d'interet    | todo |
|Visite            | todo|
|Achat            | sac de courses| 
|Divertissement    | todo |
|Aeroport        | todo |
|Stations de bus   | todo|
|gare            | todo |
|port            | todo |
|restaurant       | todo |
|nuit (hotel)    | todo|

const ITINERARYITEM_POI = "POI"; const ITINERARYITEM_BEGIN = "BEGIN"; const ITINERARYITEM_END = "END";  
const ITINERARYITEM_SHOPPING = "SHOPPING"; ok const ITINERARYITEM_AIRPORT = "AIRPORT"; const ITINERARYITEM_BUS = "BUS";
const ITINERARYITEM_TRAIN = "TRAIN"; const ITINERARYITEM_BOAT = "BOAT"; const ITINERARYITEM_NIGHT = "NIGHT"; const
ITINERARYITEM_VISITE = "VISITE"; const ITINERARYITEM_RESTAURANT = "RESTAURANT"; const ITINERARYITEM_ENTERTAINMENT = "
ENTERTAINMENT"

## Structure

componentDidUpdate(prevProps) { JSON.stringify(this.props.positions)); if (prevProps.positions !== this.props.positions)
{ this.setState({ positions: this.props.positions }); }

## React structure

{names.map(function(name, index){ return <li key={ index }>{name}</li>; })}

Contexte
https://fr.reactjs.org/docs/context.html

## Aria role

To avoid the warning Line 418:101:  Elements with ARIA roles must use a valid, non-abstract ARIA role jsx-a11y/aria-role

Add role="group" aria-label="Status"

# Open question

/* ******************************************************************************** */
/*                                                                                  */ /*  Open
Question                                                                    */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

Comment locker un record dans la table ? Voici mon cas d'uisage. Une evenement peut etre modifié en meme temps par 2
utilisateurs, donc 2 threads en meme temps. Il me faut donc, quand je veut modifier l'element, faire un "lock Event
ID=444 / Read Event id=444/ My manipulation / Save Eventid=444 / Unlock ID=444 Et si le thread n'arrive pas a faire le
lock, je vais mettre en place une strategie tel "sleep 5 s / reesaaye Si j'en crois
https://www.baeldung.com/java-jpa-transaction-locks
https://www.baeldung.com/jpa-pessimistic-locking

SQL ACID Atomic Consistent Isolation...
 
 Je devrais mettre devant ma methode
 
 @Lock(LockModeType.PESSIMISTIC_WRITE)
mais tous les exemples sont pour des requetes en READ. 
De plus, moi je veux mettre cet @ dans ma classe d'application (qui est un @Service, et j'ai l'impression que c'est a mettre que devant les SELECT pour proteger les selects
je me trompe ? 

 

 
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

## tools to explore

buildpack.io : create a docker container easely

# API

/* ******************************************************************************** */
/*                                                                                  */ /*  Google
API                                                                      */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

## google

Aller dans API & Service / Credential Creer API pour ToghClient(googleAPIKey)
ToghServer(TranslateKeyAPI)

Enable API:

* Maps JavaScript API
* directions API/Distance Matrix API/Geocoding API/Maos javascript API / Maps static API
* Cloud Translation API

https://console.cloud.google.com/apis

https://code.google.com/archive/p/google-translate-api-v2-java/ ==> 2011

https://cloud.google.com/translate/docs/quickstarts

stocker les photo de l'API Key pour Flick

## Google SSO

https://blog.prototypr.io/how-to-build-google-login-into-a-react-app-and-node-express-api-821d049ee670
https://dev.to/sivaneshs/add-google-login-to-your-react-apps-in-10-mins-4del

Go to your project

1. Select "Credentials"
2. Create OAuthClient Id / Name "Single Sign On with Google"
3. Add "http://toghevent.com" in the Authorized Javascript Origins
4. Add "http://toghevent.com" in the Authorized redirect URIs
5. Copy the Client Id
6.

GoogleClientId:
81841339298-lh7ql69i8clqdt0p7sir8eenkk2p0hsr.apps.googleusercontent.com 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
81841339298-lh7ql69i8clqdt0p7sir8eenkk2p0hsr.apps.googleusercontent.com

we’ll add our own Google account as a test user.

# Cloud Google & Docker

/* ******************************************************************************** */
/*                                                                                  */ /*  Google Cloud &
docker                                                           */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
https://container-registry.oracle.com
docker pull container-registry.oracle.com/java/openjdk:latest

## 1. Create Back End Togh Docker image

mvn install build and push the docker image on docker image cloud (but we need the image in gcr.io).

```
> mvn install
```

To do it manually:

```
> docker build -t pierreyvesmonnet/togh:1.0.0 .
> docker push pierreyvesmonnet/togh:1.0.0
```

### Tag the release

Done automaticaly

### Option Create Front End Togh Docker image

```
> cd npm
> docker build -t pierreyvesmonnet/frontendtogh:1.0.0 .
> docker push pierreyvesmonnet/frontendtogh:1.0.0
```

### Option execute Locally Docker image

```
> docker run --name togh -h localhost -e SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST> -e SPRING_DATASOURCE_USERNAME=<USERNAME> -e SPRING_DATASOURCE_PASSWORD=<Password> --network="host" pierreyvesmonnet/togh:1.0.0 

> docker run --name frontendtogh -h localhost -d -p 3000:3000 -p 80:80 pierreyvesmonnet/frontendtogh:1.0.0
```

Docker compose does not works... but it should be:
docker-compose up

## 2. Push image to gcr.io

First time, the PC must be log on grc:

```
gcloud auth login
```

After, this command works

```
> cd cloud
> pushImagesGoogle.bat
```

or

```
> docker tag pierreyvesmonnet/togh:1.0.0 gcr.io/intricate-gamma-325323/togh:1.0.0
> docker push gcr.io/intricate-gamma-325323/togh:1.0.0

> docker tag pierreyvesmonnet/frontendtogh:1.0.0 gcr.io/intricate-gamma-325323/frontendtogh:1.0.0
> docker push gcr.io/intricate-gamma-325323/frontendtogh:1.0.0
> 
```

Check on https://console.cloud.google.com/gcr/images/intricate-gamma-325323/global/togh?project=intricate-gamma-325323

## 3. Google Cloud instance

Connect on the VM Instance Download the docker image:

```
> docker pull gcr.io/intricate-gamma-325323/togh:2.0.0

> docker pull gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
```

Here is the complete procedure:

```
$ cat updateTogh.sh

$ docker container stop togh frontendtogh
$ docker container rm togh frontendtogh
$ docker image ls -a | grep togh | awk '{ print $3 " " $1}'
$ docker image rm XXX


$ docker pull gcr.io/intricate-gamma-325323/togh:2.0.0
$ docker pull gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
$ docker run --name togh \
-e SPRING_DATASOURCE_URL=jdbc:postgresql://0.0.0.0:5432/togh \
-e SPRING_DATASOURCE_USERNAME=<USERNAME> \
-e SPRING_DATASOURCE_PASSWORD=<PASSWORD> \
--network="host" --log-driver=gcplogs -d gcr.io/intricate-gamma-325323/togh:2.0.0

$ docker run --name frontendtogh  --network="host"  --log-driver=gcplogs \
-d gcr.io/intricate-gamma-325323/frontendtogh:1.0.0


$ curl http://34.125.198.71:7080/togh/ping
$ curl http://34.125.198.71:3000
```

### Option : run docker compose

export GCP_KEY_PATH=/home/toghnow/intricate-gamma-325323-ContainerRegistry.json docker pull
gcr.io/intricate-gamma-325323/togh:1.0.0

docker run --rm \
-v /var/run/docker.sock:/var/run/docker.sock \
-v "$PWD:$PWD" \
-p 5432:5432  \
-w="$PWD" \
-e GOOGLE_APPLICATION_CREDENTIALS=/tmp/keys/keyfile.json \
-v /home/toghnow/intricate-gamma-325323-ContainerRegistry.json:/tmp/keys/keyfile.json:ro \
docker/compose:1.24.0 up &

     -p 5432:5432  \
    -p 7080:7080 \
    -p 3000:3000 \

## SQL Database production update (sql update)

# Documentation

## cloud

Deployer une image docker: 	
https://cloud.google.com/kubernetes-engine/docs/tutorials/hello-app

Deployer Postgres
https://cloud.google.com/solutions/deploying-highly-available-postgresql-with-gke

# google cloud

https://cloud.google.com/community/tutorials/kotlin-springboot-compute-engine

cd d:\atelier\cloud cloud_env.bat

# Create Google instance

> https://cloud.google.com/community/tutorials/cloud-run-local-dev-docker-compose

1. Create a Compute Engine instance, named toghinstance

2. Create a Service Account with the role "ContainerRegistry". Generate the KEY, get the JSON file and copy the file to
   the host

3. upload file from D:\dev\git\togh\GoogleCloud\intricate-gamma-325323-ContainerRegistry

docker login -u _json_key --password-stdin https://gcr.io  <intricate-gamma-325323-ContainerRegistry.json

export GCP_KEY_PATH=~/intricate-gamma-325323-ContainerRegistry.json

## Allow Docker to publish to gcr.io:

As a Windows administrateur

```
> net localgroup docker-users rhaegal\pymonnet /add
> gcloud auth activate-service-account --key-file=D:\dev\git\togh\GoogleCloud\intricate-gamma-325323-0c23d50f1d04.json
> gcloud auth configure-docker
```

Create a firewall rule to allow my PC to access Postgres: 5432

docker run -it --rm --network="host" postgres psql -h 34.94.244.105 -U toghp

docker run -it --rm postgres psql -h toghpostgres -U toghpostgres

$ docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres $ docker run -it --rm --network
some-network postgres psql -h some-postgres -U postgres

- SPRING_DATASOURCE_PASSWORD=root


gcloud config set project <<YOUR_PROJECT_NAME>>
gcloud config set compute/zone <<YOUR_SELECT_ZONE>>
docker logs <containerid>
sudo netstat -tulpn | grep LISTEN
/* ******************************************************************************** */
/*                                                                                  */
/*  Docker									                                        */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
# Docker Other commands
### docker image tomcat:
docker pull tomcat
docker pull ubuntu
docker run --name togh -h localhost -v d:/tmp/docker:/opt/togh -d -p 8080:8080 pierreyvesmonnet/togh:1.0.0



### main command
> image
docker image ls
docker image rm

> docker en execution
docker ps -a
docker stop <container id>
docker rm <container id>

Access the content of a container
docker container ls -a
>>> without -a, there is nothing if the container does not run


docker export <containerId> > docimage.tar

docker logs 221

test backend:
http://localhost:7080/ping
test front end
http://localhost:3000

-------------------------------------- container vide Ajout des composants vi & postgres apt-get update apt-get install
vi

apt-get install postgresql postgresql-contrib

su - postgres pg_ctlcluster 11 main start

creation de la la base de donnée \conninfo psql CREATE DATABASE together;

## Arret de docker windows

wsl --shutdown

## domain:

www.1and1.com

## CI

https://tomgregory.com/building-a-spring-boot-application-in-jenkins/

# Administration

Go into an image docker run -it image_name sh docker run -it gcr.io/intricate-gamma-325323/togh:1.0.0 sh

/* ******************************************************************************** */
/*                                                                                  */ /*
Administration                                                                    */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

## Database

Dump:
``
pg_dump --blobs --create --encoding=UTF8 --host=34.125.198.71 --port=5432 --username=toghp --dbname=togh > d:\temp\togh.bak
``
password: ThisIsThog4Postgres

Import:
A/ Rename in d:\temp\togh.bak the database "togh" by the name togh_<date>
``
CREATE DATABASE togh_20211228 WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.utf8'; ALTER DATABASE togh_20211228 OWNER TO toghp; \connect togh_20211228
``

B/ Execute
``
psql --username=postgres < d:\temp\togh.bak
``
password: postgres Go to <Intellij>/src/main.ressource/application.properties and change the database
``
spring.datasource.url=jdbc:postgresql://localhost:5432/togh_20211228
``

## Hebergement

ovh

Pour l'hebergeur, j'étais chez O2switch. Ils sont français et tu as la main sur les registres DNS. Je les utilisais
juste pour ca et pour héberger des sous domaines en php. Ils sont aussi réactifs, j'étais tres satisfait.

https://github.com/chDame/fabulexie/blob/master/INSTALL.MD

## Default admin

private static final String TOGHADMIN_EMAIL = "toghadmin@togh.com"; private static final String TOGHADMIN_USERNAME = "
toghadmin"; private static final String TOGHADMIN_PASSWORD = "togh";

# RoadMap

/* ******************************************************************************** */
/*                                                                                  */ /*
Roadmap                                                                         */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

## infrastructure

- Avoir un cluster kubernetes
- deployer un jenkins + backup de la base de données en locale
- CDI : je commit, et le deployment est automatique sur mon google
- make install, et ca push une nouvelle version dans docker

    - change email server (envoi SMS possible)
      https://developers.sendinblue.com/reference/sendtransacemail
      https://github.com/sendinblue/APIv3-java-library
      or
      https://developers.google.com/identity/protocols/oauth2

Simon: Docker basé sur JDK 17.

## Tasks

Simon: Comment afficher un itineraire avec google-map-react (attention, map sans S car une autre librairie existe
google-maps-react)

Simon (P2): Date Carbon: le format de date ne respecte pas la langue

Simon: login google, faire une investigation Simon: Email SMTP Google avec clee Auth 2.0

	Comment surcharger une class CSS ? bx--content-switcher-btn bx--content-switcher--selected ==> changer le fond noir en fond plus doux
		.togh.bx--content-switcher--selected ==> Marche pas

10. Refresh sur plusieurs postes : Walter modifie une valeur, elle doit etre repercutée ailleurs
11. Revoir la deconnection, le feedback user quand on fait un save

Forgot my password: email incorrect registration invitation

## Cours terme

* What change on the event avec badge sur les fonctions
* what change by email


## interne

Test unitaire sur le SLAB

## Event

Possibilite de supprimer une invitation dans un event (et dans ce cas, l'user peut etre supprimer s'il n'était invoqué
que dans un event)
En tant que Owner, je devrais pouvoir bannir une invitation

Log sur un event: quand il a changé de status, envoi d'invitation...

Notification ; je veux pouvoir dire "previens moi si qq chose change

Changer le niveau d'un utilisateur de FREE a PREMIUM => TOus les events dont c'est l'owner doit changer Expense
Preference affichage Budget Administration Statistics usage

## integration

Integration what's app & Instagram Weather

Integrate in Google Calendar Integrate in Google Drive Coder whatismyip dans l'administration

## Mes amis

Avoir une liste de mes amis que je ^peux importer depuis Google. Pouvoir inviter mes amis

## Contact & support

## Participants

Ajouter la date de dernier acces du participant dans l'évent: ainsi on peut calculater le "what's news" et mettre des
badges

## notification / event & General

Ajouter une notification dans l'évent qui apparait en gros "He, remplissez ce sondage"

Notification generale dans toutes les langues

## look and feel

Simon: Comment limiter la taille des TextInput/TextArea?
https://react.carbondesignsystem.com/?path=/story/components-textarea--default
enableCounter & maxCount n'a pas d'effet
https://codesandbox.io/s/carbon-components-react-730-textinputtextarea-value-z63zr?file=/src/index.js

## invitation

Pouvoir mettre une photo dans l'invitation pour le House Warming

Invitation: copier le contenu de l'email dans l'écran: l'user peut alors faire un copier coller pour le mettre ou il
veut Notification: je veux pouvoir dire "je veux recevoir un email en cas de modification"

Jpoin Togh: je met un email et ca envoi un email pour dire "join moi sur Togh"

## Administration

Supprimer un user invalide Graph des events / mois Purger les users invite, jamais venus purger plus rapidement encore
les users invite mais plus retirer des events ou dans des events clotures

Admin: avoir un "automatique refresh check box" : penible de clicker "connected" et de faire search

	Event/Participant: mettre l'icone + un component qui donne toutes les coordonnées
 	
	MonitorService.registerErrorEvents() ==> Implementer
	
	Integrer weather  : https://openweathermap.org/api
	
	RestLoginController : google donne la langue preferer de l'utilisateur, quand on cree un user, on pourrais la stocker
	Permettre a un utilisateur de changer son username
	
	My Friend = recuperer les users de google
		

	Admin gestion user
		- invited + password : c'est un probleme, l'utilisateur ne peut plus se connecter
		- invited depuis trop longtemps : a purger
		- inactif depuis trop longtemps : 

## bugs


game true or lie

* validate my sentence : le bouton ne s'invalide pas
* j'ai pu valider avec 2 sequences vide
* je suis le seul participant, je click sur vote, ca part en couille
* en tant qu'administrateur, je doit pouvoir reouvrir une sequence

invitation:


Invitation Je veux inviter <caromaillebiau@gmail.com>= > Togh refuse Je veux inviter caro@maillebiau.com ==> Toujours
pas possible car cet utilisateur est en mode INVITE et donc je ne peux pas l'inviter a un 2eme event

Invitation Un utilisateur (makeba) est invité sur un event Elle cree un nouvel event : c'est possible et on a 2 users
avec le meme email. Et Makeba ne vois pas l'évent

EventEntity.getMap : normaliser les constantes ici avec les SLAB_OPERATION (une seule constante)

* Je suis 2 fois : email en Majuscule et en Minuscule. Faire un test ignore case
* changeemail

* Date des messages dans le chat qui est la date du jour Chat : "TheFriday" ==> "On Friday"

------------ Revoir l'URL : faire un bouton Share

Adresse 405 Bellevue Oakland n'est pas geolocalisée

bouton ADD plus gros dans les tabs

Togh & Cupris : faire des fond transparent

Horaire de l'email : je met 12:00 et le mail envoi 8:00.L Mettre dans l'email heure + time zone

Forget my password : le lien doit experirer dans les 10 mn, et on ne doit pas pouvoir le reutiliser plusieurs fois.

