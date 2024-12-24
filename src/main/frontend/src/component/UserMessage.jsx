// -----------------------------------------------------------
//
// UserMessage
//
// Display a message to the user
//
// -----------------------------------------------------------

import React from 'react';


import { Check, ShieldFillX } from 'react-bootstrap-icons';

class UserMessage extends React.Component {
		
	constructor( props ) {
		super();
		this.state={ message : props.message, status: props.status};
	}
		
	componentDidUpdate(prevProps) {
  		if (prevProps.status !== this.props.status) {
    		this.setState( {status: this.props.status});
		}
		if (prevProps.message !== this.props.message) {
    		this.setState( {message: this.props.message});
		}
	}
	//----------------------------------- Render
	render() {
		return (
				<div>
					<table>
					<tr style={{paddingTop:"20px", paddingBottom:"20px"}}>
						<td style={{paddingRight:"5px"}}>
							{this.state.status === "OK" && <div style={{color:"green"}}><Check width="30px" height="30px"/></div>}
							{this.state.status === "FAIL" && <div style={{color:"red"}}><ShieldFillX  width="20px" height="20px"/></div>}
						</td>
						<td>
							{this.state.message}
						</td></tr></table>
				</div>
		)							
	}
		
}
export default UserMessage;		
