// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

class Event extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : {}, 
						'eventid' : props.eventid,
						'show' : { 'secShoppinglist' : 'OFF'},
						showRegistration : false
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.showRegistration = this.showRegistration.bind(this);
		this.accessShoppingList	= this.accessShoppingList.bind(this);

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
		
		return ( 
			<div> 
				<div class="row">
					<div class="col-sm-6">
						<h1>Event {this.state.event.name}
						</h1>
					</div>
					<div class="col-sm-2">
						<button class="glyphicon glyphicon-pencil"></button>
					</div>
					<div class="col-sm-2">
					 	<select value={this.state.event.typeEvent} onChange={(event) => this.setAttribut( "event.typeEvent", event.target.value )}>
							<option value="OPEN">Open</option>
							<option value="OPENCONF">Open on confirmation</option>
							<option value="LIMITED">Limited</option>
							<option value="SECRET">Secret</option>
						</select>
        			</div>
	
				</div>
				<div class="row">
					<div class="col-sm-6">
						<i>Name:</i><br/>
						<input value={this.state.event.name} onChange={(event) => this.setAttribut( "event.name", event.target.value )} class="toghinput"></input><br />
					</div>
					<div class="col-sm-6">
						<i>Date:</i><br/>
						<input type="datetime" value={this.state.event.dateEvent} onChange={(event) => this.setAttribut( "event.dateEvent", event.target.value )} class="toghinput"></input><br />
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<i>Description</i>
						<textarea  style={{width: "100%", "max-width": "100%"}} rows="5" value={this.state.event.description} onChange={(event) => this.setAttribut( "event.description", event.target.value )}></textarea>
					</div>
				</div>	
				
				<div class="row">
					<div class="btn-group" role="group" style={{padding: "10px 10px 10px 10px"}}>
						<button class="glyphicon glyphicon-user" style={{"margin-left": "10px"}} onClick={this.secParticipant} title="Participants" disabled={true}></button>
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
		console.log("Event.setAttribut: attribut:"+name+" <= "+value);
		var eventValue = { [name] : value};
		this.setState( { "event" : eventValue});
		if (this.timer)
			clearTimeout( this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave();  }, 5000);
	}
	
	pingEvent() {
		console.log("Event.pingEvent child change:"+JSON.stringify(this.state.event));
		if (this.timer)
			clearTimeout( this.timer);
		this.timer = this.timer = setTimeout(() => { this.automaticSave();  }, 5000);

	}
	automaticSave() {
		console.log("Event.AutomaticSave event="+JSON.stringify(this.state.event));
		
		
	}
	
	// -------------------------------------------- Access different part
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
