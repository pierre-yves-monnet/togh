// -----------------------------------------------------------
//
// EventCtrl
//
// This class is not a ReactComponent, and it used to control the object.
// 
// * Data are save in the REACT component, Event. This compoment this.state.event IS THE DATA
// 
// * this object is created in the <Event> object, after an event is loaded
// 
// * when the Event has a sub Component (like EventTaskList), the main parameters given is this controller
//  
// * to update a attribut, the component call this.eventCtrl.setAttribut(<name>, <value>, <localisation>). 
//    EventCtrl will execute a this.event.setState( event ) after update, which reresh the screen TOCONFIRM
//
//
//  * some information is store in the controler. Example, we edit one survey at a time: what is the current Survey edited ? 
//    the EventCtrl keep this currentSurvey, and it's not in the React part. Then, <EventSurvey> ask the controler to get the current Survey data
//    Note: this currentSurvey Data is managed by REACT in the <Event> component : the controller just return the object using the current Id
// 
// * in some case, the sub component need to force an refresh on its date (<EventSurvey> must refresh <EventSurveyList> when the title change)
//   this is the responsability of the SubComponent to deal with that

// This class contains all the controleur use to manipulate the object;
//
// -----------------------------------------------------------
//

import SurveyCtrl from './SurveyCtrl';
import UserParticipantCtrl from './UserParticipantCtrl';

import FactoryService from './../service/FactoryService'
import SlabEvent from './../service/SlabEvent';
import BasketSlabEvent from './../service/BasketSlabEvent';
import HttpResponseMockup from './../service/HttpResponseMockup';


import EventPreferencesCtrl from './EventPreferencesCtrl';

import * as participantConstant from './../EventParticipants';

class EventCtrl {
	
	// event is the React object
	constructor(eventReact, event) {
		this.eventReact 				= eventReact;
		this.event 						= event;
		this.ctrlId 					= new Date().getTime();
		this.eventPreferences           = new EventPreferencesCtrl(this, this.updateEventfct);
		this.currentBasketSlabEvent 	= new BasketSlabEvent( this );
	}
	
	getEvent() {
		return this.event;
	}
	
	
	
	// --------------------------------------------------------------
	// 
	// Main control function
	// 
	// --------------------------------------------------------------


		// -------------------------------------------- CompleteEvent
    completeEvent() {
		
		// console.log("EventCtrl.completeEvent: Start=" + JSON.stringify( this.event ));
		var log=""; 
		// TextArea must not be null
		if (! this.event.description)
			this.event.description='';

		if (!this.event.participants) {
			log = log.concat("create participants;");
			this.event.participants = [];
		}
		if (!this.event.itinerarylist) {
			log = log.concat("create ItineraryList;");
			this.event.itinerarylist = [];
		}
			
		if (!this.event.expenses) {
			log = log.concat("create Expenses;");
			this.event.expenses = {};
		}
		
		if (!this.event.geoLocalisation) {
			log = log.concat("create geoLocalisation");
			this.event.geoLocalisation = {};
		}
		if (!this.event.shoppinglist) {
			log = log.concat("create shoppinglist");
			this.event.shoppinglist = [];
		}
		
		if (!this.event.tasklist) {
			console.log("Event.completeEvent: no task list exist, create one");
			this.event.tasklist = [];
		}
		if (!this.event.surveylist) {
			log = log.concat("create surveylist");
			this.event.surveylist = [];
		}
		if (!this.event.preferences) {
			log = log.concat("create preferences");
			this.event.preferences = {};
		}
		
		// console.log("EventCtrl.loadEvent: completionDone event=" + JSON.stringify(this.event) );

		console.log("Event.preferences: end of "+log+" event=" + JSON.stringify(this.event ));
	} 
	
	
		
	// --------------------------------------------------------------
	// 
	// Update the event
	// 
	// --------------------------------------------------------------


