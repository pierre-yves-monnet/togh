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
import SlabEvent from './service/SlabEvent';


import * as surveyConstant from './entity/Survey';
import EventSurvey from './EventSurvey';
import Survey from './entity/Survey';

class EventSurveyList extends React.Component {
	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {
			event: props.event,
			currentSurveyId : null, 
			show: {
				showAll: true,
				showOnlyNonAnswered : false,
				showOnlyAnswered : false,
				showSurveyAdmin:false
			}
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.addSurvey 					= this.addSurvey.bind(this);
		this.getCurrentSurvey			= this.getCurrentSurvey.bind( this );		
		
	}
	
// Calculate the state to display
	componentDidMount () {
		if (this.state.event.surveylist.length >0 ) {
			// curreent survey is the first one then
			this.setState( { currentSurveyId : this.state.event.surveylist[ 0 ].id });
		}
	}
	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		console.log("EventSurveyList: render");
		var userParticipant = this.props.getUserParticipant();
		
		var headerSection =(
			<div>	
				<div class="eventsection">
					<div style={{ float: "left" }}>
						<img style={{ "float": "right" }} src="img/btnSurvey.png" style={{ width: 100 }} /><br />
					</div>
					<FormattedMessage id="EventSurveyList.MainTitleSurveyList" defaultMessage="Survey" />
					<div style={{ float: "right" }}>
						<button class="btn btn-success btn-xs " 
							 title={<FormattedMessage id="EventTaskList.AddSurvey" defaultMessage="Add a survey in the list" />}>
							<PlusCircle onClick={() => this.addSurvey()} />
						</button>
					</div>
				</div>			
				<UserTips id="survey" text={<FormattedMessage id="EventSurveyList.SurveyTip" defaultMessage="Participants prefer to visit an art or an aerospace museum? Prefer Japanase, Italian or French restaurant? Create a survey and collect review." />}/>
			</div> 
			)
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
			
			
			for (var i in this.state.event.surveylist ) {
				var item = this.state.event.surveylist[ i ];
				var classSurvey = "";
				var styleSurvey="";
				// color
				if (item.status === surveyConstant.STATUS_INPREPARATION)
					classSurvey= "list-group-item list-group-item-dark"
				else if (item.status === surveyConstant.STATUS_OPEN)
					classSurvey= "list-group-item list-group-item-primary"
				else 			
					classSurvey= "list-group-item list-group-item-info";
				
				// Tab
				if (item.id === this.state.currentSurveyId) {					
					// classSurvey = classSurvey.concat(" active");
					styleSurvey = { borderTop: "4px solid black", borderLeft: "4px solid black", borderBottom: "4px solid black"};
				}
				else {
					styleSurvey = {borderRight:"4px solid black"};
				}
				
				listSurveyHtml.push( 
					<li class={classSurvey} style={styleSurvey}
						id={item.id}
						onClick={ (event) =>{console.log("EventSurveyList.click on "+event.target.value);}} > 
						{item.title}&nbsp;						
						<span class="badge bg-primary rounded-pill">{item.answers.length + "/14"}</span>
					</li>
					);
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
							survey={this.getCurrentSurvey() } 
							getUserParticipant={this.props.getUserParticipant}
							updateEvent={(slabEvent) => {
								// My child say something change. So update myself
								var currentSurvey = this.getCurrentSurvey();
								console.log("EventSurveyList.EventSurverCallback : my currentServey is " + JSON.stringify( currentSurvey ));

								// Please refresh myself
								this.setState( { survey: currentSurvey });
								this.props.updateEvent( slabEvent);
							}} />
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
		console.log("EventSurveyList.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
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
		console.log("EventSurveyList.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ show: showPropertiesValue })
	}	
	
	addSurvey() {
		console.log("EventSurveyList.addSurvey: addItem " );
		var currentEvent = this.state.event;
		var surveyToAdd = Survey.getDefaultSurvey();
		
		// call the server to get an ID on this survey
		var slabEvent = SlabEvent.getAddList(this.state.event, "surveylist", surveyToAdd, "/");
 		this.props.updateEvent( slabEvent );
	
	
		surveyToAdd.id = new Date().getTime();
		this.setState( {currentSurveyId: surveyToAdd.id});
		
		const newList = currentEvent.surveylist.concat( surveyToAdd );
		
		currentEvent.surveylist = newList;
		this.setState({ event: currentEvent, show: { showSurveyAdmin : true} });
		
		
		this.props.updateEvent( slabEvent );
	}
	
	/**
	* return the current survey choose by the user  */
	getCurrentSurvey() {
		for (var i in this.state.event.surveylist) {
			if (this.state.event.surveylist[ i ].id ===  this.state.currentSurveyId)
				return this.state.event.surveylist[ i ];
		}
		return null;
	}
	
}

export default injectIntl(EventSurveyList);