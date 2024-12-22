// -----------------------------------------------------------
//
// ChooseParticipant
//
// Choose a participant
//
// -----------------------------------------------------------
//

import React from 'react';
import { Search } from '@carbon/react';
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

	


	
	
	//  -------------------------------------------- Render
	render() {
		
		if (!this.state.modifyParticipant) {
			return (
				<div>
					{this.state.participant.user !== '' && (<div>{this.state.value}</div>)}
					{this.state.participant.user.sourceUser === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}
				</div>
			);
		}
	
		var value = this.state.value;
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
				{this.state.label && <div>{this.state.label}</div>}

				<Autosuggest
					suggestions={this.state.suggestions}
					onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
					onSuggestionsClearRequested={this.onSuggestionsClearRequested}
					getSuggestionValue={this.getSuggestionValue}
					renderSuggestion={this.renderSuggestion}
					inputProps={inputSelectParticipant}
					renderInputComponent={this.renderInputComponent}
					renderSuggestionsContainer={this.renderSuggestionsContainer}
				/>
			</div>
		);
		
	}

	
	// --------------------------------------------------------------
	// 
	// function for the autosuggest
	// 
	// --------------------------------------------------------------
	/**
	 * 
	 */	

	onChange = (event, { newValue, method }) => {
		console.log("ChooseParticipant.onChange: newValue="+newValue);
		this.setState({
			value: newValue,
		});
		// search if the new value mache a participant : if this is the cass, do the callback
		for(var i in this.state.event.participants) {
			console.log(" Compare [" + this.state.event.participants[ i ].user.longLabel+"] <=> ["+newValue+"]");
			if (this.getValueFromParticipant( this.state.event.participants[ i ]) === newValue) {
				this.onChangeParticipantfct( this.itemToCarry,  this.state.event.participants[ i ].user.id );
			}
		}
	};
	
	
	// https://developer.mozilla.org/en/docs/Web/JavaScript/Guide/Regular_Expressions#Using_Special_Characters
	escapeRegexCharacters(str) {
		return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	}

	/**
	* return a list of Object Participant from the value which is what the user gave
 	*/
	getSuggestions(value, participants) {
		console.log("ChooseParticipant.getSuggestions:");

		const escapedValue = this.escapeRegexCharacters(value.trim());
		
		if (escapedValue === '') {
			return [];
		}

		// const regex = new RegExp('^' + escapedValue, 'i');
		const regex = new RegExp('.*' + escapedValue + '.*', 'i');

		var result= participants.filter(participant => regex.test(participant.user.longLabel));
		console.log("ChooseParticipant.getSuggestions: result = "+JSON.stringify(result));
		return result;
	}


	
	renderSuggestion(suggestionParticipant) {
		console.log("ChooseParticipant.renderSuggestion: for participant["+JSON.stringify(suggestionParticipant)+"]");
		return (
			<span>{this.getValueFromParticipant( suggestionParticipant) }</span>
		);
	}

	renderInputComponent(inputProps) {
		console.log("ChooseParticipant.renderInputComponent:");
		return (
			<div><Search id="search-1" labelText="" {...inputProps} /></div>
		);
	}
	
	/**
	* For a participant, get back the label.
	Call by the autosuggest via getSuggestionValue(), or when the value has to be displayed
	*/
	getValueFromParticipant( participant ) {
		if (participant && participant.user && participant.user.longLabel)  {
			console.log("ChoosePartipant.getValueFormPartipant: "+participant.user.id+" : "+participant.user.longLabel);
			return participant.user.longLabel.toString();
		}
		
		console.log("ChoosePartipant.getValueFormPartipant [unknown] ?? ");
		return "";
	}
	
	renderSuggestionsContainer({ containerProps, children, query }) {
		if (! children)
			return "";
	  return (
		<div style={{ boxShadow: "3px 3px #888888", 
			border: "1px solid", 
			marginLeft: "15px", 
			marginBottom: "20px", 
			padding: "0px 4px 0px 4px",
			lineHeight : "160%"}} {...containerProps}>
	      {children}	      
	    </div>
	  );
	}
	
	// --------------------------------------------------------------
	// 
	// manage suggestion
	// 
	// --------------------------------------------------------------	
	/**
	When the user clicks on a suggestion, this method is called
	
	 */
	onSuggestionsFetchRequested = ({ value }) => {
		console.log("ChoosePartipant.onSuggestionsFetchRequested value=["+value+"]");
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
		console.log("ChoosePartipant.getSuggestionValue["+suggestionParticipant+"]");
		return this.getValueFromParticipant( suggestionParticipant );
	}
	
}





export default ChooseParticipant;
