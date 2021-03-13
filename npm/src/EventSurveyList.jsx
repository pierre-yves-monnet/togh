// -----------------------------------------------------------
//
// EventSurveyList
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl"; 


import { PlusCircle} from 'react-bootstrap-icons';

import UserTips from './component/UserTips';
import FactoryService from './service/FactoryService';

import SlabEvent from './service/SlabEvent';
import EventSectionHeader from './component/EventSectionHeader';


import * as surveyConstant from './controller/SurveyCtrl';
import EventSurvey from './EventSurvey';
import EventCtrl from './controller/EventCtrl';
import SurveyCtrl from './controller/SurveyCtrl';

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
			}
		};
		// show : OFF, ON, COLLAPSE
		// console.log("EventSurveyList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.addSurvey 					= this.addSurvey.bind(this);
		this.addSurveyCallback			= this.addSurveyCallback.bind( this );		
		
	}
	
	// Calculate the state to display
	componentDidMount () {
		console.log("EventSurveyList.componentDidMount");
		if (this.eventCtrl.getSurveyList().length >0 ) {
			// current survey is the first one then
			this.eventCtrl.setCurrentSurveyId( this.getSurveyList()[ 0 ].id );
			
		}
	}
	
	
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
				addItemCallback={this.addSurvey}
				/>
				);

		var contentPage = (<div/>);
							
		if (this.state.event.surveylist.length === 0) {
			contentPage= (
				<div>
					<FormattedMessage id="EventSurveyList.NoItem" defaultMessage="You don't have any survey ready in the list." />
					{ userParticipant.isParticipant() && 
						<button class="btn btn-success btn-xs" 
							onClick={() => this.addSurvey()}
							title={intl.formatMessage({id: "EventSurveyList.addItem",defaultMessage: "Create a new survey in the list"})}>
							<PlusCircle onClick={() => this.addSurvey()} />
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
				if (item.status === surveyConstant.STATUS_INPREPARATION)
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
						{item.title}&nbsp;						
						<span class="badge bg-primary rounded-pill">{item.answers.length + "/"+nbParticipants}</span>
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
								this.forceUpdate()}}
							 />
					</div>
				</div>
			)
		}
		return (
			<div>
				{headerSection}
				{contentPage}<br/><br/>
			</div>
			
		)							
	}
	
	
	
	setChildAttribut(name, value, item, localisation) {
		// console.log("EventSurveyList.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		const currentEvent = this.state.event;

		item[name] = value;

		var completeLocalisation = "/surveylist/"+survey.id+"/"+localisation;
		var survey = this.state.event.surveylist [ this.state.currentSurveyId ];

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ event: currentEvent });
		
		var slabEvent = SlabEvent.getUpdate(this.state.event, name, value, completeLocalisation);
		this.props.updateEvent( slabEvent );
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
	
	addSurvey() {
		console.log("EventSurveyList.addSurvey" );
		
		var surveyToAdd = SurveyCtrl.getDefaultSurvey();
		
		// call the server to get an ID on this survey
 		this.eventCtrl.addEventChildFct( "surveylist", surveyToAdd, "/", this.addSurveyCallback);
	}
	
	addSurveyCallback(httpPayload) {
		console.log("EventSurveyList.addSurveyCallback ");
		if (httpPayload.isError()) {
				// feedback to user is required
				console.log("EventSurveyList.addSurveyCallback: ERROR ");
		} else {
			var surveyToAdd = httpPayload.getData().child;
			this.eventCtrl.addSurveyInEvent( surveyToAdd );
			this.setState( { event: this.eventCtrl.getEvent(), show: { showSurveyAdmin : true} });
		}
	}
	
	
	
}

export default injectIntl(EventSurveyList);