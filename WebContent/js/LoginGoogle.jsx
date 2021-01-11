
// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------


class LoginGoogle extends React.Component {
	constructor() {
		super();

	}

	render() {
		return (<div />)
	}
}



// I would love to add this part in the React module, but I can't include the GHTML part in the React.render. It's visible, but to call the function does not works

function onSignIn(googleUser) {
	var profile = googleUser.getBasicProfile();
	console.log('LoginGoogle.IN LOGIN.JSW ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	console.log('LoginGoogle.Name: ' + profile.getName());
	console.log('LoginGoogle.Image URL: ' + profile.getImageUrl());
	console.log('LoginGoogleEmail: ' + profile.getEmail()); // This is null if the 'email' scope is not present.


	var idtokengoogle = googleUser.getAuthResponse().id_token;

	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
			// Considere we are connected if (httpPayload.isConnected) {
			//	console.log("Loging.connectStatus : redirect then");
			window.location.href = "homeTogh.html";
			// }
			// else {
			//	this.setState( {badConnection: ! httpPayload.isConnected, isLog: httpPayload.isConnected});
			// }
		}
	}
	xmlHttp.open("GET", "logingoogle?idtokengoogle=" + idtokengoogle, true); // true for asynchronous 
	xmlHttp.send(null);
	console.log("LoginGoogle.HttpSent");
}