// -----------------------------------------------------------
//
// EventGeolocalisation
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { TextInput, Toggle } from 'carbon-components-react';


import GoogleMapReact from 'google-map-react';
import Geocode from "react-geocode";



import { Icon } from '@iconify/react'
import locationIcon from '@iconify/icons-mdi/map-marker'


class EventGeolocalisation extends React.Component {
	
	// this.props.pingEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : props.event, 
						'show' : props.show,
						'collapse' : props.collapse
						};
		// show : OFF, ON, COLLAPSE
		console.log("secGeolocalisation.constructor show="+ +this.state.show+" event="+JSON.stringify(this.state.event));
		this.collapse 				= this.collapse.bind(this);
		this.setAttribute			= this.setAttribute.bind(this);
	}

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventGeolocalisation.render: visible="+this.state.show);
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		const zoomLevel=8;
		const googlelocation = {
  			address: this.state.event.geoaddress,
			};
			
			
			
			
		const location = {
		  address: '1600 Amphitheatre Parkway, Mountain View, california.',
		  lat: 37.42216,
		  lng: -122.08427,
		}			
		const LocationPin = ({ text }) => (
			  <div className="pin">
			    <Icon icon={locationIcon} className="pin-icon" />
			    <p className="pin-text">{text}</p>
			  </div>
			)
			
	

			
		return ( <div>
					<div class="eventsection"> 
						<a href="secGeolocalisation"></a>
						<a onClick={this.collapse} style={{verticalAlign: "top"}}>
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down" style={{fontSize: "small"}}></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"  style={{fontSize: "small"}}></span>}
						</a><FormattedMessage id="EventGeolocalisation.MainTitleGeolocation" defaultMessage="Geolocalisation"/>
					</div> 
					{this.state.show ==='ON' && (
						<div>
						<table >
						<tr><td style={{"paddingRight":"30px"}}>
							<Toggle labelText={<FormattedMessage id="EventGeolocalisation.ShareMyLocation" defaultMessage="Share my localisation during the event"/>} aria-label="toggle button" 
								
								onChange={(event) => this.setAttributeCheckbox( "geosharemylocation", event.target.value )}
	      						id="shareMyLocation" />
	      				</td><td></td></tr>
						</table>
	    
						<TextInput labelText={<FormattedMessage id="EventGeolocalisation.Address" defaultMessage="Address"/>}  style={{width: "100%", maxWidth: "100%"}} rows="4" value={this.state.event.geoaddress} 
								onChange={(event) => this.setAttribute( "geoaddress", event.target.value )}></TextInput>					
						<br/>
						<div style={{ height: '100vh', width: '100%' }}>
							<GoogleMapReact
					          		bootstrapURLKeys={{ key: "AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es" }}
									defaultCenter={location}
									defaultZoom={zoomLevel}
						        >
					           <LocationPin
									lat={location.lat}
	          						lng={location.lng}
					          	text={location.address}
					        />
					        </GoogleMapReact>
						</div>
	
						<br/> 
						</div>)}
					
				</div>
				);
				
			// lat / lng for the pin
		}
		
		// Geocodage
		// https://www.npmjs.com/package/react-geocode
	/*
	
	
	

	const locations = [
    {
      name: "Location 1",
      location: { 
        lat: 41.3954,
        lng: 2.162 
      },
    },
    {
      name: "Location 2",
      location: { 
        lat: 41.3917,
        lng: 2.1649
      },
    },
    {
      name: "Location 3",
      location: { 
        lat: 41.3773,
        lng: 2.1585
      },
    },
    {
      name: "Location 4",
      location: { 
        lat: 41.3797,
        lng: 2.1682
      },
    },
    {
      name: "Location 5",
      location: { 
        lat: 41.4055,
        lng: 2.1915
      },
    }
  ];
	
		<GoogleMapReact
				        bootstrapURLKeys={{ key: 'AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es' }}
				        defaultCenter={location}
				        defaultZoom={zoomLevel}
				      >
				        <LocationPin
				          text={location.address}
				        />
				      </GoogleMapReact>
	
	<GoogleMapReact
				        bootstrapURLKeys={{ key: 'AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es' }}
				        defaultCenter={location}
				        defaultZoom={zoomLevel}
				      >
				        {locations.map(item => {
							              return (
							              <LocationPin text={item.name} lat={item.location.lat} lng={item.location.lat}/>
							              )
							            })
							}
				      </GoogleMapReact>
						<LoadScript
				       googleMapsApiKey='AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es'>
				        <GoogleMap
				          mapContainerStyle={mapStyles}
				          zoom={13}
				          center={defaultCenter}>
							{locations.map(item => {
							              return (
							              <Marker key={item.name} position={item.location}/>
							              )
							            })
							}
						</GoogleMap>
				     </LoadScript>

	
					
*/

				
	collapse() {
		console.log("EventShoppinglist.collapse");
		if (this.state.show === 'ON')
			this.setState( { 'show' : 'COLLAPSE' });
		else
			this.setState( { 'show' : 'ON' });
	}
	
	setAttribute( name, value ) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:"+name+" <= "+value);
  		const currentEvent = this.state.event;

  		currentEvent[ name ] = value;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	setAttributeCheckbox( name, value ) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:"+name+" <= "+value);
  		const currentEvent = this.state.event;
		if (value === 'on')
  			currentEvent[ name ] = true;
		else
  			currentEvent[ name ] = false;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
}		
export default EventGeolocalisation;
	