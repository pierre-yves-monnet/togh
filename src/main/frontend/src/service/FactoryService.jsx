// -----------------------------------------------------------
//
// Factory Service
//
// Factory. This object must be call one time (in index.js), then the setInstance() to save this object only one time
//
// -----------------------------------------------------------

import AuthService 					from 'service/AuthService';
import RestCallService 				from 'service/RestCallService';
import ToolService 					from 'service/ToolService';
import UserService 					from 'service/UserService';
import CurrencyService 				from 'service/CurrencyService';
import ApiKeyService 				from 'service/ApiKeyService';
import GoogleMapService 			from 'service/GoogleMapService';
import MobileService 			    from 'service/MobileService';


var instanceFactory;

class FactoryService  {

	constructor() {
		this.uniqId = new Date().getTime();
		console.log("FactoryService.constructor id="+this.uniqId);
		this.authService = new AuthService( this );
		this.restCallService = new RestCallService( this );
		this.toolService = new ToolService( this );
		this.currencyService = new CurrencyService( this );
		this.userService = new UserService( this );
		this.apiKeyService = new ApiKeyService( this );
		this.googleMapService =  new GoogleMapService( this );
		this.mobileService = new MobileService(this);

		this.authService.init();
		this.restCallService.init();
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
	getRestCallService() {
		// console.log("FactoryService.getRestCallService");
		return this.restCallService;
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

	getMobileService() {
	    return this.mobileService;
	}

	// use FactoryService.getInstance()
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
