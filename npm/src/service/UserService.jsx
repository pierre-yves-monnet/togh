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



class UserService {

	constructor( factoryService ) {
		console.log("UserService: ------------ constructor ");
		this.factoryService = factoryService;
		}
	
	/*
	* Return more information on each item
	*/
	
	prefsDisplayTips() {
		let authService = this.factoryService.getAuthService();
		// console.log("UserService.user="+authService.getUser());
		let user = authService.getUser();
		if (! user)
			return true;
		// if null
		if ( user.showTipsUser == null)
			return true;
			
		return user.showTipsUser;
	}

}
export default UserService;