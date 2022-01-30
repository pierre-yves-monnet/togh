// -----------------------------------------------------------
//
// EventsList
//
// HomeEvents page.Display the list of events
//
// -----------------------------------------------------------

import React from 'react';

import { injectIntl, FormattedMessage,FormattedDate } from "react-intl";

import { PlusCircle,ArrowRepeat,ClipboardData, EnvelopeOpen, Calendar2Check, CalendarWeek } from 'react-bootstrap-icons';


import FactoryService               from 'service/FactoryService';
import MobileService                from 'service/MobileService.jsx';
import EventState                   from 'event/EventState';
import * as userFeedbackConstant    from 'component/UserFeedback';
import UserFeedback                 from 'component/UserFeedback';

import UserTips 		            from 'component/UserTips';


export const FILTER_EVENT = {
        NEXTEVENTS: "NextEvents",
		MYEVENTS: "MyEvents",
		ALLEVENTS : "AllEvents",
		MYINVITATIONS: "MyInvitations"
	}

class EventsList extends React.Component {

	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.homeSelectEvent( event.id)
	constructor(props) {
		super();

		console.log("EventsList.constructor");
		this.state = { filterEvents: props.filterEvents,
		            titleFrame : props.titleFrame,
		            message: "",
					events: [],
					operation: {
						inprogress: false,
						label:"",
						status:"",
						result:"",
						listlogevents: []
					} };
		if (! this.state.filterEvents)
		    this.state.filterEvents=FILTER_EVENT.NEXTEVENTS;
		if (this.state.titleFrame === 'MYINVITATIONS')
		    this.state.filterEvents=FILTER_EVENT.MYINVITATIONS;

		// this is mandatory to have access to the variable in the method... thank you React!   

		this.createEvent = this.createEvent.bind(this);
		this.refreshListEvents = this.refreshListEvents.bind(this);

		console.log("EventsList.constructor: END");
	}

	componentDidMount () {
		console.log("EventsList.componentDidMount: BEGIN");
        let filter;


        if (this.state.titleFrame === 'MYINVITATIONS')
            filter=FILTER_EVENT.MYINVITATIONS;
        else
            filter=FILTER_EVENT.MYEVENTS;

        this.setState({filterEvents:filter});

		this.refreshListEvents(filter);
	}

