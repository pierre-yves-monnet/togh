// -----------------------------------------------------------
//
// InvitationAgain
//
// Be able to send again the invitation
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { Tag, ModalWrapper, TextInput, TextArea } from 'carbon-components-react';
import { Envelope,Files } from 'react-bootstrap-icons';


import FactoryService from 'service/FactoryService';

class InvitationAgain extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {  'event' : props.event,
		                'participant': props.participant,
						'subject': 'You are invited to a ToghEvent',
						'message': 'Please join this event.'
						};
        console.log("InvitationAgain.participant :"+JSON.stringify(props.participant));
        this.inviteResend = this.inviteResend.bind(this);
	}


	// --------------------------------------------------------------
	//
	// Render HTML
	//
	// --------------------------------------------------------------

	render() {
			const intl = this.props.intl;

	    return (
	        <table>
	            <tr><td>
                    <Tag type="teal">
                        <Envelope/>
                        &nbsp;
                        <FormattedMessage id="EventParticipant.InvitationInProgress" defaultMessage="Invited"/>
                    </Tag>
                </td><td style={{verticalAlign:"bottom"}}>
    			    <ModalWrapper
                        passiveModal
                        buttonTriggerText={<FormattedMessage id="InvitationAgain.Invitation" defaultMessage="Manage invitation"/>}
                        modalLabel={intl.formatMessage({id: "InvitationAgain.InvitationLabel", defaultMessage: "Invitation"})}
                        size='lg'>
                            <div class="row">
                                <p>
                                <FormattedMessage id="InvitationAgain.InvitationTo" defaultMessage="Invitation sent to"/>
                                &nbsp;
                                {this.state.participant.user.label}
                                </p>
                            </div>
                            <div class="row" style={{marginTop : "10px"}}>
                                <h5><FormattedMessage id="InvitationAgain.EmailTitle" defaultMessage="Send the invitation email"/></h5>
                            </div>
                            <div class="row">
                                <FormattedMessage id="InvitationAgain.SendAgainExplanation" defaultMessage="Send again the invitation by email"/>
                            </div>
                            <div class="row">
                                <TextInput labelText={<FormattedMessage id="InvitationAgain.Subject" defaultMessage="Subject" />}
                                                                        value={this.state.subject}
                                                                        onChange={(event) => this.setState( { subject: event.target.value })}
                                                                        id="InvitationAgain.subject"/>

                                <TextArea labelText={intl.formatMessage({id: "InvitationAgain.Message", defaultMessage: "Message"})}
                                    value={this.state.message} onChange={(event) => this.setState({ message: event.target.value })} ></TextArea><br />

                                <div col="col-2">
                                    <input type="checkbox"
                                        style={{marginLeft: "15px", marginRight:"5px"}}
                                        onChange={(event) =>
                                               this.setState({ 'useMyEmailAsFrom': event.target.value==='on'})
                                           }
                                        title={intl.formatMessage({id:"InvitationAgain.UseMyEmailAsFromExplantion", defaultMessage:"In order to be accepted by the recipient, your email is used to be the sender of the email "})}
                                    />
                                    <FormattedMessage id="InvitationAgain.UseMyEmailAsFrom" defaultMessage="Use my email in the From message"/>

                                    <button class="btn btn-info btn-lg"
                                        style={{margin: "10px 10px 10px 10px"}}
                                        onClick={() => {this.inviteResend()}}>
                                        <FormattedMessage id="EventParticipant.SendAgainInvitation" defaultMessage="Send again"/>
                                    </button>
                                </div>
                                <div class="row">
                                    {this.state.participant.messageResend && <div class="alert alert-info" style={{margin: "10px 10px 10px 10px"}}>
                                        <span style={{fontStyle: "italic"}}>
                                            {this.state.participant.messageResend}
                                        </span>
                                       </div>}
                                </div>
                            </div>
                             <div class="row">
                                <h5><FormattedMessage id="InvitationAgain.UrlTitle" defaultMessage="Url"/></h5>
                            </div>

                            <div class="row">
                                <FormattedMessage id="InvitationAgain.GetTheUrlExplanation" defaultMessage="Get the URL, and use it"/>
                            </div>
                            <div class="row">
                                <span
                                    style={{fontSize: "small", fontStyle:"italic", marginLeft:"10px"}}
                                    title={intl.formatMessage({id: "EventParticipant.TitleUrlInvitation", defaultMessage: "Copy the URL in your clipboard. Paste it and Use it in a direct email"})}>

                                    {this.state.participant.urlInvitation}
                                    <Files onClick={() => {navigator.clipboard.writeText(this.state.participant.urlInvitation)}} />
                                </span>
                            </div>

                    </ModalWrapper>
                </td></tr>
            </table>);
	}


    // Send again the invitation
    inviteResend() {
	    console.log("InvitationAgain.inviteResend");
        this.setState({inprogress: true, 'messageResend':''});
        let param={
            eventId: this.state.event.id,
            participantId: this.state.participant.id,
            subject: this.state.subject,
            message: this.state.message,
            useMyEmailAsFrom:  this.state.participant.useMyEmailAsFrom
        };
        let restCallService = FactoryService.getInstance().getRestCallService();
        restCallService.postJson('/api/event/invite/resend', this, param, httpPayload =>{
            // httpPayload.trace("AdminTranslator.completeDictionary");
            this.setState({inprogress: false });
            // retrieve the participant
            let event = this.state.event;
            let participantEvent;
            for (let i in event.participants) {
                let participantIndex = event.participants[i];
                if (participantIndex.id === this.state.participant.id) {
                    participantEvent=participantIndex;
                }
            }
            const intl = this.props.intl;
            if (httpPayload.isError()) {
                participantEvent.messageResend="Server connection error";
                this.setState({ message: "Server connection error"});
            } else if (httpPayload.getData().status === "INVITATIONSENT"){
                participantEvent.messageResend= intl.formatMessage({id: "EventParticipants.EmailSentOk",defaultMessage: "Email sent"});
            } else {
                 participantEvent.messageResend= intl.formatMessage({id: "EventParticipants.EmailSentError",defaultMessage: "Email error"});
            }
            this.setState({event: event});
        });
	}
}
export default injectIntl(InvitationAgain);
