// -----------------------------------------------------------
//
// Factory Service
//
// Factory. This object must be call one time (in index.js), then the setInstance() to save this object only one time
//
// -----------------------------------------------------------

import AuthService from './AuthService';
import RestcallService from './RestcallService';
import ToolService from './ToolService';
import UserService from './UserService';
import CurrencyService from './CurrencyService';

var instanceFactory;

class FactoryService  {

	
	constructor() {
		console.log("FactoryService.constructor");
		this.authService = new AuthService( this );
		this.restcallService = new RestcallService( this );
		this.toolService = new ToolService( this );
		this.currencyService = new CurrencyService( this );
		this.userService = new UserService( this );
		
		this.authService.init();
		this.restcallService.init();
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
