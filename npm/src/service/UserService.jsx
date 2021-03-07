// -----------------------------------------------------------
//
// UserService
//
// All information about the user - profile, preference
// access it by
// 
// var userService = FactoryService.getInstance().getUserService();
//
// -----------------------------------------------------------


// import FactoryService from './FactoryService';
// import RestcallService from './RestcallService';


class UserService {

	constructor( factoryService ) {
		console.log("UserService: ------------ constructor ");
		this.factoryService = factoryService;

	}
	
	/*
	* Return more information on each item
	*/
	
	prefsDisplayTips() {
		return true;
	}

}
export default UserService;