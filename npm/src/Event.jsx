// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

import React from 'react';

import FactoryService from './service/FactoryService';

// import { Button } from 'carbon-components-react';
import { DatePicker } from 'carbon-components-react';
import { DatePickerInput } from 'carbon-components-react';
import { TimePicker } from 'carbon-components-react';
import { RadioButtonGroup } from 'carbon-components-react';
import { RadioButton } from 'carbon-components-react';
import { TextArea } from 'carbon-components-react';
import { TextInput } from 'carbon-components-react';
import { Select } from 'carbon-components-react';

// import DatePickerSkeleton from '@bit/carbon-design-system.carbon-components-react.DatePicker/DatePicker.Skeleton';
// import TimePicker from '@bit/carbon-design-system.carbon-components-react.time-picker';


import EventParticipants from './EventParticipants';
import EventShoppingList from './EventShoppingList';
import EventGeolocalisation from './EventGeolocalisation';

import EventState from './EventState';


class Event extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : {}, 
						'eventid' : props.eventid,
						'show' : { 'secParticipant': 'ON', 'secShoppinglist' : 'OFF', 'secGeolocalisation' : 'OFF'},
						showRegistration : false
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.showRegistration 			= this.showRegistration.bind(this);
		this.accessParticipantList 		= this.accessParticipantList.bind(this);
		this.accessShoppingList			= this.accessShoppingList.bind(this);
		this.accessGeolocalisation		= this.accessGeolocalisation.bind(this);
		

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.refreshEvent 		= this.refreshEvent.bind(this);
		this.changeState 		= this.changeState.bind(this);
		this.setAttribut 		= this.setAttribut.bind(this);
		this.pingEvent			= this.pingEvent.bind(this);
		this.refreshEvent();

	}

	//----------------------------------- Render
	render() {
		console.log("Event.render eventId="+this.props.eventid + " event="+JSON.stringify(this.state.event));

		// no map read, return
		if (! this.state.event || Object.keys(this.state.event).length === 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}
				
		console.log("Event.render: section=secShoppinglist= "+this.state.show.secShoppinglist);
		
				
		var datePanelHtml = (
			<div>
				<RadioButtonGroup
					name="datePolicy"
					valueSelected={this.state.event.datePolicy}
					legend="Legend"
					onChange={(event) => {
							console.log("RadioGroup.DataPolicy on change=");
        					
							this.setAttribut( "datePolicy", event)}
							}
						>
					<RadioButton value="ONEDATE" id="r1" labelText="One date" labelPosition="right" />
					<RadioButton value="PERIOD" id="r2"  labelText="Period" labelPosition="right"/>
				</RadioButtonGroup>
				{ this.state.event.datePolicy === 'ONEDATE' && (	
					<div>
						<DatePicker datePickerType="single"
							onChange={(dates) => {
									console.log("SingleDatePicker :"+dates.length+" is an array "+Array.isArray(dates));
 
									if (dates.length >= 1) {
										console.log("SingleDatePicker set Date");
										this.setAttribut( "dateEvent", dates[0] );
									}
								}
							}
						  	value={this.getDateListFromDate(this.state.event.dateEvent)}

							>
        							 
							<DatePickerInput
						    	placeholder="mm/dd/yyyy"
						      	labelText="Date Event"
						      	id="date-picker-simple"
						    />
						</DatePicker>
						<TimePicker
							id="eventTime"
						  	value={this.state.event.timeEvent} 
						  	onChange={(event) => this.setAttribut( "timeEvent", event.target.value )}
						>
							
						</TimePicker>
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
								 	value={this.getDateListFromDate(this.state.event.dateStartEvent, this.state.event.dateEndEvent)}

						>
						      <DatePickerInput
						        id="date-picker-input-id-start"
						        placeholder="mm/dd/yyyy"
						        labelText="Start date"								
						      />
						      <DatePickerInput
						        id="date-picker-input-id-finish"
						        placeholder="mm/dd/yyyy"
						        labelText="End date"								
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
						<div class="fieldlabel">Status</div>
						<EventState statusEvent={this.state.event.statusEvent} modifyEvent={true} changeState={this.changeState} />
					</div>
					<div class="col-sm-2">
					 	<Select  labelText="Scope" 
							id="typeEvent"
							value={this.state.event.typeEvent} 
							onChange={(event) => this.setAttribut( "typeEvent", event.target.value )}>
							<option value="OPEN">Open</option>
							<option value="OPENCONF">Open on confirmation</option>
							<option value="LIMITED">Limited</option>
							<option value="SECRET">Secret</option>
						</Select>
						<br/>
        			</div>
	
				</div>
				<div class="row">
					<div class="col-sm-6">
						<TextArea id="description"
							labelText="Description"
							style={{width: "100%", maxWidth: "100%"}} 
							rows="5" 
							value={this.state.event.description} 
							onChange={(event) => this.setAttribut( "description", event.target.value )}></TextArea>
					</div>
					<div class="col-sm-6">
						<div class="panel panel-info">
							<div class="panel-heading">Date</div>
							<div class="panel-body">
								{datePanelHtml}								
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<TextInput labelText="Name" value={this.state.event.name} onChange={(event) => this.setAttribut( "name", event.target.value )}></TextInput><br />
					</div>
				</div>	
				
				<div class="row" style={{ padding: "10px 30px 10px"}}>
					
					<div class="btn-toolbar mb-3" role="toolbar" aria-label="Toolbar with button groups" >
				  		<div class="btn-group mr-2" role="group" aria-label="First group">
							<button   onClick={this.secParticipant} title="Participants" onClick={this.accessParticipantList} class="btn btn-primary">
								<img  style={{"float": "right"}} src="img/btnParticipants.png" style={{width:45}} />
							</button>
						</div>
						
			
							
							
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button onClick={this.secDates} title="Dates" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-calendar"></div> 
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secChat} title="Chat channel" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-bullhorn" ></div>
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secTasks} title="Tasks" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-tasks"></div> 
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} title="Shopping list : what to brings?" onClick={this.accessShoppingList} class="btn btn-primary">
								<div class="glyphicon glyphicon-shopping-cart"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secSurvey} title="Manage Survey" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-ok-circle"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.accessGeolocalisation} title="Where is the event?" class="btn btn-primary">
								<div class="glyphicon glyphicon-globe"></div>
							</button>
						</div>
						
						
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secPointofInterest} title="Point of interest" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-camera"></div>
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secNight} title="What do we sleep?" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-home"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secExpense} title="Expense" disabled={true} class="btn btn-primary">
								<div class="glyphicon glyphicon-piggy-bank"></div>
							</button>
						</div>

					</div>
					
				</div>
				{ this.state.show.secParticipant !== 'OFF' && <EventParticipants event={this.state.event} show={this.state.show.secParticipant} pingEvent={this.pingEvent}/>}
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
	
	// Shopping list
	accessShoppingList() {
		console.log("accessShoppingList !!!!!!!!!!!!!!!!!!");
		var event = this.state.event; 
		if (!  event.shoppinglist ) {
			console.log("Event.accessShoppingList: no shopping list exist, create one");
			event.shoppinglist=[];
			
			// add one line
			event.shoppinglist.push( { 'what:':''});
			
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
	
	getDateListFromDate( dateone, datetwo ) {
		console.log("Event.getDateListFromDate: ");
		var listDates = [];
		listDates.push( dateone);
		if (datetwo)
			listDates.push( datetwo);
		return listDates;
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