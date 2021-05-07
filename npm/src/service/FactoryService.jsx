// -----------------------------------------------------------
//
// Factory Service
//
// Factory. This object must be call one time (in index.js), then the setInstance() to save this object only one time
//
// -----------------------------------------------------------

import AuthService 					from 'service/AuthService';
import RestcallService 				from 'service/RestcallService';
import ToolService 					from 'service/ToolService';
import UserService 					from 'service/UserService';
import CurrencyService 				from 'service/CurrencyService';
import ApiKeyService 				from 'service/ApiKeyService';
import GoogleMapService 			from 'service/GoogleMapService';


var instanceFactory;

class FactoryService  {

	
	constructor() {
		console.log("FactoryService.constructor");
		this.authService = new AuthService( this );
		this.restcallService = new RestcallService( this );
		this.toolService = new ToolService( this );
		this.currencyService = new CurrencyService( this );
		this.userService = new UserService( this );
		this.apiKeyService = new ApiKeyService( this );
		this.googleMapService =  new GoogleMapService( this );
		
		this.authService.init();
		this.restcallService.init();
		this.apiKeyService.init();
	}

	getAuthService() {
		// console.log("FactoryService.getAuthService");
		return this.authService;
	}
	
	getUserService() {
		// console.log("FactoryService.getUserService");
		return this.userService;
	}
	getRestcallService() {
		// console.log("FactoryService.getRestcallService");
		return this.restcallService;
	}
	
	getToolService() {
		// console.log("FactoryService.getToolService");
		return this.toolService;
	}
	
	getCurrencyService(){
		return this.currencyService;
	}
	
	getApiKeyService() {
		return this.apiKeyService;
	};
	
	getGoogleMapService() {
		return this.googleMapService;
	}
	static getInstance() {
		// console.log("FactoryService.getInstance")
		return instanceFactory;
	}
	static setInstance( factory) {
		// console.log("FactoryService.setInstance")
		instanceFactory = factory;
	}
	
}


export default FactoryService;
