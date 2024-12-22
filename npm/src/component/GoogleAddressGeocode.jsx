// -----------------------------------------------------------
//
// GoogleAddressGeocode
//
// Propose a Text Input. When user give a value, the geocode is runnning and calculate lat/long
//
// -----------------------------------------------------------
//

import React from 'react';
import { TextInput } from '@carbon/react';


import FactoryService 		from 'service/FactoryService';

export const CHANGE_ADDRESS= "Address";
export const CHANGE_LATLNG= "LatLong";

class GoogleAddressGeocode extends React.Component {
	constructor(props) {
		super();
		this.state={ 
			item: props.item,
			labelField: props.labelField,
			
		};
		this.changeCallbackfct					= props.changeCallbackfct;
		
		this.changeAddress 						= this.changeAddress.bind( this );
		this.changeAddressGoogleCallback 		= this.changeAddressGoogleCallback.bind( this );
			
	}	
	
	
	render() {
		return ( <div>
					<TextInput labelText={this.state.labelField} 
						style={{ width: "100%", maxWidth: "100%" }} value={this.state.item.geoaddress}
						onChange={(event) => this.changeAddress( event.target.value)}></TextInput>
					<div style={{fontSize:"10px", fontStyle:"italic", textAlign:"right"}} >lat:{this.state.item.geolat}, lng:{this.state.item.geolng}</div>
				</div>
			)
	}
	
	
	changeAddress( address ) {
		var currentItem = this.state.item;
		currentItem.geoaddress = address;
		// recalculate the new lat/long
		  if (this.timeout) 
			clearTimeout(this.timeout);
		this.changeCallbackfct( CHANGE_ADDRESS, currentItem );
					
    	this.timeout = setTimeout(() => {
			console.log("AddressGeocode; Timeout after 2000 ms");
     		var googleMapService = FactoryService.getInstance().getGoogleMapService();
			googleMapService.geocode( address, this.changeAddressGoogleCallback);
    	}, 2000);
	}
	
	
	changeAddressGoogleCallback( status, lat, lng) {
		console.log("AddressGeocode; changeAddressCallback location=("+lat+","+lng+")");
		var currentItem = this.state.item;
		currentItem.geostatus = status;
		if (status !== "ERROR") {
    		currentItem.geolat = lat;
	    	currentItem.geolng = lng;
		}

		this.setState({ item: currentItem });
		this.changeCallbackfct(CHANGE_LATLNG, currentItem );
	}
}
export default GoogleAddressGeocode;
