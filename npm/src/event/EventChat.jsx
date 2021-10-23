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

import { TextArea } from 'carbon-components-react';

import { SkipBackwardCircle } from 'react-bootstrap-icons';



import EventSectionHeader 		from 'component/EventSectionHeader';





// const STATUS_TODO = "TODO";
// const STATUS_DONE = "DONE";
// const STATUS_CANCEL = "CANCEL";


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
		console.log("EventChat.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.addMessage 		= this.addMessage.bind(this);
		this.addMessageCallback	= this.addMessageCallback.bind( this );
	}

	componentDidMount() {
		// Ok, now we do the load
    }


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {

		console.log("EventChat.render: visible=" + this.state.show);

		let headerSection = (
			<EventSectionHeader id="chat"
				image="img/btnChat.png"
				title={<FormattedMessage id="EventChat.MainTitleChat" defaultMessage="Chat" />}
				showPlusButton={false}
				userTipsText={<FormattedMessage id="EventChat.ChatTip" defaultMessage="Chat with all participants" />}
			/>
		);

		//------------------------------ show the list
		if (! this.state.event || ! this.state.event.chatlist) {
		    return "<div/>";
		}

		console.log("EventChat.render: list calculated from " + JSON.stringify(this.state.event.chatlist));

		// class="table table-striped toghtable"
		return (<div>
			{headerSection}

			{  this.state.event.groupchatlist && this.state.event.groupchatlist.map( (group) => {
      				return group.chatlist.map((chat) => {
        				return (
							<div class="row">
								<div class="col-12">
									<div class="toghBlock" style={{ padding: "10px 10px 10px 10px"}}>
										<span style={{whiteSpace: "pre-line"}}>{chat.message}</span>
										<div style={{fontSize:"10px", fontStyle:"italic", textAlign:"left", marginTop:"10px"}}>
											<FormattedMessage id="EventChat.SendBy" defaultMessage="Send by" /> {this.eventCtrl.getParticipantName( chat.whoid)} &nbsp;&nbsp;
											<FormattedMessage id="EventChat.The" defaultMessage="The" />
											<FormattedDate
									           	value= {chat.datemessage}
									           	year = 'numeric'
									           	month= 'long'
									           	day = 'numeric'
									           	weekday = 'long'
									       		/>
										</div> 
									</div>
								</div>
							</div>)
						})
					}
			)}
			<div class="row">
				<div class="col-10">
					<TextArea
						labelText={<FormattedMessage id="EventChat.WhatYouWantToSay" defaultMessage="What do you want to say?" />}
						value={this.state.chat} 
						onChange={(event) => this.setState( {"chat": event.target.value })} />
				</div>
				<div class="col-2" style={{ paddingTop: "20px"}}>
					<SkipBackwardCircle onClick={this.addMessage} width="50px" height="50px"/>
				</div>
			</div>
	
		</div>
		);
	}

	
/*
	
			*/
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


	addMessage() {
		console.log("EventChat.addMessage: addItem item=" + JSON.stringify(this.state.event));
		// don't set the whoId: the server will add the caller
		var newLine = { message: this.state.chat };
		this.eventCtrl.addEventChildFct("chat", newLine, "", this.addMessageCallback);
	}

	addMessageCallback(httpPayload) {
		console.log("EventChat.addMessageCallback ");
		if (httpPayload.isError()) {
			// feedback to user is required
			console.log("EventShoppinglist.addMessageCallback: ERROR ");
		} else {
			this.setState({ chat: "" });
			var event = this.eventCtrl.getEvent();
			event.groupchatlist = httpPayload.getData().groupchatlist;
			this.setState({ event: event });
		}
	}


}
export default injectIntl(EventChat);