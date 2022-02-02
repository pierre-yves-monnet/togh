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


import HttpResponse  		from 'service/HttpResponse';
import FactoryService 		from 'service/FactoryService';




class AuthService {
	constructor(factoryService ) {
		console.log("AuthService: ------------ constructor ");
		this.token=null;
		this.factoryService = factoryService;
	}

	init() {
		this.restcallService = this.factoryService.getRestCallService();
	}
	
	isConnected() {
		let connection=this.token !==null;
		// console.log("AuthService.isConnected:"+connection);
		return connection;
	}
	
	// ---- getUser. When the user is connected, return all information about him
	getUser() {
		// console.log("AuthService user=["+this.user.tagid+"] showTipsUser=["+this.user.showTipsUser+"]");
		return this.user;
	}
	// user can change (in the profile) : save the change
	setUser( user ) {
		this.user = user;
		// console.log("AuthService user=["+this.user.tagid+"] showTipsUser=["+this.user.showTipsUser+"]");
	}
	
	getMethodConnection() {
		return this.connectMethod;
	}
	
	getHeaders( headers) {
		if (headers===null)
			headers= {};
		if (this.token !== null)
			headers.Authorization = this.token;
		headers["Access-Control-Allow-Origin"] ="*";

		return headers;
	}	
	
	
	//--------------------------------------- Login
	login( connectMethod, param, objToCall, fctToCallback ) {
		console.log("AuthService.login, param="+JSON.stringify(param));
		
		this.connectMethod = connectMethod;
		var self=this;
		try {
			const requestOptions = {
	        headers: this.getHeaders({})
	    };
		
		
		axios.post( this.restcallService.getUrl('/api/login?'), param, requestOptions)
				.then( axiosPayload => {
					console.log("AuthService.loginCallback, httpPayload="+JSON.stringify(axiosPayload));
					self.token = axiosPayload.data.token;
					self.user =  axiosPayload.data.user;
					
					console.log("AuthService.loginCallback, token="+self.token+" in this="+self);
					var httpResponse = new HttpResponse( axiosPayload, null);
					fctToCallback.call(objToCall, httpResponse);		
				})
				 .catch((err) => {
					console.error("AuthService.loginCallback: Catch error:"+err);
					var httpResponse =  new HttpResponse( {}, err);
					httpResponse.trace(AuthService.loginCallback);
					fctToCallback.call(objToCall, httpResponse);		
				}
			
			
			)
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
				.then( axiosPayload => {
					console.log("AuthService.loginGoogle, httpPayload="+JSON.stringify(axiosPayload));
					self.token = axiosPayload.data.token;
					self.user =  axiosPayload.data.user;
										
					var httpResponse = new HttpResponse( axiosPayload, null);
					fctToCallback.call(objToCall, httpResponse);		
				})
				.catch((err) => {
					console.error("AuthService.loginCallback: Catch error:"+err);
					var httpResponse =  new HttpResponse( {}, err)
					fctToCallback.call(objToCall, httpResponse);		
				});
		 } catch (err) {
        	// Handle Error Here
        	console.log("AuthService.connect.Error "+err);
			return {};
		}
	}
    //--------------------------------------- ghostLogin
	ghostLogin( param, objToCall, fctToCallback ) {
		console.log("AuthService.ghostLogin, param="+JSON.stringify(param));

		this.connectMethod = 'DIRECT';
		var self=this;
		try {
			const requestOptions = {
	        headers: this.getHeaders({})
	    };


		axios.post( this.restcallService.getUrl('/api/login/ghostuser?'), param, requestOptions)
				.then( axiosPayload => {
					console.log("AuthService.ghostLoginCallback, httpPayload="+JSON.stringify(axiosPayload));
					self.token = axiosPayload.data.token;
					self.user =  axiosPayload.data.user;

					console.log("AuthService.ghostLoginCallback, token="+self.token+" in this="+self);
					var httpResponse = new HttpResponse( axiosPayload, null);
					fctToCallback.call(objToCall, httpResponse);
				})
				 .catch((err) => {
					console.error("AuthService.ghostLoginCallback: Catch error:"+err);
					var httpResponse =  new HttpResponse( {}, err);
					httpResponse.trace(AuthService.loginCallback);
					fctToCallback.call(objToCall, httpResponse);
				}


			)
		 } catch (err) {
        	// Handle Error Here
        	console.log("AuthService.ghostLoginCallback.Error "+err);
			return {};
		}
    }
	//--------------------------- RegisterUser
	//  param= { email: this.state.email, password: this.state.password, firstName:this.state.firstName, lastName: this.state.lastName };
	registerUser(param, objToCall, fctToCallback) {
		console.log("AuthService.registerUser: Register param=" + JSON.stringify(param));
	    var self=this;
	    const restCallService = FactoryService.getInstance().getRestCallService();
        restCallService.postJson('/api/login/registernewuser?', this, param,
            httpPayload => {
			    console.log("AuthService.registerStatus: registerStatus = "+JSON.stringify(httpPayload.getData()));
			    debugger;
                self.token = httpPayload.getData().token;
                self.user =  httpPayload.getData().user;
                self.connectMethod='DIRECT';

                var httpResponse = new HttpResponse( httpPayload.getData(), null);
                fctToCallback.call(objToCall, httpPayload);
            });
	}
	
	/**
	 User is connected by a different URL than the password (example, in the changePassword method) */
	setConnection( httpPayload ) {
		this.token = httpPayload.token;
		this.user =  httpPayload.user;
		this.connectMethod='DIRECT';
	}

	refreshUser( objToCall, fctToCallback ) {
	  console.log("AuthService.refreshUser: ");
	  let restCallService = FactoryService.getInstance().getRestCallService();
      restCallService.getJson('/api/user?id='+this.user.id, this,
        httpPayload =>{
            httpPayload.trace("MyProfile.getUser");
            if (httpPayload.isError()) {
                console.log("AuthService.error during refresh user: ");
                fctToCallback.call( objToCall, this.user, httpPayload );
            }
            else {
                this.user= httpPayload.getData();
                fctToCallback.call( objToCall, this.user,httpPayload );
            }
        }
      );
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
				axiosPayload => {
					console.log("AuthService.logout, httpPayload="+JSON.stringify(axiosPayload));
					self.token = null;
					self.user =  null;
			
					var httpResponse = new HttpResponse( axiosPayload, null);		
					fctToCallback.call(objToCall, httpResponse);	
				}
			);
	}
	
	
}
export default AuthService;