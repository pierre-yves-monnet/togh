// -----------------------------------------------------------
//
// Survey
//
// This class is not a ReactComponent, and it used to manipulate a Survey 
//
// -----------------------------------------------------------
//
import FactoryService from './../service/FactoryService';

import SlabEvent from './../service/SlabEvent';

export const STATUS_INPREPARATION = 'INPREPARATION';
export const STATUS_OPEN = 'OPEN';
export const STATUS_CLOSE = 'CLOSE';

const CHILD_CHOICES="choices";
const CHILD_ANSWER="answers";

class Survey {
	
	// props.text is the text to display, translated
	constructor(event, survey, userParticipant, updateEventfct) {
		this.event = event;
		this.survey = survey;
		this.userParticipant = userParticipant;
		this.updateEventfct = updateEventfct;

		if (! this.survey) {
			console.log("Event.constructor : survey does not exist, this is not expected")
			this.survey= Survey.getDefaultSurvey();
		}
		if (! this.survey.status) {
			this.survey.status= STATUS_INPREPARATION;
		}
		if (! this.survey.choices)
			this.survey.choices=[];
		if (! this.survey.answers)
			this.survey.answers=[];;		
	}

	getValue() {
		return this.survey;
	}
	
	getStatus() {
		return this.survey.status;
	}
	/** ------------------------------------------------------
	*   Choice
	*/
	addChoice() {
		var toolService = FactoryService.getInstance().getToolService();

		var uniqCode = toolService.getUniqueCodeInList( this.survey.choices, "code");
		var newchoices = this.survey.choices.concat( { code:uniqCode, propositiontext:''});
		this.setChildAttribut( CHILD_CHOICES, newchoices, "");
	}

	removeChoice( code ) {
		const newChoices = this.survey.choices.filter((index) => index.code !== code);
 		this.setChildAttribut( CHILD_CHOICES,newChoices, "");
	}
	
	/** ------------------------------------------------------
	*   Answer
	*/
	setAnswer( answerParticipant, surveyChoiceCode, value) {
		console.log("Survey.setAnswer: userId "+answerParticipant.userid+" choice:"+surveyChoiceCode+" value="+value);
		// answerParticipant && surveyChoice are correctly pointed to the value expected
		
		// avoid the JSON Circular
		for (var i in this.survey.answers) {
			if (this.survey.answers[ i ].userid === answerParticipant.userid ) {
				this.survey.answers[ i ].decision [ surveyChoiceCode ] = value;
			}
		}
		
 		this.setChildAttribut( CHILD_ANSWER, this.survey.answers, "");
	
	}



	// -----------------------------------------------
	// Tools
	/**
	 */
	completeSurveyWithMe() {
		// Ok, I must be part on this survey, ins't ?
		for (var i in this.survey.answers) {
			if (this.userParticipant.getUser() && this.survey.answers[ i ].userid === this.userParticipant.getUser().id) {
				// I'm in !
				return;
			}
		}
		// so, add me
		var newlist = this.survey.answers.concat( { userid : this.userParticipant.getUser().id,
													username: this.userParticipant.getUser().label,
													 decision: {}});
		this.survey.answers = newlist;
		this.setChildAttribut( CHILD_ANSWER, newlist, "/");
	}

	// -----------------------------------------------
	// Set value to update the survey
	/**
	 */
	setAttribut(name, value) {
		this.setChildAttribut(name, value, this.survey, "/");
	}

	setChoiceValue( name, value, item) {
		console.log("Survey.setChoiceValue: set attribut:" + name + " <= " + value + " survey=" + JSON.stringify(this.survey));
		item[name] = value;
		// the choice is updated
		this.setChildAttribut(CHILD_CHOICES, this.survey.choices, "" );
	} 
	/**
	 * 
	*/
	setChildAttribut(name, value,localisation) {
		console.log("Survey.setChildAttribut: set attribut:" + name + " <= " +JSON.stringify(value) + " survey=" + JSON.stringify(this.survey));
		
		this.survey[name] = value;

		var completeLocalisation = "/surveylist/"+this.survey.id+"/"+localisation;

		// currentEvent.shoppinglist[0].[name] = value;

		
		var slabEvent = SlabEvent.getUpdate(this.event, name, value, completeLocalisation);
		this.updateEventfct( slabEvent );
	}




	/**
	* getDefaultSurvey
	*
	* Return a default survey 
 	*/	
	static getDefaultSurvey() {
		return {status: STATUS_INPREPARATION, choices: [], answers:[] };
	}
	
	

}

export default Survey;