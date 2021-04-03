/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


import React from 'react';


import {  PlusCircle } from 'react-bootstrap-icons';

import UserTips from './UserTips';

// -----------------------------------------------------------
//
// EventSectionHeader
//
// Normalize the header of a section 
//
// -----------------------------------------------------------

class LogEvent extends React.Component {
	
	// props.addItemCallback must be defined if a button PLUS is displayed
	constructor(props) {
		super();
		this.state = {
			listEvents: props.listEvents
		};
				
		console.log("LogEvent: constructor "+JSON.stringify(this.state.listEvents));

	}
	componentDidUpdate(prevProps) {
		console.log("LogEvent.componentDidUpdate listEvents=" + JSON.stringify(this.props.listEvents));
		if (prevProps.listEvents !== this.props.listEvents) {
			this.setState({ listEvents: this.props.listEvents });
		}
	}

	

  
	render() {
		console.log("LogEvent: render "+JSON.stringify(this.state.listEvents));
		
		
		if (! this.state.listEvents || this.state.listEvents.length===0)
			return (<div/>);
		return (<div>
				{this.state.listEvents.map((event) =>
						<div>
							<div dangerouslySetInnerHTML={{__html: event.html}}></div>
							<br/>						
						</div>
					)}
				</div>);
	}
}
export default LogEvent