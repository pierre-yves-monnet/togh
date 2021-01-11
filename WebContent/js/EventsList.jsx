// -----------------------------------------------------------
//
// HomeEvents
//
// HomeEvents page.Display the list of events
//
// -----------------------------------------------------------

class EventsList extends React.Component {
	
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.selectEvent( event.id)
	constructor(props) {
		super();
		this.state = {  }
		console.log("EventsList.constructor - expected selectEvent in the props");
		this.state = { filterEvents: "", "message": "", events: []};
		// this is mandatory to have access to the variable in the method... thank you React!   
		this.refreshListEvents = this.refreshListEvents.bind(this);
		this.createEventCallback = this.createEventCallback.bind( this);
		this.refreshListEvents();

	}


	// -------------------------------------------- render
	render() {
		console.log("EventList.render listEvents "+JSON.stringify(this.state.events));
		// no map read, return
		var listEventsHtml=[];
		if ( this.state.events) {
			listEventsHtml = this.state.events.map((event) =>
			  <tr onClick={() =>this.props.selectEvent( event.id)}>
				<td><button class="glyphicon glyphicon glyphicon-tint" title="Access this event"></button> {event.name}</td>
				<td>{event.dateevent}</td>
			 </tr>
			);
		}
		return ( 
			<div class="container-fluid"> 
				<div class="row">
					<h1>List events</h1>
					<div style={{float: "right"}}>
					 	<button class="btn btn-info btn-lg" onClick={this.createEvent}>
							<div class="glyphicon glyphicon-plus"> </div>&nbsp;Create an Event</button>
					</div>
				</div>
				<div class="row">
					<div class="btn-group" role="group" style={{padding: "10px 10px 10px 10px"}}>
						<button class="glyphicon glyphicon-refresh" style={{"margin-left": "10px"}} onClick={this.refreshListEvents}></button>
						<button class="glyphicon glyphicon-menu-hamburger" title="All events" style={{"margin-left": "10px"}}></button>
						<button class="glyphicon glyphicon-user" title="My events"  style={{"margin-left": "10px"}}></button>
					</div>
					
				</div>
				<div class="row">
					<table class="table table-striped">
					<tr>
					
						<th>Name</th>
						<th>Date</th>
						<th>Participants</th>
					</tr>
					{listEventsHtml}
					</table>
				</div>
			</div>)
	}
	
	
	// -------------------------------------------- Call REST
	createEvent() {
		console.log("EventsList.createEvent: http[event/create?]");
		
		const requestOptions = {
	        method: 'POST',
	        headers: { 'Content-Type': 'application/json' }
	    };
    	fetch('event/create?', requestOptions)
			.then( response => response.json())
        	.then( httpPayload => this.createEventCallback( httpPayload ));
	}
	
	createEventCallback( httpPayload) {
		console.log("EventList.createEventCallback payload=");
		if (httpPayload.eventid) {
			this.props.selectEvent( httpPayload.eventid)
		} else {
			this.setState( {"message" : httpPayload.message } );
		}
			
	}
	
	
	refreshListEvents() {
		console.log("EventsList.refreshListEvents http[event/list?filterEvents="+this.state.filterEvents+"]");
		this.setState( {events: []});
		
		const requestOptions = {
	        method: 'GET',
	        headers: { 'Content-Type': 'application/json' }
	    };
    	fetch('event/list?filterEvents='+this.state.filterEvents, requestOptions)
			.then(response => response.json())
        	.then( httpPayload => this.refreshListEventsCallback( httpPayload ));
	}
	
	refreshListEventsCallback( httpPayload ) {
		console.log("EventsList.refreshListEventsCallback: connectStatus = "+JSON.stringify(httpPayload));
 		this.setState( {events: httpPayload.events});
		
	} // end connectStatus
}


console.log("EventsList Render id=" + document.getElementById('reactEventsList'));

// the marker is maybe not in the page
if (document.getElementById('reactEventsList'))
	ReactDOM.render(<EventsList />, document.getElementById('reactEventsList'));
