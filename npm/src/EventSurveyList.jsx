/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl"; 


import { PlusCircle} from 'react-bootstrap-icons';

import FactoryService from './service/FactoryService';
import SlabRecord from './service/SlabRecord';
import EventSectionHeader from './component/EventSectionHeader';

import * as userFeedbackConstant from './component/UserFeedback';
import UserFeedback  from './component/UserFeedback';


import * as surveyConstant from './controller/SurveyCtrl';

import EventCtrl from './controller/EventCtrl';
import SurveyCtrl from './controller/SurveyCtrl';

import EventSurvey from './EventSurvey';


// -----------------------------------------------------------
//
// EventSurveyList
//
// Display one event
//
// -----------------------------------------------------------


export const NAMEENTITY = "surveylist";


class EventSurveyList extends React.Component {
	// this.props.updateEvent()
	constructor(props) {
		super();
		console.log("EventSurveyList.constructor");
		this.eventCtrl =  props.eventCtrl;

		// keep the event in the state 
		this.state = {
			event: this.eventCtrl.getEvent(),
			show: {
				showAll: true,
				showOnlyNonAnswered : false,
				showOnlyAnswered : false,
				showSurveyAdmin:false
			},
			operation: {
				inprogress: false,
				label:"",
				status:"",
				result:"",
				listlogevents: [] 
			}
		};
		// show : OFF, ON, COLLAPSE
		// console.log("EventSurveyList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.addItem 					= this.addItem.bind(this);
		this.addItemCallback			= this.addItemCallback.bind( this );		
		
	}
	
	// Calculate the state to display
	componentDidMount () {
		console.log("EventSurveyList.componentDidMount");
		if (this.eventCtrl.getSurveyList().length >0 ) {
			// current survey is the first one then
			let survey = this.eventCtrl.getSurveyList()[ 0 ];
			this.eventCtrl.setCurrentSurveyId( survey.id );
			
		}
	}
	
	
	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		console.log("EventSurveyList: render event="+JSON.stringify(this.state.event));
		var userParticipant = this.eventCtrl.getUserParticipant();
		var nbParticipants  = this.eventCtrl.getTotalParticipants();
		var headerSection =(
			<EventSectionHeader id="survey" 
				image="img/btnSurvey.png" 
				title={<FormattedMessage id="EventSurveyList.MainTitleSurveyList" defaultMessage="Surveys" />}
				showPlusButton  = {true}
				showPlusButtonTitle={<FormattedMessage id="EventSurveyList.AddSurvey" defaultMessage="Add a survey in the list" />}
				userTipsText={<FormattedMessage id="EventSurveyList.SurveyTip" defaultMessage="Participants prefer to visit an art or an aerospace museum? Prefer Japanase, Italian or French restaurant? Create a survey and collect review." />}
				addItemCallback={this.addItem}
				/>
				);

		var contentPage = (<div/>);
							
