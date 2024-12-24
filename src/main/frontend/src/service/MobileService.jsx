/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


// -----------------------------------------------------------
//
// MobileService
//
// Group all function to have a different behavior on mobile
//
// -----------------------------------------------------------

class MobileService {

	constructor(factoryService ) {
		console.log("MobileService: ------------ constructor ");
		this.factoryService = factoryService;
	}

    isSmallScreen() {
        let isSmallScreen= ( ( window.innerWidth <= 800 ) && ( window.innerHeight <= 600 ) );
        console.log("MobileCtrl.isSmallScreen? "+isSmallScreen+" ("+window.innerWidth+"x"+window.innerHeight+")");
        return isSmallScreen;
    }
    isLargeScreen() {
        return ! this.isSmallScreen();
    }
}
export default MobileService;
