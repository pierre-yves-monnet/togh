// -----------------------------------------------------------
//
// BodyTogh
//
// Manage the main Tog Home application
//
// -----------------------------------------------------------

import React from 'react';

import Login from './Login';
import Banner from './Banner';
import Menu from './Menu';
import RegisterNewUser from './RegisterNewUser';
import EventsList from './EventsList';
import Event from './Event';

import FactoryService from './service/FactoryService';
import AuthService from './service/AuthService';

class BodyTogh extends React.Component {
	constructor( props ) {
		super();
		this.state = {}
		console.log("BodyTogh.constructor");

		this.authCallback = this.authCallback.bind(this);

		this.homeSelectEvent = this.homeSelectEvent.bind(this)
		
		this.clickMenu = this.clickMenu.bind( this )
		this.showMenu = this.showMenu.bind( this );
		
		// this is mandatory to have access to the variable in the method... thank you React!   
		// this.connect = this.connect.bind(this);
		this.state = { frameContent: 'frameEvents', 
						showmenu : true, 
						sizeMenu:  '10%',
						currenteventid : null };
					
			
	}

// { this.state.frameContent == 'frameEvents' && <EventsList selectEvent={this.homeSelectEvent} />}
	// 			{ this.state.frameContent == 'event' &&	<Event eventid={this.state.currenteventid} /> }
	render() {
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();
		console.log("BodyTogh.render isConnected="+authService.isConnected()+" frameContent=["+this.state.frameContent+"] sizeMenu["+this.state.sizeMenu+"]");

	
		if (authService.isConnected() === false) {
			return (				
				<div>
					<Banner />

					<div class="container">
  						<div class="row">
							<div class="col-sm-2" >
								Welcome to Togh<br/><br/>
								<img  style={{"float": "right"}} src="img/togh.jpg" style={{width:350}} />
							</div>
								
							<div class="col-sm-5">	
								<Login authCallback={this.authCallback}/>
							</div>
							<div class="col-sm-5">
								<RegisterNewUser authCallback={this.authCallback}/>
							</div>
						  
						</div>
					</div>
				</div>  
			)
		};
	
		// we are connected, display the frame now
		const styleMenu = {
			width: this.state.sizeMenu,       
			"vertical-align": "top", 
			"border-right": "2px solid #194063",
			"padding-left" : "30px"
        };
		return (
			<div>
				<Banner />
				<div class="row">
					<table style={{width: "100%", "height": "100%"}}>
						<tr>
							<td style={styleMenu} >
								<Menu showMenu={this.showMenu} clickMenu={this.clickMenu} authCallback={this.authCallback}/>
							</td>
							<td style={{padding: "10px", "vertical-align": "top"}} >
								{ this.state.frameContent === 'frameEvents' && <EventsList homeSelectEvent={this.homeSelectEvent} />}
								{ this.state.frameContent === 'event' && <Event eventid={this.state.currenteventid} />}
		
							</td>
						</tr>
		
					</table>
				</div>	
			</div>		
		);
		
	} // end render
	
	authCallback( login ) {
		console.log("BodyTogh.logcallback: "+login );
		this.setState( {'currenteventid' : null }); 
	}
	
	homeSelectEvent( eventId ) {
		console.log("BodyTogh.selectEvent: get it, an event is selected :"+eventId);
		this.setState( {'currenteventid' : eventId, 'frameContent': 'event' });
	}

	clickMenu( menuName ) {
		console.log("BodyTogh.clickMenu: menuAction"+menuName);
		this.setState( {'currenteventid' : null, 'frameContent':'frameEvents' });
	}
	
	showMenu( isVisible ) {
		console.log("BodyTogh.showMenu");
		this.setState( {'sizeMenu' : (isVisible? "10%": "2%") } );
	}

	
	
	
	

	
};

export default BodyTogh;
	
  