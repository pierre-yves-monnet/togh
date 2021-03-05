// -----------------------------------------------------------
//
// BasketSlabEvent
//
// User modifidy one item, then send to to the server.
// -----------------------------------------------------------

class BasketSlabEvent {
	
	constructor( event ) {
		this.event = event;
		this.listSlabEvent = [];
		console.log("BasketSlabEvent.constructor on event="+this.event.id);
	}
	
	addSlabEvent( slabEvent ) {
		this.listSlabEvent.push( slabEvent );
	}
	sendToServer( ) {
		console.log("UpdateSlabEvent: send this slabBasket to the server eventId="+this.event.id+" basket="+this.getString());
	}
	
	getString() {
		var listStr = "";
		for (var i in this.listSlabEvent) {
			listStr.concat( this.listSlabEvent[ i ].getString());
			listStr.concat( ";");
		}
		return listStr;
	}
}


export default BasketSlabEvent;