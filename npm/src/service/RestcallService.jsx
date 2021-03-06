// -----------------------------------------------------------
//
// RestcallService
//
// RestcallService
//
//  var restCallService = FactoryService.getInstance().getRestcallService();

// -----------------------------------------------------------

import axios from 'axios';

// import FactoryService from './FactoryService';
// import AuthService from './AuthService';
import HttpResponse  from './HttpResponse';


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
	
	
	// According https://www.intricatecloud.io/2020/03/how-to-handle-api-errors-in-your-web-app-using-axios/
	// the function fctToCallback must be
	// iReceivedMyAnswer( httpPayLoad)
	//  	httpPayLoad.data ==> Content
	//      httpPayLoad.status => http status except 400 / 500 which come with error
	//      httpPayload.err => contains boolean lile err.response 
	getJson(uri, objToCall, fctToCallback) {
		var headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		
		const requestOptions = {
	        headers: headers
	    };
		console.log("RestCallServer call ["+uri+"]");
		var fct=fctToCallback;
    	axios.get( this.getUrl( uri ), requestOptions)
        	.then( axiosPayload => { 
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));	
				var httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(err => {
				console.error("RestCallService.getJson: catch error:"+err);	
				var httpResponse =  new HttpResponse( {}, err);
				fctToCallback.call(objToCall, httpResponse); 

				});
	}
	
	// PostJson
	postJson(uri, objToCall, param, fctToCallback ) {
		var headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		
		const requestOptions = {
	        headers: headers
	    };
    	axios.post( this.getUrl( uri), param, requestOptions)
        	.then( axiosPayload => { 
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));	
				var httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(err => {
				console.error("RestCallService.getJson: catch error:"+err);	
				var httpResponse =  new HttpResponse( {}, err)
				fctToCallback.call(objToCall, httpResponse); 

				});
	}
		
}
export default RestcallService;