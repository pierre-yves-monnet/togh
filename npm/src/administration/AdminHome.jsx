// -----------------------------------------------------------
//
// Administration
//
// Display Administration tool
//
// -----------------------------------------------------------

import React from 'react';

import { FormattedMessage } from "react-intl";

// import { Button } from 'carbon-components-react';

import AdminUsers 				from 'administration/AdminUsers';
import AdminTranslator 			from 'administration/AdminTranslator';
import AdminEmail               from 'administration/AdminEmail';
import AdminGoogle 				from 'administration/AdminGoogle';
import AdminAPIKey 				from 'administration/AdminAPIKey';
import AdminInfo 				from 'administration/AdminInfo';

import FactoryService 			from 'service/FactoryService';
// import DatePickerSkeleton from '@bit/carbon-design-system.carbon-components-react.DatePicker/DatePicker.Skeleton';
// import TimePicker from '@bit/carbon-design-system.carbon-components-react.time-picker';



class AdminHome extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'show' : { 
							secTranslatetor: 'ON', 
						},
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		
		// this is mandatory to have access to the variable in the method... thank you React!   

	}

	//----------------------------------- Render
	render() {
		console.log("AdminHome.render ");
		 


		

		// <div class="btn-group mr-2" role="group" aria-label="First group">
	//							<button style={{"marginLeft ": "10px"}} onClick={this.secItineraire} title="Itineraire" disabled={true} class="btn btn-primary">
	//								<div class="glyphicon glyphicon-road"></div>
	//						</button>
	//					</div>
											
		// -----------------	 
		return ( 
			<div>
				<div class="row">
					<div class="col-12"> 
						<AdminUsers/>
					</div>
				</div>		
				<div class="row">
					<div class="col-6"> 
						<AdminTranslator/>
						<AdminEmail/>
					</div>
					<div class="col-6"> 
						<AdminGoogle/><br/>
						<AdminInfo/>
						<AdminAPIKey/>
					</div>
				</div>

			</div>)	
	} //---------------------------- end Render



		
	
	
	// -------- Rest Call
	
}
export default AdminHome;