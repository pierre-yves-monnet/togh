// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

import React from 'react';

import { FormattedMessage } from "react-intl";

// import { Button } from 'carbon-components-react';
import { DatePicker, DatePickerInput, TimePicker, RadioButtonGroup, RadioButton, TextArea, TextInput, Select } from 'carbon-components-react';

import * as participantConstant from './EventParticipants';


import FactoryService from './service/FactoryService';
// import DatePickerSkeleton from '@bit/carbon-design-system.carbon-components-react.DatePicker/DatePicker.Skeleton';
// import TimePicker from '@bit/carbon-design-system.carbon-components-react.time-picker';


import EventParticipants from './EventParticipants';
import EventItinerary from './EventItinerary';
import EventShoppingList from './EventShoppingList';
import EventGeolocalisation from './EventGeolocalisation';
import EventTaskList from './EventTaskList';
import EventState from './EventState';
import EventExpense from './EventExpense';
import EventSurveyList from './EventSurveyList';
import BasketSlabEvent from './service/BasketSlabEvent';
import UserParticipantCtrl from './controller/UserParticipantCtrl';
import EventPreferencesCtrl from './controller/EventPreferencesCtrl';

import EventCtrl from './controller/EventCtrl';

const TAB_PARTICIPANT='Participant';
const TAB_ITINERARY = 'Itinerary';
const TAB_SHOPPINGLIST = 'ShoppingList';
const TAB_GEOLOCALISATION='Geolocalisation';
const TAB_EXPENSE = 'Expense';
const TAB_CHAT = 'Chat';
const TAB_TASKLIST = 'TaskList';
const TAB_SURVEY = 'Survey';
const TAB_PHOTO = 'Photo';
			

class Event extends React.Component {
	constructor(props) {
		super();
		console.log("Event.constructor eventId="+props.eventid);

		this.state = {
			'event': {},
			'eventid': props.eventid,
			show: {
				currentSection: TAB_PARTICIPANT
			}
		};

		
		
		// TextArea must not be null
		if (!this.state.event.description)
			this.state.event.description = '';

		// this is mandatory to have access to the variable in the method... thank you React!   

		this.accessTab			 		= this.accessTab.bind(this);

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.loadEvent 					= this.loadEvent.bind(this);
		this.changeState 				= this.changeState.bind(this);
		this.setAttribut 				= this.setAttribut.bind(this);
		this.updateEventfct 			= this.updateEventfct.bind(this);
		this.getUserParticipant			= this.getUserParticipant.bind(this);
		

	}
	componentDidMount() {
		// Ok, now we do the load
		this.loadEvent();
		
		
	}

