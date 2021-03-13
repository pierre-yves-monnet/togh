// -----------------------------------------------------------
//
// BodyTogh
//
// Manage the main Tog Home application
//
// -----------------------------------------------------------

import React from 'react';

import { IntlProvider, FormattedMessage } from "react-intl";

import Login from './Login';
import Banner from './Banner';
import Footer from './Footer';
import Menu from './Menu';
import{ MENU_NAME} from './Menu';
import RegisterNewUser from './RegisterNewUser';
import EventsList from './EventsList';
import Event from './Event';
import AdminHome from './administration/AdminHome';


import FactoryService from './service/FactoryService';
import AuthService from './service/AuthService';

 

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
		
		this.clickMenu = this.clickMenu.bind( this )
		this.showMenu = this.showMenu.bind( this );
		this.changeLanguage = this.changeLanguage.bind( this );
		
		// this is mandatory to have access to the variable in the method... thank you React!   
		// this.connect = this.connect.bind(this);
		// currenteventid : we keep the ID here, but we don't load it. Component Event will be call, and it will be in charge to load it.
		this.state = { frameContent: 'frameEvents', 
						showmenu : true, 
						sizeMenu:  '10%',
						currenteventid : null,
						language: props.language};
			
	}

// { this.state.frameContent == 'frameEvents' && <EventsList selectEvent={this.homeSelectEvent} />}
	// 			{ this.state.frameContent == 'event' &&	<Event eventid={this.state.currenteventid} /> }
	render() {
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();
		console.log("BodyTogh.render isConnected="+authService.isConnected()+" frameContent=["+this.state.frameContent+"] sizeMenu["+this.state.sizeMenu+"]");

	
		if (authService.isConnected() === false) {
			return (	
				<IntlProvider locale={this.state.language}  messages={messages[ this.state.language  ]} >			
					<div>
						<Banner language={this.state.language} changeLanguage={this.changeLanguage} />
	
						<div class="container">
	  						<div class="row">
								<div class="col-sm-2" >
									<FormattedMessage id="BodyTogh.welcome" defaultMessage="Welcome to Togh D" />
									
								<br/><br/>
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
						<Footer language={this.state.language} />					
					</div>  
				</IntlProvider>
			)
		};
	
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
									{ this.state.frameContent === 'frameEvents' && <EventsList homeSelectEvent={this.homeSelectEvent} />}
									{ this.state.frameContent === 'event' && <Event eventid={this.state.currenteventid} />}
									{ this.state.frameContent === 'frameAdministration' && <AdminHome />}
			
								</td>
							</tr>
						</table>
					</div>
				</div>	
				<Footer language={this.state.language} />
			</div>	
			</IntlProvider>	
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
		if (menuName === MENU_NAME.ADMINISTRATION)
				this.setState( {'currenteventid' : null, 'frameContent':'frameAdministration' });
		else
			this.setState( {'currenteventid' : null, 'frameContent':'frameEvents' });
	}
	
	
	showMenu( isVisible ) {
		console.log("BodyTogh.showMenu");
		this.setState( {'sizeMenu' : (isVisible? "15%": "2%") } );
	}

	changeLanguage( newlanguage ) {
		console.log("BodyTogh.changeLanguage "+newlanguage);
		this.setState( {'language' : newlanguage } );
	}
	
	
	
	

	
};

export default BodyTogh;
	
  