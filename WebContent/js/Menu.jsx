// -----------------------------------------------------------
//
// Event
//
// Display one event
//
// -----------------------------------------------------------

function generalGoogleSignOut() {
			console.log("Menu.generalGoogleSignOut : start");

    	var auth2 = gapi.auth2.getAuthInstance();
	    auth2.signOut().then(function () {
      		console.log('User signed out.');
			});
}	



class Menu extends React.Component {
	
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.clickMenu( '<action>')
	// this.props.showMenu ( isVisible )
	constructor(props) {
		super();
		this.state = {  }
		console.log("menu.constructor");
		this.state = { showMenu: true, events: []};
		// this.setVisibleMenu = this.setVisibleMenu.bind(this)

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
					<a onClick={() =>this.setVisibleMenu(false)}><span class="glyphicon glyphicon-chevron-left" ></span></a>
				 </div>
				&nbsp;<p/>
				
				<a onClick={() =>this.props.clickMenu( 'eventlist')}>Events</a><p/>
				My profile<p/>
				My friends<p/>
				My Profile
				&nbsp;<p />
				&nbsp;<p />
				&nbsp;<p />
				<a class="btn btn-warning" onClick={() =>this.googleSignOut()} disabled={true}>Sign out</a>		 
			</div>
			)
		} else {
			return ( 
				<div> 
					<div style={{float: "right"}}>
						<a onClick={() =>this.setVisibleMenu(true)}>
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
	
}



	