	componentDidUpdate (prevProps) {
		console.log("EventsList.componentDidUpdate titleFrame=("+this.props.titleFrame+") prevProps=("+prevProps.titleFrame+")");
		if (prevProps.titleFrame !== this.props.titleFrame) {
            console.log("EventsList.componentDidUpdate: Change titleFrame=("+this.props.titleFrame+")");
            let filter;
            if (this.props.titleFrame == 'MYINVITATIONS')
                filter=FILTER_EVENT.MYINVITATIONS;
            else
                filter=FILTER_EVENT.MYEVENTS;

            this.setState({titleFrame: this.props.titleFrame, filterEvents: filter} );
       		this.refreshListEvents(filter);

        }
		if (prevProps.filterEvents !== this.props.filterEvents) {
		    console.log("EventsList.componentDidUpdate: Change filterEvents=("+this.props.filterEvents+")");
		    this.refreshListEvents( this.props.filterEvents );
		}

    }
	
	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// -------------------------------------------- render
	render() {
		console.log("EventList.render titleFrame=("+this.state.titleFrame+") listEvents=" + JSON.stringify(this.state.events));
		let factory = FactoryService.getInstance();
	    let mobileService = factory.getMobileService();

		// no map read, return
		var listEventsHtml = [];

		// <button class="glyphicon glyphicon glyphicon-tint" title={<FormattedMessage id="EventsList.AccessThisEvent" defaultMessage="Access this event" />}></button>
		if (this.state.events) {
			listEventsHtml = this.state.events.map((event,index) => {
			    let mobileService = FactoryService.getInstance().getMobileService();

			    return (
				<div class="toghBlock"
				    style={{marginTop: "20px", border: "2px solid rgba(0,0,0,.125)", padding: "15px"}}
				    onClick={() => this.props.homeSelectEvent(event.id)}
				 key={index}>
				    <div class="row">

                        <div class="col-lg-2" >
                            <img src="img/toghEvent.jpg" style={{ width: 90 }}     />
                        </div>
                        <div class="col-lg-10" >
                             <div class="row">
                                <div class="col-lg-4" style={{verticalAlign: "middle"}}>
                                    <EventState statusEvent={event.statusEvent} disabled={true} />
                                </div>
                                <div class="col-lg-6" style={{verticalAlign: "middle"}}>{event.name}</div>
                                <div class="col-lg-2" style={{verticalAlign: "middle"}}>

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
                                     {((event.datePolicy !== 'ONEDATE' && event.datePolicy !== 'PERIOD')
                                      || (event.datePolicy === 'ONEDATE' && !event.dateEvent )) &&
                                      <FormattedMessage id="EventsList.NoDateEvent" defaultMessage="No date" />
                                      }
                                </div>
                            </div>
                            {mobileService.isLargeScreen() && <div class="row">
                                <div class="col-lg-12" style={{verticalAlign: "middle",fontSize: "12px", lineHeight: "15px"}}>
                                    <span><FormattedMessage id="EventsList.WithParticipants" defaultMessage="With" />
                                    &nbsp;&nbsp; AA
                                    {event.listParticipants}
                                    </span>
                                </div>
                            </div>
                            }
                        </div>
                    </div>
				</div>
				)}
			);
		}
		return (
			<div class="container-fluid">
				<div class="row">
					<div class="col">
					    <h1>
					        {this.state.titleFrame === 'EVENTS' &&
					            <FormattedMessage id="EventsList.TitleEvents" defaultMessage="Events" />}
                            {this.state.titleFrame === 'MYINVITATIONS' &&
					            <FormattedMessage id="EventsList.TitleMyInvitations" defaultMessage="My Invitations" />}
					        </h1></div>
					<div class="col"><div style={{ float: "right" }}>
						<button class="btn btn-info btn-lg" onClick={this.createEvent}>
							<PlusCircle/> &nbsp;<FormattedMessage id="EventsList.CreateAnEvent" defaultMessage="Create an Event"/></button>
						<br/>
						<div style={{color: "red"}}> {this.state.message}</div>
					</div>
					</div>
				</div>
				<div class="row">
                    <UserTips id="eventtakeatour"
                            text={<FormattedMessage id="EventsList.TakeATour"
                            defaultMessage="Explore all the functions. Click on 'Take a tour' icon to access information and help." />}
                        />
				</div>
				<div class="row">
					<div class="col-sm">
						<div class="btn-group" role="group" style={{ padding: "10px 10px 10px 10px" }}>
						    {this.state.titleFrame === 'MYINVITATIONS' &&
                                <button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}
                                    onClick={() => this.refreshListEvents(this.state.filterEvents)}>
                                    <ArrowRepeat/><FormattedMessage id="EventsList.Refresh" defaultMessage="Refresh"/>
                                </button>
							}
							{this.state.titleFrame === 'EVENTS' &&
							    <div>
							        <button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}
                                            onClick={() => this.refreshListEvents(FILTER_EVENT.NEXTEVENTS) }>
                                            <CalendarWeek/>
                                            {mobileService.isSmallScreen() &&
                                                <FormattedMessage id="EventsList.NextEventsShort" defaultMessage="Next"/>
                                            }
                                            {! mobileService.isSmallScreen() &&
                                                <FormattedMessage id="EventsList.NextEvents" defaultMessage="Next events"/>
                                            }
                                	</button>
							        <button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}
                                							            onClick={() => this.refreshListEvents(FILTER_EVENT.MYEVENTS) }>
							            <Calendar2Check/>
							            {mobileService.isSmallScreen() &&
							                <FormattedMessage id="EventsList.MyEventsShort" defaultMessage="Mine"/>
                                        }
                                        {! mobileService.isSmallScreen() &&
                                            <FormattedMessage id="EventsList.NextEvents" defaultMessage="Next events"/>
                                        }
							        </button>
							        <button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}
                                							            onClick={() => this.refreshListEvents(FILTER_EVENT.MYINVITATIONS) }>
							            <EnvelopeOpen/>
                                        {mobileService.isSmallScreen() &&
							                <FormattedMessage id="EventsList.MyInvitationsShort" defaultMessage="Invitations"/>
                                        }
                                        {! mobileService.isSmallScreen() &&
    							            <FormattedMessage id="EventsList.MyInvitations" defaultMessage="My invitations"/>
                                        }

							            <FormattedMessage id="EventsList.MyInvitations" defaultMessage="My invitations"/>
							        </button>
							        <button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }}
                                							            onClick={() => this.refreshListEvents(FILTER_EVENT.ALLEVENTS) }>
							            <ClipboardData/>
                                        {mobileService.isSmallScreen() &&
							                <FormattedMessage id="EventsList.AllEventsShort" defaultMessage="All"/>
                                        }
                                        {! mobileService.isSmallScreen() &&
							                <FormattedMessage id="EventsList.AllEvents" defaultMessage="All events"/>
                                        }

							        </button>
							    </div>
							}
						</div>
					</div>
				</div>
				<UserFeedback inprogress= {this.state.operation.inprogress}
							label= {this.state.operation.label}
							status= {this.state.operation.status}
							result= {this.state.operation.result}
							listlogevents= {this.state.operation.listlogevents} />
					

				{listEventsHtml}

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
		const restCallService = FactoryService.getInstance().getRestCallService();
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
	refreshListEvents( filterEventsValue ) {
		console.log("EventsList.refreshListEvents http[event/list?filterEvents=" + filterEventsValue + "]");
		this.setState({ events: [], filterEvents: filterEventsValue });
		var restCallService = FactoryService.getInstance().getRestCallService();
		restCallService.getJson('/api/event/list?withParticipants=true&filterEvents=' + filterEventsValue, this, this.refreshListEventsCallback );
	}
	
	refreshListEventsCallback(httpPayload) {
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