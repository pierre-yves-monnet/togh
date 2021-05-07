/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { injectIntl, FormattedMessage, FormattedDate} from "react-intl";

import { TextInput, TextArea, OverflowMenu, OverflowMenuItem, Tag, Toggle } from 'carbon-components-react';

import { SkipBackwardCircle } from 'react-bootstrap-icons';

import ChooseParticipant 		from 'component/ChooseParticipant';
import Expense 					from 'component/Expense';
import EventSectionHeader 		from 'component/EventSectionHeader';
import TagDropdown 				from 'component/TagDropdown';

import SlabRecord 				from 'service/SlabRecord';


const STATUS_TODO = "TODO";
const STATUS_DONE = "DONE";
const STATUS_CANCEL = "CANCEL";


// -----------------------------------------------------------
//
// EventChat
//
// Display one event
//
// -----------------------------------------------------------

class EventChat extends React.Component {
	// this.props.updateEvent()
	// props.eventPreferences is provide (or should be at least, I think, I don't know)
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;

		this.state = {
			event: this.eventCtrl.getEvent(),
			chat: "",
			showProperties: {
				showDetail: true,
				showExpense: false
			}
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setCheckboxValue 	= this.setCheckboxValue.bind(this);
		this.addMessage 		= this.addMessage.bind(this);
		this.addMessageCallback	= this.addMessageCallback.bind( this );
		
	}



	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		console.log("EventShoppinglist.render: visible=" + this.state.show);

		var headerSection = (
			<EventSectionHeader id="chat"
				image="img/btnChat.png"
				title={<FormattedMessage id="EventChat.MainTitleChat" defaultMessage="Chat" />}
				showPlusButton={false}
				userTipsText={<FormattedMessage id="EventChat.ChatTip" defaultMessage="Chat with all participants" />}
			/>
		);

		//------------------------------ show the list
		console.log("EventChat.render: list calculated from " + JSON.stringify(this.state.event.chatlist));

		// class="table table-striped toghtable"
		return (<div>
			{headerSection}

			{  this.state.event.chatlist && this.state.event.chatlist.map( (event) => {
				return (
					<div class="row">
						<div class="col-12">
							<div class="toghBlock" style={{ padding: "10px 10px 10px 10px"}}>
								{event.chat}
								<div style={{fontSize:"10px", fontStyle:"italic", textAlign:"right"}}>
									<FormattedMessage id="EventChat.SendBy" defaultMessage="Send by" /> {event.name} 
									<FormattedMessage id="EventChat.The" defaultMessage="The" />
									<FormattedDate
							           	value= {event.datemessage}
							           	year = 'numeric'
							           	month= 'long'
							           	day = 'numeric'
							           	weekday = 'long'
							       		/>
								</div> 
							</div>
						</div>
					</div>)
			})}
			<div class="row">
				<div class="col-10">
					<TextArea
						labelText={<FormattedMessage id="EventChat.WhatYouWantToSay" defaultMessage="What you want to say?" />} 
						onChange={(event) => this.setState( {"chat": event.target.value })} />
				</div>
				<div class="col-2" style={{ paddingTop: "20px"}}>
					<SkipBackwardCircle onClick={this.addMessage} width="80px" height="80px"/>
				</div>
			</div>
	
		</div>
		);
	}

	/**
	 * Filter the different task
	 */
	getFilterTaskHtml() {
		return (<div class="row">
			<div class="col">
				<Toggle labelText="" aria-label=""
					labelA={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					onChange={(event) => this.setCheckboxValue("showDetail", event.target.value)}
					defaultToggled={this.state.showProperties.showDetail}
					id="showDetail" />
			</div>
			<div class="col">
				<Toggle labelText="" aria-label=""
					labelA={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					onChange={(event) => this.setCheckboxValue("showExpense", event.target.value)}
					defaultToggled={this.state.showProperties.showExpense}
					id="showexpense" />
			</div>
		</div>)
	}


	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	/** --------------------
	 * Set attribut 
		  */
	setCheckboxValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue set " + name + "<=" + value.target.checked + " showProperties =" + JSON.stringify(showPropertiesValue));
		if (value.target.checked)
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ "showProperties": showPropertiesValue })
	}


	
	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	addMessage() {
		console.log("EventChat.addMessage: addItem item=" + JSON.stringify(this.state.event));
		// call the server to get an ID on this taskList
		var newLine = { chat: this.state.chat, who:-1 };
		this.eventCtrl.addEventChildFct("chat", newLine, "", this.addMessageCallback);
	}

	addMessageCallback(httpPayload) {
		console.log("EventChat.addMessageCallback ");
		if (httpPayload.isError()) {
			// feedback to user is required
			console.log("EventShoppinglist.addMessageCallback: ERROR ");
		} else {
			var chatToAdd = httpPayload.getData().child;
			var event = this.eventCtrl.getEvent();
			var newList = event.chatlist.concat(chatToAdd);
			event.chatlist = newList;
			this.setState({ event: event });
		}
	}


}
export default injectIntl(EventChat);