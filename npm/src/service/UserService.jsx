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
		const authService = this.factoryService.getAuthService();
		// console.log("UserService.user="+authService.getUser());
		let user = authService.getUser();
		if (! user)
			return true;
		// if null
		if ( user.showTipsUser == null)
			return true;
			
		return user.showTipsUser;
	}

	prefsDisplayTakeATour() {
		const authService = this.factoryService.getAuthService();
		// console.log("UserService.user="+authService.getUser());
		let user = authService.getUser();
		if (! user)
			return true;
		// if null
		if ( user.showTakeATour == null)
			return true;

		return user.showTakeATour;
	}

    setPrefsDisplayTakeATour( active ) {
    	const authService = this.factoryService.getAuthService();
        // console.log("UserService.user="+authService.getUser());
        let user = authService.getUser();
        if (user)
            user.showTakeATour=active;
    }

}
export default UserService;