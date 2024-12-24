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
// User modify one item, then send to to the server.
// -----------------------------------------------------------
import FactoryService 		from 'service/FactoryService';

class BasketSlabRecord {
	
	constructor( eventCtrl ) {
		this.eventCtrl 			= eventCtrl;
		this.listSlabRecord 	= [];
		console.log("BasketSlabRecord.constructor on event="+ this.eventCtrl.getEvent().id);
	}
	
	addSlabRecord( SlabRecord ) {
		this.listSlabRecord.push( SlabRecord );
	}
	// callbackfct : call this method when we received the answer.
	// to call the method inside the this object, use a lambda: (data)=>this.myMethod(data)
	sendToServer( callbackfct ) {
		console.log("BasketSlabRecord.sendToServer: send this slabBasket to the server eventId="+this.eventCtrl.getEvent().id+" s="+this.listSlabRecord.length+" basket="+this.getString());
		// PostJson
		let param = this.getBasketJson();
		const restCallService = FactoryService.getInstance().getRestCallService();
		restCallService.postJson('/api/event/update', this, param, httpPayload => {
			// console.log("BasketSlabRecord.Callback ! ");
			httpPayload.trace("BasketSlabRecord.callback");
			callbackfct( httpPayload );
			}
		);
	}

	// return the current bask as JSON to be ready to send to the server
	getBasketJson() {
	    let listParamSlab = [];
        for (let i = this.listSlabRecord.length - 1; i >=0 ; i--) {
            // We kept only one SlabRecord per localisation / attribut
            let slabRecord = this.listSlabRecord[ i ];
            let existInRecord=false;
            for (let j in listParamSlab) {
                if (listParamSlab[ j ].localisation === slabRecord.localisation
                    && listParamSlab[ j ].name === slabRecord.name)
                    existInRecord=true;
            }
            if (! existInRecord)
                listParamSlab.push( slabRecord.getJson());
        }


        let param = {eventid:this.eventCtrl.event.id, listslab: listParamSlab};
    	return param;
	}
	getString() {
		// console.log("BasketSlabRecord:getString size="+this.listSlabRecord.length);
		let listStr = "size="+this.listSlabRecord.length+" ";
		for (let i in this.listSlabRecord) {
			console.log("BasketSlabRecord:getString_ "+i+" : "+this.listSlabRecord[ i ].getString());
			listStr.concat( this.listSlabRecord[ i ].getString());
			listStr.concat( ";");
		}
		return listStr;
	}
}


export default BasketSlabRecord;