	/**
	* setAttribut
	* a component update a value. Then this value is register in slabEvent  
	*/
	setAttribut(name, value, item, localisation) {
		console.log("EventCtrl.setAttribut: set attribut:" + name + " <= " + value + " localisation=" +  localisation);
		// const currentEvent = this.event;
		// item is part of the event at one moment
		item[name] = value;

		// we send a setState to refresh the value
		this.eventReact.refreshEventfct();
		
		var slabEvent = SlabEvent.getUpdate(this.event, name, value, localisation);
		this.updateEventfct( slabEvent );
	}
	
	
	updateEventfct( slabEvent ) {	
		this.currentBasketSlabEvent.addSlabEvent( slabEvent );
		if (this.timer)
			clearTimeout(this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave(); }, 2000);
	}
	
	

	/**
	 * Add an compo,nent (like a new Survey, a new expense). The call is immediately done, and the callback is called
     * example addChild("surveylist", {name:"Restaurant?"}, "/", callbackfct)) 
    * thjis function does not add the value in the event. It will be te responsability of the callback to do it
	 */
	addEventChildFct(listname, value, localisation, callbackfct) {
		console.log("EventCtrl:addEventChildFct."+this.ctrlId+" child="+listname)
		// MOCKUP 
		var toolService = FactoryService.getInstance().getToolService();
		value.id = toolService.getUniqueCodeInList( this.event[ listname ], "id");		
		var dataHttp ={ child : value};
		var httpResponse = new HttpResponseMockup(dataHttp);
		
		console.log("EventCtrl:addEventChildFct callBack now")
 
		callbackfct( httpResponse )
		
		if (this.timer)
			clearTimeout(this.timer);
		/*
		var slabEvent = SlabEvent.getAddList(this.event, listname, value, localisation);
		this.currentBasketSlabEvent.addSlabEvent( slabEvent );
		var readyToSendBasket = this.currentBasketSlabEvent;
		this.currentBasketSlabEvent = new BasketSlabEvent( this );
		readyToSendBasket.sendToServer( callbackfct );
		*/
	}
	removeEventChild(listname, value, localisation, callbackfct) {
		console.log("EventCtrl.removeEventChildFct."+this.ctrlId+" child="+listname)
		var dataHttp ={ child : value};
		var httpResponse = new HttpResponseMockup(dataHttp);
		
		console.log("EventCtrl.removeEventChildFct callBack now")
 
		callbackfct( httpResponse );
		
		/*
		var slabEvent = SlabEvent.getRemoveList(this.event, listname, value, localisation);
		this.currentBasketSlabEvent.addSlabEvent( slabEvent );
		var readyToSendBasket = this.currentBasketSlabEvent;
		this.currentBasketSlabEvent = new BasketSlabEvent( this );
		readyToSendBasket.sendToServer( callbackfct );
		*/
	}
	updateEventChild( listname,value, localisation, callbackfct) {
		console.log("EventCtrl.updateEventChild."+this.ctrlId+" child="+listname)
		var dataHttp ={ child : value};
		var httpResponse = new HttpResponseMockup(dataHttp);
		callbackfct( httpResponse );

	}
	
	automaticSave() {
		console.log("EventCtrl.AutomaticSave: ListSlab=" +this.currentBasketSlabEvent.length);
		var readyToSendBasket = this.currentBasketSlabEvent;
		this.currentBasketSlabEvent = new BasketSlabEvent( this );
		readyToSendBasket.sendToServer( this.callbackSendFct );
		if (this.timer)
			clearTimeout(this.timer);
	}
	
	callbackSendFct( httpPayload ) {
		console.log("EventCtrl.callbackSendFct: status back from sendBasket");
	}
	// --------------------------------------------------------------
	// 
	// Get some information
	// 
	// --------------------------------------------------------------

	getSurveyList () {
		return this.event.surveylist;
	}
	
	getUpdateEventfct() {
		return this.updateEventfct;
	}
	
	getEventPreferences() {
		return this.eventPreferences;
	}
	getUserParticipant() {
		var authService = FactoryService.getInstance().getAuthService();
		// console.log("Event.getUserPartipant.start");
		var user= authService.getUser();
		// search the access right for this user
		for (var i in this.event.participants) {
			if (this.event.participants[ i ].user && this.event.participants[ i ].user.id === user.id) {
				return new UserParticipantCtrl(this.event,  this.event.participants[ i ] )
			}
		}
		return new UserParticipantCtrl(this.event,null );
	}
	
	getTotalParticipants() {
		var total=0;
		for (var i in this.event.participants) {
			if (this.event.participants[ i ].status === participantConstant.STATUS_ACTIF) {
				total ++;
			}
		}
		return total;
	}
		
	// --------------------------------------------------------------
	// 
	// Survey
	// 
	// --------------------------------------------------------------

	/**
	 currentSurveyId is not part of the data, it's part of the controller (which survey are currently displayed)
	 */
	setCurrentSurveyId( surveyId )  {

		this.currentSurveyId = surveyId;
		var currentSurvey = this.getCurrentSurvey();
		this.currentSurveyCtrl = new SurveyCtrl( this, currentSurvey ); // userParticipant, props.updateEvent);
		console.log("EventCtrl.setCurrentSurveyId."+this.ctrlId+": surveyId = "+JSON.stringify(this.currentSurveyEntity));
	}
	
	getCurrentSurveyId() {
		// Get the current Id, but if no one is set, and a list exist, move to the first one
		if (! this.currentSurveyId && this.getSurveyList().length >0)
			this.setCurrentSurveyId( this.getSurveyList()[ 0 ].id);
			
		return this.currentSurveyId;
	}
	
	
	
	getCurrentSurveyCtrl() {
		console.log("EventCtrl.getCurrentSurveyCtrl."+this.ctrlId+": ");
		return this.currentSurveyCtrl;
	}
	
	
	addSurveyInEvent( surveyToAdd ) {
		console.log("EventCtrl.addSurveyInEvent."+this.ctrlId+":"+JSON.stringify(surveyToAdd));

		this.event.surveylist = this.event.surveylist.concat( surveyToAdd );
		this.currentSurveyId = surveyToAdd.id;
		this.currentSurveyCtrl =  new SurveyCtrl( this, surveyToAdd );
	}

	/**
	* return the current survey choose by the user  */
	getCurrentSurvey() {
		// console.log("EventCtrl.getCurrentSurvey."+this.ctrlId+": CurrentSurvey Start search "+this.currentSurveyId)
		if ( ! this.currentSurveyId)
			return null;
		for (var i in this.event.surveylist) {
			// console.log("EventCtrl.getCurrentSurvey."+this.ctrlId+": i="+i+" surverLis[].id="+this.event.surveylist[ i ].id)

			if (this.event.surveylist[ i ].id.toString() ===  this.currentSurveyId.toString()) {
				var survey = this.event.surveylist[ i ];
				console.log("EventCtrl.getCurrentSurvey We get a candidat id="+survey.id);				
				return survey;
			}
		}
		console.log("EventCtrl.getCurrentSurvey."+this.ctrlId+": CurrentSurvey not found ? ! ")
		return null;
	}
}

export default EventCtrl;