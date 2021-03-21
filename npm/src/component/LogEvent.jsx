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
			listevents: props.listevents
		};
				
		console.log("LogEvent: constructor "+JSON.stringify(this.state.listevents));

	}
	componentDidUpdate(prevProps) {
		console.log("LogEvent.componentDidUpdate listevents=" + JSON.stringify(this.props.listevents));
		if (prevProps.listevents !== this.props.listevents) {
			this.setState({ listevents: this.props.listevents });
		}
	}


	render() {
		console.log("LogEvent: render "+JSON.stringify(this.state.listevents));
		if (! this.state.listevents || this.state.listevents.length===0)
			return (<div/>);
		return (<div>UNE ERREUR ! </div>);
	}
}
export default LogEvent