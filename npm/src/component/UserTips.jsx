// -----------------------------------------------------------
//
// UserTips
//
// According the user profile, display tips and information 
//
// -----------------------------------------------------------
//

import React from 'react';

import { FormattedMessage } from "react-intl";
import {  InfoCircle } from 'react-bootstrap-icons';


import FactoryService from './../service/FactoryService';


class UserTips extends React.Component {
	
	// props.text is the text to display, translated
	constructor(props) {
		super();
		this.state = {
			text: props.text,
			id: props.tip
		};
		
	}

	render() {
		var userService = FactoryService.getInstance().getUserService();
		// console.log("UserTip ; display tip Preference= "+userService.prefsDisplayTips());
		
		
		if (userService.prefsDisplayTips()) {
			return (<div class="toghTips">
						<div class="row">
							<div class="col-1">
								<InfoCircle width="50px" height="50px" color="#1f78b4"/>
							</div>
						<div class="col-9">
							{this.props.text}
							<br/>
						<div style={{ fontStyle: "italic", paddingTop:"15px"}}><FormattedMessage id="UserTips.DeactivateTipInPreference" defaultMessage="Tips can be deactived in your preference" /></div>
						</div>
					</div>
				</div>)
		}
		else
			return (<div/>)
	}
}

export default UserTips;