// -----------------------------------------------------------
//
// LogEvents
//
// HomeEvents page.Display the list of events
//
// -----------------------------------------------------------

import React from 'react';



class LogEvents extends React.Component {

	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.homeSelectEvent( event.id)
	constructor(props) {
		super();
		this.state = {}
		console.log("EventsList.constructor");
		this.state = { listEvents: props.listEvents};
	}
	
	render() {
		console.log("LogEvents.render: listEvents=" + JSON.stringify( this.state.listEvents));
		var listEventsHtml = this.state.listEvents.map((event) =>
		<div>
			{event.html}
			<br/>
		</div>);
		return ({listEventsHtml});
	}
}

export default LogEvents;