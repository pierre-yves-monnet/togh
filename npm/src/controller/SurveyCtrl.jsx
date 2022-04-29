/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

export const STATUS_INPREPAR = 'INPREPAR';
export const STATUS_OPEN = 'OPEN';
export const STATUS_CLOSE = 'CLOSE';

export const CHILD_CHOICE="choicelist";
export const CHILD_ANSWER="answerlist";

// -----------------------------------------------------------
//
// SurveyCtrl
//
// This class is not a ReactComponent, and it used to manipulate a Survey 
//
// -----------------------------------------------------------
class SurveyCtrl {
	
	// props.text is the text to display, translated
	constructor(eventCtrl, survey) {
		this.eventCtrl = eventCtrl;
		this.event = eventCtrl.getEvent();
		this.survey = survey;
		this.userParticipant = eventCtrl.getUserParticipant();
		this.updateEventFct = eventCtrl.getUpdateEventFct();

		if (! this.survey) {
			console.log("SurveyEntity.constructor : survey does not exist, this is not expected")
			this.survey= SurveyCtrl.getDefaultSurvey();
		}
		if (! this.survey.status) {
			this.survey.status= STATUS_INPREPAR;
		}
		if (! this.survey.choicelist)
			this.survey.choicelist=[];
		if (! this.survey.answerlist)
			this.survey.answerlist=[];

	
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
	*   Answer
	*/
	setDecision( answerParticipant, surveyChoiceCode, value) {
		console.log("SurveyEntity.setAnswer: userId "+answerParticipant.userid+" choice:"+surveyChoiceCode+" value="+value);
		// answerParticipant && surveyChoice are correctly pointed to the value expected

		let surveyAnswer=null;
		// avoid the JSON Circular
		for (var i in this.survey[ CHILD_ANSWER ]) {
			if (this.survey[ CHILD_ANSWER ][i].whoid === answerParticipant.whoid ) {
				surveyAnswer=this.survey[ CHILD_ANSWER ][i];
				surveyAnswer.decision[ surveyChoiceCode ] = value;
 				this.setChildAttribut( "decision", surveyAnswer.decision, "/"+CHILD_ANSWER+"/"+answerParticipant.id);
				return;
			}
		}
		
	
	}



	// -----------------------------------------------
	// Tools
	/**
	 */
	
	
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
		this.eventCtrl.setAttribut(name, value,  this.survey, "/surveylist/"+this.survey.id+"/"+CHILD_CHOICE+"/"+item.id );
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
		return {status: STATUS_INPREPAR };
	}
	
	

}

export default SurveyCtrl;