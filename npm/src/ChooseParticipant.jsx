// -----------------------------------------------------------
//
// ChooseState
//
// Choose a participant
//
// -----------------------------------------------------------
//

import React from 'react';
import {Search} from 'carbon-components-react';
import Autosuggest from 'react-autosuggest';


// https://developer.mozilla.org/en/docs/Web/JavaScript/Guide/Regular_Expressions#Using_Special_Characters
function escapeRegexCharacters(str) {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function getSuggestions(value, participants) {
  const escapedValue = escapeRegexCharacters(value.trim());
  
  if (escapedValue === '') {
    return [];
  }

  const regex = new RegExp('^' + escapedValue, 'i');

  return participants.filter(participant => regex.test(participant.user.email));
}

function getSuggestionValue(suggestion) {
  return suggestion.user.email;
}

function renderSuggestion(suggestion) {
  return (
	<span>{suggestion.user.email}</span>
  );
}

function renderInputComponent(inputProps) {
//	return (
//		<div><input {...inputProps} /></div>
//	);
	return (
		<div><Search id="search-1" {...inputProps} /></div>
	);
}


class ChooseParticipant extends React.Component {
  constructor(props) {
    super();
    this.state = {
      'value': '',
      'suggestions': [],
	  'event' : props.event,
	  'modifyParticipant' : props.modifyParticipant,
    };
  }

// TMP BLOCK // These were the initial tests, no need for it anymore
//	users = [
//	  {
//	    user: {
//			lastname: 'Pierre-Yves',
//	    	firstname: 'Monnet',
//			email: 'pierre-yves.monnet@laposte.net'
//		}
//	  },
//	  {
//	    user: {
//			lastname: 'toto',
//	    	firstname: 'toto',
//			email: 'test@test.test'
//		}
//	  },
//	];
// TMP BLOCK

  onChange = (event, { newValue, method }) => {
    this.setState({
      value: newValue,
	  participant: newValue,
    });
  };
  
  onSuggestionsFetchRequested = ({ value }) => {
    this.setState({
//      	suggestions: getSuggestions(value,this.users) // THIS WORKS // 
		suggestions: getSuggestions(value,this.state.event.participants)
    });
  };

  onSuggestionsClearRequested = () => {
    this.setState({
      suggestions: []
    });
  };

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
    const { value } = this.state;
   if (typeof this.state.participant !== 'undefined'){
		if (typeof this.state.participant.user !== 'undefined'){
			value = this.state.participant.user.email
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
        getSuggestionValue={getSuggestionValue}
        renderSuggestion={renderSuggestion}
        inputProps={inputSelectParticipant}
		renderInputComponent={renderInputComponent}
		/>
    );
  }
}





//import React from 'react';
//
//
//
//import { Search, Select, SelectItem } from 'carbon-components-react';
//import { SearchFilterButton } from 'carbon-components-react';
//import { SearchLayoutButton } from 'carbon-components-react';
//import Autosuggest from 'react-autosuggest';
//
//
//const languages = [
//	{
//		name: 'C',
//		year: 1972
//	},
//	{
//		name: 'Elm',
//		year: 2012
//	}
//]
//
//	
//function getSuggestions(value) {
//	const inputValue = value;
//	const inputLength = inputValue.length;
//	
//	return inputLength === 0 ? [] : languages.filter(
//		lang => lang.slice(0,inputLength) === inputValue);
//};
//
//function getSuggestionValue(suggestion){
//	return suggestion.name;
//}
//
//function renderSuggestions(suggestion) {
//	return(
//		<div>
//			{suggestion.name}
//		</div>	
//	);
//}
//	
//class ChooseParticipant extends React.Component {
//	
//	// pingChangeParticipant
//	constructor( props ) {
//		super();
//		
////		this.state = { 'event' : props.event,
////						'participant' : props.participant,
////						'modifyParticipant' : props.modifyParticipant,
////						value: "",
////						suggestions: []}
//
//		this.state = { 
//						value: "TEST",
//						suggestions: ["toto","titi","tata"]}
//						
////		this.setParticipant = this.setParticipant.bind(this);						
//	}
//	
//	onChange = (event, {newValue, method}) => {
//		this.setState({ value: newValue})
//	}
//	
//	onSuggestionsFetchRequested = ({value}) => {
//		this.setState({
//			suggestions: getSuggestions(value)
//		});
//	}
//
//	onSuggestionsClearRequested = () => {
//		this.setState({
//			suggestions: []
//		});
//	}
//	
////----------------------------------- Render
//	render() {
////		if (! this.state.modifyParticipant) {
////			return (
////				<div>
////					{this.state.participant.user !== '' && ( <div>{this.state.participant.user.firstname} {this.state.participant.user.lastname}</div>)}
////					{this.state.participant.user === '' && ( <div>{this.state.participant.user.email}</div>)}
////					{this.state.participant.user.sourceUser === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}
////				</div>
////				);
////			};
//		
//		const {value, suggestions } = this.state;
//			
//		// return (<Search id="searchparticipant" placeHolderText="Participant"></Search>);
//		const inputProps = {
//			placeholder: 'Select a participant',
//			value,
//			onChange: this.state.onChange
//		}
//		return(
//		<Autosuggest
//			suggestions={suggestions}
//			onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
//			onSuggestionsClearRequested={this.onSuggestionsClearRequested}
//			getSuggestionValue={getSuggestionValue}
//			renderSuggestion={renderSuggestions}
//			inputProps={inputProps}
//			/>
//		)
//		
////		// BELOW IS A SELECT LIST THAT WORKS (EXCEPT lastName is not set' ?)
////		// But that probably a db issue, because the value is actually null, not undefined as if this was a variable name error
////		let sortedList =  this.state.event.participants.map( (participant) => (
////			<SelectItem value={participant.id} text={participant.user.lastName+' '+participant.user.email}  />)
////			);
////			
////		 return (
////			<div>
//// 				<Select defaultValue="placeholder-item" onChange={(event) => this.setParticipant( event.target.value )}>
////					<SelectItem disabled hidden value="placeholder-item" text="Choose a participant" /> 
////					{sortedList}
////				</Select>
////			</div>)			
//		}
//	setParticipant() {
//		
//	}	
//		
//}

export default ChooseParticipant; 
		