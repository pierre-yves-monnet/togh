// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import Login from './Login';


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
		console.log("menu.constructor");
		this.state = { showMenu: true, events: [], 'wayOfConnect':props.wayOfConnect};
		this.authCallback = this.authCallback.bind(this);
	}
		
	setVisibleMenu( setNewState ) {
		console.log("Menu.setVisibleMenu "+setNewState)
		this.setState ( { showMenu: setNewState} ); 
		this.props.showMenu( setNewState );
	}
		// -------------------------------------------- render
	render() {
		console.log("Menu.render");

		if (this.state.showMenu) {
			return ( <div  > 
				
				<div style={{float: "right"}}>
					<a onClick={() =>this.setVisibleMenu(false)} href="/#"><span class="glyphicon glyphicon-chevron-left" href="/#"></span></a>
				 </div>
				&nbsp;<p/>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#">
					<FormattedMessage id="Menu.Events" defaultMessage="Events" />
				</a><p/>
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#">
					<FormattedMessage id="Menu.MyInvitations" defaultMessage="My Invitations" />
				</a><p/>
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#">
					<FormattedMessage id="Menu.MyFriends" defaultMessage="My Friends" />
				</a><p/>
				<a onClick={() =>this.props.clickMenu( 'eventlist')} href="/#">
					<FormattedMessage id="Menu.MyProfile" defaultMessage="My Profile" />
				</a><p/>
				<a onClick={() =>this.props.clickMenu( MENU_NAME.ADMINISTRATION )} href="/#">
					<FormattedMessage id="Menu.Administration" defaultMessage="Administration" />
				</a><p/>
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
						<span class="glyphicon glyphicon-chevron-right" ></span></a>
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


	