// -----------------------------------------------------------
//
// EventSurvey
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl"; 

import { TextInput, TextArea, Checkbox } from 'carbon-components-react';
import { Pencil, Eye, DashCircle, PlusCircle, Check2Square, Square} from 'react-bootstrap-icons';



import TagDropdown from './component/TagDropdown';
import UserParticipantCtrl from './controller/UserParticipantCtrl';
import SurveyCtrl from './controller/SurveyCtrl';
import * as surveyConstant from './controller/SurveyCtrl';

import FactoryService from './service/FactoryService';


import * as surveyListConstant from './EventSurveyList';
	

const DISPLAY_SURVEY = "SURVEY";
const DISPLAY_ADMIN= "ADMIN";
const DISPLAY_NOACCESS = "NOACCESS";

class EventSurvey extends React.Component {
	// this.props.updateEvent()
	// this.props.getSurveyfct()
	constructor(props) {
		super();
		this.state = {
			event: props.event,
			show: {
				typeDisplay: DISPLAY_SURVEY,
			}

		};
		console.log("EventSurvey.constructor ");
		this.eventCtrl = props.eventCtrl;
		this.forceUpdatefct =  props.forceUpdatefct;
		
		this.surveyCtrl =  this.eventCtrl.getCurrentSurveyCtrl();
		
		
		// show : OFF, ON, COLLAPSE
		this.setAttributCheckbox		= this.setAttributCheckbox.bind( this );
		this.renderSurveyAdmin			= this.renderSurveyAdmin.bind( this );
		this.renderSurvey				= this.renderSurvey.bind( this );
		this.getTagState				= this.getTagState.bind(this);
		
	}
	// Calculate the state to display
	componentDidMount () {
		console.log("EventSurvey.componentDidMount");
		// survey May be completed, set it again	
		// this.setState( {survey: this.surveyEmbeded.getValue()});
		this.currentSurveyCtrl =  this.eventCtrl.getCurrentSurveyCtrl();

		this.userParticipant = this.eventCtrl.getUserParticipant();
		if ( this.currentSurveyCtrl.getStatus() === surveyConstant.STATUS_INPREPAR) {
			// so move to the ADMIN or the NOACCESS, depending of the user permission
			if (this.userParticipant.isParticipant()) 
				this.setState( { show: { typeDisplay: DISPLAY_ADMIN}});
			else
				this.setState( { show: { typeDisplay: DISPLAY_NOACCESS}});
		}
		else
			this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
	}
	
	
	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventSurvey: render survey");
		this.surveyCtrl = this.eventCtrl.getCurrentSurveyCtrl();

		if (! this.surveyCtrl ) {
			return (<div/>)
		}
	
		// refresh the current survey embeded
		// this.surveyEmbeded = new Survey( this.state.event, currentSurvey, this.userParticipant, this.updateEventfct);

