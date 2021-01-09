// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

class Event extends React.Component {
	constructor(props) {
		super();
		this.state = {  }
		console.log("Event.constructor : eventSelected="+props.eventid);
		
		this.state = { 'event' : {}, 'eventid' : props.eventid};
		// this is mandatory to have access to the variable in the method... thank you React!   
		this.refreshEvent = this.refreshEvent.bind(this);
		this.refreshEvent();

	}


	// -------------------------------------------- render
	render() {
		console.log("Event.render eventId="+this.props.eventid + " event="+this.state.event);
		// no map read, return
		if (! this.state.event || Object.keys(this.state.event).length == 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}
				
				
		console.log("Event.render: Display the event="+JSON.stringify(this.state.event));

		return ( 
			<div> 
				<h1>Event Name: {this.state.event.name}</h1>
				
				
				<hr></hr>
				<h2>Partipants</h2>
				Participants : <button class="btn btn-info">Add Participant</button><p/>
				<h2>Dates</h2>

				Dates : 
				<h2>Tasks</h2>
				<h2>Lists to bring</h2>

				<h2>Survey</h2>
				<h2>Localisation</h2>
				<h2>Itineraires</h2>
				<h2>Point of interest</h2>
				<h2>Nights</h2>
				<h2>Expenses</h2>
				
				
			</div>)
	}
	/**
	<textarea value="{this.state.event.description}"></textarea>
				
				Date : {this.state.event.dateOfEvent}<p/>
				<h2>Partipants</h2>
				Participants : <button class="btn btn-info">Add Participant</button><p/>
				<h2>Dates</h2>
				<input type="checkbox">Multiple date</input>
				Dates : 
				<h2>Tasks</h2>
				<h2>Lists to bring</h2>

				<h2>Survey</h2>
				<h2>Localisation</h2>
				<h2>Itineraires</h2>
				<h2>Point of interest</h2>
				<h2>Nights</h2>
				<h2>Expenses</h2>
				 */
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
        	.then( httpPayload => this.setEvent( httpPayload ));
	}
	setEvent( httpPayload ) {
		console.log("Event.getPayload: get "+JSON.stringify(httpPayload)); 
		this.setState( {event: httpPayload.event});
	}
}


console.log("Event Render id=" + document.getElementById('reactEvent'));

// the marker is maybe not in the page
if (document.getElementById('reactEvent'))
	ReactDOM.render(<Event />, document.getElementById('reactEvent'));