	//----------------------------------- Render
	render() {
		console.log("Event.render eventId=" + this.state.eventid + " event=" + JSON.stringify(this.state.event) + " show:" + JSON.stringify(this.state.show));


		// no map read, return
		if (!this.state.event || Object.keys(this.state.event).length === 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}

		
		var toolService = FactoryService.getInstance().getToolService();


		var datePanelHtml = (
			<div>
				<RadioButtonGroup
					name="datepolicy"
					valueSelected={this.state.event.datePolicy}
					legend={<FormattedMessage id="Event.DatePolicy" defaultMessage="Date policy" />}
					onChange={(event) => {
						console.log("RadioGroup.DataPolicy on change=");

						this.setAttribut("datePolicy", event)
					}
					}
				>
					<RadioButton value="ONEDATE" id="r1" labelText={<FormattedMessage id="Event.OneDate" defaultMessage="One date" />} labelPosition="right" />
					<RadioButton value="PERIOD" id="r2" labelText={<FormattedMessage id="Event.Period" defaultMessage="Period" />} labelPosition="right" />
				</RadioButtonGroup>
				{this.state.event.datePolicy === 'ONEDATE' && (
					<div>
						<table><tr>
							<td colspan="2">
								<DatePicker datePickerType="single"
									onChange={(dates) => {
										console.log("SingleDatePicker :" + dates.length + " is an array " + Array.isArray(dates));

										if (dates.length >= 1) {
											console.log("SingleDatePicker set Date");
											this.setAttribut("dateEvent", dates[0]);
										}
									}
									}
									value={toolService.getDateListFromDate(this.state.event.dateEvent)}

								>

									<DatePickerInput
										placeholder="mm/dd/yyyy"
										labelText={<FormattedMessage id="Event.DateEvent" defaultMessage="Date Event" />}
										id="date-picker-simple"
									/>
								</DatePicker>
							</td></tr>
							<tr><td>
								<TimePicker
									id="eventTime"
									labelText={<FormattedMessage id="Event.TimeEvent" defaultMessage="Time" />}
									value={this.state.event.timeEvent}
									onChange={(event) => this.setAttribut("timeEvent", event.target.value)} />
							</td><td>
									<TimePicker
										id="durationTime"
										labelText={<FormattedMessage id="Event.DurationEvent" defaultMessage="Duration" />}
										value={this.state.event.durationEvent}
										onChange={(event) => this.setAttribut("durationEvent", event.target.value)} />
								</td></tr></table>
					</div>)
				}
				{this.state.event.datePolicy !== 'ONEDATE' && (
					<div>
						<DatePicker datePickerType="range"
							onChange={(dates) => {
								if (dates.length > 1) {
									this.setAttribut("dateStartEvent", dates[0]);
									this.setAttribut("dateEndEvent", dates[1]);
								}
							}}
							value={toolService.getDateListFromDate(this.state.event.dateStartEvent, this.state.event.dateEndEvent)}

						>
							<DatePickerInput
								id="date-picker-input-id-start"
								placeholder="mm/dd/yyyy"
								labelText={<FormattedMessage id="Event.StartDate" defaultMessage="Start Date" />}
							/>
							<DatePickerInput
								id="date-picker-input-id-finish"
								placeholder="mm/dd/yyyy"
								labelText={<FormattedMessage id="Event.EndDate" defaultMessage="End Date" />}
							/>
						</DatePicker>
					</div>)
				}



			</div>
		);


		// <div class="btn-group mr-2" role="group" aria-label="First group">
		//							<button style={{"marginLeft ": "10px"}} onClick={this.secItineraire} title="Itineraire" disabled={true} class="btn btn-primary">
		//								<div class="glyphicon glyphicon-road"></div>
		//						</button>
		//					</div>

		// -----------------	
		console.log("Event.render : statusEvent="+JSON.stringify(this.state.event.statusEvent)+" Participants="+JSON.stringify(this.state.event.participants)); 
		return (
			<div>
				<div class="row">
					<div class="col-sm-1">
						<img src="img/toghEvent.jpg" style={{ width: 90 }}     />
					</div>
						
					<div class="col-sm-4">
						<TextInput labelText="" 
							id="name"
							value={this.state.event.name}
							style={{fontSize: "24px", height: "50px", color: "#ac1e4a", maxWidth: "315px"}} 
							onChange={(event) => this.setAttribut("name", event.target.value)} /><br />
					</div>
					<div class="col-sm-5">
						<div class="fieldlabel">{<FormattedMessage id="Event.Status" defaultMessage="Status" />}</div>
						<EventState statusEvent={this.state.event.statusEvent} modifyEvent={true} changeState={this.changeState} />
					</div>
					<div class="col-sm-2">
						<Select labelText={<FormattedMessage id="Event.Scope" defaultMessage="Scope" />}
							id="typeEvent"
							value={this.state.event.typeEvent}
							onChange={(event) => this.setAttribut("typeEvent", event.target.value)}>


							<FormattedMessage id="Event.ScopeOpen" defaultMessage="Open">
								{(message) => <option value="OPEN">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeOpenConfirmation" defaultMessage="Open on confirmation">
								{(message) => <option value="OPENCONF">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeLimited" defaultMessage="Limited">
								{(message) => <option value="LIMITED">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeSecret" defaultMessage="Secret">
								{(message) => <option value="SECRET">{message}</option>}
							</FormattedMessage>


						</Select>
						<br />
					</div>

				</div>
				<div class="row">
					<div class="col-sm-6">
						<TextArea id="description"
							labelText={<FormattedMessage id="Event.Description" defaultMessage="Description" />}
							style={{ width: "100%", maxWidth: "100%" }}
							rows={5}
							value={this.state.event.description}
							onChange={(event) => this.setAttribut("description", event.target.value)} />
					</div>
					<div class="col-sm-6">
						<div class="card" style={{marginTop: "10px"}}>
							<div class="card-header" style={{backgroundColor:"#decbe4"}}>
								<FormattedMessage id="Event.EventDate" defaultMessage="Date" />
							</div>
							<div class="card-body">
								{datePanelHtml}
							</div>
						</div>
					</div>
				</div>

				<div class="row" style={{ padding: "10px 30px 10px" }}>

					<div class="col-lg">

						<button onClick={() => this.accessTab( TAB_PARTICIPANT )}
								title={<FormattedMessage id="Event.TitleParticipant" defaultMessage="Invite participant to your event" />}
								 class="btn btn-primary">
							<img style={{ "float": "right" }} src="img/btnParticipants.png" style={{ width: 45 }} /><br />
							<FormattedMessage id="Event.Participants" defaultMessage="Participants" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_ITINERARY )}
								title={<FormattedMessage id="Event.TitleItinerary" defaultMessage="Define your itinerary, and point of interest" />} 
								class="btn btn-primary" style={{ "marginLeft ": "10px" }}>
							<img style={{ "float": "right" }} src="img/btnItinerary.png" style={{ width: 45 }} /><br />
							<FormattedMessage id="Event.Itinerary" defaultMessage="itinerary" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_CHAT) } 
							title={<FormattedMessage id="Event.TitleChat" defaultMessage="Chat" />} disabled={true} class="btn btn-primary"
							style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnChat.png" style={{ width: 45 }} /><br />
							<FormattedMessage id="Event.Chat" defaultMessage="Chat" />
						</button>
						&nbsp;

						<button  onClick={() => this.accessTab( TAB_TASKLIST )} 
							title={<FormattedMessage id="Event.TitleTasks" defaultMessage="Tasks" />} 
							class="btn btn-primary" style={{ "marginLeft ": "10px" }}>
							<img style={{ "float": "right" }} src="img/btnTask.png" style={{ width: 45 }} /><br />
							<FormattedMessage id="Event.Tasks" defaultMessage="Tasks" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_SHOPPINGLIST)} 
							title={<FormattedMessage id="Event.TitleShoppingList" defaultMessage="Shopping list : what to brings?" />} 
							class="btn btn-primary" style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnShoppingList.png" style={{ width: 45 }} /><br />							
							<FormattedMessage id="Event.ShoppingList" defaultMessage="Shopping List" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_SURVEY )} 
							title={<FormattedMessage id="Event.TitleSurvey" defaultMessage="Survey" />}  
							class="btn btn-primary" style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnSurvey.png" style={{ width: 45 }} /><br />							
							<FormattedMessage id="Event.Survey" defaultMessage="Survey" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_GEOLOCALISATION )} 
							title={<FormattedMessage id="Event.TitleGeolocalisation" defaultMessage="Where is the event?" />} 
							class="btn btn-primary" style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnGeolocalisation.png" style={{ width: 45 }} /><br />							
							<FormattedMessage id="Event.Geolocalisation" defaultMessage="Geolocalisation" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_PHOTO )} 
							title={<FormattedMessage id="Event.TitlePhotos" defaultMessage="Photos" />} 
							disabled={true} 
							class="btn btn-primary" style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnPhoto.png" style={{ width: 45 }} /><br />							
							<FormattedMessage id="Event.Photos" defaultMessage="Photos" />
						</button>
						&nbsp;

						<button onClick={() => this.accessTab( TAB_EXPENSE )} 
							title={<FormattedMessage id="Event.TitleExpense" defaultMessage="Manage and share expenses" />} 
							class="btn btn-primary"  style={{ "marginLeft ": "10px" }} >
							<img style={{ "float": "right" }} src="img/btnExpense.png" style={{ width: 45 }} /><br />							
							<FormattedMessage id="Event.Expense" defaultMessage="Expense" />
						</button>

					</div>

				</div>
				{this.state.show.currentSection === TAB_PARTICIPANT && <EventParticipants event={this.state.event} 
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
				{this.state.show.currentSection === TAB_ITINERARY && <EventItinerary event={this.state.event}
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
				{this.state.show.currentSection === TAB_TASKLIST && <EventTaskList event={this.state.event}
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
				{this.state.show.currentSection === TAB_SHOPPINGLIST && <EventShoppingList event={this.state.event}
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
				{this.state.show.currentSection === TAB_GEOLOCALISATION && <EventGeolocalisation event={this.state.event}
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
				{this.state.show.currentSection === TAB_SURVEY && <EventSurveyList 
																			eventCtrl={this.eventCtrl}
																			/>}
				{this.state.show.currentSection === TAB_EXPENSE  && <EventExpense event={this.state.event}
																			updateEvent={this.updateEventfct}
																			getUserParticipant={this.getUserParticipant}
																			eventPreferences={this.eventPreferences}/>}
			</div>)
	} //---------------------------- end Render


	changeState(event) {
		console.log("Event.setState event ");
		// this.setAttribut("eventState", event);
	}

	

	// provide automatic save
	setAttribut(name, value) {
		console.log("Event.setAttribute: attribut:" + name + " <= " + value + " typeof=" + (typeof value) + " EventinProgress=" + JSON.stringify(this.state.event));
		this.eventCtrl.setAttribut( name, value, this.state.event, "");
		/*
		var eventValue = this.state.event;
		eventValue[name] = value;

		this.setState({ "event": eventValue });
		if (this.timer)
			clearTimeout(this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave(); }, 2000);
		*/
	}


	/** */
	refreshEventfct(  ) {
		console.log("Event.refreshEventfct Start!! event="+JSON.stringify(this.eventCtrl.getEvent()));

		this.setState( { event: this.eventCtrl.getEvent()} );
	}
	
	/**
	Something change in the event, all subcomponent refer it with a Slab, which is the information which change */
	updateEventfct( slab ) {
			console.log("Event.updateEventfct : DEPRECATED METHOD !!");
	}
	
	
	// -------------------------------------------- Access different part
	accessTab( accessTab ) {
		console.log("Event.accessTab tab=" + JSON.stringify(accessTab));
		this.setState( { show: { currentSection: accessTab} });

	}

	// -------------------------------------------- Tool Service
	getUserParticipant() {
		var authService = FactoryService.getInstance().getAuthService();
		// console.log("Event.getUserPartipant.start");
		var user= authService.getUser();
		// search the access right for this user
		for (var i in this.state.event.participants) {
			if (this.state.event.participants[ i ].user && this.state.event.participants[ i ].user.id === user.id) {
				return new UserParticipantCtrl(this.state.event,  this.state.event.participants[ i ] )
			}
		}
		return new UserParticipantCtrl(this.state.event,null );
	}
	


	// -------------------------------------------- Call REST
	loadEvent() {
		console.log("Event.loadEvent: event?id=" + this.state.eventid + "]");
		this.setState({ event: {} });

		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson('/api/event?id=' + this.state.eventid, this, httpPayload => {
			httpPayload.trace("Event.getPayload");

			if (httpPayload.isError()) {
				
			}
			else {

				
				// not in the state: we don't want a render when something is added
				// so, this is the moment to create the Basket
				// this.currentBasketSlabEvent= new BasketSlabEvent( this.state.event );
	
				// console.log("Event.loadEvent: eventLoaded=" + JSON.stringify(httpPayload.getData().event) + "]");
				this.eventCtrl = new EventCtrl( this, httpPayload.getData().event );
				// console.log("Event.loadEvent: before complementeion event=" + JSON.stringify(this.eventCtrl.getEvent()));
				this.eventCtrl.completeEvent();
				// console.log("Event.loadEvent: end of complete, event=" + JSON.stringify(this.eventCtrl.getEvent()));
				this.setState({ event: this.eventCtrl.getEvent(), show: { currentSection : TAB_PARTICIPANT } });
				// console.log("Event.loadEvent: the end ");
			}
		});

	}




	// -------- Rest Call

}
export default Event;