		if (this.state.show.typeDisplay === DISPLAY_NOACCESS) {
			return (
				<div>
					<div class="row toghSectionHeader"> {this.survey.title}</div>
					<div><FormattedMessage id="EventSurvey.NoAccessForSurveyInPreparation" defaultMessage="This survey is in preparation. You can't access it for the moment." /></div>
				</div>
			);
		}	
		if (this.state.show.typeDisplay === DISPLAY_ADMIN) 
			return this.renderSurveyAdmin();
		else
			return this.renderSurvey();
	}
	
	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	/**
	 */
	renderSurveyAdmin() {
		const intl = this.props.intl;
		
		var survey = this.surveyCtrl.getValue();
		
		var listChoiceHtml = [];
		
		listChoiceHtml = survey.choices.map((item, index) =>
			<tr key={item.code}>
				<td> 
					<TextInput value={item.propositiontext} 
						onChange={(event) => {
						this.surveyCtrl.setChoiceValue("choice", event.target.value, item);
						this.setState( {survey: this.surveyCtrl.getValue()});
					}}
					 labelText="" ></TextInput>
				</td>
				<td><button class="btn btn-danger btn-xs" 					 
					title={intl.formatMessage({id: "EventSurvey.removeItem",defaultMessage: "Remove this item"})}
					onClick={() => {
								this.surveyCtrl.removeChoice( item.code );
								this.setState( {survey: this.surveyCtrl.getValue()});															
						}} >
						<DashCircle onClick={() => {
								this.surveyCtrl.removeChoice( item.code );
								this.setState( {survey: this.surveyCtrl.getValue()});															
						}}  />
					</button>
				</td>
			</tr>
			);
		// add a + proposition
		return (<div>
					<div class="row">
						<div class="col-2">
							{this.getTagState( survey )}
						</div>
						<div class="col-8">
							<TextInput value={survey.name} 
								onChange={(event) => {
									this.setAttribut("name", event.target.value);			
									// console.log("EventSurvey:forceUpdate my parent");
									this.forceUpdatefct();
									}
								} 
								labelText={<FormattedMessage id="EventSurvey.Title" defaultMessage="Title" />} />
								bob id={survey.it}
						</div>
						<div class="col-2" style={{ float: "right" }}>
							<button  class="btn btn-primary btn-xs" 
								onClick={(event) => {
									console.log("EventItinerary.ClickOnButtonView : ");
									this.surveyCtrl.completeSurveyWithMe();									
									this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
								}}>
							<Eye   
								onClick={(event) => {
									console.log("EventSurvey : ClickOnEyeView");
									this.surveyCtrl.completeSurveyWithMe();
									this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
								}
								}/>
							</button>
						</div> 
					</div>
					<div class="row">
						<div class="col-8">
							<TextArea value={survey.description} 
								onChange={(event) => {
									this.setAttribut("description", event.target.value);
									this.setState({  event: this.eventCtrl.getEvent() });
								}}  
								labelText={<FormattedMessage id="EventSurvey.Description" defaultMessage="Description" />} />
						</div>
						<div class="col-4">
							<table>
							<tr>
								<th><FormattedMessage id="EventSurvey.Option" defaultMessage="Option" /></th>
								<th><button class="btn btn-success btn-xs" 					 
									title={intl.formatMessage({id: "EventSurvey.AddOption",defaultMessage: "Option"})}
									onClick={() => { 
											console.log("EventSurvey.addButtonChoice");
											this.surveyCtrl.addChoice();
											this.setState({  event: this.eventCtrl.getEvent() });
									}}	>
									<PlusCircle onClick={() => {
											console.log("EventSurvey.addPlusChoice"); 
											//this.surveyCtrl.addChoice();
											//this.setState({  event: this.eventCtrl.getEvent() });
									}}/>
									</button>
								</th></tr>
								{listChoiceHtml}
								</table>
						</div>
					</div>
				</div> );
	}
	
	/** ------------------------------------------------------------------------------
	 * RenderSurvey
	 */
	renderSurvey () {
				
		var userParticipant = this.eventCtrl.getUserParticipant();

		var survey = this.surveyCtrl.getValue();

		var headerList = [];
		headerList.push(<th class="toghSectionHeader"></th>);
		for (var i in survey.choices) {
			headerList.push(
				<th class="toghSectionHeader" style={{textAlign: "center"}}>
					{survey.choices[ i ].propositiontext} ({this.getNumberOfVote(survey.choices[ i ].code)})
				</th> );
		}
		
		var participantList=[];
		
		if (survey.state === surveyConstant.STATUS_INPREPAR) {
			participantList.push(<tr><td><FormattedMessage id="EventSurvey.SurveyInPreparationNoParticipantsVisible" defaultMessage="This survey is in preparation. Participants are not visible" /></td></tr>)
		} else {
			// Checkbox can't be center		
			for (var i in survey.answers) {
				var answerParticipant = survey.answers[ i ];
				
				participantList.push(<tr>
					<td> {answerParticipant.username}</td>
					{ survey.choices.map( surveyChoice => 
						{ 
							var itemSquare=(<div/>);
							if (survey.status === surveyConstant.STATUS_CLOSE || answerParticipant.userid !== userParticipant.getUser().id) {
								if (answerParticipant.decision[ surveyChoice.code ])
									itemSquare= (<Check2Square/>);
								else	
									itemSquare= (<Square/>);
							} else {
								itemSquare = (<input type="checkbox"
										 	id ={answerParticipant.userid+"_"+surveyChoice.code}
											style={{ width: "1rem", height:"1rem",  margin: "0.125rem"}}
											defaultChecked={answerParticipant.decision[ surveyChoice.code ]}
											onChange={( event ) => {
												console.log("EventSurvey.clickOnCheckBox code="+surveyChoice.code);
												this.surveyCtrl.setAnswer( answerParticipant, surveyChoice.code, event.target.checked );
												this.setState({ survey: this.surveyCtrl.getValue() });
												}} />)
							}
							return ( 
								<td style={{textAlign: "center"}}>
									{itemSquare}							
								</td>) } ) 
						}
					</tr>);
			}		
		}
			
		
		
		return (
			<div>
				<div class="row ">
					<div class="col-10">{survey.title}</div>
					{ userParticipant.isParticipant() && 
					 	<div class="col-2" style={{ float: "right" }}>
							<button  class="btn btn-primary btn-xs" 
								onClick={(event) => {
									console.log("EventItinerary.ClickOnButtonModify : ");
									this.setState( { show: { typeDisplay: DISPLAY_ADMIN}});
								}}>
							<Pencil   
								onClick={(event) => {
									console.log("EventSurvey : ClickOnPencilModify");
									this.setState( { show: { typeDisplay: DISPLAY_ADMIN}});
								}
								}/>
							</button>
						</div>
					}
				</div>
				
				<div class="row">
					<div class="col-12">
						<br/><TextArea value={survey.description} readOnly /><br/>
					</div>
				</div>
				
				<div class="row">
					<table>
						<tr>
							{headerList}
						</tr>
						{participantList}
					</table>
				</div>
			</div>);
	}
	

	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------
	/**
 	*/
	setAttribut(name, value, item) {
		console.log("EventSurvey.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		
		this.eventCtrl.setAttribut(name, value, item, surveyListConstant.NAMEENTITY+"/"+item.id);
	
	}
	setAttributCheckbox(name, value) {		
		console.log("EventSurvey.setAttributCheckbox set " + name + "<=" + value.target.checked);
		if (value.target.checked)
			this.state.event[name] = true;
		else
			this.state.event[name] = false;
		this.eventCtrl.setAttribut(name, this.state.event[name], this.state.event, "" );
		this.setState({ event: this.state.event });
	}	
	

	
	
	
	getTagState( survey ) {
		// console.log("EventSurvey.getTagState item.status="+survey.status);
		const intl = this.props.intl;
		
		const listOptions = [
			{ label: intl.formatMessage({id: "EventSurvey.InPreparation",defaultMessage: "In preparation"}),
			 value: surveyConstant.STATUS_INPREPAR,
			 type: "teal" },			
			{ label: intl.formatMessage({id: "EventSurvey.InProgress",defaultMessage: "In progress"}),
			 value:  surveyConstant.STATUS_OPEN,
			 type: "blue" },
			{ label: intl.formatMessage({id: "EventSurvey.Closed",defaultMessage: "closed"}),
			 value:  surveyConstant.STATUS_CLOSE,
			 type: "green" }
		];
		return (<TagDropdown listOptions={listOptions} value={survey.status} readWrite={true} 
				changeState={(value) => {
					this.surveyCtrl.setAttribut("status", value);
					this.setState( {survey: this.surveyCtrl.getValue()});
					}} />);		
	}
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------

	/**
	* calculoate the numbre of vote 
	 */
	
	getNumberOfVote( surveyCode ) {
		var survey = this.surveyCtrl.getValue();
		if (!survey)
			return 0;
			
		var total=0;
		for (var i in survey.answers) {
			var answerParticipant = survey.answers[ i ];
			if (answerParticipant.decision[ surveyCode ])
				total++;				
		}
		return total;
	}
	
}

export default injectIntl(EventSurvey);