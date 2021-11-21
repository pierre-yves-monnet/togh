// -----------------------------------------------------------
//
// BodyTogh
//
// Manage the main Tog Home application
//
// -----------------------------------------------------------

import React from 'react';

import { IntlProvider, FormattedMessage } from "react-intl";


import Login 			    from 'Login';
import Banner 			    from 'Banner';
import Footer 			    from 'Footer';
import Menu 			    from 'Menu';
import {MENU_NAME} 		    from 'Menu';
import {FILTER_EVENT}	    from 'EventsList';
import RegisterNewUser 	    from 'RegisterNewUser';
import ResetPassword 	    from 'ResetPassword';
import EventsList 		    from 'EventsList';
import Event 			    from 'event/Event';
import AdminHome 		    from 'administration/AdminHome';
import AdminUsers 		    from 'administration/AdminUsers';
import AdminLogConnection	from 'administration/AdminLogConnection';
import MyProfile		    from 'user/MyProfile.jsx'


import FactoryService from './service/FactoryService';

import fr from "./lang/fr.json";
import en from "./lang/en.json";
import pt from "./lang/pt.json";
import de from "./lang/de.json";
import el from "./lang/el.json";
import hi from "./lang/hi.json";
import it from "./lang/it.json";
import ko from "./lang/ko.json";
import ja from "./lang/ja.json";
import ar from "./lang/ar.json";
import es from "./lang/es.json";


const FRAME_NAME = {
		EVENTS_LIST: 	"frameListEvents",
		EVENT:			"frameEvent",
		ADMINISTRATION: "frameAdministration",
		ADMINISTRATION_USERS:"frameAdministrationUsers",
		ADMINISTRATION_LOGCONNECTION: "frameAdministrationLogCOnnection",
		MY_PROFILE:		"frameMyProfile"
};

const messages = {
	    'fr': fr,
	    'en': en,
		'pt': pt,
		'de': de,
		'el': el,
		'hi': hi,
		'it': it,
		'ko': ko,
		'ja': ja,
		'ar': ar,
		'es': es
		
	};
	

class BodyTogh extends React.Component {
	
 

