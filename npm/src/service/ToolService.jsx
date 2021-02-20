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
}
export default ToolService;