// -----------------------------------------------------------
//
// EventPreferences
//
// For one event, the event preferences  
//
// -----------------------------------------------------------
//

import FactoryService 		from 'service/FactoryService';


class EventPreferences {
	
	// props.text is the text to display, translated
	constructor(event) {
		this.event = event;
		this.currencyCode = "USD";
	}
	
	setCurrency(currency) {
		this.currencyCode = currency
	}
	
	getCurrency(){
		var currencyService = FactoryService.getInstance().getCurrencyService();
		return currencyService.getCurrencyInfoByCode(this.currencyCode);
	}
	
	getCurrencyCode(){
		return this.currencyCode;
	}
	
	getCurrencySymbolPrefix(){
		// console.log("EventPreferences.Prefix ==== entering function")
		var currencyService = FactoryService.getInstance().getCurrencyService();
		var set = currencyService.getCurrencyInfoByCode(this.currencyCode);
		console.log("EventPreferences.Prefix.currencycode ==== "+JSON.stringify(this.currencyCode));
		console.log("EventPreferences.Prefix.set ==== "+JSON.stringify(set));
		if (set){
			console.log("EventPreferences.Prefix ==== "+JSON.stringify(set.prefix))
			return set.prefix;
		}
		else{
			return "";
		}
	}
	
	
	getCurrencySymbolSuffix(){
		var currencyService = FactoryService.getInstance().getCurrencyService();
		var set = currencyService.getCurrencyInfoByCode(this.currencyCode);
		if (set){
			return set.suffix;
		}
		else{
			return "";
		}
	}

}

export default EventPreferences;
	