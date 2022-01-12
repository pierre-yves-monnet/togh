// -----------------------------------------------------------
//
// EventState
//
// Display the event state
//
// -----------------------------------------------------------

import React from 'react';

import { injectIntl } from "react-intl";
import TagDropdown from 'component/TagDropdown';


class EventState extends React.Component {

	constructor( props ) {
		super();
		
		this.state = { 'statusEvent' : props.statusEvent,
						'disabled' : props.disabled};
	    console.log("EventState: statusEvent["+props.statusEvent+"] disabled=["+props.disabled+"]")
	}

	
//----------------------------------- Render
	render() {
		// console.log("EventState.render Status="+JSON.stringify(this.state.statusEvent));

		const intl = this.props.intl;
		
		const listOptions = [
			{ label: intl.formatMessage({id: "EventState.InPreparation",defaultMessage: "In Preparation"}),
			 value: "INPREPAR",
			 type: "teal" },			
			{ label: intl.formatMessage({id: "EventState.Actif",defaultMessage: "Actif"}),
			 value:  "INPROG",
			 type: "green" },
			{ label: intl.formatMessage({id: "EventState.Done",defaultMessage: "Done"}),
			 value:  "CLOSED",
			 type: "warm-gray" },
			{ label: intl.formatMessage({id: "EventState.Cancelled",defaultMessage: "Cancelled"}),
			 value:  "CANCELLED",
			 type: "red" },
		];
	
		
			
		return (<TagDropdown listOptions={listOptions} value={this.state.statusEvent} 
					disabled={this.state.disabled}
					changeState={(value) => {
						this.props.changeState( value );
					}} />);
			

	}
}
export default injectIntl(EventState);