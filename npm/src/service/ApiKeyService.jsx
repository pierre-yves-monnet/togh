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
		this.setKeysForUser = this.setKeysForUser.bind( this );
	}
	
	init() {
	}
	
	setKeysForUser( apikeys ) {
		this.googleAPIKey= apikeys.googleAPIKey;
		this.geocodeAPIKey= apikeys.geocodeAPIKey;
	}
	getGoogleAPIKey(){
		
		return this.googleAPIKey;
	}
	
	getGeocodeAPIKey() {
		return this.geocodeAPIKey;
	}
}

export default ApiKeyService;
	