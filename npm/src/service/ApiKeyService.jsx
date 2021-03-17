// -----------------------------------------------------------
//
// ApiKeyService
//
// var currencyService = FactoryService.getInstance().getCurrencyService();
//
// -----------------------------------------------------------


class ApiKeyService {
	constructor(factoryService ) {
		console.log("ApiKeyService: ------------ constructor ");
		this.token=null;
		this.factoryService = factoryService;
		
		this.googleAPIKey= "AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es";
		this.geocodeAPIKey = "AIzaSyBH4nfLPRIghcsU26YguJIVNjbI5RG-QZo"
	}
	
	init() {
		// call the server to get all keys
		
	}
		
	getGoogleAPIKey(){
		
		return this.googleAPIKey;
	}
	
	getGeocodeAPIKey() {
		return this.geocodeAPIKey;
	}
}

export default ApiKeyService;
	