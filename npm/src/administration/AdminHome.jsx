// -----------------------------------------------------------
//
// Administration
//
// Display Administration tool
//
// -----------------------------------------------------------

import React from 'react';

import AdminUsersStats  	    	from 'administration/AdminUsersStats';
import AdminTranslator 			    from 'administration/AdminTranslator';
import AdminEmail               	from 'administration/AdminEmail';
import AdminGoogle 			        from 'administration/AdminGoogle';
import AdminAPIKey 			        from 'administration/AdminAPIKey';
import AdminInfo 			        from 'administration/AdminInfo';

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

	}

	//----------------------------------- Render
	render() {
		console.log("AdminHome.render ");

		// -----------------	 
		return ( 
			<div class="AdminHome.jsx">
				<div class="row">
					<div class="col-12"> 
						<AdminUsersStats/>
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