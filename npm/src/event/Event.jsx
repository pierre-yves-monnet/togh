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

// import { Button } from 'carbon-components-react';
import { DatePicker, DatePickerInput, TimePicker, RadioButtonGroup, RadioButton, TextArea, TextInput, Select } from 'carbon-components-react';

import FactoryService from 'service/FactoryService';

import UserParticipantCtrl from 'controller/UserParticipantCtrl';

import EventSectionHeader 			from 'component/EventSectionHeader';
import EventParticipants            from 'event/EventParticipants';
import EventItinerary               from 'event/EventItinerary';
import EventShoppingList            from 'event/EventShoppingList';
import EventGeolocalisation         from 'event/EventGeolocalisation';
import EventTaskList                from 'event/EventTaskList';
import EventState                   from 'event/EventState';
import EventExpense                 from 'event/EventExpense';
import EventSurveyList              from 'event/EventSurveyList';
import EventChat                    from 'event/EventChat';
import EventPreferences             from 'event/EventPreferences';

import EventCtrl                    from 'controller/EventCtrl';

// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

const TAB_PARTICIPANT='Participant';
const TAB_ITINERARY = 'Itinerary';
const TAB_SHOPPINGLIST = 'ShoppingList';
const TAB_GEOLOCALISATION='Geolocalisation';
const TAB_EXPENSE = 'Expense';
const TAB_CHAT = 'Chat';
const TAB_TASKS = 'TaskList';
const TAB_SURVEY = 'Survey';
const TAB_PHOTO = 'Photo';
const TAB_BUDGET = 'Budget';
const TAB_PREFERENCES= 'Preferences';

class Event extends React.Component {
	constructor(props) {
		super();
		// console.log("Event.constructor eventId="+props.eventid);

		this.state = {
			'event': {},
			'eventid': props.eventid,
			show: {
				currentSection: TAB_PARTICIPANT
			}
		};



		// TextArea must not be null
		if (!this.state.event.description)
			this.state.event.description = '';

		// this is mandatory to have access to the variable in the method... thank you React!

		this.accessTab			 		= this.accessTab.bind(this);

		// this is mandatory to have access to the variable in the method... thank you React!
		this.loadEvent 					= this.loadEvent.bind(this);
		this.changeStateCallback 		= this.changeStateCallback.bind(this);
		this.setAttribut 				= this.setAttribut.bind(this);
		this.updateEventFct 			= this.updateEventFct.bind(this);
		this.getUserParticipant			= this.getUserParticipant.bind(this);
        this.getDisabledState           = this.getDisabledState.bind(this);

	}

	componentDidMount() {
		// Ok, now we do the load
		this.loadEvent();
	}

	//

