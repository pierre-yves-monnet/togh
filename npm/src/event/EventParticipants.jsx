// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { Select, Tag, TextInput}      from 'carbon-components-react';
import { Clipboard, ClipboardCheck, ClipboardX} from 'react-bootstrap-icons';

import Invitation                       from 'event/Invitation';
import InvitationAgain                  from 'event/InvitationAgain';
import FactoryService 		            from 'service/FactoryService';
import EventSectionHeader 		        from 'component/EventSectionHeader';


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
		this.renderLargeScreen      = this.renderLargeScreen.bind( this );
		this.renderSmallScreen      = this.renderSmallScreen.bind( this );
	}	


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	render() {
		let factory = FactoryService.getInstance();
   	    let mobileService = factory.getMobileService();
		let authService = factory.getAuthService();

		let mySelfUser= authService.getUser();

		// search my role in this event
		let totalParticipants=0;
		let myRoleInTheEvent='';
		for (let i in this.state.event.participants) {
		    let participant = this.state.event.participants[ i ];
		    if (participant.user.id === mySelfUser.id) {
		        myRoleInTheEvent=participant.role;
		    }
		    if (participant.status !== 'INVITED' && participant.status !== 'STATUS_LEFT' && participant.partOf === 'PARTOF') {
		        if (participant.numberOfParticipants>0)
		            totalParticipants += parseInt(participant.numberOfParticipants);
		    }
		}
		let administratorEvent=myRoleInTheEvent === ROLE_OWNER || myRoleInTheEvent === ROLE_ORGANIZER;
		// console.log("EventParticipant.render:  Participants:"+JSON.stringify(this.state.event.participants) );
		let headerSection = (
        			<EventSectionHeader id="participant"
        				image="img/btnParticipants.png"
        				title={<FormattedMessage id="EventParticipant.MainTitleParticipant" defaultMessage="Participants" />}
        				showPlusButton={false}
        				userTipsText={<FormattedMessage id="EventParticipant.ParticipantTip" defaultMessage="Invite participant to the event" />}
        			/>
        		);
		// show the list
		let listParticipantsHtml = null;
        if (mobileService.isLargeScreen())
             listParticipantsHtml = this.renderLargeScreen(totalParticipants, administratorEvent);
        else
            listParticipantsHtml = this.renderSmallScreen(totalParticipants, administratorEvent);

		return (
		    <div>
                <div>
        			{headerSection}
                    <div style={{float: "right"}}>
                        <Invitation event={this.state.event} participantInvited={this.participantInvited}/>
                    </div>
                </div>
                {listParticipantsHtml}



            </div>
            );
		}


	renderLargeScreen( totalParticipants,administratorEvent ) {
		const intl = this.props.intl;
        let factory = FactoryService.getInstance();
        let authService = factory.getAuthService();
        let mySelfUser= authService.getUser();

	    return (
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
                    return (<tr key={index} style={{borderBottom: "aliceblue", borderStyle: "solid"}}>
                        <td style={{borderBottom: "aliceblue", borderStyle: "solid"}}>
                            {item.user.label}
                        </td><td  style={{minWidth:"120px"}}>
                            {item.status === 'INVITED' && (
                                <InvitationAgain  event={this.state.event} participant={item}/>
                               )}
                            {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' && (
                                <div class="btn-group btn-group-sm Basic radio toggle button group" role="group" aria-label="participants">
                                    <input type="radio"
                                        class="btn-check"
                                        name={"btnpartofradio-"+item.id}
                                        id={"btnpartof1-"+item.id}
                                        autoComplete="off"
                                        checked={item.partOf === "DONTKNOW"}
                                        onChange={() => {
                                            this.setChildAttribut( "numberOfParticipants",0, item);
                                            this.setChildAttribut("partOf", "DONTKNOW", item)
                                                }
                                        }/>

                                    <label class="btn btn-outline-primary"
                                        for={"btnpartof1-"+item.id}>
                                        <Clipboard/>&nbsp;<FormattedMessage id="EventParticipant.PartOfDoNotKnow" defaultMessage="Do not know" />
                                    </label>

                                    <input type="radio"
                                        class="btn-check"
                                        name={"btnpartofradio-"+item.id}
                                        id={"btnpartof2-"+item.id}
                                        autoComplete="off"
                                        checked={item.partOf === "PARTOF"}
                                        onChange={() => {
                                            if ( item.numberOfParticipants===0 ) {
                                                this.setChildAttribut( "numberOfParticipants",1, item);
                                            }
                                            this.setChildAttribut("partOf", "PARTOF", item)
                                        }
                                        }/>
                                    <label class="btn btn-outline-primary"
                                        for={"btnpartof2-"+item.id}>
                                        <ClipboardCheck color="#87f787"/>&nbsp;<FormattedMessage id="EventParticipant.PartOfParticipate" defaultMessage="Participate" />
                                    </label>

                                    <input type="radio" class="btn-check"
                                        name={"btnpartofradio-"+item.id}
                                        id={"btnpartof3-"+item.id}
                                        autoComplete="off"
                                        checked={item.partOf === "NO"}
                                        onChange={() => {
                                            this.setChildAttribut( "numberOfParticipants",0, item);
                                            this.setChildAttribut("partOf", "NO",item)
                                            }
                                        }/>
                                    <label class="btn btn-outline-primary"
                                        for={"btnpartof3-"+item.id}>
                                        <ClipboardX color="red"/>&nbsp;<FormattedMessage id="EventParticipant.PartOfNo" defaultMessage="No" />
                                    </label>
                                </div>
                                )}
                         </td><td>
                             {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' && item.partOf === 'PARTOF' && (
                                 <TextInput
                                        onChange={(event) => this.setChildAttribut( "numberOfParticipants", event.target.value, item )}
                                        keyboardType='numeric'
                                        value={item.numberOfParticipants}
                                        disabled={(! (item.partOf === 'PARTOF' && (administratorEvent || item.user.id === mySelfUser.id)))}
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
            </table> )
    }

    //---------------------------------------
    //
	renderSmallScreen( totalParticipants,administratorEvent ) {
		const intl = this.props.intl;
        let factory = FactoryService.getInstance();
        let authService = factory.getAuthService();
        let mySelfUser= authService.getUser();

        return (
            <div>
                <div class="row" style={{height: "40px"}}>
                    <FormattedMessage id="EventParticipant.Participation" defaultMessage="Participation"/> ( {totalParticipants} )
                </div>
                {this.state.event.participants && this.state.event.participants.map( (item, index) => {
                    return (
                        <div class="toghBlock"
                    				    style={{marginTop: "20px", border: "2px solid rgba(0,0,0,.125)", padding: "15px"}}
                    				    key={index}>
                            <table>
                            <tr>
                                <td>
                                    {item.user.label}
                                </td>
                                <td  >
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
                                <td  >
                                    {item.status==='ACTIF' && <Tag  type="green" title={intl.formatMessage({id: "EventParticipant.TitleActiveParticipant",defaultMessage: "Active participant"})}><FormattedMessage id="EventParticipant.Actif" defaultMessage="Actif"/></Tag>}
                                    {item.status==='LEFT' && <Tag  type="red" title={intl.formatMessage({id: "EventParticipant.TitleLeft",defaultMessage: "The participant left the event"})}><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></Tag>}
                                </td>
                            </tr>
                            <tr>
                                {item.status === 'INVITED' &&
                                    <td  style={{paddingLeft: "20px"}} colspan="3">
                                        <InvitationAgain  event={this.state.event} participant={item}/>
                                    </td>
                                }
                                {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' &&
                                    <td colspan="2">
                                        <div class="btn-group btn-group-sm Basic radio toggle button group" role="group" aria-label="Status" >
                                            <input type="radio"
                                                class="btn-check"
                                                name={"btnpartofradio-"+item.id}
                                                id={"btnpartof1-"+item.id}
                                                autocomplete="off"
                                                checked={item.partOf === "DONTKNOW"}
                                                onChange={() => {
                                                    this.setChildAttribut( "numberOfParticipants",0, item);
                                                    this.setChildAttribut("partOf", "DONTKNOW", item)
                                                        }
                                                }/>

                                            <label class="btn btn-outline-primary"
                                                for={"btnpartof1-"+item.id}>
                                                <Clipboard/>&nbsp;<FormattedMessage id="EventParticipant.PartOfDoNotKnow" defaultMessage="Do not know" />
                                            </label>

                                            <input type="radio"
                                                class="btn-check"
                                                name={"btnpartofradio-"+item.id}
                                                id={"btnpartof2-"+item.id}
                                                autocomplete="off"
                                                checked={item.partOf === "PARTOF"}
                                                onChange={() => {
                                                    {
                                                        if ( item.numberOfParticipants===0 ) {
                                                            this.setChildAttribut( "numberOfParticipants",1, item);
                                                        }
                                                        this.setChildAttribut("partOf", "PARTOF", item)
                                                    }
                                                }
                                                }/>
                                            <label class="btn btn-outline-primary"
                                                for={"btnpartof2-"+item.id}>
                                                <ClipboardCheck color="#87f787"/>&nbsp;<FormattedMessage id="EventParticipant.PartOfParticipate" defaultMessage="Participate" />
                                            </label>

                                            <input type="radio" class="btn-check"
                                                name={"btnpartofradio-"+item.id}
                                                id={"btnpartof3-"+item.id}
                                                autocomplete="off"
                                                checked={item.partOf === "NO"}
                                                onChange={() => {
                                                    this.setChildAttribut( "numberOfParticipants",0, item);
                                                    this.setChildAttribut("partOf", "NO",item)
                                                    }
                                                }/>
                                            <label class="btn btn-outline-primary"
                                                for={"btnpartof3-"+item.id}>
                                                <ClipboardX color="red"/>&nbsp;<FormattedMessage id="EventParticipant.PartOfNo" defaultMessage="No" />
                                            </label>
                                        </div>
                                    </td>
                                }
                                {item.status !== 'INVITED' && item.status !== 'STATUS_LEFT' && item.partOf === 'PARTOF' &&
                                    <td style={{paddingLeft: "10px"}}>
                                         <TextInput
                                                onChange={(event) => this.setChildAttribut( "numberOfParticipants", event.target.value, item )}
                                                keyboardType='numeric'
                                                value={item.numberOfParticipants}
                                                disabled={(! (item.partOf === 'PARTOF' && (administratorEvent || item.user.id === mySelfUser.id)))}
                                                placeholder={intl.formatMessage({id: "EventParticipant.NumberOfParticipants", defaultMessage: "Number of participants"})}
                                                style={{width: "100px"}}
                                              />

                                    </td>
                                }
                            </tr>
                            </table>
                        </div>
                    )
                    } )
                }
            </div>
        )
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


	

}		
export default injectIntl(EventParticipants);
