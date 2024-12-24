// -----------------------------------------------------------
//
// AdminGoogle
//
// Google access
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

// -----------------------------------------------------------
//
// AdminGoogle
//
// Google access
//
// -----------------------------------------------------------
class AdminGoogle extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {};
		
		
	}
	

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		// console.log("AdminGoogle.render:");
		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminGoogle.Google" defaultMessage="Google" />
				</div>
				<div class="card-body">
				 	<a class="btn btn-primary" href="https://console.cloud.google.com/apis/dashboard?project=togh-2021" target="blank"><FormattedMessage id="AdminGoogle.Dahsboard" defaultMessage="Dashboard"/></a>
					<br/><br/>
					<a class="btn btn-primary" href="https://console.cloud.google.com/billing/01FCEA-A20325-32EEDF?project=togh-2021" target="blank"><FormattedMessage id="AdminGoogle.Billing" defaultMessage="Billing"/></a>
				</div>	 
			</div>
			);
	}
	
	
}

export default AdminGoogle;

