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
import { Pencil, Eye, DashCircle, PlusCircle} from 'react-bootstrap-icons';


import UserTips from './component/UserTips';
import TagDropdown from './component/TagDropdown';
import UserParticipant from './entity/UserParticipant';
import Survey from './entity/Survey';
import * as surveyConstant from './entity/Survey';

import FactoryService from './service/FactoryService';


	

const DISPLAY_SURVEY = "SURVEY";
const DISPLAY_ADMIN= "ADMIN";
const DISPLAY_NOACCESS = "NOACCESS";

class EventSurvey extends React.Component {
	// this.props.updateEvent()
	constructor(props) {
		super();
		this.state = {
			event: props.event,
			survey : props.survey, 
			show: {
				typeDisplay: DISPLAY_SURVEY,
			}
		};
		console.log("EventSurvey.constructor survey="+JSON.stringify(props.survey));

		var userParticipant = props.getUserParticipant();
		this.surveyEmbeded = new Survey( props.event, props.survey, userParticipant, props.updateEvent);
		
		// show : OFF, ON, COLLAPSE
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.renderSurveyAdmin			= this.renderSurveyAdmin.bind( this );
		this.renderSurvey				= this.renderSurvey.bind( this );
		this.getTagState				= this.getTagState.bind(this);
		
	}
	// Calculate the state to display
	componentDidMount () {
		console.log("EventSurvey.componentDidMount surveyEmbeded="+JSON.stringify(this.surveyEmbeded.getValue()));
		// survey May be completed, set it again	
		this.setState( {survey: this.surveyEmbeded.getValue()});
		
		var userParticipant = this.props.getUserParticipant();
		if ( this.surveyEmbeded.getStatus() === surveyConstant.STATUS_INPREPARATION) {
			// so move to the ADMIN or the NOACCESS, depending of the user permission
			if (userParticipant.isParticipant()) 
				this.setState( { show: { typeDisplay: DISPLAY_ADMIN}});
			else
				this.setState( { show: { typeDisplay: DISPLAY_NOACCESS}});
		}
		else
			this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
	}
	
	
	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventSurvey: render");
		if (! this.state.survey) {
			return (<div/>)
		}
		
