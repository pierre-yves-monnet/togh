// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

// import { Button } from 'carbon-components-react';


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


	render() {
		
	
		console.log("Event.render eventId="+this.props.eventid + " event="+JSON.stringify(this.state.event));

		// no map read, return
		if (! this.state.event || Object.keys(this.state.event).length == 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}
				
		console.log("Event.render: section=secShoppinglist= "+this.state.show.secShoppinglist);
		
		var statusHtml;
		
		if (this.state.statusEvent === 'INPREPAR')
			statusHtml= (<div class="label label-info">In preparation</div>);			
		else if (this.state.statusEvent === 'INPROG')
			statusHtml= (<div class="label label-info">Actif</div>);
		else if (this.state.statusEvent === 'CLOSED')
			statusHtml= (<div class="label label-warning">Done</div>);
		else if (this.state.statusEvent === 'CANCEL')
			statusHtml= (<div class="label label-danger">Cancelled</div>);
		else 
			statusHtml= (<div class="label label-danger">{this.state.statusEvent}</div>);

		return ( 
			<div> 
				Hello
				<div class="row">
					<div class="col-sm-6">
						<h1>{this.state.event.name}
						</h1>
					</div>
					<div class="col-sm-2">
						satus={statusHtml}  = {this.state.statusEvent}
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
						<div class="fieldlabel">Date</div>
						<input type="datetime" value={this.state.event.dateEvent} onChange={(event) => this.setAttribut( "dateEvent", event.target.value )} class="toghinput"></input><br />
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<div class="fieldlabel">Description</div>
						<textarea  style={{width: "100%", "max-width": "100%"}} rows="5" value={this.state.event.description} onChange={(event) => this.setAttribut( "description", event.target.value )}></textarea>
					</div>
				</div>	
				
				<div class="row">
					<div class="btn-group" role="group" style={{padding: "10px 10px 10px 10px"}}>
						<button class="glyphicon glyphicon-user" style={{"margin-left": "10px"}} onClick={this.secParticipant} title="Participants" onClick={this.accessParticipantList}></button>
						<button class="glyphicon glyphicon-calendar" style={{"margin-left": "10px"}} onClick={this.secDates} title="Dates" disabled={true}></button>						 
						<button class="glyphicon glyphicon-bullhorn" style={{"margin-left": "10px"}} onClick={this.secChat} title="Chat channel" disabled={true}></button>
						<button class="glyphicon glyphicon-tasks" style={{"margin-left": "10px"}} onClick={this.secTasks} title="Tasks"></button>

						<button class="glyphicon glyphicon-shopping-cart" style={{"margin-left": "10px"}} title="Shopping list : what to brings?" onClick={this.accessShoppingList}></button>
						
						<button class="glyphicon glyphicon-ok-circle" style={{"margin-left": "10px"}} onClick={this.secSurvey} title="Manage Survey"></button>
						<button class="glyphicon glyphicon-globe" style={{"margin-left": "10px"}} onClick={this.secLocalisation} title="Where is the event?"></button>
						<button class="glyphicon glyphicon-road" style={{"margin-left": "10px"}} onClick={this.secItineraire} title="Itineraire"></button>
						<button class="glyphicon glyphicon-camera" style={{"margin-left": "10px"}} onClick={this.secPointofInterest} title="Point of interest"></button>

						<button class="glyphicon glyphicon-home" style={{"margin-left": "10px"}} onClick={this.secNight} title="What do we sleep?"></button>
						<button class="glyphicon glyphicon-piggy-bank" style={{"margin-left": "10px"}} onClick={this.secExpense} title="Expense"></button>
					</div>
					
				</div>
				{ this.state.show.secParticipant !== 'OFF' && <EventParticipant event={this.state.event} show={this.state.show.secParticipant} pingEvent={this.pingEvent}/>}
				{ this.state.show.secShoppinglist !== 'OFF' && <EventShoppingList event={this.state.event} show={this.state.show.secShoppinglist} pingEvent={this.pingEvent}/>}
			</div>)
	
	}

	showRegistration() {
		console.log("ShowRegistration !!!!!!!!!!!!!!!!!!");
		console.log("Event.showRegistration state="+JSON.stringify(this.state));

		this.setState( {showRegistration: true });
	}

	


	// provide automatic save
	setAttribut( name, value ) {
		console.log("Event.setAttribut: attribut:"+name+" <= "+value+" event="+this.state.event);
		var eventValue = this.event;
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
		
		const requestOptions = {
	        method: 'GET',
	        headers: { 'Content-Type': 'application/json' }
	    };
    	fetch('event?id='+this.state.eventid, requestOptions)
			.then(response => response.json())
        	.then( httpPayload => this.setEventCallback( httpPayload ));
	}
	setEventCallback( httpPayload ) {
		console.log("Event.getPayload: get "+JSON.stringify(httpPayload)); 
		this.setState( {event: httpPayload.event});
		var show = this.state.show;
		if (httpPayload.event && httpPayload.event.shoppinglist)
			show.secShoppingList = 'COLLAPSE';
				
		console.log("Event.getPayload: show "+JSON.stringify(show)); 

		this.setState( {show: show});
	}

	
	
	
	// -------- Rest Call
	
}
console.log("Event Render id=" + document.getElementById('reactEvent'));

// the marker is maybe not in the page
if (document.getElementById('reactEvent'))
	ReactDOM.render(<Event />, document.getElementById('reactEvent'));