	//----------------------------------- Render
	render() {
		console.log("Event.render eventId=" + this.state.eventid + " event=" + JSON.stringify(this.state.event) + " show:" + JSON.stringify(this.state.show));
        const intl = this.props.intl;

	    var userService = FactoryService.getInstance().getUserService();


		// no map read, return
		if (!this.state.event || Object.keys(this.state.event).length === 0) {
			console.log("Event.render: noEvent ");
			return (<div />);
		}


		let toolService = FactoryService.getInstance().getToolService();


		var datePanelHtml = (
			<div>
				<RadioButtonGroup
				    disabled={this.getDisabledState("datePolicy")}
					name="datepolicy"
					valueSelected={this.state.event.datePolicy}
					legend={<FormattedMessage id="Event.DatePolicy" defaultMessage="Date policy" />}
					onChange={(event) => {
						console.log("RadioGroup.DatePolicy on change="+event);
						this.eventCtrl.setAttribut( "datePolicy", event, this.state.event, "");
					}
					}
				>
					<RadioButton value="ONEDATE"
    				    disabled={this.getDisabledState("datePolicy")}
					    id="r1" labelText={<FormattedMessage id="Event.OneDate" defaultMessage="One date" />} labelPosition="right" />
					<RadioButton value="PERIOD"
       				    disabled={this.getDisabledState("datePolicy")}
                        id="r2" labelText={<FormattedMessage id="Event.Period" defaultMessage="Period" />} labelPosition="right" />
				</RadioButtonGroup>
				{this.state.event.datePolicy === 'ONEDATE' && (
					<div>
						<table><tr>
							<td colspan="2">

								<DatePicker datePickerType="single"
								    disabled={this.getDisabledState("dateStartEvent")}
									onChange={(dates) => {
										console.log("SingleDatePicker :" + dates.length + " is an array " + Array.isArray(dates));
											if (dates.length >= 1) {
												// DatePicker does not accept that we set a Date. We have to set a String...
												// else we face a "Non React Object" during the setEvent()
												// and whatever is the value() method
												let dateSingleSt = dates[0].toISOString();
												this.setAttribut("dateEvent", dateSingleSt);
											}
										}
									}
									/**Actually, there is no impact here */
									value={toolService.getIsoStringFromDate(this.state.event.dateEvent) }
								>
									<DatePickerInput
                                        disabled={this.getDisabledState("dateStartEvent")}
										// placeholder='mm/dd/yyyy' // To get from a service that returns the format based on the language selected
										placeholder="mm/dd/yyyy"
										labelText={<FormattedMessage id="Event.DateEvent" defaultMessage="Date Event" />}
										id="date-picker-simple"

									/>
								</DatePicker>
							</td></tr>
							<tr><td>
								<TimePicker
								    disabled={this.getDisabledState("timeEvent")}
									id="eventTime"
									labelText={<FormattedMessage id="Event.TimeEvent" defaultMessage="Time" />}
									value={this.state.event.timeEvent}
									onChange={(event) => this.setAttribut("timeEvent", event.target.value)} />
							</td><td>
								<TimePicker
									    disabled={this.getDisabledState("durationEvent")}
										id="durationTime"
										labelText={<FormattedMessage id="Event.DurationEvent" defaultMessage="Duration" />}
										value={this.state.event.durationEvent}
										onChange={(event) => this.setAttribut("durationEvent", event.target.value)} />
								</td></tr>
								<tr><td colspan="2">
									<FormattedMessage id="Event.ExplanationYourTimeZone" defaultMessage="This date is calculated in your time zone" />
								</td></tr>
								</table>
					</div>)
				}
				{this.state.event.datePolicy !== 'ONEDATE' && (
					<div>
						<DatePicker datePickerType="range"
							onChange={(dates) => {
								if (dates.length > 1) {
									this.setAttribut("dateStartEvent", dates[0]);
									this.setAttribut("dateEndEvent", dates[1]);
								}
							}}
							value={toolService.getDateListFromDate(this.state.event.dateStartEvent, this.state.event.dateEndEvent)}
						>
							<DatePickerInput
					            disabled={this.getDisabledState("dateStartEvent")}
								id="date-picker-input-id-start"
								placeholder="mm/dd/yyyy"
								labelText={<FormattedMessage id="Event.StartDate" defaultMessage="Start Date" />}
							/>
							<DatePickerInput
								disabled={this.getDisabledState("dateEndEvent")}
								id="date-picker-input-id-finish"
								placeholder="mm/dd/yyyy"
								labelText={<FormattedMessage id="Event.EndDate" defaultMessage="End Date" />}
							/>
						</DatePicker>
					</div>)
				}



			</div>
		);


		// <div class="btn-group mr-2" role="group" aria-label="First group">
		//							<button style={{"marginLeft ": "10px"}} onClick={this.secItineraire} title="Itineraire" disabled={true} class="btn btn-primary">
		//								<div class="glyphicon glyphicon-road"></div>
		//						</button>
		//					</div>

		// -----------------
		// console.log("Event.render : statusEvent="+JSON.stringify(this.state.event.statusEvent)+" Participants="+JSON.stringify(this.state.event.participants));
		return (
			<div>
				{ this.state.event.systemerror && <div class="alert alert-danger">{this.state.event.systemerror}</div>}
				<div class="row">
					<div class="col-sm-1">
						<img src="img/toghEvent.jpg" style={{ width: 90 }}     />
					</div>

					<div class="col-sm-4">
						<TextInput labelText=""
				            disabled={this.getDisabledState("name")}
							id="name"
							value={this.state.event.name}
							style={{fontSize: "24px", height: "50px", color: "#ac1e4a", maxWidth: "315px"}}
							onChange={(event) => this.setAttribut("name", event.target.value)} /><br />
					</div>
					<div class="col-sm-5">
						<div class="fieldlabel">{<FormattedMessage id="Event.Status" defaultMessage="Status" />}</div>
						<EventState statusEvent={this.state.event.statusEvent}
						    disabled={this.getDisabledState("statusEvent")}
						    changeState={this.changeStateCallback} />
					</div>
					<div class="col-sm-2">
						<Select labelText={<FormattedMessage id="Event.Scope" defaultMessage="Scope" />}
				            disabled={this.getDisabledState("typeEvent")}
							id="typeEvent"
							value={this.state.event.typeEvent}
							onChange={(event) => this.setAttribut("typeEvent", event.target.value)}>


							<FormattedMessage id="Event.ScopeOpen" defaultMessage="Open">
								{(message) => <option value="OPEN">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeOpenConfirmation" defaultMessage="Open on confirmation">
								{(message) => <option value="OPENCONF">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeLimited" defaultMessage="Limited">
								{(message) => <option value="LIMITED">{message}</option>}
							</FormattedMessage>

							<FormattedMessage id="Event.ScopeSecret" defaultMessage="Secret">
								{(message) => <option value="SECRET">{message}</option>}
							</FormattedMessage>


						</Select>
						<br />
					</div>

				</div>
				<div class="row">
					<div class="col-sm-6">
						<TextArea id="description"
				            disabled={this.getDisabledState("description")}
                            labelText={<FormattedMessage id="Event.Description" defaultMessage="Description" />}
							style={{ width: "100%", maxWidth: "100%" }}
							rows={5}
							value={this.state.event.description}
							onChange={(event) => this.setAttribut("description", event.target.value)} />
					</div>
					<div class="col-sm-6">
						<div class="card" style={{marginTop: "10px"}}>
							<div class="card-header" style={{backgroundColor:"#decbe4"}}>
								<FormattedMessage id="Event.EventDate" defaultMessage="Date" />
							</div>
							<div class="card-body">
								{datePanelHtml}
							</div>
						</div>
					</div>
				</div>

                <EventSectionHeader id="helptabs"
                    showPlusButton  = {false}
                    userTipsText={<FormattedMessage id="Event.HelpTabs" defaultMessage="You have access to different tools in the event. Explore them" />}
				    />
				<div class="row" style={{ padding: "10px 30px 10px" }}>
                    <ul class="nav nav-tabs" style={{borderBottom: "6px solid #e9ecef"}}>
                        <li class="nav-item">
                            <button class={this.getTabCssClass( TAB_CHAT )}
								style={this.getTabCssStyle( TAB_CHAT )} aria-current="page"
								onClick={() => this.accessTab( TAB_CHAT ) }
                            title={intl.formatMessage({id:"Event.TitleChat", defaultMessage:"Chat with all participants"})}>
                                <div style={{textAlign: "center"}}>
                                    <img style={{float: "right", width: 50 }} src="img/btnChat.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Chat" defaultMessage="Chat"/>
                                        </div>}
                                </div>
                            </button>
                        </li>

                        <li class="nav-item">
                            <button class={this.getTabCssClass( TAB_PARTICIPANT )}
								style={this.getTabCssStyle( TAB_PARTICIPANT )} aria-current="page"
								onClick={() => this.accessTab( TAB_PARTICIPANT )}
                                title={intl.formatMessage({id:"Event.TitleParticipant", defaultMessage:"Invite participant to your event"})}>
                                <div style={{textAlign: "center"}}>
                                    <img style={{ "float": "right", width: 50 }} src="img/btnParticipants.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div>
                                            <FormattedMessage id="Event.Participant" defaultMessage="Participants"/>
                                        </div>
                                    }
                                </div>
                      	    </button>
                        </li>

                        <li class="nav-item">
                             <button class={this.getTabCssClass( TAB_ITINERARY )}
								style={this.getTabCssStyle( TAB_ITINERARY )} aria-current="page"
								onClick={() => this.accessTab( TAB_ITINERARY )}
								title={intl.formatMessage({id:"Event.TitleItinerary", defaultMessage:"Define your itinerary, and point of interest"})}>
								<div style={{textAlign: "center"}}>
							        <img style={{ "float": "right",width: 50 }} src="img/btnItinerary.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Itinerary" defaultMessage="Itinerary"/>
                                        </div>}
                                </div>
						    </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_TASKS )}
								style={this.getTabCssStyle( TAB_TASKS )} aria-current="page"
								onClick={() => this.accessTab( TAB_TASKS )}
							    title={intl.formatMessage({id:"Event.TitleTasks", defaultMessage:"Tasks" })}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right",width: 50 }} src="img/btnTask.png" />
							        { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Tasks" defaultMessage="Tasks"/>
                                        </div>}
							    </div>
						    </button>
                        </li>

                        <li class="nav-item">
					        <button class={this.getTabCssClass( TAB_SHOPPINGLIST )}
								style={this.getTabCssStyle( TAB_SHOPPINGLIST )} aria-current="page"
								onClick={() => this.accessTab( TAB_SHOPPINGLIST )}
							    title={intl.formatMessage({id:"Event.TitleBringList", defaultMessage:"What to brings?" })}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right",width: 50 }} src="img/btnShoppingList.png" />
							        { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.BringList" defaultMessage="Bring List"/>
                                        </div>}
							    </div>
						    </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_SURVEY )}
								style={this.getTabCssStyle( TAB_SURVEY )} aria-current="page"
								onClick={() => this.accessTab( TAB_SURVEY )}
							    title={intl.formatMessage({id:"Event.TitleSurvey", defaultMessage:"Survey"})}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right",width: 50 }} src="img/btnSurvey.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Survey" defaultMessage="Survey"/>
                                        </div>}
                                </div>
						    </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_GEOLOCALISATION )}
								style={this.getTabCssStyle( TAB_GEOLOCALISATION )}
								 aria-current="page"
								onClick={() => this.accessTab( TAB_GEOLOCALISATION )}
							    title={intl.formatMessage({id:"Event.TitleGeolocalisation",defaultMessage:"Where is the event?"})}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right", width: 50}} src="img/btnGeolocalisation.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Geolocalisation" defaultMessage="Geolocalisation"/>
                                        </div>}
							    </div>
						    </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_PHOTO )}
								style={this.getTabCssStyle( TAB_PHOTO )} aria-current="page"
							    title={intl.formatMessage({id:"Event.TitlePhotos", defaultMessage:"Photos" })}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right", width: 50 }} src="img/btnPhoto.png" /><br />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Photos" defaultMessage="Photos"/>
                                        </div>}
							    </div>
                            </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_EXPENSE )}
								style={this.getTabCssStyle( TAB_EXPENSE )} aria-current="page"
							    title={intl.formatMessage({id:"Event.TitleExpense",  defaultMessage:"Manage and share expenses" })}>
							    <div style={{textAlign: "center"}}>
							        <img src="img/btnExpense.png" style={{ width: 50 }} />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Expense" defaultMessage="Expenses"/>
                                        </div>}
							    </div>
						    </button>
                        </li>

                        <li class="nav-item">
						    <button class={this.getTabCssClass( TAB_BUDGET )}
								style={this.getTabCssStyle( TAB_BUDGET )} aria-current="page"
							    title={intl.formatMessage({id:"Event.TitleBudget", defaultMessage:"Budget" })}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right", width: 50 }} src="img/btnBudget.png" />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Budget" defaultMessage="Budget"/>
                                        </div>}
							    </div>
						    </button>
                        </li>

                        <li class="nav-item">
                            <button class={this.getTabCssClass( TAB_PREFERENCES )}
								style={this.getTabCssStyle( TAB_PREFERENCES )} aria-current="page" onClick={() => this.accessTab( TAB_PREFERENCES )}
							    title={intl.formatMessage({id:"Event.TitlePreferences", defaultMessage:"Preferences" })}>
							    <div style={{textAlign: "center"}}>
							        <img style={{ "float": "right",width: 50 }} src="img/btnPreferences.png"  />
                                    { userService.prefsDisplayTips() &&
                                        <div >
                                            <FormattedMessage id="Event.Preferences" defaultMessage="Preferences"/>
                                        </div>}
							    </div>
						    </button>
                        </li>
                    </ul>

				</div>





				{this.state.show.currentSection === TAB_PARTICIPANT && <EventParticipants event={this.state.event}
																			updateEvent={this.updateEventFct}
																			eventCtrl={this.eventCtrl}
																			getUserParticipant={this.getUserParticipant}/>}
				{this.state.show.currentSection === TAB_ITINERARY && <EventItinerary eventCtrl={this.eventCtrl} />}

				{this.state.show.currentSection === TAB_CHAT && <EventChat eventCtrl={this.eventCtrl} />}

				{this.state.show.currentSection === TAB_TASKS && <EventTaskList eventCtrl={this.eventCtrl} />}

				{this.state.show.currentSection === TAB_SHOPPINGLIST && <EventShoppingList eventCtrl={this.eventCtrl} />}

				{this.state.show.currentSection === TAB_GEOLOCALISATION && <EventGeolocalisation eventCtrl={this.eventCtrl} />}

				{this.state.show.currentSection === TAB_SURVEY && <EventSurveyList eventCtrl={this.eventCtrl} />}
				{this.state.show.currentSection === TAB_EXPENSE  && <EventExpense event={this.state.event}
																			updateEvent={this.updateEventFct}
																			getUserParticipant={this.getUserParticipant}/>}
				{this.state.show.currentSection === TAB_PREFERENCES  && <EventPreferences eventCtrl={this.eventCtrl} />}
		</div>)

	} //---------------------------- end Render


	changeStateCallback(event) {
		console.log("Event.setState event ");
		this.setAttribut("statusEvent", event);
	}



	// provide automatic save
	setAttribut(name, value) {
		console.log("Event.setAttribute: attribut:" + name + " <= " + value + " typeof=" + (typeof value) + " EventinProgress=" + JSON.stringify(this.state.event));
		this.eventCtrl.setAttribut( name, value, this.state.event, "");
	}


	/** */
	refreshEventFct(  ) {
		console.log("Event.refreshEventFct Start!! event="+JSON.stringify(this.eventCtrl.getEvent()));

		this.setState( { event: this.eventCtrl.getEvent()} );
	}

	/**
	Something change in the event, all subcomponent refer it with a Slab, which is the information which change */
	updateEventFct( slab ) {
			console.log("Event.updateEventFct : DEPRECATED METHOD !!");
	}


	// --------------------------------------------------------------
	//
	// Component controls
	//
	// --------------------------------------------------------------
    getDisabledState( fieldName) {
        if (this.state && this.state.event && this.state.event.readOnlyFields && this.state.event.readOnlyFields.includes(fieldName))
            return true;
        return false;
    }

	// -------------------------------------------- Access different part
	accessTab( accessTab ) {
		console.log("Event.accessTab tab=" + JSON.stringify(accessTab));
		this.setState( { show: { currentSection: accessTab} });
	}

    getTabCssClass( tab ) {
        if (this.state.show.currentSection === tab ) {
            return "nav-link active"
        }
        return "nav-link";
    }

    getTabCssStyle( tab ) {
		const style = {
	      active : {
	        borderWidth: '9px',
			borderColor: "#e9ecef #e9ecef white #e9ecef",
			margin: '-7px',
	        transition: 'all 0.2s ease-in-out',
	        // width: '100px',
	        // textAlign: 'center',
	      }, 
	      inactive : { //this will only be applied when state is Circle
			borderWidth: '2px',
			opacity: '0.5',
			// backgroundColor: 'grey',
	        // borderRadius: '50%',
	        // height: '100px',
	      }
	    }
        if (this.state.show.currentSection === tab ) {
            return style.active
        }
        return style.inactive;
    }

	// -------------------------------------------- Tool Service
	getUserParticipant() {
		var authService = FactoryService.getInstance().getAuthService();
		// console.log("Event.getUserParticipant.start");
		var user= authService.getUser();
		// search the access right for this user
		for (var i in this.state.event.participants) {
			if (this.state.event.participants[ i ].user && this.state.event.participants[ i ].user.id === user.id) {
				return new UserParticipantCtrl(this.state.event,  this.state.event.participants[ i ] )
			}
		}
		return new UserParticipantCtrl(this.state.event,null );
	}



	// -------------------------------------------- Call REST
	loadEvent() {
		console.log("Event.loadEvent: event?id=" + this.state.eventid + "]");
		this.setState({ event: {} });

		var restCallService = FactoryService.getInstance().getRestCallService();
		restCallService.getJson('/api/event?id=' + this.state.eventid, this, httpPayload => {
			httpPayload.trace("Event.getPayload");

			if (! httpPayload.isError()) {
				// not in the state: we don't want a render when something is added
				// so, this is the moment to create the Basket

				// console.log("Event.loadEvent: eventLoaded=" + JSON.stringify(httpPayload.getData().event) + "]");
				this.eventCtrl = new EventCtrl( this, httpPayload.getData().event );
				// console.log("Event.loadEvent: before completion event=" + JSON.stringify(this.eventCtrl.getEvent()));
				this.eventCtrl.completeEvent();
				// console.log("Event.loadEvent: end of complete, event=" + JSON.stringify(this.eventCtrl.getEvent()));
				this.setState({ event: this.eventCtrl.getEvent(), show: { currentSection : TAB_PARTICIPANT } });
				// console.log("Event.loadEvent: the end ");
			}
		});

	}




	// -------- Rest Call

}
export default injectIntl(Event);