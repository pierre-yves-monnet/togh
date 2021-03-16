// -----------------------------------------------------------
//
// EventsList
//
// HomeEvents page.Display the list of events
//
// -----------------------------------------------------------

import React from 'react';

import { FormattedMessage } from "react-intl";
import { DropletFill, PlusCircle,ArrowRepeat,ClipboardData,PersonCircle } from 'react-bootstrap-icons';


import FactoryService from './service/FactoryService';

import EventState from './EventState';

class EventsList extends React.Component {

	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.homeSelectEvent( event.id)
	constructor(props) {
		super();
		this.state = {}
		console.log("EventsList.constructor");
		this.state = { filterEvents: "", "message": "", 
					events: [] };
		// this is mandatory to have access to the variable in the method... thank you React!   

		this.createEvent = this.createEvent.bind(this);
		this.createEventCallback = this.createEventCallback.bind(this);
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
		console.log("EventList.render listEvents " + JSON.stringify(this.state.events));
		// no map read, return
		var listEventsHtml = [];
		// <button class="glyphicon glyphicon glyphicon-tint" title={<FormattedMessage id="EventList.AccessThisEvent" defaultMessage="Access this event" />}></button>
		if (this.state.events) {
			listEventsHtml = this.state.events.map((event,index) =>
				<tr onClick={() => this.props.homeSelectEvent(event.id)} class="itemcontent" key={index}>
					<td><button title={<FormattedMessage id="EventList.AccessThisEvent" defaultMessage="Access this event" />}>
							<DropletFill/>
						</button></td>
					<td>
						<EventState statusEvent={event.statusEvent} modifyEvent={false} />
					</td>
					<td>{event.name}</td>
					<td>{event.dateevent}</td>
				</tr>
			);
		}
		return (
			<div class="container-fluid">
				<div class="row">
					<div class="col"><h1>Events</h1></div>
					<div class="col"><div style={{ float: "right" }}>
						<button class="btn btn-info btn-lg" onClick={this.createEvent}>
							<PlusCircle/> &nbsp;<FormattedMessage id="EventList.CreateAnEvent" defaultMessage="Create an Event"/></button>
					</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm">
						<div class="btn-group" role="group" style={{ padding: "10px 10px 10px 10px" }}>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={this.refreshListEvents}><ArrowRepeat/><FormattedMessage id="EventList.Refresh" defaultMessage="Refresh"/></button>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}><ClipboardData/> <FormattedMessage id="EventList.AllEvents" defaultMessage="All events"/></button>
							<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}><PersonCircle/> <FormattedMessage id="EventList.MyEvents" defaultMessage="My events"/></button>
						</div>
					</div>
				</div>
				<div class="row">
					<table class="table table-striped toghtable">
						<thead>
						<tr>
							<th></th>
							<th colSpan="2"><FormattedMessage id="EventList.Name" defaultMessage="Name"/></th>
							<th><FormattedMessage id="EventList.Dae" defaultMessage="Date"/></th>
							<th><FormattedMessage id="EventList.Participants" defaultMessage="Participants"/></th>
						</tr>
						</thead>
						{listEventsHtml}
					</table>
				</div>
			</div>)
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

		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/event/create', this, {}, httpPayload => {
			httpPayload.trace("EventList.createEventCallback");
			if (httpPayload.isError()) {
				this.setState({ "message": "Server connection error"});
			}
			else if (httpPayload.getData().eventid) {
				this.props.homeSelectEvent(httpPayload.data.eventid)
			} else {
				this.setState({ "message": httpPayload.data.message });
			}
		});
	}

	createEventCallback(httpPayload) {
		console.log("EventList.createEventCallback payload=");
		if (httpPayload.eventid) {
			this.props.selectEvent(httpPayload.eventid)
		} else {
			this.setState({ "message": httpPayload.message });
		}

	}

	// ----------- refresh list event
	refreshListEvents() {
		console.log("EventsList.refreshListEvents http[event/list?filterEvents=" + this.state.filterEvents + "]");
		this.setState({ events: [] });


		var restCallService = FactoryService.getInstance().getRestcallService();

		restCallService.getJson('/api/event/list?filterEvents=' + this.state.filterEvents, this, httpPayload => {
			httpPayload.trace("EventsList.refreshListEventsCallback");
			this.setState({ events: httpPayload.getData().events });
		});

	}

}

export default EventsList;