	constructor( props ) {
		super();
		console.log("BodyTogh.constructor");

		this.authCallback = this.authCallback.bind(this);

		this.homeSelectEvent = this.homeSelectEvent.bind(this)
		
		this.clickMenu                  = this.clickMenu.bind( this )
		this.showMenu                   = this.showMenu.bind( this );
		this.changeLanguage             = this.changeLanguage.bind( this );
		this.getInfoOnNewUser           = this.getInfoOnNewUser.bind( this );

		// this is mandatory to have access to the variable in the method... thank you React!   
		// this.connect = this.connect.bind(this);
		// currentEventId : we keep the ID here, but we don't load it. Component Event will be call, and it will be in charge to load it.
		this.state = { frameContent: FRAME_NAME.EVENTS_LIST,
						showmenu : true, 
						sizeMenu:  '10%',
						showLoginPanel : true,
						showRegisterUserPanel:true,
						showRegisterUserForm:false,
						readOnlyRegisterEmail:false,
						currentEventId : null,
						language: props.language,
						ignoreAction:false,
						titleFrame: 'EVENTS'};
		// action: when a URL with an action arrive (like "resetpassword"), we manage it.
		// But after this management, we stay in the same object, and we do not want to manage this action anymore
		// so, the toggle "ignoreAction" is set to true
		const params = new URLSearchParams(window.location.search)
		let action 		= params.get('action');
		let eventId 	= params.get("eventid");
		let email 		= params.get("email");

		if (action && action === 'invitedNewUser') {
			// switch to the register user page
			console.log("NewUser: redirect him directly to the register page!")
			this.state.showLoginPanel		= false;
			this.state.showRegisterUserForm	= true;
			this.state.defaultLoginEmail	= email;
			this.state.currentEventId		= eventId;
			if (eventId)
				this.state.frameContent		= FRAME_NAME.EVENT;
            let invitationStamp 		    = params.get('invitationStamp');

		    // maybe the user is registered now?
			this.getInfoOnNewUser( email, invitationStamp );
			
		}
		if (action && action === 'invitedUser') {
			// switch to the register user page
			console.log("NewUser: redirect him directly to the register page!")
			this.state.showLoginPanel		= true;
			this.state.showRegisterUserForm	= false;
			this.state.defaultLoginEmail	= email;	
			this.state.currentEventId		= eventId;
			if (eventId)
				this.state.frameContent		= FRAME_NAME.EVENT;		
		}
	}


// { this.state.frameContent == 'frameEvents' && <EventsList selectEvent={this.homeSelectEvent} />}
	// 			{ this.state.frameContent == 'event' &&	<Event eventid={this.state.currentEventId} /> }
	render() {
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();
		// console.log("BodyTogh.render isConnected="+authService.isConnected()+" frameContent=["+this.state.frameContent+"] sizeMenu["+this.state.sizeMenu+"]");
		
		const params = new URLSearchParams(window.location.search)
		let action = params.get('action');
		if (this.state.ignoreAction) {
			action='';
		}
		
		if (action && action === 'resetpassword') {
			const uuid = params.get('uuid');
			return (<IntlProvider locale={this.state.language}  messages={messages[ this.state.language  ]} >	
						<div>
							<Banner language={this.state.language} changeLanguage={this.changeLanguage} />						
							<div class="container">
		  						<div class="row">
									<div class="col-sm-5" >
										<h1><FormattedMessage id="BodyTogh.ResetYourPassword" defaultMessage="Reset your password" /></h1>
										
									<br/><br/>
										<img  style={{"float": "right"}} src="img/togh.jpg" style={{width:350}} />
									</div>
									<div class="col-sm-5" >
										<ResetPassword uuid= {uuid}  authCallback={this.authCallback}/>
									</div>
									<div class="col-sm-2" ></div>
								</div>
							</div>
						</div>
					</IntlProvider>);
		}


		// ------------------------------------------- Not connected, connection or register user
		else if (authService.isConnected() === false) {
			// Explanation are in the registerNewUser
			console.log("BodyTog.render: NonConnected - LoginPanel:"+this.state.showLoginPanel+" showRegisterUserPanel:"+this.state.showRegisterUserPanel);
			return (	
				<IntlProvider locale={this.state.language}  messages={messages[ this.state.language  ]} >			
					<div>
						<Banner language={this.state.language} changeLanguage={this.changeLanguage} />						
						<div class="container">
	  						{this.state.currentEventId && <div class="row" >
									<div class="col-sm-12" >
									 	<div style={{border:"1px solid",
													margin: "5px 5px 5px 5px", 
													padding: "10px 10px 10px 10px", 
													backgroundColor: "#337ab7", 
													borderColor: "#2e6da4",
													color: "#ffffff"}}>
                							<center>
												<FormattedMessage id="BodyTogh.YouAreInvited" defaultMessage="You are invited to an event! Register or Connect to access it" />
											</center>
										</div>	
									</div>
								</div> }
								
	  						<div class="row">
								<div class="col-sm-2" style={{fontFamily: "Brush Script MT, cursive", fontSize:"40px"}} >
									<FormattedMessage id="BodyTogh.welcome" defaultMessage="Welcome to Togh" />
									
								<br/><br/>
									<img  style={{"float": "right"}} src="img/togh.jpg" style={{width:350}} />
								</div>
									
								<div class="col-sm-5">	
									{this.state.showLoginPanel && 
										<Login authCallback={this.authCallback}
											defaultLoginEmail={this.state.defaultLoginEmail} 
										/>}
								</div>
								<div class="col-sm-5">
									{this.state.showRegisterUserPanel && 
										<RegisterNewUser authCallback={this.authCallback} 
											showRegisterUserForm={this.state.showRegisterUserForm}
											defaultLoginEmail={this.state.defaultLoginEmail}
											readOnlyRegisterEmail={this.state.readOnlyRegisterEmail}
										/>}
									
								</div>
							  
							</div>
						</div>
						<Footer language={this.state.language} />					
					</div>  
				</IntlProvider>
			)
		}
		else { 
			// we are connected, display the frame now
			const styleMenu = {
				width: this.state.sizeMenu,       
				"verticalAlign": "top", 
				"borderRight": "2px solid #194063",
				"paddingLeft" : "30px"
	        };
	
			return (
				<IntlProvider locale={this.state.language}  messages={messages[ this.state.language  ]} >
				<div>
					<Banner language={this.state.language} changeLanguage={this.changeLanguage}/>
					<div class="row">
						<div class="col-xs-12">
							<table style={{width: "100%", "height": "100%"}}>
								<tr>
									<td style={styleMenu} >
										<Menu showMenu={this.showMenu} clickMenu={this.clickMenu} authCallback={this.authCallback}/>
									</td>
									<td style={{padding: "10px", "verticalAlign": "top"}} >
										{ this.state.frameContent === FRAME_NAME.EVENTS_LIST &&
										    <EventsList homeSelectEvent={this.homeSelectEvent}
										        filterEvents={this.state.filterEvents}
										        titleFrame={this.state.titleFrame}/>}
										{ this.state.frameContent === FRAME_NAME.EVENT && <Event eventid={this.state.currentEventId} />}
										{ this.state.frameContent === FRAME_NAME.MY_PROFILE && <MyProfile  />}
										{ this.state.frameContent === FRAME_NAME.ADMINISTRATION && <AdminHome />}
										{ this.state.frameContent === FRAME_NAME.ADMINISTRATION_USERS && <AdminUsers />}
										{ this.state.frameContent === FRAME_NAME.ADMINISTRATION_LOGCONNECTION && <AdminLogConnection />}

									</td>
								</tr>
							</table>
						</div>
					</div>	
					<Footer language={this.state.language} />
				</div>	
				</IntlProvider>	
			);
		}
	} // end render
	
