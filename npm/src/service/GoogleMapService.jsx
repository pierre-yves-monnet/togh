// -----------------------------------------------------------
//
// GoogleMapService
//
// var googleMapService = FactoryService.getInstance().getGoogleMapService();
//
// -----------------------------------------------------------
import Geocode from "react-geocode";

import FactoryService 		from 'service/FactoryService';

class GoogleMapService {
	constructor(factoryService ) {
		console.log("GoogleMapService: ------------ constructor ");
		this.factoryService = factoryService;
	}
	
	
	// https://www.npmjs.com/package/react-geocode
	geocode( address, callbackFct ){
		var apiKeyService = FactoryService.getInstance().getApiKeyService();

		// set Google Maps Geocoding API for purposes of quota management. Its optional but recommended.
		Geocode.setApiKey( apiKeyService.getGeocodeAPIKey() );

		// set response language. Defaults to english.
		Geocode.setLanguage("en");
		Geocode.fromAddress( address ).then(
  			(httpPayload) => {
    			const { lat, lng } = httpPayload.results[0].geometry.location;
    			console.log("GoogleMapService : ("+lat+","+ lng+") JSON="+JSON.stringify(httpPayload.results[0].geometry.location));
				callbackFct( "OK", lat, lng );
  			},
  			(error) => {
    			console.error(error);
				callbackFct( "ERROR", error);
  			});
		};	
}

export default GoogleMapService;