		if (this.state.show.typeDisplay === DISPLAY_NOACCESS) {
			return (
				<div>
					<div class="row toghSectionHeader">{this.state.survey.title}</div>
					<div><FormattedMessage id="EventSurvey.NoAccessForSurveyInPreparation" defaultMessage="This survey is in preparation. You can't access it for the moment." /></div>
				</div>
			);
		}	
		if (this.state.show.typeDisplay === DISPLAY_ADMIN) 
			return this.renderSurveyAdmin();
		else
			return this.renderSurvey();
	}
	
	/**
	 */
	renderSurveyAdmin() {
		const intl = this.props.intl;

		var toolService = FactoryService.getInstance().getToolService();
		
		var listChoiceHtml = [];
		
		listChoiceHtml = this.surveyEmbeded.getValue().choices.map((item, index) =>
			<tr key={item.code}>
				<td> 
					<TextInput value={item.propositiontext} 
						onChange={(event) => {
						this.surveyEmbeded.setChoiceValue("propositiontext", event.target.value, item);
						this.setState( {survey: this.surveyEmbeded.getValue()});
					}}
					 labelText="" ></TextInput>
				</td>
				<td><button class="btn btn-danger btn-xs" 					 
					title={intl.formatMessage({id: "EventSurvey.removeItem",defaultMessage: "Remove this item"})}>
						<DashCircle onClick={() => {
								this.surveyEmbeded.removeChoice( item.code );
								this.setState( {survey: this.surveyEmbeded.getValue()});															
						}}  />
					</button>
				</td>
			</tr>
			);
		// add a + proposition
		return (<div>
					<div class="row">
						<div class="col-2">
							{this.getTagState( this.surveyEmbeded.getValue() )}
						</div>
						<div class="col-8">
							<TextInput value={this.state.survey.title} 
								onChange={(event) => {
									this.surveyEmbeded.setAttribut("title", event.target.value);									
									this.setState({ survey: this.surveyEmbeded.getValue() });
									}
								} 
								labelText={<FormattedMessage id="EventSurvey.Title" defaultMessage="Title" />} />
						</div>
						<div class="col-2" style={{ float: "right" }}>
							<button  class="btn btn-primary btn-xs" 
								onClick={(event) => {
									console.log("EventItinerary.ClickOnButtonView : ");
									this.surveyEmbeded.completeSurveyWithMe();									
									this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
								}}>
							<Eye   
								onClick={(event) => {
									console.log("EventSurvey : ClickOnEyeView");
									this.surveyEmbeded.completeSurveyWithMe();
									this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
								}
								}/>
							</button>
						</div> 
					</div>
					<div class="row">
						<div class="col-8">
							<TextArea value={this.state.survey.description} 
								onChange={(event) => {
									this.surveyEmbeded.setAttribut("description", event.target.value);
									this.setState({ survey: this.surveyEmbeded.getValue() });
								}}  
								labelText={<FormattedMessage id="EventSurvey.Description" defaultMessage="Description" />} />
						</div>
						<div class="col-4">
							<table>
							<tr>
								<th><FormattedMessage id="EventSurvey.Option" defaultMessage="Option" /></th>
								<th><button class="btn btn-success btn-xs" 					 
									title={intl.formatMessage({id: "EventSurvey.AddOption",defaultMessage: "Option"})}>
									<PlusCircle onClick={() => { 
											this.surveyEmbeded.addChoice();
											this.setState({ survey: this.surveyEmbeded.getValue() });
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
				
		var userParticipant = this.props.getUserParticipant();
		var survey =this.surveyEmbeded.getValue();

		var headerList = [];
		headerList.push(<th class="toghSectionHeader"></th>);
		for (var i in survey.choices) {
			headerList.push(
				<th class="toghSectionHeader" style={{textAlign: "center"}}>
					{survey.choices[ i ].propositiontext}
				</th> );
		}
		
		var participantList=[];
		
		if (survey.state === surveyConstant.STATUS_INPREPARATION) {
			participantList.push(<tr><td><FormattedMessage id="EventSurvey.SurveyInPreparationNoParticipantsVisible" defaultMessage="This survey is in preparation. Participants are not visible" /></td></tr>)
		} else {
		
			for (var i in survey.answers) {
				var answerParticipant = survey.answers[ i ];
				var participantAnswer = [];
				for (var j in survey.choices) {
					var surveyChoice = survey.choices[ j ]
					participantAnswer.push(<td style={{textAlign: "center"}}>
						<center>
						<Checkbox defaultChecked={answerParticipant[ surveyChoice.code ]}
							readOnly={survey.state === surveyConstant.STATUS_CLOSE} 
							onChange={(value,event ) => {
								this.surveyEmbeded.setAnswer( answerParticipant, surveyChoice, value );
								this.setState({ survey: this.surveyEmbeded.getValue() });
								}} />
							</center>
						</td>);
				}
				participantList.push(<tr>
					<td> {answerParticipant.username}</td>
					{participantAnswer}
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
	


	setAttributeCheckbox(name, value) {
		let showPropertiesValue = this.state.show;
		console.log("EventSurvey.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ show: showPropertiesValue })
	}	
	
	
	getTagState( survey ) {
		console.log("EventSurvey.getTagState item.status="+survey.status);
		const intl = this.props.intl;
		
		const listOptions = [
			{ label: intl.formatMessage({id: "EventSurvey.InPreparation",defaultMessage: "In preparation"}),
			 value: surveyConstant.STATUS_INPREPARATION,
			 type: "tear" },			
			{ label: intl.formatMessage({id: "EventSurvey.InProgress",defaultMessage: "In progress"}),
			 value:  surveyConstant.STATUS_OPEN,
			 type: "blue" },
			{ label: intl.formatMessage({id: "EventSurvey.Closed",defaultMessage: "closed"}),
			 value:  surveyConstant.STATUS_CLOSE,
			 type: "green" }
		];
		return (<TagDropdown listOptions={listOptions} value={survey.status} readWrite={true} 
				changeState={(value) => {
					this.surveyEmbeded.setAttribut("status", value);
					this.setState( {survey: this.surveyEmbeded.getValue()});
					}} />);		
	}
	
}

export default injectIntl(EventSurvey);