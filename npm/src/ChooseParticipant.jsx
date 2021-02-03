// -----------------------------------------------------------
//
// ChooseState
//
// Choose a participant
//
// -----------------------------------------------------------

import React from 'react';



import { Search, Select } from 'carbon-components-react';
import { SearchFilterButton } from 'carbon-components-react';
import { SearchLayoutButton } from 'carbon-components-react';

class ChooseParticipant extends React.Component {
	// this.props.changeState();

	constructor( props ) {
		super();
		
		
		this.state = { 'event' : props.event,
						'participant' : props.participant,
						'modifyParticipant' : props.modifyParticipant}
						
		this.setParticipant = this.setParticipant.bind(this);						
	}

	
//----------------------------------- Render
	render() {
		if (! this.state.modifyParticipant) {
			return (
				<div>
					{this.state.participant.user !== '' && ( <div>{this.state.participant.user.firstname} {this.state.participant.user.lastname}</div>)}
					{this.state.participant.user === '' && ( <div>{this.state.participant.user.email}</div>)}
					{this.state.participant.user.sourceUser === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}
				</div>
				);
			};
			
		// return (<Search id="searchparticipant" placeHolderText="Participant"></Search>);
		return (<div></div>);
		
		/*
		let sortedList =  this.state.event.participants.map( (participant) => (
			<option key={participant.user.id}>{participant.user.firstName} {participant.user.lastName}</option>)
			);
			
		 return (<Select  labelText="Scope" value={this.state.participant.user} onChange={(event) => this.setParticipant( event.target.value )}> 
			{sortedList}
					</Select>)
					*/
			
		}
	setParticipant() {
		
	}	
		
}

export default ChooseParticipant; 
		