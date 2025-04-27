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
import { InfoCircle } from 'react-bootstrap-icons';


import FactoryService 		from 'service/FactoryService';


class UserTips extends React.Component {
	
	// props.text is the text to display, translated
	constructor(props) {
		super();
		this.state = {
			text: props.text,
			id: props.tip
		};

		this.deactivateTip = this.deactivateTip.bind(this);
	}

	render() {
		const userService = FactoryService.getInstance().getUserService();
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
							 <table width="100%">
                                <tr><td>
                                    <div style={{marginBottom: "10px", fontSize: "12px", fontWeight: "bold", paddingLeft: "20px"}}>
                                        <FormattedMessage id="UserTips.DeactivateTip"
                                            defaultMessage="You can deactivate tips, and enable it again in your preferences"/></div>
                                   </td><td>
                                    <button class="btn btn-info btx-sm" style={{fontSize: "10px", margin: "5px 0px 5px 0px", padding: "0px 5px 0px 5px"}}
                                        onClick={this.deactivateTip}>
                                        <FormattedMessage id="UserTips.Deactivate" defaultMessage="Deactivate"/>
                                    </button>
                                   </td></tr>
                             </table>
						</div>
					</div>
				</div>)
		}
		else
			return (<div/>)
	}

	deactivateTip() {
		const restCallService = FactoryService.getInstance().getRestCallService();
        restCallService.postJson('/api/user/tips?active=false', this, {name:"new event"}, httpPayload => {
            httpPayload.trace("UserTips.createEventCallback");
        });
    }
}

export default UserTips;