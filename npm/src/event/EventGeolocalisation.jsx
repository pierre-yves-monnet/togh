// -----------------------------------------------------------
//
// EventGeolocalisation
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { Toggle, TextArea } from 'carbon-components-react';

import EventSectionHeader 		from 'component/EventSectionHeader';
import GoogleAddressGeocode 	from 'component/GoogleAddressGeocode';
import * as GeocodeConstant 	from 'component/GoogleAddressGeocode';
import GoogleMapDisplay 		from 'component/GoogleMapDisplay';


 


class EventGeolocalisation extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;

		this.state = {
			event: this.eventCtrl.getEvent()
		};
		this.timeout =  0;
		
		// show : OFF, ON, COLLAPSE
		console.log("secGeolocalisation.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttribut 				= this.setAttribut.bind(this);
		this.changeAddressCallbackfct	= this.changeAddressCallbackfct.bind( this);
	}

	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventGeolocalisation.render: ");
		

		// show the list
		const zoomLevel = 15;

		var positions = [];
		
		if (this.state.event.geolat) {
			// no lat was calculated, to use the default
			console.log("EventGeolocalisation.render: setPositionLat="+this.state.event.geolat+", lng="+this.state.event.geolng);

			positions.push( { key:"Event", 
					lat: this.state.event.geolat, 
					lng: this.state.event.geolng, 
					address: this.state.event.geoaddress} );
		}
		
		console.log("EventGeolocalisation.render: positions=" + JSON.stringify( positions ) );


		// // <div style={{ height: '100vh', width: '100%' }}>	
		return (<div>
			<EventSectionHeader id="geolocalisation"
				image="img/btnGeolocalisation.png"
				title={<FormattedMessage id="EventGeolocalisation.MainTitleGeolocation" defaultMessage="Geolocalisation" />}
				showPlusButton={false}
				userTipsText={<FormattedMessage id="EventGeolocalisation.GeolocalisationTip" defaultMessage="Where is the event? Do I want to share my position ? Note: for a multi place event, use the Itinerary." />}
			/>
			<div class="eventsection">
				<a href="secGeolocalisation"></a>
				<a onClick={this.collapse} style={{ verticalAlign: "top" }}>
					{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down" style={{ fontSize: "small" }}></span>}
					{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right" style={{ fontSize: "small" }}></span>}
				</a><FormattedMessage id="EventGeolocalisation.MainTitleGeolocation" defaultMessage="Geolocalisation" />
			</div>
			<div>
				<table >
					<tr><td style={{ "paddingRight": "30px" }}>
						<Toggle labelText=""  aria-label=""
							labelA={<FormattedMessage id="EventGeolocalisation.ShareMyLocation" defaultMessage="Share my localisation during the event" />}
							labelB={<FormattedMessage id="EventGeolocalisation.ShareMyLocation" defaultMessage="Share my localisation during the event" />}
							onChange={(event) => this.setAttributCheckbox("geosharemylocation", event.target.value)}
							id="shareMyLocation" />
					</td><td></td></tr>
				</table>

				<GoogleAddressGeocode item={this.state.event} 
							labelField={<FormattedMessage id="EventGeolocalisation.Address" defaultMessage="Address" />} 
							changeCallbackfct={this.changeAddressCallbackfct} />
				<br />
				<div class="row">
					<div class="col-sm-4">
						<TextArea id="geoinstructions"
								labelText={<FormattedMessage id="EventGeolocalisation.Instructions" defaultMessage="Instructions" />}
								style={{ width: "100%", maxWidth: "100%" }}
								rows={5}
								value={this.state.event.geoinstructions}
								onChange={(event) => this.setAttribut("geoinstructions", event.target.value)} />
						</div>
						<div class="col-sm-8">
							<GoogleMapDisplay zoom={zoomLevel} positions={positions} />
						</div>
					
				</div> 

				<br />
			</div>

		</div>
		);
	}


	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------
	changeAddressCallbackfct(type, eventUpdated ) {
		// we pass the total event 
		console.log("EventGeolocalisation; changeAddressCallbackfct type="+type+" location=("+eventUpdated.geolat+","+eventUpdated.geolng+") ["+eventUpdated.geoaddress+"]");
		if (type === GeocodeConstant.CHANGE_ADDRESS) {
			this.setAttribut("geoaddress", eventUpdated.geoaddress);
		}
		if (type === GeocodeConstant.CHANGE_LATLNG) {
			this.setAttribut("geolat", eventUpdated.geolat);
			this.setAttribut("geolng", eventUpdated.geolng);
			this.forceUpdate(); // refresh the map
		}	
		this.setState( { event: eventUpdated });		
	}
	

	setAttribut(name, value) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:" + name + " <= " + value);
		this.eventCtrl.setAttribut(name, value, this.state.event, "");
	}
	setAttributCheckbox(name, value) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:" + name + " <= " + value);
		const currentEvent = this.state.event;
		if (value === 'on')
			currentEvent[name] = true;
		else
			currentEvent[name] = false;

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ "event": currentEvent });
		this.props.updateEvent();
	}


	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------
	// geocodage 
	// npm install --save react-geocode



}
export default EventGeolocalisation;
