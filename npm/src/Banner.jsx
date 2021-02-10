// -----------------------------------------------------------
//
// Banner
//
// Banner controler. Display the banner in top.
//  Get the user information. If nobody are connected, be back immediately ! 
//
// -----------------------------------------------------------
import React from 'react';

import FactoryService from './service/FactoryService';

class Banner extends React.Component {
	constructor() {
		super();
		}


	render() {
		var authService = FactoryService.getInstance().getAuthService();
		console.log("banner.render isconnected="+authService.isConnected());
		// 			<!--  green: #067c04; -->

		if (authService.isConnected()) {
			var user = authService.getUser();
			console.log("User Connected "+JSON.stringify(user));
			return ( 
				<div class="container-fluid">
					<div class="row">
						<div class="col-xs-12 banner">
						<table width="100%">
							<tr><td style={{"color":"#888787", verticalAlign: "top"}}>
								<img src="img/togh.jpg" style={{width:20}} />
							</td><td style={{"color":"#888787", verticalAlign: "top"}}>
								Togh
							</td>
							<td style={{"color":"#888787", textAlign: "right" , verticalAlign: "top"}}>
								Welcome {user.firstname} {user.lastname}
							</td>
							</tr>
							</table>
						</div>
					</div>
				</div>
				)
		}
		else {
			return ( 
				<div class="container-fluid">
					<div class="row">
						<div class="col-xs-12 banner">
							<div style={{"color":"white", "textAlign": "right"}}>
							Welcome to Togh
							</div>
						</div>
					</div>
				</div>)
		}
	}
	
}	
export default Banner;
