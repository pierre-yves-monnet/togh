// -----------------------------------------------------------
//
// BasketSlabEvent
//
// User modifidy one item, then send to to the server.
// -----------------------------------------------------------
import FactoryService from './FactoryService';

class BasketSlabEvent {
	
	constructor( eventCtrl ) {
		this.eventCtrl 			= eventCtrl;
		this.listSlabEvent 		= [];
		console.log("BasketSlabEvent.constructor on event="+ this.eventCtrl.getEvent().id);
	}
	
	addSlabEvent( slabEvent ) {
		this.listSlabEvent.push( slabEvent );
	}
	sendToServer( callbackfct ) {
		console.log("BasketSlabEvent.sendToServer: send this slabBasket to the server eventId="+this.eventCtrl.getEvent().id+" basket="+this.getString());
		// PostJson
		var param = [];
		for (var i in this.listSlabEvent) {
			param.push( this.listSlabEvent[ i ].getJson());
		}
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/event/update', this, param, httpPayload => {
			console.log("UpdateSlabEvent.Callback ! ");
			httpPayload.trace("BasketSlabEvent.callback");
			callbackfct( httpPayload);
			}
		);
	}
	
	getString() {
		console.log("BasketSlabEvent:getString");
		var listStr = "";
		for (var i in this.listSlabEvent) {
			console.log("BasketSlabEvent:getString_ "+i+" : "+this.listSlabEvent[ i ].getString());
			listStr.concat( this.listSlabEvent[ i ].getString());
			listStr.concat( ";");
		}
		return listStr;
	}
}


export default BasketSlabEvent;