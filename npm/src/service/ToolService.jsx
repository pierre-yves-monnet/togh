// -----------------------------------------------------------
//
// ToolService
//
//  var ToolService = FactoryService.getInstance().getToolService();

// -----------------------------------------------------------


class ToolService {

	getDateListFromDate( dateone, datetwo ) {
		// console.log("ToolService.getDateListFromDate: "+JSON.stringify(dateone));
		var listDates = [];
		listDates.push( dateone);
		if (datetwo)
			listDates.push( datetwo);
		return listDates;
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
		if (dateSt instanceof Date) 
			return dateSt;
		return new Date( dateSt );
	}
	
	getDayOfDate( dateObj ) {
		// to be sure, first translate to a date 
		var dateReal = this.getDateFromString( dateObj );
		if (! dateReal)
			return null;
		console.log("ToolService.getDayOfDate dateObj="+dateObj+ " Date="+JSON.stringify( dateReal ));
		return dateReal.toISOString().slice(0,10);
	}
}
export default ToolService;