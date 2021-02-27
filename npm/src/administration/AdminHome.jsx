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

import AdminTranslator from './AdminTranslator';
import FactoryService from '../service/FactoryService';
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
				<h1><FormattedMessage id="AdminHome.Title" defaultMessage="Administration" /></h1>
				<AdminTranslator/>
			</div>)	
	} //---------------------------- end Render



		
	
	
	// -------- Rest Call
	
}
export default AdminHome;