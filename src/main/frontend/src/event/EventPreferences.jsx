/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { FormattedMessage } from "react-intl";
import { Select, Toggle } from '@carbon/react';
import FactoryService 		from 'service/FactoryService';
import EventSectionHeader 	from 'component/EventSectionHeader';

// -----------------------------------------------------------
//
// EventPreferences
//
// Display one event
//
// -----------------------------------------------------------

class EventPreferences extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;
		this.preferencesCallback = props.preferencesCallback;
		this.state = { event: this.eventCtrl.getEvent()};
		// show : OFF, ON, COLLAPSE
		console.log("EventPreferences.constructor show="+ this.state.show+" event="+JSON.stringify(this.state.event));
		
	}


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		var currencyService = FactoryService.getInstance().getCurrencyService();
		// --- Header
		let cssStyle={margin: "15px 80px ", borderTop:"solid 1px #1f78b4" };
		return (<div>
					<div class="row" >
		            <EventSectionHeader id="eventPreferences"
        				image="img/btnPreferences.png"
        				title={<FormattedMessage id="EventPreferences.MainTitlePreferences" defaultMessage="Preferences" />}
        				showPlusButton  = {false}
        				userTipsText={<FormattedMessage id="EventPreferences.PreferencesTip" defaultMessage="Set up all preferences for the event" />}
        				/>
                    </div>
					<div class="row" style={cssStyle}>
                        <div class="col-3">
						    <FormattedMessage id="EventPreferences.CurrencyOnEvent" defaultMessage="Currency used in this event" />
						</div>
						<div class="col-8">
							<Select labelText=""
								id="currentEvent"
								value={this.eventCtrl.getEventPreferences().getCurrencyCode()}
								onChange={(event) => 
										{ this.eventCtrl.getEventPreferences().setCurrency( event.target.value );
											this.setState( {event: this.state.event});
										}
									}>
									{currencyService.getCurrencyList().map( (item) => {
										return ( <option value={item.code}> {item.code} {item.label}</option>)
									})}
							</Select>
						</div>
					</div>

                    <div class="row" style={cssStyle}>
                        <div class="col-3">
                            <FormattedMessage id="EventPreferences.DisplayFunction" defaultMessage="Display Functions" />
                        </div>
						<div class="col-8">
						    <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Chat")}
                                    selectorPrimaryFocus={this.eventCtrl.accessChat}
                                    labelA={<FormattedMessage id="EventPreferences.accessChatNo" defaultMessage="Chat" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessChatYes" defaultMessage="Chat" />}
                                    onChange={(event) => {this.setAccessAttribut("Chat", event);}}/>

						    <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Itinerary")}
                                    selectorPrimaryFocus={this.eventCtrl.accessItinerary}
                                    labelA={<FormattedMessage id="EventPreferences.accessItineraryNo" defaultMessage="Itinerary" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessItineraryYes" defaultMessage="Itinerary" />}
                                    onChange={(event) => {this.setAccessAttribut("Itinerary", event);}}/>

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Tasks")}
                                    selectorPrimaryFocus={this.eventCtrl.accessTasks}
                                    labelA={<FormattedMessage id="EventPreferences.accessTasksNo" defaultMessage="Tasks" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessTasksYes" defaultMessage="Tasks" />}
                                    onChange={(event) => {this.setAccessAttribut("Tasks", event);}}/>

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Bring")}
                                    selectorPrimaryFocus={this.eventCtrl.accessBring}
                                    labelA={<FormattedMessage id="EventPreferences.accessBringNo" defaultMessage="Bring list" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessBringYes" defaultMessage="Bring list" />}
                                    onChange={(event) => {this.setAccessAttribut("Bring", event);}} />

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Surveys")}
                                    selectorPrimaryFocus={this.eventCtrl.accessSurveys}
                                    labelA={<FormattedMessage id="EventPreferences.accessSurveysNo" defaultMessage="Surveys" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessSurveysYes" defaultMessage="Surveys" />}
                                    onChange={(event) => { this.setAccessAttribut("Surveys", event);}}/>

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Localisation")}
                                    selectorPrimaryFocus={this.eventCtrl.accessLocalisation}
                                    labelA={<FormattedMessage id="EventPreferences.accessLocalisationNo" defaultMessage="Localisation" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessLocalisationYes" defaultMessage="Localisation" />}
                                    onChange={(event) => {this.setAccessAttribut("Localisation", event);}}/>

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Games")}
                                    selectorPrimaryFocus={this.eventCtrl.accessGames}
                                    labelA={<FormattedMessage id="EventPreferences.accessGamesNo" defaultMessage="Games" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessGamesYes" defaultMessage="Games" />}
                                    onChange={(event) => {this.setAccessAttribut("Games", event);}}/>

                            <Toggle labelText=""
                                    toggled={this.eventCtrl.getEventPreferences().getAccess("Photos")}
                                    disabled={true}
                                    selectorPrimaryFocus={this.eventCtrl.accessPhotos}
                                    labelA={<FormattedMessage id="EventPreferences.accessPhotosNo" defaultMessage="Photos" />}
                                    labelB={<FormattedMessage id="EventPreferences.accessPhotosYes" defaultMessage="Photos" />}
                                    onChange={(event) => {this.setAccessAttribut("Photos", event);}}/>

                           <Toggle labelText=""
                                   toggled={this.eventCtrl.getEventPreferences().getAccess("Expenses")}
                                   disabled={true}
                                   selectorPrimaryFocus={this.eventCtrl.accessExpenses}
                                   labelA={<FormattedMessage id="EventPreferences.accessExpensesNo" defaultMessage="Expenses" />}
                                   labelB={<FormattedMessage id="EventPreferences.accessExpensesYes" defaultMessage="Expenses" />}
                                   onChange={(event) => {this.setAccessAttribut("Expenses", event);}}/>

                         <Toggle labelText=""
                                 toggled={this.eventCtrl.getEventPreferences().getAccess("Budget")}
                                 disabled={true}
                                 selectorPrimaryFocus={this.eventCtrl.accessBudget}
                                 labelA={<FormattedMessage id="EventPreferences.accessBudgetNo" defaultMessage="Budget" />}
                                 labelB={<FormattedMessage id="EventPreferences.accessBudgetYes" defaultMessage="Budget" />}
                                 onChange={(event) => {this.setAccessAttribut("Budget", event);}}/>

                     </div>
                </div>

                    <div class="row" style={cssStyle}>
                        <div class="col-3">
						    <FormattedMessage id="EventPreferences.EventSubscription" defaultMessage="Subscription" />
						</div>
						<div class="col-8">
						    {this.eventCtrl.getEvent().subscriptionEvent}<br/>
							{this.eventCtrl.getEvent().subscriptionEvent === 'FREE' 
								&& <FormattedMessage id="EventPreferences.SubscriptionFreeExplanation" 
									defaultMessage="A Free event contains all functions except the budget and is limited in the number of participants (20) and items (50)" />}
							{this.eventCtrl.getEvent().subscriptionEvent === 'PREMIUM' 
								&& <FormattedMessage id="EventPreferences.SubscriptionPremiumExplanation" 
									defaultMessage="A Premium event contains all functions, upper limitation participants (100) and items (100)" />}
							{this.eventCtrl.getEvent().subscriptionEvent === 'EXCELLENCE' 
								&& <FormattedMessage id="EventPreferences.SubscriptionExcellenceExplanation" 
									defaultMessage="A Excellence event contains all functions, and a limit of 1000 per items" />}
						</div>
					</div>

	
				</div>
			)
	}
	
	
		
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	
	setAttribut(name, value, item ) {
		console.log("EventPreferences.setAttribut: set attribut:" + name + " <= " + value );
		this.eventCtrl.setAttribut( name, value, this.state.event, "");

	}
	setAccessAttribut(name, value) {
    		console.log("EventPreferences.setAttribut set " + name + "<=" + value.target.checked);
    		let eventVar = this.state.event;
    		if (value.target.checked)
    			eventVar[name] = true;
    		else
    			eventVar[name] = false;
    		this.eventCtrl.getEventPreferences().setAccess(name, eventVar[name]  );
    		this.setState( {event : eventVar});
    		// Call the Event for a refresh now
            this.preferencesCallback();
    	}
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


}


export default EventPreferences;
	