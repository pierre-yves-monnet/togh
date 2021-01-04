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
		
		// this is mandatory to have access to the variable in the method... thank you React!   
		// this.connect = this.connect.bind(this);
		this.state = { frameContent: "frameEvents" };
	}


	homeSelectEvent( eventId ) {
		console.log("HomeTogh.selectEvent: get it, an event is selected :"+eventId);
		this.setState( {'currenteventid' : eventId });
		this.state.frameContent = 'event';
	}

	render() {
		console.log("HomeTogh.render frameContent=["+this.state.frameContent+"]");


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
	
	} // end render
	
};

function MenuHtml() {
	return ( <div class="col-md-auto" > 
				<div class="panel panel-info">
					<div class="panel-heading" >Operations
					</div>
					<div class="panel panel-body" >
						Events<p/>My profile<p/>My friends<p/>
					</div>
				</div>
			</div>
		)
}


// console.log("TogHome. Render id=" + document.getElementById('reactHomeTog'));

ReactDOM.render(<HomeTogh />, document.getElementById('reactHomeTogh'));

	
  