// -----------------------------------------------------------
//
// SurveyCtrl
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

class SurveyCtrl {
	
	// props.text is the text to display, translated
	constructor(eventCtrl, survey) {
		this.eventCtrl = eventCtrl;
		this.event = eventCtrl.getEvent();
		this.survey = survey;
		this.userParticipant = eventCtrl.getUserParticipant();
		this.updateEventfct = eventCtrl.getUpdateEventfct();

		if (! this.survey) {
			console.log("SurveyEntity.constructor : survey does not exist, this is not expected")
			this.survey= SurveyCtrl.getDefaultSurvey();
		}
		if (! this.survey.status) {
			this.survey.status= STATUS_INPREPARATION;
		}
		if (! this.survey.choices)
			this.survey.choices=[];
		if (! this.survey.answers)
			this.survey.answers=[];

	
	}

	getValue() {
		return this.survey;
	}
	
	getStatus() {
		return this.survey.status;
	}
	
	getEventCtrl() {
		return this.eventCtrl;
	}
	
	/** ------------------------------------------------------
	*   Choice
	*/
	addChoice() {
		console.log("SurveyEntity.addChoice !!")
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
		console.log("SurveyEntity.setAnswer: userId "+answerParticipant.userid+" choice:"+surveyChoiceCode+" value="+value);
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
		this.setChildAttribut(name, value, "");
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
	setChildAttribut(name, value, sublocalisation) {
		console.log("SurveyCtrl.setChildAttribut: set attribut:" + name + " <= " +JSON.stringify(value) + " survey=" + JSON.stringify(this.survey));
		
		// this.survey[name] = value;

		var completeLocalisation = "/surveylist/"+this.survey.id+"/"+sublocalisation;

		this.eventCtrl.setAttribut(name, value, this.survey, completeLocalisation);
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

export default SurveyCtrl;