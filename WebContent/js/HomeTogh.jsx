// -----------------------------------------------------------
//
// TogHome
//
// Manage the main Tog Home application
//
// -----------------------------------------------------------

class HomeTogh extends React.Component {
	constructor() {
		super();
		this.state = { 'currenteventid' : null }
		console.log("HomeTog.constructor");
		this.homeSelectEvent = this.homeSelectEvent.bind(this)
		this.clickMenu = this.clickMenu.bind(this)
		
		// this is mandatory to have access to the variable in the method... thank you React!   
		// this.connect = this.connect.bind(this);
		this.state = { frameContent: "frameEvents", showmenu : true };
	}


	homeSelectEvent( eventId ) {
		console.log("HomeTogh.selectEvent: get it, an event is selected :"+eventId);
		this.setState( {'currenteventid' : eventId });
		this.state.frameContent = 'event';
	}

	clickMenu( menuName ) {
		console.log("HomeTogh.clickMenu: menuAction"+menuName);
		this.setState( {'currenteventid' : null });
		this.state.frameContent = 'frameEvents';
		
	}

	render2() {
		console.log("HomeTogh.render frameContent=["+this.state.frameContent+"]");


		return (
				<div class="row">
				<table style="width:100%">
				<tr>
					<td style="vertical-align: top;border-right: 3px solid #194063;vertical-align: top;" >
						<Menu />
						
					</td>
					<td>
						{ this.state.frameContent == 'frameEvents' && <EventsList selectEvent={this.homeSelectEvent} />}
						{ this.state.frameContent == 'event' &&	<Event eventid={this.state.currenteventid} /> }
					</td>
				</tr>
				</table>
				</div>
				)
	
	} // end render



	render() {
		console.log("HomeTogh.render frameContent=["+this.state.frameContent+"]");

		
		return (
			<div class="row">
			<table style={{width: "100%", "height": "100%"}}>
				<tr>
					<td style={{width:"10%", "vertical-align": "top", "border-right": "2px solid #194063"}} >
							
						<Menu clickMenu={this.clickMenu}/>
					</td>
					<td style={{padding: "10px", "vertical-align": "top"}} >
						{ this.state.frameContent == 'frameEvents' && <EventsList selectEvent={this.homeSelectEvent} />}
						{ this.state.frameContent == 'event' && <Event eventid={this.state.currenteventid} />}

					</td>
				</tr>

			</table>
			</div>			
		);
		/*
		if (this.state.frameContent == 'frameEvents') {
			return (
				<div class="row">
					<MenuHtml />
					<div class="col-sm-10">	
						<EventsList selectEvent={this.homeSelectEvent} />
					</div>
				</div>
				)
		}
		if (this.state.frameContent == 'event') {
			console.log("HomeTogh.render: Event");

			return (
				<div class="row">
					<MenuHtml />
					<div class="col-sm-10">	
						<Event eventid={this.state.currenteventid} />
					</div>
				</div>
				)
		}
	*/
	} // end render
	
};


// console.log("TogHome. Render id=" + document.getElementById('reactHomeTog'));

ReactDOM.render(<HomeTogh />, document.getElementById('reactHomeTogh'));

	
  