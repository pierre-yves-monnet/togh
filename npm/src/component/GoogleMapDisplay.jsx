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
		this.state = {
			center: props.center,
			zoom: props.zoom,
			positions: props.positions

		};
		console.log("GoogleMapDisplay.constructor center=" + props.center + " positions=" + JSON.stringify(props.positions));

	}
	componentDidUpdate(prevProps) {
		console.log("GoogleMapDisplay.componentDidUpdate center=" + this.props.center + " positions=" + JSON.stringify(this.props.positions));
		if (prevProps.positions !== this.props.positions) {
			this.setState({ positions: this.props.positions });
		}
		if (prevProps.center !== this.props.center) {
			this.setState({ center: this.props.center });
		}
		if (prevProps.zoom !== this.props.zoom) {
			this.setState({ zoom: this.props.zoom });
		}
	}


	render() {

		var apiKeyService = FactoryService.getInstance().getApiKeyService();

		var center = this.state.center;
		// console.log("GoogleMapDisplay.render: center=" + center + ", positions=" + JSON.stringify(this.state.positions));
		if (!center || !center.lat) {
			// console.log("GoogleMapDisplay: center is null, calculate from the position");
			// calculate the center according the list of position
			if (this.state.positions && this.state.positions.length > 0) {
				// console.log("GoogleMapDisplay: center is null, calculate from the position");
				var result = this.getLimitPositions(this.state.positions);
				center = { lat: result.minlat + (result.maxlat - result.minlat) / 2, lng: result.minlng + (result.maxlng - result.minlng) / 2 };
			}

		}
		if (!center || !center.lat) {
			// console.log("GoogleMapDisplay: center is still null, use the default ");
			center = {
				address: "Albany",
				lat: 37.8908755,
				lng: -122.2932777
			}
		}
		var positions = this.state.positions;
		if (!positions) {
			positions = [center];
		}
		// calculate the direction
		if (positions) {
			for (var i = 0; i < positions.length - 1; i++) {
				var pos = positions[i];
				var nextpos = positions[i + 1];
				pos.direction = nextpos;
			}
		}


		var zoom = this.state.zoom;
		if (!zoom) {
			zoom = this.getZoomLevel(positions, { height: 400, width: 1000 });
		}

		console.log("GoogleMapDisplay: zoom=" + zoom + ", center=" + JSON.stringify(center) + ", positions=" + JSON.stringify(positions));
		return (
			<div style={{ height: '400px', width: '1000px' }}>
				<GoogleMapReact
					bootstrapURLKeys={{ key: apiKeyService.getGoogleAPIKey() }}
					center={center}
					zoom={zoom}
				>

					{positions.map((pos) => {
						// console.log("GoogleMapDisplay: marker lat=" + pos.lat + " lng=" + pos.lng);
						if (pos.direction) {
							return (
								<div key={pos.key}
									lat={pos.lat}
									lng={pos.lng}
									direction={pos.direction}
								>
									<GeoAltFill width="30px" height="30px" style={{ color: "red" }} />
								</div>
							)
						}
						else {
							return (
								<div key={pos.key}
									lat={pos.lat}
									lng={pos.lng}
								>
									<GeoAltFill width="30px" height="30px" style={{ color: "red" }} />
								</div>
							)
						}
					})
					}
				</GoogleMapReact>
			</div>
		);

	} // end render



	// https://stackoverflow.com/questions/6048975/google-maps-v3-how-to-calculate-the-zoom-level-for-a-given-bounds
	getZoomLevel(positions, mapDim) {
		var WORLD_DIM = { height: 256, width: 256 };
		var ZOOM_MAX = 17;
		if (positions.length<=1)
			return ZOOM_MAX;

		var result = this.getLimitPositions(positions);
		// north East
		var ne = { lat: result.minlat, lng: result.maxlng };
		// south west
		var sw = { lat: result.maxlat, lng: result.minlng };;

		var latFraction = (this.latRad(ne.lat) - this.latRad(sw.lat)) / Math.PI;
		var latZoom = this.zoom(mapDim.height, WORLD_DIM.height, latFraction);
		if (!latZoom)
			latZoom=ZOOM_MAX;
		
		var lngDiff = ne.lng - sw.lng;
		var lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
		var lngZoom = this.zoom(mapDim.width, WORLD_DIM.width, lngFraction);
		if (!lngZoom)
			lngZoom=ZOOM_MAX;

		var zoom= Math.min(latZoom, lngZoom, ZOOM_MAX);
		console.log("GoogleMapDisplay: ne="+JSON.stringify(ne)+" sw="+JSON.stringify(sw)+" latzoom= "+latZoom+" lngzoom="+lngZoom+" zoom="+zoom);
		return zoom;
	}

	latRad(lat) {
		var sin = Math.sin(lat * Math.PI / 180);
		var radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
		console.log("GoogleMapDisplay.latRad: lat="+lat+" sin="+sin+" radX2="+radX2);
		
		return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}

	/** if the fraction is too small, may return null */
	zoom(mapPx, worldPx, fraction) {
		var result= Math.floor(Math.log(mapPx / worldPx / fraction) / Math.LN2) -1;
		console.log("GoogleMapDisplay.zoom: mapPx="+mapPx+" worldPx="+worldPx+" fraction="+fraction+" ==> "+result);
		return result;
	}


	getLimitPositions(positions) {
		var result = {};
		result.minlat = positions[0].lat;
		result.maxlat = positions[0].lat;
		result.minlng = positions[0].lng;
		result.maxlng = positions[0].lng;
		positions.map((pos) => {
			if (pos.lat < result.minlat)
				result.minlat = pos.lat;
			if (pos.lat > result.maxlat)
				result.maxlat = pos.lat;

			if (pos.lng < result.minlng)
				result.minlng = pos.lng;
			if (pos.lng > result.maxlng)
				result.maxlng = pos.lng;
		});
		return result;
	}
	/*
		googleV3API() {
			var latlngList = [];
			latlngList.push(new google.maps.LatLng(lat, lng));
			
			var bounds = new google.maps.LatLngBounds();
			latlngList.each(function(n) {
				bounds.extend(n);
			});
			
			map.setCenter(bounds.getCenter()); //or use custom center
			map.fitBounds(bounds);
			//remove one zoom level to ensure no marker is on the edge.
			map.setZoom(map.getZoom() - 1); 
			// set a minimum zoom 
			// if you got only 1 marker or all markers are on the same address map will be zoomed too much.
			if(map.getZoom() > 15){
				map.setZoom(15);
			var zoom=map.getZoom();
			
		}
		
	}*/


}

export default GoogleMapDisplay;
