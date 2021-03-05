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
		this.state = {
			'value': '',
			'suggestions': [],
			'event': props.event,
			'modifyParticipant': props.modifyParticipant,
		};
		
		this.onSuggestionsFetchRequested 	= this.onSuggestionsFetchRequested.bind( this );
		this.onSuggestionsClearRequested	= this.onSuggestionsClearRequested.bind( this );
		this.getSuggestionValue				= this.getSuggestionValue.bind( this );
		this.renderSuggestion				= this.renderSuggestion.bind( this );
		this.renderInputComponent			= this.renderInputComponent.bind( this );
	}

	onChange = (event, { newValue, method }) => {
		this.setState({
			value: newValue,
			participant: newValue,
		});
	};

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
	getSuggestionValue(suggestion) {
		return suggestion.user.longlabel;
	}
	
	
	//  -------------------------------------------- Render
	render() {
		if (!this.state.modifyParticipant) {
			return (
				<div>
					{this.state.participant.user !== '' && (<div>{this.state.participant.user.label}</div>)}
					{this.state.participant.user.sourceUser === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}
				</div>
			);
		};

		const { value } = this.state;
		if (typeof this.state.participant !== 'undefined') {
			if (typeof this.state.participant.user !== 'undefined') {
				value = this.state.participant.user.label
			}
		}
		const inputSelectParticipant = {
			placeholder: "Select Participant",
			value,
			onChange: this.onChange
		};


		return (
			<Autosuggest
				suggestions={this.state.suggestions}
				onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
				onSuggestionsClearRequested={this.onSuggestionsClearRequested}
				getSuggestionValue={this.getSuggestionValue}
				renderSuggestion={this.renderSuggestion}
				inputProps={inputSelectParticipant}
				renderInputComponent={this.renderInputComponent}
			/>
		);
	}


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



	renderSuggestion(suggestion) {
		return (
			<span>{suggestion.user.longlabel}</span>
		);
	}

	renderInputComponent(inputProps) {
		return (
			<div><Search id="search-1" labelText="" {...inputProps} /></div>
		);
	}
}





export default ChooseParticipant;
