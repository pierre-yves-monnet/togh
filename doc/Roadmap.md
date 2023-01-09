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

Simon: login google, faire une investigation py: comment afficher le login google?

Simon: Email SMTP Google avec clee Auth 2.0

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

