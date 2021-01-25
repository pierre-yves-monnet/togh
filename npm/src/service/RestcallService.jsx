// -----------------------------------------------------------
//
// RestcallService
//
// RestcallService
//
//  var restCallService = FactoryService.getInstance().getRestcallService();

// -----------------------------------------------------------

import axios from 'axios';
import FactoryService from './FactoryService';
import AuthService from './AuthService';


class RestcallService {
	constructor( factoryService) {
		console.log("AuthService: ------------ constructor ");
		this.factoryService = factoryService;
	}

	// init - separe of constructor because service use themself
	init() {
		console.log( "RestcallService.init");
		this.authService= this.factoryService.getAuthService();		
	}
	
	// From the URL, complete to have a complete URL
	getUrl( uri ) {
		return 'http://localhost:7080/togh'+uri;
	}
	
	getJson(uri, fctPayload) {
		var headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		
		const requestOptions = {
	        headers: headers
	    };
    	axios.get( this.getUrl( uri), requestOptions)
        	.then( httpPayload => fctPayload( httpPayload ) );
	}
	
	// PostJson
	postJson(uri, param, fctPayload ) {
		var headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		
		const requestOptions = {
	        headers: headers
	    };
    	axios.post( this.getUrl( uri), param, requestOptions)
        	.then( httpPayload => fctPayload( httpPayload ) );
	}
		
}
export default RestcallService;