	authCallback( login ) {
		console.log("BodyTogh.logcallback: "+login );
		// at this moment, we ignore the action on the URL
		this.setState( { ignoreAction: true }); 
	}
	
	homeSelectEvent( eventId ) {
		console.log("BodyTogh.selectEvent: get it, an event is selected :"+eventId);
		this.setState( {'currentEventId' : eventId, 'frameContent': FRAME_NAME.EVENT });
	}

    /**
    *
    */
	clickMenu( menuName ) {
		console.log("BodyTogh.clickMenu: menuAction=["+menuName+"]");
      	if (menuName === MENU_NAME.EVENTS_LIST) {
		    this.setState( {currentEventId : null,
		            frameContent: FRAME_NAME.EVENTS_LIST,
		            filterEvents: FILTER_EVENT.MY_EVENTS,
		            titleFrame: 'EVENTS'
		         });
        } else if (menuName === MENU_NAME.MY_INVITATIONS) {
		    this.setState( {currentEventId : null,
		        frameContent: FRAME_NAME.EVENTS_LIST,
		        filterEvents: FILTER_EVENT.MY_INVITATIONS,
		        titleFrame: 'MYINVITATIONS'
		        });
		} else if (menuName === MENU_NAME.MY_PROFILE) {
			this.setState( {'currentEventId' : null, 'frameContent': FRAME_NAME.MY_PROFILE });
        } else if (menuName === MENU_NAME.ADMINISTRATION) {
            this.setState( {'currentEventId' : null, 'frameContent': FRAME_NAME.ADMINISTRATION });
        } else if (menuName === MENU_NAME.ADMINISTRATION_USERS) {
            this.setState( {'currentEventId' : null, 'frameContent': FRAME_NAME.ADMINISTRATION_USERS });
        } else if (menuName === MENU_NAME.ADMINISTRATION_LOGCONNECTION) {
            this.setState( {'currentEventId' : null, 'frameContent': FRAME_NAME.ADMINISTRATION_LOGCONNECTION });
        } else {
        	console.log("BodyTogh.clickMenu: Unknow menu=["+menuName+"]");
        }
	}
	
	
	showMenu( isVisible ) {
		console.log("BodyTogh.showMenu");
		this.setState( {'sizeMenu' : (isVisible? "15%": "2%") } );
	}

	changeLanguage( newlanguage ) {
		console.log("BodyTogh.changeLanguage "+newlanguage);
		this.setState( {'language' : newlanguage } );
	}

	getInfoOnNewUser( email, invitationStamp ) {
        console.log("BodyTogh.getInfoOnNewUser");
        var restCallService = FactoryService.getInstance().getRestCallService();

   		restCallService.postJson('/api/userinfo', this, {email: email, invitationStamp:invitationStamp}, httpPayload => {
            httpPayload.trace("BodyTogh.getInfoOnNewUserCallback");
            if (httpPayload.isError()) {
                console.log("BodyTogh.getInfoOnNewUserCallback: ERROR ");
            } else if (httpPayload.getData().isUser) {
                let isInvited = httpPayload.getData().isInvited;
                console.log("BodyTogh.getInfoOnNewUserCallback: isUser- "+httpPayload.getData().isUser+" isInvited:"+isInvited);
                this.setState( {showLoginPanel: !isInvited,
                                showRegisterUserPanel: isInvited,
                                showRegisterUserForm: isInvited,
                                readOnlyRegisterEmail:isInvited});
            }
        });
    }

}

export default BodyTogh;
