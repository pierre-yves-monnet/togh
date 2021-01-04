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
		console.log("Event.render eventId="+this.props.eventid);
		// no map read, return
		if (! this.props.eventid) {
			return (<div />);
		}
				
				
		console.log("Event.render Display event="+JSON.stringify(this.state.event));

		return ( 
			<div> 
				<div class="row">
					<table><tr><td><h1>Event</h1> </td>
					</tr>
					</table>
				</div>
				
				Name: {this.state.event.name}<p/>
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
				
				
			</div>)
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
        	.then( httpPayload => { console.log("event: get "+httpPayload); this.setState( {event: httpPayload.event}); });
	}
	
}


console.log("Event Render id=" + document.getElementById('reactEvent'));

ReactDOM.render(<Event />, document.getElementById('reactEvent'));
