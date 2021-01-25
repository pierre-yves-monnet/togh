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
		if (authService.isConnected()) {
			var user = authService.getUser();
			return ( <div> Welcome {user.firstName} {user.lastName}</div>)
		}
		else {
			return ( <div> Welcome to Togh</div>)
		}
	}
	
}	
export default Banner;
