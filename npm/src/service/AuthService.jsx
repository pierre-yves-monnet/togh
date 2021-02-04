// -----------------------------------------------------------
//
// AuthService
//
// AuthService. Access all autorisation API
// access it by
// 
// var authService = FactoryService.getInstance().getAuthService();
//
// -----------------------------------------------------------

import axios from 'axios';

import FactoryService from './FactoryService';
import RestcallService from './RestcallService';


class AuthService {
	constructor(factoryService ) {
		console.log("AuthService: ------------ constructor ");
		this.token=null;
		this.factoryService = factoryService;

	}

	init() {
		this.restcallService = this.factoryService.getRestcallService();
	};
	
	isConnected() {
		var connection=this.token !==null;
		console.log("AuthService.isConnected:"+connection+ +" in this="+this);
		return connection;
	}
	
	// ---- getUser. When the user is connected, return all information about hime
	getUser() {
		return this.user;
	};
	
	getMethodConnection() {
		return this.connectMethod;
	}
	
	getHeaders( headers) {
		if (headers===null)
			headers= {};
		headers.Authorization = this.token;
		return headers;
	}	
	
	
	//--------------------------------------- Login
	login( connectMethod, param, objToCall, fctToCallback ) {
		console.log("AuthService.login, param="+JSON.stringify(param));
		this.connectMethod = connectMethod;
		var self=this;
		try {
			axios.post( this.restcallService.getUrl('/api/login?'), param)
				.then( httpPayload => {
					console.log("AuthService.loginCallback, httpPayload="+JSON.stringify(httpPayload));
					self.token = httpPayload.data.token;
					self.user =  httpPayload.data.user;
					console.log("AuthService.loginCallback, token="+self.token+" in this="+self);

					fctToCallback.call(objToCall, httpPayload.data);		
				});
		 } catch (err) {
        	// Handle Error Here
        	console.log("AuthService.connect.Error "+err);
			return {};
		}
    }
	
	loginGoogle( googleInformation, objToCall, fctToCallback ) {
		try {
			console.log("AuthService.loginGoogle, objToCall="+objToCall+" googleInformation="+JSON.stringify(googleInformation));
			var self=this;

			axios.get( this.restcallService.getUrl('/api/logingoogle?idtokengoogle=' + googleInformation.tokenId))
				.then( httpPayload => {
					console.log("AuthService.loginGoogle, httpPayload="+JSON.stringify(httpPayload));
					self.token = httpPayload.data.token;
					self.user =  httpPayload.data.user;
					fctToCallback.call(objToCall, httpPayload.data);		
				});
		 } catch (err) {
        	// Handle Error Here
        	console.log("AuthService.connect.Error "+err);
			return {};
		}
	}
	//--------------------------- RegisterUser
	//  param= { email: this.state.email, password: this.state.password, firstName:this.state.firstName, lastName: this.state.lastName };
	registerUser(param, objToCall, fctToCallback) {
		console.log("AuthService.registerUser: Register param=" + JSON.stringify(param));
		var self=this;
		try {
			axios.post( this.restcallService.getUrl('/api/login/registernewuser?'), param)
				.then( httpPayload => {
					console.log("AuthService.registerStatus: registerStatus = "+JSON.stringify(httpPayload.data));
					self.token = httpPayload.data.token;
					self.user =  httpPayload.data.user;
					self.connectMethod='DIRECT';
					fctToCallback.call(objToCall, httpPayload.data);	
				});
		 } catch (err) {
        	// Handle Error Here
        	console.log("AuthService.registerUser.Error "+err);
			return {};
		}
	}
	
	
	//--------------------------------------- Logout
	logout( objToCall, fctToCallback) {
		console.log("AuthService.logout: token = " +this.token);
	
		var param = {};
		var self=this;
		var headers= {'Authorization': this.token};
		axios.post( this.restcallService.getUrl('/api/logout?'), param, 
			{
    			headers: this.getHeaders(null)
  			}).then(
				httpPayload => {
					console.log("AuthService.logout, httpPayload="+JSON.stringify(httpPayload));
					self.token = null;
					self.user =  null;
					fctToCallback.call(objToCall, httpPayload.data);	
				}
			);
	}
	
	
}
export default AuthService;