// -----------------------------------------------------------
//
// ChooseState
//
// Choose a participant
//
// -----------------------------------------------------------

import React from 'react';



import { Search } from 'carbon-components-react';
import { SearchFilterButton } from 'carbon-components-react';
import { SearchLayoutButton } from 'carbon-components-react';

class ChooseParticipant extends React.Component {
	// this.props.changeState();

	constructor( props ) {
		super();
		
		
		this.state = { 'event' : props.event,
						'participant' : props.participant,
						'modifyParticipant' : props.modifyParticipant}
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
			
		// Display it and allow to change it		
		var stateOptions = [ 
			{key: "1", text:"Hello", value:"hello"},
			{key: "1", text:"The", value:"the"}
		
		]
		return (<Search id="searchparticipant" placeHolderText="Participant"></Search>);

		 //  return (<Dropdown placeholder='Partipant' search selection options={stateOptions} /> )

			
		}
		
}

export default ChooseParticipant; 
		