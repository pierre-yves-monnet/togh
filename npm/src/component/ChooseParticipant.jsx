// -----------------------------------------------------------
//
// ChooseParticipant
//
// Choose a participant
//
// -----------------------------------------------------------
//

import React from 'react';
import { Search } from 'carbon-components-react';
import Autosuggest from 'react-autosuggest';

// test push




class ChooseParticipant extends React.Component {
	constructor(props) {
		super();
				
				
		// modifyParticipant : true/false to say if the user can be modified
		this.state = {
			value: '',
			label: props.label,
			suggestions: [],
			event: props.event,
			modifyParticipant: props.modifyParticipant,
		};
		// onChange : callback when a user change. Value is the user.
		this.onChangeParticipantfct =props.onChangeParticipantfct;
		
		// if an userId is given, calulate the value from this userid
		console.log("ChooseParticipant - search for userId="+ props.userid);
		if( props.userid) {
			for(var i in this.state.event.participants) {
				if (this.state.event.participants[ i ].user && 
					this.state.event.participants[ i ].user.id === props.userid) {
						console.log("ChooseParticipant found an participant for userid="+this.state.event.participants[ i ].user.id);
						this.state.value = this.getValueFromParticipant( this.state.event.participants[ i ]);
					}
			}
		}
		console.log("ChooseParticipant - search for userId="+ props.userid+" value="+this.state.value);
		
		// itemToCarry is only to give it back for the onChange 
		this.itemToCarry = props.item;
		this.onSuggestionsFetchRequested 	= this.onSuggestionsFetchRequested.bind( this );
		this.onSuggestionsClearRequested	= this.onSuggestionsClearRequested.bind( this );
		this.getSuggestionValue				= this.getSuggestionValue.bind( this );
		this.renderSuggestion				= this.renderSuggestion.bind( this );
		this.renderInputComponent			= this.renderInputComponent.bind( this );
	}

	onChange = (event, { newValue, method }) => {
		console.log("ChooseParticpant : onChange, newValue="+newValue);
		this.setState({
			value: newValue,
		});
		// search if the new value mache a participant : if this is the cass, do the callback
		for(var i in this.state.event.participants) {
			console.log(" Compare [" + this.state.event.participants[ i ].user.longlabel+"] <=> ["+newValue+"]");
			if (this.getValueFromParticipant( this.state.event.participants[ i ]) === newValue) {
				this.onChangeParticipantfct( this.itemToCarry,  this.state.event.participants[ i ].user.id );
			}
		}
	};


	
	
	//  -------------------------------------------- Render
	render() {
		if (!this.state.modifyParticipant) {
			return (
				<div>
					{this.state.participant.user !== '' && (<div>{this.state.value}</div>)}
					{this.state.participant.user.sourceUser === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}
				</div>
			);
		};

		let value = this.state;
		if (typeof this.state.participant !== 'undefined') {
			if (typeof this.state.participant.user !== 'undefined') {
				value = this.getValueFromParticipant( this.state.participant );
			}
		}
		const inputSelectParticipant = {
			placeholder: "Select Participant",
			value,
			onChange: this.onChange
		};


		return (
			<div>
				{this.state.label && (this.state.label)}
			<Autosuggest
				suggestions={this.state.suggestions}
				onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
				onSuggestionsClearRequested={this.onSuggestionsClearRequested}
				getSuggestionValue={this.getSuggestionValue}
				renderSuggestion={this.renderSuggestion}
				inputProps={inputSelectParticipant}
				renderInputComponent={this.renderInputComponent}
			/>
			</div>
		);
	}

	
	// --------------------------------------------------------------
	// 
	// function for the autosuggest
	// 
	// --------------------------------------------------------------	

	// https://developer.mozilla.org/en/docs/Web/JavaScript/Guide/Regular_Expressions#Using_Special_Characters
	escapeRegexCharacters(str) {
		return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	}

	getSuggestions(value, participants) {
		const escapedValue = this.escapeRegexCharacters(value.trim());

		if (escapedValue === '') {
			return [];
		}

		// const regex = new RegExp('^' + escapedValue, 'i');
		const regex = new RegExp('.*' + escapedValue + '.*', 'i');

		return participants.filter(participant => regex.test(participant.user.longlabel));
	}



	renderSuggestion(suggestionParticipant) {
		return (
			<span>{this.getValueFromParticipant( suggestionParticipant) }</span>
		);
	}

	renderInputComponent(inputProps) {
		return (
			<div><Search id="search-1" labelText="" {...inputProps} /></div>
		);
	}
	
	getValueFromParticipant( participant ) {
		console.log("ChoosePartipant : getValueFormPartipant "+participant.user.id+" : "+participant.user.longlabel);
		if (participant.user)
			return participant.user.longlabel;
		return "";
	}
	// --------------------------------------------------------------
	// 
	// manage suggestion
	// 
	// --------------------------------------------------------------	
	onSuggestionsFetchRequested = ({ value }) => {
		this.setState({
			suggestions: this.getSuggestions(value, this.state.event.participants)
		});
	};

	onSuggestionsClearRequested = () => {
		this.setState({
			suggestions: []
		});
	};
	getSuggestionValue(suggestionParticipant) {
		return this.getValueFromParticipant( suggestionParticipant );
	}
	
}





export default ChooseParticipant;
