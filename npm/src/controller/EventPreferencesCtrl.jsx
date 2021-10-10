/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import FactoryService 		from 'service/FactoryService';
// CurrencyService 			from 'service/CurrencyService';
import SlabRecord 			from 'service/SlabRecord';

const CURRENCY_ATTRIBUT_NAME= "currency";

// -----------------------------------------------------------
//
// EventPreferenceCtrl
//
// For one event, the event preferences
// Preferences are saved as data, but have a big impact on the interface or on the event, and are not supposed to change too much
//  Example of preferences:
//    - Currency used in the event
//    - budget are used or not ?
//    - time zone
// -----------------------------------------------------------

class EventPreferenceCtrl {
	
	// props.updateEvent must be defined
	// props.eventPreferences
	// props.text is the text to display, translated
	constructor(eventCtrl, updateEventfct ) {
		this.eventCtrl = eventCtrl;
		this.updateEventfct = updateEventfct;
		let event = eventCtrl.getEvent();
		if (event && event.preferences)
			this.currencyCode = event.preferences[ CURRENCY_ATTRIBUT_NAME ];
			
		if (! this.currencyCode)
			this.currencyCode="USD";
	}
	
	
	// --------------------------------------------------------------
	// 
	// Currency
	// 
	// --------------------------------------------------------------
	
	setCurrency(currencyCode) {
		this.currencyCode = currencyCode;

		let slab = SlabRecord.getUpdate(this.event, CURRENCY_ATTRIBUT_NAME, currencyCode, "/preferences");
		this.eventCtrl.updateEventfct( slab );
	}
	
	getCurrency(){
		let currencyService = FactoryService.getInstance().getCurrencyService();
		return currencyService.getCurrencyInfoByCode(this.currencyCode);
	}
	
	getCurrencyCode(){
		return this.currencyCode;
	}
	
	getCurrencySymbolPrefix(){
		// console.log("EventPreferenceCtrl.getCurrencySymbolPrefix");
		let currency = this.getCurrency();
		if (currency)
			return currency.prefix;
		else
			return "";
	}
	
	
	getCurrencySymbolSuffix(){
		// console.log("EventPreferenceCtrl.getCurrencySymbolSuffix");
		let currency = this.getCurrency();
		if (currency)
			return currency.suffix;
		else
			return "";

	}

}

export default EventPreferenceCtrl;
	