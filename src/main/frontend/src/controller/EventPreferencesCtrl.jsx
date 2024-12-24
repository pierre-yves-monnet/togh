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

const CURRENCY_CODE_ATTRIBUT_NAME= "currencyCode";

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
	constructor(eventCtrl, updateEventFct ) {
		this.eventCtrl = eventCtrl;
		this.updateEventFct = updateEventFct;
		let event = eventCtrl.getEvent();
		if (event && event.preferences)
			this.currencyCode = event.preferences[ CURRENCY_CODE_ATTRIBUT_NAME ];
			
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

		let slab = SlabRecord.getUpdate(this.event, CURRENCY_CODE_ATTRIBUT_NAME, currencyCode, "/preferences");
		this.eventCtrl.updateEventFct( slab );
	}
	
	getCurrency(){
		let currencyService = FactoryService.getInstance().getCurrencyService();
		return currencyService.getCurrencyInfoByCode(this.currencyCode);
	}
	
	getCurrencyCode(){
		return this.currencyCode;
	}

	getAccess( functionName ){
	    let attributName = "access"+functionName;
	    console.log("EventPreferenceCtrl.getAccess: function="+functionName+" value="+this.eventCtrl.event.preferences[ attributName ]);
    	return this.eventCtrl.event.preferences[ attributName ];
    }

    setAccess( functionName, value ) {

   	    let attributName = "access"+functionName;
    	this.eventCtrl.event.preferences[ attributName ] = value ;

        let slab = SlabRecord.getUpdate(this.event, attributName, value, "/preferences" );
        this.eventCtrl.updateEventFct( slab );
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
	