		if (this.state.event.surveylist.length === 0) {
			contentPage= (
				<div>
					<FormattedMessage id="EventSurveyList.NoItem" defaultMessage="You don't have any survey ready in the list." />
					{ userParticipant.isParticipant() && 
						<button class="btn btn-success btn-xs" 
							onClick={() => this.addItem()}
							title={intl.formatMessage({id: "EventSurveyList.addItem",defaultMessage: "Create a new survey in the list"})}>
							<PlusCircle onClick={() => this.addItem()} />
							<FormattedMessage id="EventSurveyList.AddOne" defaultMessage="Add one !" />
						</button>
					}
				</div>
				)
		}
		else { 
			var listSurveyHtml = [];
			
			listSurveyHtml.push( this.state.event.surveylist.map( (item, index) => {
				var classSurvey = "";
				var styleSurvey="";
				// color
				if (item.status === surveyConstant.STATUS_INPREPAR)
					classSurvey= "list-group-item list-group-item-dark"
				else if (item.status === surveyConstant.STATUS_OPEN)
					classSurvey= "list-group-item list-group-item-warning"
				else 			
					classSurvey= "list-group-item list-group-item-success";
				
				// Tab
				if (item.id.toString() === this.eventCtrl.getCurrentSurveyId().toString()) {					
					// classSurvey = classSurvey.concat(" active");
					styleSurvey = { borderTop: "2px solid black", borderLeft: "2px solid black", borderBottom: "2px solid black"};
				}
				else {
					styleSurvey = {borderRight:"2px solid black"};
				}
				return(
					<li class={classSurvey} style={styleSurvey}
						key={index}
						id={item.id}
						onClick={ (event) =>{
								console.log("EventSurveyList.click on "+event.target.id);
								this.eventCtrl.setCurrentSurveyId(event.target.id);
								// do a setState to force to redisplay the component -- maybe use the context ?
								this.setState({currentSurveyId: event.target.id}  );
								}} > 
						{item.name}&nbsp;						
						<span class="badge bg-primary rounded-pill">{item.answerlist.length + "/"+nbParticipants}</span>
					</li>
					) }
					)
			);
			listSurveyHtml.push(<li  style={{borderRight:"2px solid black", height: "40px"}}/>);
						
			
			var currentSurvey = this.eventCtrl.getCurrentSurvey();
			var surid=-1;
			if (currentSurvey) {
				console.log("EventSurveyList.currentSurveyd = "+currentSurvey.id);
				var surid=currentSurvey.id;
			 }
			contentPage= (
				<div class="row">
					<div class="col-2">
						<ul class="list-group">
							{listSurveyHtml}
						</ul>
					</div>
					<div class="col-10"> 
						<EventSurvey event={this.state.event} 
							eventCtrl={this.eventCtrl}
							forceUpdatefct={() => {
								console.log("EventSurveyList.forceUpdate");
								// forceUpdate is a React function
								this.forceUpdate()}}
							 />
					</div>
				</div>
			)
		}
		return (
			<div>
				{headerSection}
				<UserFeedback inprogress= {this.state.operation.inprogress}
					label= {this.state.operation.label}
					status= {this.state.operation.status}
					result= {this.state.operation.result}
					listlogevents= {this.state.operation.listlogevents} />
			
				{contentPage}<br/><br/>
			</div>
			
		)							
	}
	
		
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	
	setChildAttribut(name, value, item, localisation) {
		// console.log("EventSurveyList.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		const currentEvent = this.state.event;

		item[name] = value;

		var completeLocalisation = "/surveylist/"+survey.id+"/"+localisation;
		var survey = this.state.event.surveylist [ this.state.currentSurveyId ];

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ event: currentEvent });
		
		var SlabRecord = SlabRecord.getUpdate(this.state.event, name, value, completeLocalisation);
		this.props.updateEvent( SlabRecord );
	}

	setAttributeCheckbox(name, value) {
		let showPropertiesValue = this.state.show;
		// console.log("EventSurveyList.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ show: showPropertiesValue })
	}	
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------
	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	/**
   */
	addItem() {
		const intl = this.props.intl;

		console.log("EventSurvey.addItem: addItem item=" + JSON.stringify(this.state.event));
		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "EventSurvey.AddingTask",defaultMessage: "Adding a task"}), 
					listlogevents: [] }});
		// call the server to get an ID on this taskList		
		var surveyToAdd = SurveyCtrl.getDefaultSurvey();
		this.eventCtrl.addEventChildFct(NAMEENTITY, surveyToAdd, "", this.addItemCallback);
	}

	/**
	* addItemCallback
 	*/
	addItemCallback(httpPayload) {
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			// feedback to user is required
			console.log("EventSurvey.addItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
			console.log("EventSurvey.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.CantaddItem",defaultMessage: "A task can't be added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if ( ! (httpPayload.getData().childEntity && httpPayload.getData().childEntity.length>0) ) {
			currentOperation.status= userFeedbackConstant.ERRORCONTRACT;
			console.log("EventSurvey.addItemCallback:  BAD RECEPTION");

		} else {
			var surveyToAdd = httpPayload.getData().childEntity[ 0 ];
			var event = this.eventCtrl.getEvent();
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventSurvey.TaskAdded",defaultMessage: "A task is added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			console.log("EventSurvey.addItemCallback ");
			this.eventCtrl.addSurveyInEvent( surveyToAdd );
			this.setState({ event: event,show: { showSurveyAdmin : true} });
		}
		this.setState({operation: currentOperation});
	}

	
	
	
}

export default injectIntl(EventSurveyList);