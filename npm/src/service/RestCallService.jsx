// -----------------------------------------------------------
//
// RestcallService
//
// RestcallService
//
//  var restCallService = FactoryService.getInstance().getRestCallService();

// -----------------------------------------------------------

import axios from 'axios';

import HttpResponse  		from 'service/HttpResponse';


class RestcallService {
	constructor( factoryService) {
		console.log("AuthService: ------------ constructor ");
		this.factoryService = factoryService;
		this.serverHttp="";
	}

/**
	* depends on where is the web site.
	* OVH: we must have an IP Address
	* Google : we don't have the IP adress, only the domain, but on Google, there is no way to redirect a POST request
	*   so we have to call the server to get it's public IP Address we can use...
	*
	*/
	async determineURlHost() {
	    // let's communicate with the server to find the header

		var i = window.location.href.indexOf(":",7); // skype http: or https:
        var urlPing = window.location.href.substring(0,i)+":7080/togh/api/ping?serverInfo=true&timezoneoffset="+(new Date()).getTimezoneOffset();
        let headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );

        const requestOptions = {
            headers: headers
        };

        var self=this;
        const axiosPayload = await axios.get( urlPing, requestOptions);

        let httpResponse = new HttpResponse( axiosPayload, null);
        self.serverHttp=httpResponse.getData().serverHttp;
        console.log("RestCallService.determineUrlHost server=["+self.serverHttp+"]");
    }

	// init - separated of constructor because service use themself
	init() {
		console.log( "RestcallService.init");
		this.authService= this.factoryService.getAuthService();
        this.determineURlHost();

	}
	
	// From the URL, complete to have a complete URL
	getUrl( uri ) {
		// console.log("RestCall.geturl : "+window.location.href);
		var headerUrl="";
		if (this.serverHttp !== "") {
    		let i = window.location.href.indexOf(":",0); // skip http: or https:
            let protocol = window.location.href.substring(0,i);
		    headerUrl=protocol+"://"+this.serverHttp+":7080/togh";
		} else {
    		var i = window.location.href.indexOf(":",7); // skip http: or https:
    			    	headerUrl = window.location.href.substring(0,i)+":7080/togh";
		}

		// search something like http://localhost:3000/#
		return headerUrl+uri; 
	}
	
	
	// According https://www.intricatecloud.io/2020/03/how-to-handle-api-errors-in-your-web-app-using-axios/
	// the function fctToCallback must be
	// iReceivedMyAnswer( httpPayLoad)
	//  	httpPayLoad.data ==> Content
	//      httpPayLoad.status => http status except 400 / 500 which come with error
	//      httpPayload.err => contains boolean lile err.response 
	getJson(uri, objToCall, fctToCallback) {
		let headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		
		const requestOptions = {
	        headers: headers
	    };
		let uriWithTimeZoneOffset = uri+"&timezoneoffset="+(new Date()).getTimezoneOffset();
		console.log("RestCallService.getJson: uri="+uriWithTimeZoneOffset);
    	axios.get( this.getUrl( uriWithTimeZoneOffset ), requestOptions)
        	.then( axiosPayload => { 
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));
				let httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(error => {
			    console.log("RestcallService.getJson() error "+error);
				if (error.response && error.response.status === 401) {
					let homeTogh=window.location.href;
					console.log("Redirect: to["+homeTogh+"] force reload");
					window.location.reload();
					window.location = homeTogh;
					return;
				}
				console.error("RestCallService.getJson: catch error:"+error);	
				let httpResponse =  new HttpResponse( {}, error);
				fctToCallback.call(objToCall, httpResponse); 

				});
	}
	
	// PostJson
	postJson(uri, objToCall, param, fctToCallback ) {
		let headers = this.authService.getHeaders( { 'Content-Type': 'application/json' } );
		param.timezoneoffset=(new Date()).getTimezoneOffset();
		console.log("RestCallService.postJson: timezoneoffset="+param.timezoneoffset);
		
		const requestOptions = {
	        headers: headers
	    };

		var selfUri = uri;
    	axios.post( this.getUrl( uri ), param, requestOptions)
        	.then( axiosPayload => {
				// console.log("RestCallService.getJson: payload:"+JSON.stringify(axiosPayload.data));	
				let httpResponse = new HttpResponse( axiosPayload, null);
				fctToCallback.call(objToCall, httpResponse); 
				})
			.catch(error => {
				console.error("RestCallService.getJson: Uri["+selfUri+"] catch error:"+error);
				if (error.response && error.response.status && error.response.status === 401) {
					let homeTogh=window.location.href;
					console.log("Redirect : to["+homeTogh+"]");
					window.location = homeTogh;
				return;
				}
				let httpResponse =  new HttpResponse( {}, error)
				fctToCallback.call(objToCall, httpResponse); 

				});
	}



}
export default RestcallService;