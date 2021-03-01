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
import { DatePicker } from 'carbon-components-react';
import { DatePickerInput } from 'carbon-components-react';
import { TimePicker } from 'carbon-components-react';
import { RadioButtonGroup } from 'carbon-components-react';
import { RadioButton } from 'carbon-components-react';
import { TextArea } from 'carbon-components-react';
import { TextInput } from 'carbon-components-react';
import { Select } from 'carbon-components-react';

import FactoryService from './service/FactoryService';
// import DatePickerSkeleton from '@bit/carbon-design-system.carbon-components-react.DatePicker/DatePicker.Skeleton';
// import TimePicker from '@bit/carbon-design-system.carbon-components-react.time-picker';


import EventParticipants from './EventParticipants';
import EventItinerary from './EventItinerary';
import EventShoppingList from './EventShoppingList';
import EventGeolocalisation from './EventGeolocalisation';
import EventTaskList from './EventTaskList';
import EventState from './EventState';



class Event extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : {}, 
						'eventid' : props.eventid,
						'show' : { 
							secParticipant: 'ON', 
							secItinerary : 'OFF',
							secShoppinglist : 'OFF',
							secGeolocalisation : 'OFF', 
							secTasklist : 'OFF'},
						showRegistration : false
						};


		// TextArea must not be null
		if (! this.state.event.description)
			this.state.event.description='';
			
		// this is mandatory to have access to the variable in the method... thank you React!   
		this.showRegistration 			= this.showRegistration.bind(this);
		this.accessParticipantList 		= this.accessParticipantList.bind(this);
		this.accessShoppingList			= this.accessShoppingList.bind(this);
		this.accessGeolocalisation		= this.accessGeolocalisation.bind(this);
		this.accessTaskList				= this.accessTaskList.bind(this);
		this.accessItinerary			= this.accessItinerary.bind(this);

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.refreshEvent 		= this.refreshEvent.bind(this);
		this.changeState 		= this.changeState.bind(this);
		this.setAttribut 		= this.setAttribut.bind(this);
		this.pingEvent			= this.pingEvent.bind(this);
		this.refreshEvent();

	}

	//----------------------------------- Render
	render() {
		console.log("Event.render eventId="+this.state.event.eventid + " event="+JSON.stringify(this.state.event)+" show:"+JSON.stringify(this.state.show));
		 

		// no map read, return
		if (! this.state.event || Object.keys(this.state.event).length === 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}
				
		console.log("Event.render: section=task:"+this.state.show.secTasklist+ " Shop:"+this.state.show.secShoppinglist);
		var toolService = FactoryService.getInstance().getToolService();
		
				
		var datePanelHtml = (
			<div>
				<RadioButtonGroup
					name="datepolicy"
					valueSelected={this.state.event.datePolicy}
					legend={<FormattedMessage id="Event.DatePolicy" defaultMessage="Date policy"/>}
					onChange={(event) => {
							console.log("RadioGroup.DataPolicy on change=");
        					
							this.setAttribut( "datePolicy", event)}
							}
						>
					<RadioButton value="ONEDATE" id="r1" labelText={<FormattedMessage id="Event.OneDate" defaultMessage="One date"/>} labelPosition="right" />
					<RadioButton value="PERIOD" id="r2"  labelText={<FormattedMessage id="Event.Period" defaultMessage="Period"/>} labelPosition="right"/>
				</RadioButtonGroup>
				{ this.state.event.datePolicy === 'ONEDATE' && (	
					<div>
						<table><tr>
						<td colspan="2">
						<DatePicker datePickerType="single"
							onChange={(dates) => {
									console.log("SingleDatePicker :"+dates.length+" is an array "+Array.isArray(dates));
 
									if (dates.length >= 1) {
										console.log("SingleDatePicker set Date");
										this.setAttribut( "dateEvent", dates[0] );
									}
								}
							}
						  	value={toolService.getDateListFromDate(this.state.event.dateEvent)}

							>
        							 
							<DatePickerInput
						    	placeholder="mm/dd/yyyy"
						      	labelText={<FormattedMessage id="Event.DateEvent" defaultMessage="Date Event"/>}
						      	id="date-picker-simple"
						    />
						</DatePicker>
						</td></tr>
						<tr><td>
							<TimePicker
								id="eventTime"
								labelText={<FormattedMessage id="Event.TimeEvent" defaultMessage="Time"/>}
							  	value={this.state.event.timeEvent} 
							  	onChange={(event) => this.setAttribut( "timeEvent", event.target.value )}/>
						</td><td>
							<TimePicker
								id="durationTime"
								labelText={<FormattedMessage id="Event.DurationEvent" defaultMessage="Duration"/>}
							  	value={this.state.event.durationEvent} 
							  	onChange={(event) => this.setAttribut( "durationEvent", event.target.value )}/>
						</td></tr></table>
					</div>)
				}	
				{ this.state.event.datePolicy !== 'ONEDATE' && (	
					<div>
						 <DatePicker datePickerType="range"
				  				onChange={(dates) => { 
									if (dates.length > 1) {
										this.setAttribut( "dateStartEvent", dates[0]);
										this.setAttribut( "dateEndEvent", dates[1]);
									}
									}}
								 	value={toolService.getDateListFromDate(this.state.event.dateStartEvent, this.state.event.dateEndEvent)}

						>
						      <DatePickerInput
						        id="date-picker-input-id-start"
						        placeholder="mm/dd/yyyy"
						        labelText={<FormattedMessage id="Event.StartDate" defaultMessage="Start Date"/>}								
						      />
						      <DatePickerInput
						        id="date-picker-input-id-finish"
						        placeholder="mm/dd/yyyy"
						        labelText={<FormattedMessage id="Event.EndDate" defaultMessage="End Date"/>}								
						      />
						</DatePicker>
					</div> )
				}

			
							
			</div>
			);
		

		// <div class="btn-group mr-2" role="group" aria-label="First group">
	//							<button style={{"marginLeft ": "10px"}} onClick={this.secItineraire} title="Itineraire" disabled={true} class="btn btn-primary">
	//								<div class="glyphicon glyphicon-road"></div>
	//						</button>
	//					</div>
											
		// -----------------	 
		return ( 
			<div> 
				<div class="row">
					<div class="col-sm-5">
						<img src="img/toghEvent.jpg" style={{width:90}} />
					
						<h1>{this.state.event.name}
						</h1>
					</div>
					<div class="col-sm-5">
						<div class="fieldlabel">{<FormattedMessage id="Event.Status" defaultMessage="Status"/>}</div>
						<EventState statusEvent={this.state.event.statusEvent} modifyEvent={true} changeState={this.changeState} />
					</div>
					<div class="col-sm-2">
					 	<Select  labelText={<FormattedMessage id="Event.Scope" defaultMessage="Scope"/>} 
							id="typeEvent"
							value={this.state.event.typeEvent} 
							onChange={(event) => this.setAttribut( "typeEvent", event.target.value )}>
													 	
													
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
						<br/>
        			</div>
	
				</div>
				<div class="row">
					<div class="col-sm-6">
						<TextArea id="description"
							labelText={<FormattedMessage id="Event.Description" defaultMessage="Description"/>}
							style={{width: "100%", maxWidth: "100%"}} 
							rows={5} 
							value={this.state.event.description} 
							onChange={(event) => this.setAttribut( "description", event.target.value )}/>
					</div>
					<div class="col-sm-6">
						<div class="panel panel-info">
							<div class="panel-heading"><FormattedMessage id="Event.EventDate" defaultMessage="Date"/></div>
							<div class="panel-body">
								{datePanelHtml}								
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<TextInput labelText={<FormattedMessage id="Event.EventName" defaultMessage="Name"/>} value={this.state.event.name} onChange={(event) => this.setAttribut( "name", event.target.value )}></TextInput><br />
					</div>
				</div>	
				
				<div class="row" style={{ padding: "10px 30px 10px"}}>
					
					<div class="btn-toolbar mb-3" role="toolbar" aria-label="Toolbar with button groups" >
				  		<div class="btn-group mr-2" role="group" aria-label="First group">
							<button onClick={this.secParticipant} title='Particpants' onClick={this.accessParticipantList} class="btn btn-primary">
								<img  style={{"float": "right"}} src="img/btnParticipants.png" style={{width:45}} /><br/>
								<FormattedMessage id="Event.Participants" defaultMessage="Participants"/>
							</button>
						</div>
						
			
							
							
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button onClick={this.secDates}  onClick={this.accessItinerary}  title={<FormattedMessage id="Event.TitleItinerary" defaultMessage="Define your itinerary, and point of interest"/>} class="btn btn-primary">
								<div class="glyphicon glyphicon-road"></div><br/>
								<FormattedMessage id="Event.Itinerary" defaultMessage="itinerary"/> 
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secChat} title={<FormattedMessage id="Event.TitleChat" defaultMessage="Chat"/>} disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-bullhorn" ></div><br/>
								<FormattedMessage id="Event.Chat" defaultMessage="Chat"/>
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secTasks} title={<FormattedMessage id="Event.TitleTasks" defaultMessage="Tasks"/>}  onClick={this.accessTaskList}  class="btn btn-primary">
								<div class="glyphicon glyphicon-tasks"></div> <br/>
								<FormattedMessage id="Event.Tasks" defaultMessage="Tasks"/>
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.accessShoppingList} title={<FormattedMessage id="Event.TitleShoppingList" defaultMessage="Shopping list : what to brings?"/>}  class="btn btn-primary">
								<div class="glyphicon glyphicon-shopping-cart"></div><br/>
								<FormattedMessage id="Event.ShoppingList" defaultMessage="Shopping List"/>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secSurvey} title={<FormattedMessage id="Event.TitleSurvey" defaultMessage="Survey"/>} disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-ok-circle"></div><br/>
								<FormattedMessage id="Event.Survey" defaultMessage="Survey"/>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.accessGeolocalisation} title={<FormattedMessage id="Event.TitleGeolocalisation" defaultMessage="Where is the event?"/>} class="btn btn-primary">
								<div class="glyphicon glyphicon-globe"></div><br/>
								<FormattedMessage id="Event.Geolocalisation" defaultMessage="Geolocalisation"/>
							</button>
						</div>
						
						
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secPointofInterest} title={<FormattedMessage id="Event.TitlePhotos" defaultMessage="Photos"/>} disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-camera"></div><br/>
								<FormattedMessage id="Event.Photos" defaultMessage="Photos"/>
							</button>
						</div>
						
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secExpense} title={<FormattedMessage id="Event.TitleExpense" defaultMessage="Manage and share expenses"/>} disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-piggy-bank"></div><br/>
								<FormattedMessage id="Event.Expense" defaultMessage="Expense"/>
							</button>
						</div>

					</div>
					
				</div>
				{ this.state.show.secParticipant !== 'OFF' && <EventParticipants event={this.state.event} show={this.state.show.secParticipant} pingEvent={this.pingEvent}/>}
				{ this.state.show.secItinerary !== 'OFF' && <EventItinerary event={this.state.event} show={this.state.show.secItinerary} pingEvent={this.pingEvent}/>}
				{ this.state.show.secTasklist !== 'OFF' && <EventTaskList event={this.state.event} show={this.state.show.secTasklist} pingEvent={this.pingEvent}/>}
				{ this.state.show.secShoppinglist !== 'OFF' && <EventShoppingList event={this.state.event} show={this.state.show.secShoppinglist} pingEvent={this.pingEvent}/>}
				{ this.state.show.secGeolocalisation !== 'OFF' && <EventGeolocalisation event={this.state.event} show={this.state.show.secGeolocalisation} pingEvent={this.pingEvent}/>}
			</div>)	
	} //---------------------------- end Render


	showRegistration() {
		console.log("ShowRegistration !!!!!!!!!!!!!!!!!!");
		console.log("Event.showRegistration state="+JSON.stringify(this.state));

		this.setState( {showRegistration: true });
	}


	changeState( event ) {
		console.log("Event.setState event ");
		// this.setAttribut("eventState", event);
	}


	// provide automatic save
	setAttribut( name, value ) {
		console.log("Event.setAttribute: attribut:"+name+" <= "+value+" typeof="+ (typeof value)+" EventinProgress="+JSON.stringify(this.state.event));
		var eventValue = this.state.event;
		eventValue[name]= value;

		this.setState( { "event" : eventValue});
		if (this.timer)
			clearTimeout( this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave();  }, 2000);
	}
	
	pingEvent() {
		console.log("Event.pingEvent child change:"+JSON.stringify(this.state.event));
		if (this.timer)
			clearTimeout( this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave();  }, 2000);

	}
	automaticSave() {
		console.log("Event.AutomaticSave: event="+JSON.stringify(this.state.event));
		if (this.timer)
			clearTimeout( this.timer);
		
	}
	
	// -------------------------------------------- Access different part
	
	// accessParticipantList
	 accessParticipantList() {		
		console.log("Event.accessParticipantList state="+JSON.stringify(this.state));
		var event = this.state.event; 
		if (!  event.participants ) {
			console.log("Event.accessParticipantList: not normal but be robust, create one");
			event.participants=[];
			this.setState( {"event" : event })
		}
		this.showSection ("secParticipantlist");

	}
	
	accessItinerary() {
		console.log("Event.accessItinerary ");
		var event = this.state.event; 
		if (! event.itinerarylist ) {
			console.log("Event.accessShoppingList: no task list exist, create one");
			event.itinerarylist=[];
			this.setState( { event : event })
		}
		this.showSection ("secItinerary");
	}
	
	// Tasks list
	accessTaskList() {
		console.log("accessTaskList ");
		var event = this.state.event; 
		if (!  event.tasklist ) {
			console.log("Event.accessShoppingList: no task list exist, create one");
			event.tasklist=[];
			// add one line
			event.tasklist.push( { status: "PLANNED",  what: "", id:1});
			this.setState( {"event" : event })
		}
		this.showSection ("secTasklist");
	}
	
	// Shopping list
	accessShoppingList() {
		console.log("accessShoppingList");
		var event = this.state.event; 
		if (!  event.shoppinglist ) {
			console.log("Event.accessShoppingList: no shopping list exist, create one");
			event.shoppinglist=[];
			
			// add one line
			event.shoppinglist.push( {  status: "TODO", what: "", id: 1});
			
			this.setState( {"event" : event })
		}
		this.showSection ("secShoppinglist");
	}
	
	// Geolocalisation
	accessGeolocalisation() {
		console.log("accessGeolocalisation !!!!!!!!!!!!!!!!!!");
		var event = this.state.event; 
		if (!  event.geoLocalisation ) {
			console.log("Event.accessShoppingList: no shopping list exist, create one");
			event.geoLocalisation={};
			this.setState( {"event" : event })
		}
		this.showSection ("secGeolocalisation");

	}
	
	
	
	// Show the section
	showSection( sectionName ) {
		console.log("Event.showSection: Show the section["+sectionName+"]");

		var show = this.state.show;
		show[ sectionName ] = 'ON'; 
		this.setState( {"show": show });
		
		/* Find matching element by id */
        const anchor = document.getElementById(sectionName);

        if(anchor) {
            /* Scroll to that element if present */
            anchor.scrollIntoView();
        }
	}
	
	
	
	// -------------------------------------------- Call REST
	refreshEvent() {
		console.log("Event: http[event?id="+this.state.eventid+"]");
		this.setState( {event: {}});
	
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson( '/api/event?id='+this.state.eventid, this, httpPayload => {
				console.log("Event.getPayload: get "+httpPayload.trace());
				
				this.setState( {event: httpPayload.getData().event});
				var show = this.state.show;
				if (httpPayload.getData().event && httpPayload.getData().event.shoppinglist)
					show.secShoppingList = 'COLLAPSE';
				this.setState( {show: show});
				});
	
	}
		
	
	
	// -------- Rest Call
	
}
export default Event;