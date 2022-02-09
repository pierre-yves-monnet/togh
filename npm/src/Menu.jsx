// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { ChevronCompactLeft, ChevronCompactRight, CalendarWeek, EnvelopeOpen, Person,Speedometer2 } from 'react-bootstrap-icons';

import Login 			from './Login';
import FactoryService 		from './service/FactoryService';


export const MENU_NAME = {
		ADMINISTRATION: "Administration",
		ADMINISTRATION_USERS: "AdministrationUsers",
		ADMINISTRATION_LOGCONNECTION: "AdministrationLogConnection",
		MY_PROFILE : "MyProfile",
		MY_INVITATIONS: "MyInvitations",
		EVENTS_LIST: "eventsList"
	}

class Menu extends React.Component {
	

	// in props, a function must be give to the call back. When we click on a line, we call
	// clickMenu is in BodyTogh.jsx
	// this.props.clickMenu( '<action>')
	// this.props.showMenu ( isVisible )
	// this.props.logout()
	constructor(props) {
		super();
		// console.log("menu.constructor");
		this.state = { showMenu: true, events: [], 'wayOfConnect':props.wayOfConnect};
		this.authCallback = this.authCallback.bind(this);
	}
		
	setVisibleMenu( setNewState ) {
		// console.log("Menu.setVisibleMenu "+setNewState)
		this.setState ( { showMenu: setNewState} ); 
		this.props.showMenu( setNewState );
	}
		// -------------------------------------------- render
	render() {
		// console.log("Menu.render");
		let authService = FactoryService.getInstance().getAuthService();
		let user = authService.getUser();
	    let mobileService = FactoryService.getInstance().getMobileService();
        let styleSmallMenu = {
                    height: "40px",
                    fontSize: "24px",
                    margin:"0px",
                    textAlign:"center"
                };
		if (this.state.showMenu) {
			return ( <div  > 
				<div style={{display:"none"}}>Menu.jsx</div>
				{mobileService.isLargeScreen() && <div style={{float: "right"}}>
					<a onClick={() =>this.setVisibleMenu(false)} href="/#">
						<ChevronCompactLeft height="40px" width="40px"/></a>
				 </div>
				 }
				&nbsp;<p/>

				{mobileService.isLargeScreen() &&
                    <div>
                        <a onClick={() =>this.props.clickMenu( MENU_NAME.EVENTS_LIST)} href="/#" class="toghMenu">
                            <FormattedMessage id="Menu.Events" defaultMessage="Events" />
                        </a>
                        <div class="toghMenuLabel">
                            <FormattedMessage id="Menu.EventsExplanation" defaultMessage="Access all events you can access" />
                        </div>

                        <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_INVITATIONS)} href="/#" class="toghMenu">
                                        <FormattedMessage id="Menu.MyInvitations" defaultMessage="My Invitations" />
                        </a>
                        <div class="toghMenuLabel">
                            <FormattedMessage id="Menu.InvitationExplanation" defaultMessage="Check your invitations, accept them." />
                        </div>

                        <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_PROFILE )} href="/#"  class="toghMenu">
                            <FormattedMessage id="Menu.MyProfile" defaultMessage="My Profile" />
                        </a>
                        <div class="toghMenuLabel">
                            <FormattedMessage id="Menu.MyProfileExplanation" defaultMessage="Manage your preferences, set up an avatar." />
                        </div>
                        <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_INVITATIONS)} href="/#" class="toghMenu">
                                        <FormattedMessage id="Menu.MyInvitations" defaultMessage="My Invitations" />
                        </a>
                        <div class="toghMenuLabel">
                            <FormattedMessage id="Menu.InvitationExplanation" defaultMessage="Check your invitations, accept them." />
                        </div>

                        <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_PROFILE )} href="/#"  class="toghMenu">
                            <FormattedMessage id="Menu.MyProfile" defaultMessage="My Profile" />
                        </a>
                        <div class="toghMenuLabel">
                            <FormattedMessage id="Menu.MyProfileExplanation" defaultMessage="Manage your preferences, set up an avatar." />
                        </div>
                        { user.privilegeUser === "ADMIN" &&
                            <div>
                                <a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION )} href="/#"  class="toghMenu">
                                    <FormattedMessage id="Menu.Administration" defaultMessage="Administration" />
                                </a>
                                <ul>
                                    <li>
                                        <a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION_USERS )} href="/#" class="toghSubMenu">
                                            <FormattedMessage id="Menu.AdministrationUsers" defaultMessage="Users" />
                                        </a>
                                    </li>
                                    <li>
                                        <a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION_LOGCONNECTION )} href="/#" class="toghSubMenu">
                                            <FormattedMessage id="Menu.AdministrationLogConnection" defaultMessage="Connection" />
                                        </a>
                                    </li>
                                </ul>
                                <div class="toghMenuLabel">
                                    <FormattedMessage id="Menu.AdmininstratorExplanation" defaultMessage="Administrator function." />
                                </div>
                            </div>
                        }
                         &nbsp;<p />
                        &nbsp;<p />
                        <Login authCallback={this.authCallback} />
                    </div>

                }
                { ! mobileService.isLargeScreen() &&
                    <div class="row">
                        <table width="100%" style={{marginLeft:"20px", marginRight:"20px"}}>
                        <tr>
                        <td style={styleSmallMenu}>
                            <a onClick={() =>this.props.clickMenu( MENU_NAME.EVENTS_LIST)} href="/#">
                                <CalendarWeek/>
                            </a>

                        </td><td style={styleSmallMenu}>
                            <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_INVITATIONS)} href="/#" >
                                <EnvelopeOpen/>
                            </a>
                        </td><td style={styleSmallMenu}>
                            <a onClick={() =>this.props.clickMenu( MENU_NAME.MY_PROFILE )} href="/#"  >
                                <Person/>
                            </a>
                        </td>
                        { user.privilegeUser === "ADMIN" &&
                            <td style={styleSmallMenu}>
                                 <a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION )} href="/#"  >
                                    <Speedometer2/>
                                </a>
                            </td>
                         }
                        <td style={styleSmallMenu}>
                            <Login authCallback={this.authCallback} />
                        </td>
                        </tr>
                        </table>
                    </div>
                }




				


			</div>
			)
		} else {
			return ( 
				<div> 
					<div style={{float: "right"}}>
						<a onClick={() =>this.setVisibleMenu(true)} href="/#">
						<ChevronCompactRight height="40px" width="40px"/></a>
					</div>
				</div>
			)
		}
	}
    /*
     								<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#" class="toghMenu">
                    					<FormattedMessage id="Menu.Join" defaultMessage="Join an event" />
                    				</a>
                    				<div class="toghMenuLabel">
                    					<FormattedMessage id="Menu.JoinExplanation" defaultMessage="Search and join events" />
                    				</div>
*/

/*
     				<a onClick={() =>this.props.clickMenu( MENU_NAME.EVENTSLIST)} href="/#"  class="toghMenu">
       					<FormattedMessage id="Menu.MyFriends" defaultMessage="My Friends" />
       				</a>
       				<div class="toghMenuLabel">
       					<FormattedMessage id="Menu.MyFriendExplanation" defaultMessage="See all your friends, invite new, send messages." />
       				</div>
*/

	// 
	googleSignOut() {
		console.log("Menu.GoogleSignOut : start");
		window["generalGoogleSignOut"]();
		
	}
	authCallback( login ) {
		console.log("Menu.authCallback login="+login);
		// call the parent then
		this.props.authCallback( login );
	}
}
export default Menu;


	