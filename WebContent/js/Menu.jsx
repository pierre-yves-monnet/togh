// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

class Menu extends React.Component {
	
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.clickMenu( '<action>')
	constructor(props) {
			super();
		this.state = {  }
		console.log("menu.constructor");
		this.state = { showMenu: true, events: []};

		}
		
		
		// -------------------------------------------- render
	render() {
		console.log("Menu.render");

		return ( <div  > 
				<div class="glyphicon glyphicon-circle-arrow-left"> </div><p/>
				<a onClick={() =>this.props.clickMenu( 'eventlist')}>Events</a><p/>
				My profile<p/>
				My friends<p/>
				My Profile) 
			</div>
		)
	}
}	