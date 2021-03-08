// -----------------------------------------------------------
//
// UserParticipant
//
// For one event, the participant information. Contains  
//
// -----------------------------------------------------------
//

import * as participantConstant from './../EventParticipants';


class UserParticipant {
	
	// props.text is the text to display, translated
	constructor(event, participant) {
		this.event = event;
		this.participant = participant;
	}


	getUser() {
		return this.participant.user;
	}
	/**
	* isOrganizer
	*
	* An organizer can do special stuff 
 	*/	
	isOrganizer() {
		if (! this.participant)
			return false;
		if (this.participant.statusUser === participantConstant.STATUS_LEFT)
			return false;
		if (this.participant.role === participantConstant.ROLE_OWNER 
			|| this.participant.role === participantConstant.ROLE_ORGANIZER)
			return true;
		return false;  
	}
	
	
	/**
	* isParticipant
	*
	* An particpant can modify and write event 
 	*/	
	isParticipant() {
		console.log("UserParticipant.isParticipant? ");
		if (! this.participant)
			return false;
			console.log("UserParticipant.isParticipant? Status="+this.participant.statusUser +" role="+this.participant.role);
		if (this.participant.statusUser === participantConstant.STATUS_LEFT)
			return false;
		if (this.participant.role === participantConstant.ROLE_OWNER 
			|| this.participant.role === participantConstant.ROLE_ORGANIZER
			|| this.participant.role === participantConstant.ROLE_PARTICIPANT)
			return true;
		return false;  
	}
}

export default UserParticipant;
	