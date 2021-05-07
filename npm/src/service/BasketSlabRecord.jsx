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
// BasketSlabRecord
//
// User modifidy one item, then send to to the server.
// -----------------------------------------------------------
import FactoryService from './FactoryService';

class BasketSlabRecord {
	
	constructor( eventCtrl ) {
		this.eventCtrl 			= eventCtrl;
		this.listSlabRecord 		= [];
		console.log("BasketSlabRecord.constructor on event="+ this.eventCtrl.getEvent().id);
	}
	
	addSlabRecord( SlabRecord ) {
		this.listSlabRecord.push( SlabRecord );
	}
	sendToServer( callbackfct ) {
		console.log("BasketSlabRecord.sendToServer: send this slabBasket to the server eventId="+this.eventCtrl.getEvent().id+" s="+this.listSlabRecord.length+" basket="+this.getString());
		// PostJson
		var listParamSlab = [];
		for (let i = this.listSlabRecord.length - 1; i >=0 ; i--) {
			// We kept only one SlabRecord per localisation / attribut
			let slabRecord = this.listSlabRecord[ i ];
			let existInRecord=false;
			for (var j in listParamSlab) {
				if (listParamSlab[ j ].localisation === slabRecord.localisation 
					&& listParamSlab[ j ].name === slabRecord.name)
					existInRecord=true;
			}
			if (! existInRecord)
				listParamSlab.push( slabRecord.getJson());
		};
		
		
		var param = {eventid:this.eventCtrl.event.id, listslab: listParamSlab};
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/event/update', this, param, httpPayload => {
			// console.log("BasketSlabRecord.Callback ! ");
			httpPayload.trace("BasketSlabRecord.callback");
			callbackfct( httpPayload );
			}
		);
	}
	
	getString() {
		// console.log("BasketSlabRecord:getString size="+this.listSlabRecord.length);
		var listStr = "size="+this.listSlabRecord.length+" ";
		for (var i in this.listSlabRecord) {
			console.log("BasketSlabRecord:getString_ "+i+" : "+this.listSlabRecord[ i ].getString());
			listStr.concat( this.listSlabRecord[ i ].getString());
			listStr.concat( ";");
		}
		return listStr;
	}
}


export default BasketSlabRecord;