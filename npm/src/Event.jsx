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
import { TimePickerSelect } from 'carbon-components-react';
import { SelectItem } from 'carbon-components-react';
import { RadioButtonGroup } from 'carbon-components-react';
import { RadioButton } from 'carbon-components-react';

// import DatePickerSkeleton from '@bit/carbon-design-system.carbon-components-react.DatePicker/DatePicker.Skeleton';
// import TimePicker from '@bit/carbon-design-system.carbon-components-react.time-picker';


import EventParticipants from './EventParticipants';
import EventShoppingList from './EventShoppingList';


class Event extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : {}, 
						'eventid' : props.eventid,
						'show' : { 'secParticipant': 'ON', 'secShoppinglist' : 'OFF'},
						showRegistration : false
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.showRegistration 			= this.showRegistration.bind(this);
		this.accessParticipantList 		= this.accessParticipantList.bind(this);
		this.accessShoppingList			= this.accessShoppingList.bind(this);
		

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.refreshEvent 		= this.refreshEvent.bind(this);
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
		
		var statusHtml = this.getStatusHtml( this.state.event);
		
	
				
		var datePanelHtml = (
			<div>
				<RadioButtonGroup
						valueSelected={this.state.event.datePolicy}
						legend="Legend"
						onChange={(event) => {
							console.log("RadioGroup on change="+JSON.stringify(event));
        					
							this.setAttribut( "datePolicy", event)}
							}
						>
					<RadioButton value="ONEDATE" id="r1" labelText="One date" labelPosition="right" />
					<RadioButton value="PERIOD" id="r2"  labelText="Period" labelPosition="right"/>
				</RadioButtonGroup>
				{ this.state.event.datePolicy === 'ONEDATE' && (	
					<div>
						<DatePicker datePickerType="single"
							onChange={(event) => this.setAttribut( "dateEvent", event ) }>
        							 
							<DatePickerInput
						    	placeholder="mm/dd/yyyy"
								dateFormat="mm/dd/yyyy"
						      	labelText="Date Picker label"
						      	id="date-picker-simple"
							  	value={this.state.event.dateEvent} 
							  	showTimeSelect
						    />
						</DatePicker>
						<TimePicker
						  value={this.state.event.timeEvent} 
						  onChange={(event) => this.setAttribut( "timeEvent", event.target.value )}
						>
							
						</TimePicker>
					</div>)
				}	
				{ this.state.event.datePolicy !== 'ONEDATE' && (	
					<div>
						 <DatePicker datePickerType="range"
				  				onChange={(selectedDates, dateStr) => { 
									console.log("RangeDatePicker :"+selectedDates+" dateStr="+dateStr);
									}}
						>
						      <DatePickerInput
						        id="date-picker-input-id-start"
						        placeholder="mm/dd/yyyy"
						        labelText="Start date"
								value={this.state.event.dateStartEvent} 
								onChange={(selectedDates, dateStr) => { 
									console.log("RangeDatePickerSTART :"+selectedDates+" dateStr="+dateStr);
									}}
						      />
						      <DatePickerInput
						        id="date-picker-input-id-finish"
						        placeholder="mm/dd/yyyy"
						        labelText="End date"
								value={this.state.event.dateEndEvent} 
								onChange={(selectedDates, dateStr) => { 
									console.log("RangeDatePickerEND :"+selectedDates+" dateStr="+dateStr);
									}}
						      />
						</DatePicker>
					</div> )
				}

				<div> <p/> Policy {this.state.event.datePolicy} 
					Date: {this.state.event.dateEvent} 
					Time: {this.state.event.timeEvent}
					Range :{this.state.event.dateStartEvent} to {this.state.event.dateEndEvent}
				</div>
							
			</div>
			);
		

					
		// -----------------	 
		return ( 
			<div> 
				<div class="row">
					<div class="col-sm-6">
						<h1>{this.state.event.name}
						</h1>
					</div>
					<div class="col-sm-2">
						<div class="fieldlabel">Status</div>
						{statusHtml}
					</div>
					<div class="col-sm-2">
						<div class="fieldlabel">Scope</div>
					 	<select value={this.state.event.typeEvent} onChange={(event) => this.setAttribut( "typeEvent", event.target.value )}>
							<option value="OPEN">Open</option>
							<option value="OPENCONF">Open on confirmation</option>
							<option value="LIMITED">Limited</option>
							<option value="SECRET">Secret</option>
						</select>
        			</div>
	
				</div>
				<div class="row">
					<div class="col-sm-6">
						<div class="fieldlabel">Name</div>
						<input value={this.state.event.name} onChange={(event) => this.setAttribut( "name", event.target.value )} class="toghinput"></input><br />
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
						<div class="fieldlabel">Description</div>
						<textarea  style={{width: "100%", maxWidth: "100%"}} rows="5" value={this.state.event.description} onChange={(event) => this.setAttribut( "description", event.target.value )}></textarea>
					</div>
				</div>	
				
				<div class="row" style={{ padding: "10px 30px 10px"}}>
					
					<div class="btn-toolbar mb-3" role="toolbar" aria-label="Toolbar with button groups" >
				  		<div class="btn-group mr-2" role="group" aria-label="First group">
							<button   onClick={this.secParticipant} title="Participants" onClick={this.accessParticipantList} class="btn btn-primary">
								<div class="glyphicon glyphicon-user"></div> 
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
							<button style={{"marginLeft ": "10px"}} onClick={this.secTasks} title="Tasks" class="btn btn-primary">
								<div class="glyphicon glyphicon-tasks"></div> 
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} title="Shopping list : what to brings?" onClick={this.accessShoppingList} class="btn btn-primary">
								<div class="glyphicon glyphicon-shopping-cart"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secSurvey} title="Manage Survey" class="btn btn-primary">
								<div class="glyphicon glyphicon-ok-circle"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secLocalisation} title="Where is the event?" class="btn btn-primary">
								<div class="glyphicon glyphicon-globe"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secItineraire} title="Itineraire" class="btn btn-primary">
								<div class="glyphicon glyphicon-road"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secPointofInterest} title="Point of interest" class="btn btn-primary">
								<div class="glyphicon glyphicon-camera"></div>
							</button>
						</div>

						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secNight} title="What do we sleep?" class="btn btn-primary">
								<div class="glyphicon glyphicon-home"></div>
							</button>
						</div>
						
						<div class="btn-group mr-2" role="group" aria-label="First group">
							<button style={{"marginLeft ": "10px"}} onClick={this.secExpense} title="Expense" class="btn btn-primary">
								<div class="glyphicon glyphicon-piggy-bank"></div>
							</button>
						</div>

					</div>
					
				</div>
				{ this.state.show.secParticipant !== 'OFF' && <EventParticipants event={this.state.event} show={this.state.show.secParticipant} pingEvent={this.pingEvent}/>}
				{ this.state.show.secShoppinglist !== 'OFF' && <EventShoppingList event={this.state.event} show={this.state.show.secShoppinglist} pingEvent={this.pingEvent}/>}
			</div>)	
	} //---------------------------- end Render


	showRegistration() {
		console.log("ShowRegistration !!!!!!!!!!!!!!!!!!");
		console.log("Event.showRegistration state="+JSON.stringify(this.state));

		this.setState( {showRegistration: true });
	}


	getStatusHtml( event ) {
		if (event.statusEvent === 'INPREPAR')
			return (<div class="label label-info">In preparation</div>);			
		else if (this.state.event.statusEvent === 'INPROG')
			return  (<div class="label label-info">Actif</div>);
		else if (this.state.event.statusEvent === 'CLOSED')
			return (<div class="label label-warning">Done</div>);
		else if (this.state.event.statusEvent === 'CANCEL')
			return (<div class="label label-danger">Cancelled</div>);
		else 
			return (<div class="label label-danger">{event.statusEvent}</div>);
	}



	// provide automatic save
	setAttribut( name, value ) {
		console.log("Event.setAttribut: attribut:"+name+" <= "+value+" valueEvent="+JSON.stringify(this.state.event));
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
		console.log("Event.accessShoppingList state="+JSON.stringify(this.state));
		var event = this.state.event; 
		if (!  event.shoppinglist ) {
			console.log("Event.accessShoppingList: no shopping list exist, create one");
			event.shoppinglist=[ {"what":"", "who":""} ];
			event.shoppinglist.push( { "what": "line 2"});
			this.setState( {"event" : event })
		}
		this.showSection ("secShoppinglist");

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
		restCallService.getJson( '/api/event?id='+this.state.eventid, httpPayload => {
				console.log("Event.getPayload: get "+JSON.stringify(httpPayload.data)); 
				this.setState( {event: httpPayload.data.event});
				var show = this.state.show;
				if (httpPayload.data.event && httpPayload.data.event.shoppinglist)
					show.secShoppingList = 'COLLAPSE';
				console.log("Event.getPayload: show "+JSON.stringify(show)); 

				this.setState( {show: show});
				});
		/*
		
 		
		const requestOptions = {
	        method: 'GET',
	        headers: { 'Content-Type': 'application/json' }
	    };
    	fetch('event?id='+this.state.eventid, requestOptions)
			.then(response => response.json())
        	.then( httpPayload => this.setEventCallback( httpPayload ));
*/
	}
	/*
	setEventCallback( httpPayload ) {
		console.log("Event.getPayload: get "+JSON.stringify(httpPayload)); 
		this.setState( {event: httpPayload.event});
		var show = this.state.show;
		if (httpPayload.event && httpPayload.event.shoppinglist)
			show.secShoppingList = 'COLLAPSE';
				
		console.log("Event.getPayload: show "+JSON.stringify(show)); 

		this.setState( {show: show});
	}
*/
	
	
	
	// -------- Rest Call
	
}
export default Event;