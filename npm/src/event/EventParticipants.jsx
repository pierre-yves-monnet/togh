// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { Select, Tag, TextInput, Toggle}      from 'carbon-components-react';
import { Files }            from 'react-bootstrap-icons';
import Invitation           from 'event/Invitation';
import FactoryService 		from 'service/FactoryService';
import EventSectionHeader 		from 'component/EventSectionHeader';


export const ROLE_OWNER = 'OWNER';
export const ROLE_ORGANIZER = 'ORGANIZER';
export const ROLE_PARTICIPANT = 'PARTICIPANT';
export const ROLE_OBSERVER = 'OBSERVER';
export const ROLE_OUTSIDE = 'OUTSIDE';

export const STATUS_LEFT = 'LEFT';
export const STATUS_ACTIF = 'ACTIF';

const NAME_ENTITY = "participantlist";


class EventParticipants extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("EventParticipants.constructor");
		this.eventCtrl = props.eventCtrl;

		this.state = { 'event' : props.event 
						};
		// show : OFF, ON, COLLAPSE
		// console.log("EventParticipant.constructor ");
		this.setChildAttribut		= this.setChildAttribut.bind(this);
		this.participantInvited 	= this.participantInvited.bind( this );
	}	


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	render() {
		const intl = this.props.intl;
		var authService = FactoryService.getInstance().getAuthService();
		let mySelfUser= authService.getUser();

		// search my role in this event
		let totalParticipants=0;
		let myRoleInTheEvent='';
		for (let i in this.state.event.participants) {
		    let participant = this.state.event.participants[ i ];
		    if (participant.user.id === mySelfUser.id) {
		        myRoleInTheEvent=participant.role;
		    }
		    if (participant.status !== 'INVITED' && participant.status !== 'STATUS_LEFT' && participant.isPartOf) {
		        if (participant.numberOfParticipants>0)
		            totalParticipants += parseInt(participant.numberOfParticipants);
		    }
		}
		let administratorEvent=myRoleInTheEvent === ROLE_OWNER || myRoleInTheEvent === ROLE_ORGANIZER;
		console.log("EventParticipant.render:  Participants:"+JSON.stringify(this.state.event.participants) );
		let headerSection = (
        			<EventSectionHeader id="participant"
        				image="img/btnParticipants.png"
        				title={<FormattedMessage id="EventParticipant.MainTitleParticipant" defaultMessage="Participants" />}
        				showPlusButton={false}
        				userTipsText={<FormattedMessage id="EventParticipant.ParticipantTip" defaultMessage="Invite participant to the event" />}
        			/>
        		);
		// show the list
		return (
		    <div>
                <div>
        			{headerSection}
                    <div style={{float: "right"}}>
                        <Invitation event={this.state.event} participantInvited={this.participantInvited}/>
                    </div>
                </div>

                <table class="table table-striped toghtable">
                    <thead>
                        <tr >
                            <th><FormattedMessage id="EventParticipant.Person" defaultMessage="Person"/></th>
                            <th colspan="2"><FormattedMessage id="EventParticipant.Participation" defaultMessage="Participation"/> ( {totalParticipants} )</th>
                            <th><FormattedMessage id="EventParticipant.Role" defaultMessage="Role"/></th>
                            <th><FormattedMessage id="EventParticipant.Status" defaultMessage="Status"/></th>
                        </tr>
                    </thead>
                    {this.state.event.participants && this.state.event.participants.map( (item, index) => {
                        return (<tr key={index}>
                            <td>
                                {item.user !== '' && ( <span>{item.user.label}</span>)}

                                {item.status === 'INVITED' && (
                                    <span>
                                        <Tag type="teal">
                                            <FormattedMessage id="EventParticipant.InvitationInProgress" defaultMessage="Invitation in progress"/>
                                        </Tag>

                                        <span>
                                            <input type="checkbox"
                                                style={{marginLeft: "15px", marginRight:"5px"}}
                                               onChange={(event) => {
                                                       let rememberBool = event.target.value==='on';
                                                       item.useMyEmailAsFrom = rememberBool}}
                                               title={intl.formatMessage({id:"EventParticipant.UseMyEmailAsFrom", defaultMessage:"Use my email in the From message"})}
                                              />


                                            <button class="btn btn-info btn-xs "
                                                onClick={() => {this.inviteResend(item)}}>
                                                <FormattedMessage id="EventParticipant.SendAgainInvitation" defaultMessage="Send again"/>
                                            </button>
                                            <span style={{fontStyle: "italic"}}>
                                                {item.messageResend}
                                            </span>
                                        </span>
                                        <span
                                            style={{fontSize: "small", fontStyle:"italic", marginLeft:"10px"}}
                                            title={intl.formatMessage({id: "EventParticipant.TitleUrlInvitation", defaultMessage: "Copy the URL in your clipboard. Paste it and Use it in a direct email"})}>
                                            <FormattedMessage id="EventParticipant.UrlInvitation" defaultMessage="Url"/>
                                            <Files onClick={() => {navigator.clipboard.writeText(item.urlInvitation)}} />
                                        </span>
                                    </span>
                                   )}
                            </td>
                            <td style={{minWidth:"120px"}}>
                                 {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' && (
                                    <Toggle labelText="" aria-label=""
                                            toggled={item.isPartOf}
                                            selectorPrimaryFocus={item.isPartOf}
                                            labelA={<FormattedMessage id="EventParticipant.NotPartOf" defaultMessage="Not part of"/>}
                                            labelB={<FormattedMessage id="EventParticipant.PartOf" defaultMessage="Part Of"/>}
                                            onChange={(event) => this.setAttributCheckbox( "isPartOf", event, item )}
                                            disabled={(! (administratorEvent || item.user.id === mySelfUser.id))}
                                            id={ 'partof'+index} />
                                  )}
                             </td><td>
                                 {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' && (
                                     <TextInput
                                            onChange={(event) => this.setChildAttribut( "numberOfParticipants", event.target.value, item )}
                                            keyboardType='numeric'
                                            value={item.numberOfParticipants}
                                            disabled={(! (item.isPartOf === true && (administratorEvent || item.user.id === mySelfUser.id)))}
                                            placeholder={intl.formatMessage({id: "EventParticipant.NumberOfParticipants", defaultMessage: "Number of participants"})}
                                            style={{width: "100px"}}
                                          />
                                      )}
                            </td>
                            <td>
                            {item.role === ROLE_OWNER && (<div class="label label-info"><FormattedMessage id="EventParticipant.Owner" defaultMessage="Owner"/></div>)}
                            {item.status === STATUS_LEFT && (<div class="label label-info"><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></div>)}

                            { (item.role !== ROLE_OWNER && item.status !== STATUS_LEFT) && (
                                <Select labelText=""
                                    inline={true}
                                    disabled={ ( ! administratorEvent) }
                                    value={item.role}
                                    onChange={(event) => this.setChildAttribut( "role", event.target.value,item )}
                                        id="EventParticipants.role">
                                    <FormattedMessage id="EventParticipant.RoleOrganizer" defaultMessage="Organizer">
                                        {(message) => <option value={ ROLE_ORGANIZER }>{message}</option>}
                                    </FormattedMessage>
                                    <FormattedMessage id="EventParticipant.RoleParticipant" defaultMessage="Participant">
                                        {(message) => <option value="PARTICIPANT">{message}</option>}
                                    </FormattedMessage>
                                    <FormattedMessage id="EventParticipant.RoleObserver" defaultMessage="Observer">
                                        {(message) => <option value="OBSERVER">{message}</option>}
                                    </FormattedMessage>

                                </Select>
                                    )}
                        </td>
                        <td>
                            {item.status==='ACTIF' && <Tag  type="green" title={intl.formatMessage({id: "EventParticipant.TitleActiveParticipant",defaultMessage: "Active participant"})}><FormattedMessage id="EventParticipant.Actif" defaultMessage="Actif"/></Tag>}
                            {item.status==='INVITED' && <Tag  type="teal" title={intl.formatMessage({id: "EventParticipant.Titleinvited",defaultMessage: "Invited participant: no confirmation is received at this moment"})}><FormattedMessage id="EventParticipant.Invited" defaultMessage="Invited"/></Tag>}
                            {item.status==='LEFT' && <Tag  type="red" title={intl.formatMessage({id: "EventParticipant.TitleLeft",defaultMessage: "The participant left the event"})}><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></Tag>}
                        </td>
                        </tr>
                        )
                        } )
                    }


                </table>

            </div>
            );
		}
		
	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	setChildAttribut(name, value, item) {
        console.log("EventParticipant.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
    	this.eventCtrl.setAttribut(name, value, item, NAME_ENTITY+"/"+item.id);
    }

    setAttributCheckbox(name, event, item) {
        console.log("EventParticipant.setAttributCheckbox set " + name + "<=" + event.target.checked);
        if (event.target.checked)
            this.setChildAttribut(name, true, item)
        else
            this.setChildAttribut(name, false, item)
    }
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------

	participantInvited( participants ) {
		console.log("EventParticipant.participantinvited event="+JSON.stringify( this.state.event));
		var currentEvent = this.state.event;
		var newList = currentEvent.participants;
		for (var i in participants ) {		
			newList = newList.concat( participants[ i ]  );
		}
		currentEvent.participants = newList;
		this.setState( { "event" : currentEvent});
		this.props.updateEvent();
	}

	inviteResend(participant) {
	    console.log("AdminTranslator.completeDictionary:");
        this.setState({inprogress: true });
        participant.messageResend="";
        let param={
            eventId: this.state.event.id,
            participantId: participant.id,
            useMyEmailAsFrom: participant.useMyEmailAsFrom
        };
        let restCallService = FactoryService.getInstance().getRestCallService();
        restCallService.postJson('/api/event/invite/resend', this, param, httpPayload =>{
            httpPayload.trace("AdminTranslator.completeDictionary");
            this.setState({inprogress: false });
            // retrieve the participant
            let event = this.state.event;
            let participantEvent;
            for (let i in event.participants) {
                let participantIndex = event.participants[i];
                if (participantIndex.id === participant.id) {
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
export default injectIntl(EventParticipants);
