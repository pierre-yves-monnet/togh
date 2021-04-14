// -----------------------------------------------------------
//
// ToolService
//
//  var toolService = FactoryService.getInstance().getToolService();

// -----------------------------------------------------------
import { FormattedMessage } from "react-intl";


class ToolService {

	getDateListFromDate( dateone, datetwo ) {
		// console.log("ToolService.getDateListFromDate: "+JSON.stringify(dateone));
		var listDates = [];
		listDates.push( dateone);
		if (datetwo)
			listDates.push( datetwo);
		return listDates;
	}
	
	
	// Generate the date picker format.
	// we used a ISO date (2018-09-25) ans ask the ReactIntl to transform it in the local. 
	// it return for example 25/09/2018 for France
	// then, replace 25 by d, 09 by m, 2018 by y. So format is d/m/y
	
	getDatePickerFormat() {
	
	  const isoString = '2018-09-25'; // example date!
	  const dateParts = isoString.split('-') // prepare to replace with pattern parts
	
	  const intlString = this.formatDate(isoString) // generate a formatted date
	
	  return intlString
	    .replace(dateParts[2], 'd')
	    .replace(dateParts[1], 'm')
	    .replace(dateParts[0], 'y')
	}
	
	getDateToCarbonPicker(isoDate) {
		return this.formatDate(isoDate)
	}
	/* We get a format like d/m/y or d.m.y for russia, or m/d/y in the US
	*/
	getCarbonPickerToDate() {
		
	}

	/**
	return the number of day between two date. Is negative if dateone > datetwo 
	 */
	getNumberOfDaysInPeriod( dateone, datetwo) {
		if (! dateone || ! datetwo)
			return 0;
		var differenceInTime = datetwo.getTime() - dateone.getTime(); 
  
		// To calculate the no. of days between two dates 
		var nbDays= differenceInTime / (1000 * 3600 * 24);
		console.log("ToolService.getNumberOfDaysInPeriod: "+nbDays+" between "+JSON.stringify(dateone)+" - datetwo "+JSON.stringify(datetwo) );
		return nbDays;
	}
	
	/**
	format is dateStartEvent: "2021-02-20T16:05:10"
	 */
	getDateFromString( dateSt ) {
		if (! dateSt)
			return null;
		if (dateSt instanceof Date) 
			return dateSt;
		return new Date( dateSt );
	}
	
	getDayOfDate( dateObj ) {
		// to be sure, first translate to a date 
		var dateReal = this.getDateFromString( dateObj );
		if (! dateReal)
			return null;
		// console.log("ToolService.getDayOfDate dateObj="+dateObj+ " Date="+JSON.stringify( dateReal ));
		return dateReal.toISOString().slice(0,10);
	}
	
	
	getDayStringFromDate( date) {
		if (date === null)
			return null;
		var st = date.toISOString();
		return st.substring(0,10);
	}
	/**
	Look all existing code, then return an uniq number
	In fact, return max( code )+1.
	Example, list is [ {"code" : 12}, {"code":3}, {"code":5}  ]" ==> Return 13 */
	getUniqueCodeInList( list, attribut) {
		var maxInList =0;
		for (var i in list) {
			var value = list[i][ attribut ];
			if (value > maxInList)
				maxInList = value;
		}
		return maxInList+1;
	}
}
export default ToolService;