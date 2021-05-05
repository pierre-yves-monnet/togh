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
		// console.log("RestCall.geturl : "+window.location.href);
		var i = window.location.href.indexOf(":",7); // skyp http: or https:
		var headerUrl = window.location.href.substring(0,i)+":7080/togh";
		// search something like http://localhost:3000/#
		return headerUrl+uri; 
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
		uri = uri+"&timezoneoffset="+(new Date()).getTimezoneOffset();
		console.log("RestCallService.getJson: uri="+uri);
		var fct=fctToCallback;
    	axios.get( this.getUrl( uri ), requestOptions)
        	.then( axiosPayload => { 

					
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));	
				var httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(error => {
				if (error.response.status==401) {
					console.log("Redirect");
					window.location.href = window.location.href;
					return;
				}
				console.error("RestCallService.getJson: catch error:"+error);	
				var httpResponse =  new HttpResponse( {}, error);
				fctToCallback.call(objToCall, httpResponse); 

				});
	}
	
	// PostJson
	postJson(uri, objToCall, param, fctToCallback ) {
		var headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		param.timezoneoffset=(new Date()).getTimezoneOffset();
		console.log("RestCallService.postJson: timezoneoffset="+param.timezoneoffset);
		
		const requestOptions = {
	        headers: headers
	    };
    	axios.post( this.getUrl( uri), param, requestOptions)
        	.then( axiosPayload => { 
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));	
				var httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(error => {
				console.error("RestCallService.getJson: catch error:"+error);	
				if (error.response.status==401) {
					console.log("Redirect");
					window.location.href = window.location.href;
					return;
				}
				var httpResponse =  new HttpResponse( {}, error)
				fctToCallback.call(objToCall, httpResponse); 

				});
	}
		
}
export default RestcallService;