// -----------------------------------------------------------
//
// SlabEvent
//
// User modifidy one item, then register it
// -----------------------------------------------------------

class SlabEvent {
	
	
	constructor( event, operation, name, value, localisation) {
		this.operation=operation;
		this.event = event;
		this.name = name;
		this.value = value;
		this.localisation = localisation;
		this.slabid = new Date().getTime();
		
	};
	
	/**
	* UPDATE name value localisation
	* 
	*  UPDATE "what" "this is where we go" "/shoppinglist/43"
	*  UPDATE "name" "My birthday" "/"
	 */
	static getUpdate (event, name, value, localisation) {
		return new SlabEvent(event, "UPDATE", name, value, localisation);
 	}

	static getUpdateList(event, name, localisation) {
		return new SlabEvent(event, "LIST", name, null, localisation);
 	}

	/**
	* node : when a new child is added, interface has to wait to received the ID of this item
 	*/
	static getAddList(event, listname, value, localisation) {
		return new SlabEvent(event, "ADD", listname, value, localisation);
 	}
	getString() {
		return this.slabid+" "+this.operation+": ["+this.name+"]="+this.value;
	}
	
}
 
export default SlabEvent;