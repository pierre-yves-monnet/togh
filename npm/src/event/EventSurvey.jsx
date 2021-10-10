// -----------------------------------------------------------
//
// EventSurvey
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl"; 

import { TextInput, TextArea } from 'carbon-components-react';
import { Pencil, Eye, DashCircle, PlusCircle, Check2Square, Square} from 'react-bootstrap-icons';

import TagDropdown 					from 'component/TagDropdown';
import * as userFeedbackConstant 	from 'component/UserFeedback';
import UserFeedback  				from 'component/UserFeedback';

import * as surveyConstant 			from 'controller/SurveyCtrl';

import FactoryService 				from 'service/FactoryService';

import * as surveyListConstant 		from 'event/EventSurveyList';


	

const DISPLAY_SURVEY = "SURVEY";
const DISPLAY_ADMIN= "ADMIN";
const DISPLAY_NOACCESS = "NOACCESS";




class EventSurvey extends React.Component {


	constructor(props) {
		super();
		this.state = {
			event: props.event,
			show: {
				typeDisplay: DISPLAY_SURVEY,
			},
			operation: {
				inProgress: false,
				label:"",
				status:"",
				result:"",
				listlogevents: [] 
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
		this.addChoice					= this.addChoice.bind( this );
		this.addChoiceCallback			= this.addChoiceCallback.bind( this );
		this.removeChoice				= this.removeChoice.bind( this );
		this.removeChoiceCallback		= this.removeChoiceCallback.bind( this );
		
		
		this.addAnswerWithMe 		= this.addAnswerWithMe.bind( this );
		this.addAnswerCallback 		= this.addAnswerCallback.bind( this );
		
	}
	componentDidMount() {
	    let bestDisplay= this.calculateBestDisplay();
	    this.setState( { show: { typeDisplay: bestDisplay}});
   		this.addAnswerWithMe();
	}
	// Calculate the state to display
	componentDidUpdate(prevProps) {
        //let valueProps=JSON.stringify(this.props);
        let prevPropsTypeDisplay='';
        if (prevProps && prevProps.show )
            prevPropsTypeDisplay=prevProps.show.typeDisplay;
	    let bestDisplay= this.calculateBestDisplay();
		console.log("EventSurvey.componentDidUpdate prevProps=("+prevPropsTypeDisplay+") typeDisplay=("+this.state.show.typeDisplay+") bestDisplay=("+bestDisplay+")");

        debugger;
        //if (typeof(prevProps) === "undefined" ) {
            // this.setState( { show: { typeDisplay: bestDisplay}});
        //} else if (typeof(prevProps.show) === "undefined") {
            // this.setState( { show: { typeDisplay: bestDisplay}});
        if (this.state.show.typeDisplay !== bestDisplay) {
            this.setState( { show: { typeDisplay: bestDisplay}});
        }
	}

	calculateBestDisplay() {
		// survey May be completed, set it again
		// this.setState( {survey: this.surveyEmbeded.getValue()});
		this.currentSurveyCtrl =  this.eventCtrl.getCurrentSurveyCtrl();

		this.userParticipant = this.eventCtrl.getUserParticipant();
		if ( this.currentSurveyCtrl.getStatus() === surveyConstant.STATUS_INPREPAR) {
			// so move to the ADMIN or the NOACCESS, depending of the user permission
			if (this.userParticipant.isParticipant()) 
				return DISPLAY_ADMIN;
			else
				return DISPLAY_NOACCESS;
		}
    	return DISPLAY_SURVEY;
	}
	
	
	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventSurvey: render survey");
		this.surveyCtrl = this.eventCtrl.getCurrentSurveyCtrl();

		if (! this.surveyCtrl ) {
			return (<div/>)
		}
	
		// refresh the current survey embedded
		// this.surveyEmbedded = new Survey( this.state.event, currentSurvey, this.userParticipant, this.updateEventfct);
		console.log("EventSurvey: typeDisplay=["+this.state.show.typeDisplay+"]");
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

		listChoiceHtml = survey.choicelist.map((item, index) =>
			<tr key={item.code}>
				<td> 
					<TextInput value={item.proptext}
					    id="proptext"
						onChange={(event) => {
							this.surveyCtrl.setChoiceValue("proptext", event.target.value, item);
							this.setState( {survey: this.surveyCtrl.getValue()});
					}}
					 labelText="" ></TextInput>
				</td>
				<td><button class="btn btn-danger btn-xs" 					 
					title={intl.formatMessage({id: "EventSurvey.removeItem",defaultMessage: "Remove this item"})}
					onClick={() => {
								this.removeChoice( item.code );
								this.setState( {survey: this.surveyCtrl.getValue()});															
						}} >
						<DashCircle onClick={() => {
								//this.surveyCtrl.removeChoice( item.code );
								//this.setState( {survey: this.surveyCtrl.getValue()});															
						}}  />
					</button>
				</td>
			</tr>
			);
		// add a + proposition
		return (<div>
					<div class="row">
						<div class="col-12">

							<UserFeedback inProgress= {this.state.operation.inProgress}
									label= {this.state.operation.label}
									status= {this.state.operation.status}
									result= {this.state.operation.result}
									listlogevents= {this.state.operation.listlogevents} />
						</div>
					</div>			
		
					<div class="row">
						<div class="col-2">
							{this.getTagState( survey )}
						</div>
						<div class="col-8">
							<TextInput value={survey.name}
							    id="name"
								onChange={(event) => {
									this.setAttribut("name", event.target.value,survey);			
									// console.log("EventSurvey:forceUpdate my parent");
									this.forceUpdatefct();
									}
								} 
								labelText={<FormattedMessage id="EventSurvey.Title" defaultMessage="Title" />} />								
						</div>
						<div class="col-2" style={{ float: "right" }}>
							<button  class="btn btn-primary btn-xs" 
								onClick={(event) => {
									console.log("EventItinerary.ClickOnButtonView : ");
									this.addAnswerWithMe();									
									this.setState( { show: { typeDisplay: DISPLAY_SURVEY}});
								}}>
							<Eye   
								onClick={(event) => {
									console.log("EventSurvey : ClickOnEyeView");
									this.addAnswerWithMe();
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
									this.setAttribut("description", event.target.value,survey);
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
											this.addChoice();
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
	 * RenderSurvey : Vote!
	 */
	renderSurvey () {
				
		var userParticipant = this.eventCtrl.getUserParticipant();

		var survey = this.surveyCtrl.getValue();
		var headerList = [];
		headerList.push(<th class="toghSectionHeader"></th>);
		for (let i in survey.choicelist) {
			headerList.push(
				<th class="toghSectionHeader" style={{textAlign: "center"}}>
					{survey.choicelist[ i ].proptext} ({this.getNumberOfVote(survey.choicelist[ i ].code)})
				</th> );
		}
		
		var participantList=[];
		if (survey.status === surveyConstant.STATUS_INPREPAR) {
			participantList.push(<tr><td><FormattedMessage id="EventSurvey.SurveyInPreparationNoParticipantsVisible" defaultMessage="This survey is in preparation. Participants are not visible" /></td></tr>)
		} else {
			
			// Checkbox can't be center		
			for (let i in survey[ surveyConstant.CHILD_ANSWER ]) {
				let answerParticipant = survey[surveyConstant.CHILD_ANSWER][i];
				
				participantList.push(<tr>
					<td> {this.eventCtrl.getParticipantName( answerParticipant.whoid) }</td>
					{ survey.choicelist.map( surveyChoice => 
						{ 
							let itemSquare=(<div/>);
							if (survey.status === surveyConstant.STATUS_CLOSE || answerParticipant.whoid !== userParticipant.getUser().id) {
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
												this.surveyCtrl.setDecision( answerParticipant, surveyChoice.code, event.target.checked );
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
				<div class="row">
					<div class="col-12">
						<UserFeedback inprogress= {this.state.operation.inProgress}
									label= {this.state.operation.label}
									status= {this.state.operation.status}
									result= {this.state.operation.result}
									listlogevents= {this.state.operation.listlogevents} />
					</div>
				</div>			

				<div class="row ">
					<div class="col-10">{survey.name}</div>
					{ userParticipant.isParticipant() && 
					 	<div class="col-2" style={{ float: "right" }}>
							<button  class="btn btn-primary btn-xs" 
								onClick={(event) => {
									console.log("EventItinerary.ClickOnButtonModify : ");
									this.setState( {show: {typeDisplay: DISPLAY_ADMIN}});
								}}>
							<Pencil   
								onClick={(event) => {
									console.log("EventSurvey : ClickOnPencilModify");
									this.setState( {show: {typeDisplay: DISPLAY_ADMIN}});
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
		let eventData = this.state.event;
		if (value.target.checked)
			eventData[name] = true;
		else
			eventData[name] = false;
		this.eventCtrl.setAttribut(name, eventData[name], eventData, "" );
		this.setState({ event: eventData });
	}	
	

	
	
	/**
	 * 
	 */
	getTagState( survey ) {
		console.log("EventSurvey.getTagState item.status="+survey.status);
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
	// Data operation
	// 
	// --------------------------------------------------------------

	/** ------------------------------------------------------
	*   Choice
	* we calculate a code in the list, this code is necessary for the answer (we prefer to not use the ID to reduce the dependency - not sur this is a good choice)
	* so, we tell the server to add the proposition, and we add it immediately for the moment.
	*/
	addChoice() {
		console.log("EventSurvey.addChoice !!");
		const intl = this.props.intl;

		var toolService = FactoryService.getInstance().getToolService();
		
		let survey = this.surveyCtrl.getValue();
		var uniqCode = toolService.getUniqueCodeInList( survey[ surveyConstant.CHILD_CHOICE ], "code");
		var choice = { code:uniqCode, proptext:''};
		
		this.setState({operation:{
					inProgress:true,
					label: intl.formatMessage({id: "EventSurvey.AddingChoice",defaultMessage: "Adding a choice"}), 
					listlogevents: [] }});
	
	
		this.eventCtrl.addEventChildFct(surveyConstant.CHILD_CHOICE, choice, "/surveylist/"+survey.id, this.addChoiceCallback);
	}
	
	
	/**
	 * AddChoiceCallback
	 */
	addChoiceCallback(httpPayload) {
		
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inProgress = false;
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			// feedback to user is required
			console.log("EventSurvey.addItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().limitsubscription) {
			console.log("EventTasklist.callbackdata: Limit Subscription");
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.LimitSubsscription",defaultMessage: "You reach the limit of choice allowed in the event. Go to your profile to see your subscription"})
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if (httpPayload.getData().status ==="ERROR") {
			console.log("EventSurvey.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.CantaddItem",defaultMessage: "A task can't be added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if ( ! (httpPayload.getData().childEntity && httpPayload.getData().childEntity.length>0) ) {
			currentOperation.status= userFeedbackConstant.ERRORCONTRACT;
			console.log("EventSurvey.addItemCallback:  BAD RECEPTION");

		} else {
			var choice = httpPayload.getData().childEntity[ 0 ];
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.TaskAdded",defaultMessage: "A task is added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			let survey = this.surveyCtrl.getValue();

			console.log("EventSurvey.addItemCallback ");
			survey.choicelist = survey.choicelist.concat( choice );
		}
		this.setState({operation: currentOperation});
	
	
	
		// nothing to do here for the moment
	}
	
	
	removeChoice( code ) {
		const intl = this.props.intl;
		console.log("EventSurvey.removeItem: event=" + JSON.stringify(this.state.event));

		this.setState({operation:{
					inProgress:true,
					label: intl.formatMessage({id: "EventSurvey.RemovingChoice",defaultMessage: "Removing a choice"}), 
					listlogevents: [] }});
	
		let survey = this.surveyCtrl.getValue();

		let choiceitem = survey[ surveyConstant.CHILD_CHOICE ].find( (index) => index.code === code );
		
		this.eventCtrl.removeEventChild(surveyConstant.CHILD_CHOICE, choiceitem.id, "/surveylist/"+survey.id, this.removeChoiceCallback);

	}

	removeChoiceCallback( httpPayload) {
		const intl = this.props.intl;
		let currentOperation = this.state.operation;
		currentOperation.inProgress = false;
		
		// find the task item to delete
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;			
			console.log("Eventitinerary.addTaskCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
				console.log("Eventitinerary.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
				currentOperation.status= userFeedbackConstant.ERROR;
				currentOperation.result=intl.formatMessage({id: "Eventitinerary.CantremoveItem",defaultMessage: "The step can't be removed"});
				currentOperation.listlogevent = httpPayload.getData().listLogEvents;

		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "Eventitinerary.StepRemoved",defaultMessage: "The step is removed"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			var currentEvent = this.state.event;
			let survey = this.surveyCtrl.getValue();
	
			let childId = httpPayload.getData().childEntityId[ 0 ];
			const newChoices = survey[ surveyConstant.CHILD_CHOICE ].filter((index) => index.id !== childId);
			survey[ surveyConstant.CHILD_CHOICE ] = newChoices;
			/*
			for( var i in survey[ surveyConstant.CHILD_CHOICE ]) {
				if ( survey[ surveyConstant.CHILD_CHOICE ] [ i ].id === childId) {
					survey[ surveyConstant.CHILD_CHOICE ].splice( survey[ surveyConstant.CHILD_CHOICE ] [ i ], 1);
					break;
				}
			}
			*/
			this.setState({ event: currentEvent });
		}
		
		this.setState({ operation: currentOperation});

	}
	
	
	// --------------------------------------------------------------
	// 
	// Add survey answer
	// 
	// --------------------------------------------------------------

	addAnswerWithMe() {
		const intl = this.props.intl;

		let survey = this.surveyCtrl.getValue();

		if (survey[ surveyConstant.CHILD_ANSWER ] === null) {
			survey[ surveyConstant.CHILD_ANSWER ] = [];
		}
		
		let currentUser = this.userParticipant.getUser();
		// We don't have a current user ? Strange, we don't want to add anything. Should be an internal view
		if (! currentUser) 
			return;
		// Ok, I must be part on this survey, ins't ?
		for (let i in survey[surveyConstant.CHILD_ANSWER]) {
			if (survey[surveyConstant.CHILD_ANSWER][i].whoid === currentUser.id) {
				// I'm in !
				return;
			}
		}
		// so, add me
		this.setState({operation:{
					inProgress:true,
					label: intl.formatMessage({id: "EventSurvey.AddAnswer",defaultMessage: "Adding your answer"}), 
					listlogevents: [] }});

		var addSurveyParticipant = { whoid : this.userParticipant.getUser().id};
												
		this.eventCtrl.addEventChildFct(surveyConstant.CHILD_ANSWER, addSurveyParticipant, "/surveylist/"+survey.id, this.addAnswerCallback);
	}

	addAnswerCallback(httpPayload) {
		
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inProgress = false;
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			// feedback to user is required
			console.log("EventSurvey.addItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
			console.log("EventSurvey.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.CantaddAnswer",defaultMessage: "Can't add the answer"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if ( ! (httpPayload.getData().childEntity && httpPayload.getData().childEntity.length>0) ) {
			currentOperation.status= userFeedbackConstant.ERRORCONTRACT;
			console.log("EventSurvey.addItemCallback:  BAD RECEPTION");

		} else {
			var answerParticipant = httpPayload.getData().childEntity[ 0 ];
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.AnswerAdded",defaultMessage: "answer added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
			
			if (! answerParticipant.decision )
				answerParticipant.decision={};
				
			let survey = this.surveyCtrl.getValue();
			var newlist = survey[ surveyConstant.CHILD_ANSWER ].concat( answerParticipant );
			survey[ surveyConstant.CHILD_ANSWER ] = newlist;

			console.log("EventSurvey.addAnswerCallback ");
		}
		this.setState({operation: currentOperation});
	
	
	
		// nothing to do here for the moment
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
		let survey = this.surveyCtrl.getValue();
		if (!survey)
			return 0;
			
		let total=0;
		for (let i in survey[ surveyConstant.CHILD_ANSWER]) {
			let answerParticipant = survey[ surveyConstant.CHILD_ANSWER][i];
			if (answerParticipant.decision[ surveyCode ])
				total++;				
		}
		return total;
	}
	
}

export default injectIntl(EventSurvey);