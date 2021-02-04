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

 
# google 
API Key AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es


	
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
  

# React

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



## Loaclisation
https://phrase.com/blog/posts/react-i18n-best-libraries/

https://www.freecodecamp.org/news/setting-up-internationalization-in-react-from-start-to-finish-6cb94a7af725/







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
 
# Database 
update eventuser set name='birthday' where id=31;

insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(40,'local', 1, 1, 30,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(41,'local', 1, 1, 28,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(42,'local', 1, 1, 29,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(43,'local', 1, 1, 5,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(44,'local', 1, 0, 33,31);
insert into evtparticipant (id,accessdata, role, status, user_id, eventid) values(45,'local', 2, 2, 3,31);

          
          
# Tasks


Simon Autocomplete

Simon Google Carte
Simon Toggle default value

Py	Sauvegarde
          
          
          
