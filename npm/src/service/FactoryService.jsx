// -----------------------------------------------------------
//
// Factory Service
//
// Factory. This object must be call one time (in index.js), then the setInstance() to save this object only one time
//
// -----------------------------------------------------------

import AuthService from './AuthService';
import RestcallService from './RestcallService';

var instanceFactory;

class FactoryService  {

	
	constructor() {
		console.log("FactoryService.constructor");
		this.authService = new AuthService( this );
		this.restcallService = new RestcallService( this );
		
		this.authService.init();
		this.restcallService.init();
	}

	getAuthService() {
		// console.log("FactoryService.getAuthService");
		return this.authService;
	}
	
	getRestcallService() {
		// console.log("FactoryService.getRestcallService");
		return this.restcallService;
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
