// -----------------------------------------------------------
//
// GoogleMapDisplay
//
// Display a Map.
// - map + center + zoom
// - list of points to display. Direction can be display via theses points  
//
// <GoogleMapDisplay zoom=15 center={lat: 37.8908755, lng: -122.2932777	} positions=[ {lat: 37.8908755, lng: -122.2932777}, {lat: 37.8908755, lng: -122.2932777	}]
//   or
// <GoogleMapDisplay positions=[ {lat: 37.8908755, lng: -122.2932777}, {lat: 37.8908755, lng: -122.2932777	}]
//  or
// <GoogleMapDisplay positions=[ {lat: 37.8908755, lng: -122.2932777} ]
// -----------------------------------------------------------
//

import React from 'react';

import { GeoAltFill } from 'react-bootstrap-icons';
import GoogleMapReact from 'google-map-react';
import FactoryService from './../service/FactoryService';
import GoogleMapService from './../service/GoogleMapService';




class GoogleMapDisplay extends React.Component {
	
	// a position is a set with lat/lng
	// exampl 
	constructor(props) {
		super();
		this.state={ 
			center: props.center, 
			zoom: props.zoom,
			positions: props.positions
			
		};
		console.log("GoogleMapDisplay.constructor center="+props.center+" positions="+JSON.stringify(props.positions));

	}
	componentDidUpdate(prevProps) {
		console.log("GoogleMapDisplay.componentDidUpdate center="+this.props.center+" positions="+JSON.stringify(this.props.positions));
	  	if(prevProps.positions !== this.props.positions) {
    		this.setState({positions: this.props.positions});
  		}
	  	if(prevProps.center !== this.props.center) {
    		this.setState({center: this.props.center});
  		}
		if(prevProps.zoom !== this.props.zoom) {
    		this.setState({zoom: this.props.zoom});
  		}
	}
	render() {
		
		var apiKeyService = FactoryService.getInstance().getApiKeyService();

		var center= this.state.center;
		console.log("GoogleMapDisplay.render: center="+center+ ", positions="+JSON.stringify(this.state.positions));
		if (! center || ! center.lat) {
			console.log("GoogleMapDisplay: center is null, calculate from the position");
			// calculate the center according the list of position
			if (this.state.positions && this.state.positions.length>0) {
				console.log("GoogleMapDisplay: center is null, calculate from the position");
				
				var minlat = this.state.positions[ 0 ].lat;
				var maxlat = this.state.positions[ 0 ].lat;
				var minlng = this.state.positions[ 0 ].lng;
				var maxlng = this.state.positions[ 0 ].lng;
				this.state.positions.map( (pos) => {
					if (pos.lat < minlat)
						minlat=pos.lat;
					if (pos.lat > maxlat)
						maxlat=pos.lat;
						
					if (pos.lng < minlng)
						minlng=pos.lng;
					if (pos.lng > maxlng)
						maxlng=pos.lng;
				})
				center = { lat: minlat+(maxlat-minlat) / 2 , lng: maxlng+(maxlng-minlng)/2};
			}
			
		}
		if ( ! center || ! center.lat) {
			console.log("GoogleMapDisplay: center is still null, use the default ");
			center = {
				address: "Albany",
				lat: 37.8908755, 
				lng: -122.2932777			
			}
		}
		var positions= this.state.positions;
		if (! positions) {
			positions = [ center ];
		}
		
		
		console.log("GoogleMapDisplay: center lat="+center.lat+" lng="+center.lng+" positions="+JSON.stringify( positions));
		return (
				<div style={{ height: '100vh', width: '100%' }}>
					<GoogleMapReact
						bootstrapURLKeys={{ key: apiKeyService.getGoogleAPIKey() }}
						center={center}
						zoom={this.state.zoom}
					>
					
					{positions.map( (pos) => {
						console.log("GoogleMapDisplay: marker lat="+pos.lat+" lng="+pos.lng);
						return (
							<div key={pos.key} 
	 							lat={pos.lat} 
	 							lng={pos.lng} >
								<GeoAltFill width="30px" height="30px" style={{color: "red"}}/> 
							</div>
						) })
					}
					</GoogleMapReact>
				</div>
			)
		
	} // end render
}

export default GoogleMapDisplay;
		