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

spring.datasource.hikari.connectionTimeout=20000 spring.datasource.hikari.maximumPoolSize=5

spring.datasource.url=jdbc:postgresql://localhost:5432/together spring.datasource.username=postgres
spring.datasource.password=postgres spring.datasource.driver-class-name=org.postgresql.Driver

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
Plug-in Intellij must be installed ( File > Settings > Plugins)

## mobile

https://flutter.dev/

## Using h2

Using h2 database instead of postgres -Dspring.profiles.active=dev or --spring.profiles.active=h2

# Start

Spring Boot :

# Architecture

Execute com.togh.ToghApplication Reac :
On a terminal

```
cd npm
$env:NODE_OPTIONS = "--openssl-legacy-provider"
npm start
```

Access http://localhost:8080

/* ******************************************************************************** */
/*                                                                                  */ /*
Architecture                                                                    */
/*                                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

com.together.spring.ServletInitializer : this is the REST call

-----------------------------------------
com.togh.Application package com.togh;

import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
-----------------------------------------



com.together.service : Service to access information and data

https://start.spring.io/

## Date management

There is two king of date : Date+Time (Zoned) and Date

* Date+Time (Zoned) : this is an Instant. When I give for the event date, Feb 1, 14:00 ==> in Pacific time. A user in
  Paris time shoud see Feb 1, 23:00 (depends of the date, there is 8 or 9 hours delay)
* Date : an absolute date. In an itinerary I give "Feb 3", that's mean it's Feb 3 and that it.

In Database, a Date+Time is saved as a LocalDateTime, UTC ( SPRING use a string: 2021-03-31 07:00:00), a Date in
LocalDate. SPRING use a string : 2021-03-31

Browser return in all case a UTC Date, whatever the widget
"2026-08-07T07:00:00.000Z"

+ the timezoneoffset: 480

So:

- for a LocalDateTime, there is nothing to do, it's already a UTC date
- for a LocalDateTime, calculation must be 1/ calculate the local time (date - offset), the get the date 	
  For example, if I give "Feb 3" in my widget at 20:00 in California, browser send "2021-02-04T05:00:00Z", timeoffset=
  480. Then calculation get Feb3

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
or (to install on a new machine)
```
npm install --legacy-peer-deps
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
@import '@carbon/react';


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
  
npm install @carbonreact
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

Marron : 210/165/108 #d2a66c 206/139/84 #ce8b54 189/126/73 #bd7d49 150/98/61 #96623d 130/80/45 #82502d

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
mais tous les exemples sont pour des requetes en READ. De plus, moi je veux mettre cet @ dans ma classe d'application (
qui est un @Service, et j'ai l'impression que c'est a mettre que devant les SELECT pour proteger les selects je me
trompe ?

## profile

See https://www.baeldung.com/spring-profiles
-Dspring.profiles.active=dev

## JPA

https://www.baeldung.com/spring-data-jpa-query

## Commentaire

/* ******************************************************************************** */
/*                                                                                  */ /*  Togh
Project                                                                    */
/*                                                                                  */ /*  This component is part of the
Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

/* ******************************************************************************** */
/*                                                                                  */ /*
EventController,                                                                 */
/*                                                                                  */ /*  Control what's happen on an
event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

React:
// ----------------------------------------------------------- // // EventExpense // // Display one event // //
-----------------------------------------------------------

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
