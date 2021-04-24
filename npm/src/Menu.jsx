// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { ChevronCompactLeft, ChevronCompactRight } from 'react-bootstrap-icons';

import Login from './Login';
import FactoryService from './service/FactoryService';


export const MENU_NAME = {
		ADMINISTRATION: "Administration"
	}

class Menu extends React.Component {
	

	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.clickMenu( '<action>')
	// this.props.showMenu ( isVisible )
	// this.props.logout()
	constructor(props) {
		super();
		this.state = {  }
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
		var authService = FactoryService.getInstance().getAuthService();
		var user = authService.getUser();
		
		if (this.state.showMenu) {
			return ( <div  > 
				
				<div style={{float: "right"}}>
					<a onClick={() =>this.setVisibleMenu(false)} href="/#">
						<ChevronCompactLeft height="40px" width="40px"/></a>
				 </div>
				&nbsp;<p/>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#" class="toghmenu">
					<FormattedMessage id="Menu.Events" defaultMessage="Events" />
				</a>
				<div class="toghmenulabel">
					<FormattedMessage id="Menu.EventsExplanation" defaultMessage="Access all events you can access" />
				</div>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#" class="toghmenu">
					<FormattedMessage id="Menu.Join" defaultMessage="Join an event" />
				</a>
				<div class="toghmenulabel">
					<FormattedMessage id="Menu.JoinExplanation" defaultMessage="Search and join events" />
				</div>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#" class="toghmenu">
					<FormattedMessage id="Menu.MyInvitations" defaultMessage="My Invitations" />
				</a>
				<div class="toghmenulabel">
					<FormattedMessage id="Menu.InvitationExplanation" defaultMessage="Check your invitations, accept them." />
				</div>
				
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#"  class="toghmenu">
					<FormattedMessage id="Menu.MyFriends" defaultMessage="My Friends" />
				</a>
				<div class="toghmenulabel">
					<FormattedMessage id="Menu.MyFriendExplanation" defaultMessage="See all your friends, invite new, send messages." />
				</div>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#"  class="toghmenu">
					<FormattedMessage id="Menu.MyProfile" defaultMessage="My Profile" />
				</a>
				<div class="toghmenulabel">
					<FormattedMessage id="Menu.MyProfileExplanation" defaultMessage="Manage your preferences, set up an avatar." />
				</div>
				
				{ user.privilegeUser =="ADMIN" &&
					<div> 
						<a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION )} href="/#"  class="toghmenu">
							<FormattedMessage id="Menu.Administration" defaultMessage="Administration" />
						</a>
						<div class="toghmenulabel">
							<FormattedMessage id="Menu.AdmininstratorExplanation" defaultMessage="Administrator function." />
						</div>
					</div>
				}
				&nbsp;<p />
				&nbsp;<p />
				<Login authCallback={this.authCallback} />
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


	