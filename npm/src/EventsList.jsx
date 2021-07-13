// -----------------------------------------------------------
//
// EventsList
//
// HomeEvents page.Display the list of events
//
// -----------------------------------------------------------

import React from 'react';

import { injectIntl, FormattedMessage,FormattedDate } from "react-intl";

import { PlusCircle,ArrowRepeat,ClipboardData,PersonCircle } from 'react-bootstrap-icons';


import FactoryService from 'service/FactoryService';

import EventState from 'event/EventState';
import * as userFeedbackConstant from 'component/UserFeedback';
import UserFeedback  from 'component/UserFeedback';

class EventsList extends React.Component {

	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.homeSelectEvent( event.id)
	constructor(props) {
		super();
		this.state = {}
		console.log("EventsList.constructor");
		this.state = { filterEvents: "", "message": "", 
					events: [],
					operation: {
						inprogress: false,
						label:"",
						status:"",
						result:"",
						listlogevents: [] 
					} };
		// this is mandatory to have access to the variable in the method... thank you React!   

		this.createEvent = this.createEvent.bind(this);
		this.refreshListEvents = this.refreshListEvents.bind(this);


		console.log("EventsList.constructor: END");
	}
	componentDidMount () {
		console.log("EventsList.componentWillMount: BEGIN");
		this.refreshListEvents(); 	
		console.log("EventsList.componentWillMount: END");
	}
	
	
	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// -------------------------------------------- render
	render() {
		console.log("EventList.render listEvents=" + JSON.stringify(this.state.events));
		const intl = this.props.intl;

		// no map read, return
		var listEventsHtml = [];
		// <button class="glyphicon glyphicon glyphicon-tint" title={<FormattedMessage id="EventsList.AccessThisEvent" defaultMessage="Access this event" />}></button>
		
		if (this.state.events) {
			listEventsHtml = this.state.events.map((event,index) =>
				<tr onClick={() => this.props.homeSelectEvent(event.id)} 
				 key={index}>
					<td >
						<img src="img/toghEvent.jpg" style={{ width: 60 }}     />
					</td>
					<td style={{verticalAlign: "middle"}}>
						<EventState statusEvent={event.statusEvent} modifyEvent={false} />
					</td>
					<td style={{verticalAlign: "middle"}}>{event.name}</td>
					<td style={{verticalAlign: "middle"}}>
						{event.datePolicy === 'ONEDATE' && event.dateEvent && <div><FormattedDate value={new Date(event.dateEvent)}/></div>}
						
						{ event.datePolicy === 'PERIOD' && event.dateStartEvent && 
							<FormattedDate value={new Date(event.dateStartEvent)} />
						}
						{ event.datePolicy === 'PERIOD' && <span>
								&nbsp;
								<FormattedMessage id="EventsList.DateFromTo" defaultMessage="to"/>
								&nbsp;
								</span>
						}
						{ event.datePolicy === 'PERIOD' && event.dateEndEvent && 
							<FormattedDate value={new Date(event.dateEndEvent)} />
						}
					</td>
					<td  style={{verticalAlign: "middle",fontSize: "12px"}}>{event.listParticipants}</td>
				</tr>
			);
		}
		return (
			<div class="container-fluid">
				<div class="row">
					<div class="col"><h1>Events</h1></div>
					<div class="col"><div style={{ float: "right" }}>
						<button class="btn btn-info btn-lg" onClick={this.createEvent}>
							<PlusCircle/> &nbsp;<FormattedMessage id="EventsList.CreateAnEvent" defaultMessage="Create an Event"/></button>
						<br/>
						<div style={{color: "red"}}> {this.state.message}</div>
					</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm">
						<div class="btn-group" role="group" style={{ padding: "10px 10px 10px 10px" }}>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={this.refreshListEvents}><ArrowRepeat/><FormattedMessage id="EventsList.Refresh" defaultMessage="Refresh"/></button>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}><ClipboardData/> <FormattedMessage id="EventsList.AllEvents" defaultMessage="All events"/></button>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}><PersonCircle/> <FormattedMessage id="EventsList.MyEvents" defaultMessage="My events"/></button>
						</div>
					</div>
				</div>
				<UserFeedback inprogress= {this.state.operation.inprogress}
							label= {this.state.operation.label}
							status= {this.state.operation.status}
							result= {this.state.operation.result}
							listlogevents= {this.state.operation.listlogevents} />
					
				<div class="row">
					<table class="toghtable" style={{padding: ".5rem .5rem", 
							borderBottomWidth: "1px", 
							boxShadow: "inset 0 0 0 9999px var(--bs-table-accent-bg)",
							borderBottomColor: "currentColor"}}>
						<thead>
						<tr >
							<th colSpan="2"></th>
							<th style={{padding: ".5rem .5rem"}}><FormattedMessage id="EventsList.Name" defaultMessage="Name"/></th>
							<th><FormattedMessage id="EventsList.Date" defaultMessage="Date"/></th>
							<th><FormattedMessage id="EventsList.Participants" defaultMessage="Participants"/></th>
						</tr>
						</thead>
						{listEventsHtml}
					</table>
				</div>
			</div>)
			// class="table table-striped toghtable"
	}

	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	// -------------------------------------------- Call REST
	createEvent() {
		console.log("EventsList.createEvent: http[event/create?]");
		const intl = this.props.intl;

		this.setState({ message :  ""});
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/event/create', this, {name:"new event"}, httpPayload => {
			httpPayload.trace("EventList.createEventCallback");

			if (httpPayload.isError()) {
				this.setState({ message: intl.formatMessage({id: "EventsList.ServerConnectionError",defaultMessage: "Server connection error"}) });
			}
			else if (httpPayload.getData().eventId) {
				this.props.homeSelectEvent(httpPayload.getData().eventId)
			} else if (httpPayload.getData().limitsubscription){
				this.setState({ message: intl.formatMessage({id: "EventsList.LimitSubscription",defaultMessage: "You reach the limit of events allowed in the last period. Go to your profile to see your subscription"}) });
			} else { 
				this.setState({ message: httpPayload.getData().message });
			}
		});
	}

	

	// ----------- refresh list event
	refreshListEvents() {
		console.log("EventsList.refreshListEvents http[event/list?filterEvents=" + this.state.filterEvents + "]");
		this.setState({ events: [] });
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson('/api/event/list?withParticipants=true&filterEvents=' + this.state.filterEvents, this, this.refreshListEventsCallback );
	}
	
	refreshListEventsCallback( httpPayload) {
		httpPayload.trace("EventsList.refreshListEventsCallback");
		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;
		const intl = this.props.intl;
			
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;			
			console.log("EventItinerary.addItemCallback: ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
				console.log("EventsList.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
				currentOperation.status= userFeedbackConstant.ERROR;
				currentOperation.result=intl.formatMessage({id: "EventsList.CantGetListItem",defaultMessage: "Can't access the list of item'"});
				currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result="";
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			this.setState({ events: httpPayload.getData().events });
		}


	}

}

export default injectIntl(EventsList);