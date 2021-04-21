// -----------------------------------------------------------
//
// AdminAPIKey
//
// Google access
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";
import { TextInput } from 'carbon-components-react';

import { Loading } from 'carbon-components-react';

import FactoryService from '../service/FactoryService';

import LogEvents from '../component/LogEvents';



// -----------------------------------------------------------
//
// AdminGoogle
//
// Google access
//
// -----------------------------------------------------------
class AdminAPIKey extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {listkeys : [], listEvents:[] };
		
		this.updateKey 			= this.updateKey.bind( this );		 
		this.getApiKeyCallback 	= this.getApiKeyCallback.bind( this );		
	}
	
	// Calculate the state to display
	componentDidMount () {
		// call the server to get the value
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();

		restCallService.getJson('/api/admin/apikey/get?', this, this.getApiKeyCallback);
	}

	getApiKeyCallback(httpPayload ) {
			httpPayload.trace("AdminAPIKey.getkey");
			
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				console.log("httpPayload.getData()="+JSON.stringify(httpPayload.getData()));
				this.setState({ listkeys : httpPayload.getData()});
			}
		
		
	}

	render() {
		console.log("AdminAPIKey.render : inprogress="+this.state.inprogress+", listkeys="+JSON.stringify(this.state.listkeys));
		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="Active loading indicator" withOverlay={true}
    						/>);


		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
				<FormattedMessage id="AdminAPIKey.Title" defaultMessage="API Keys" />
				</div>
				<div class="card-body">
				 	{inprogresshtml}
					{this.state.listkeys.map( (item, index) => {
						console.log("item="+JSON.stringify(item));
						return (
							<div  key={index}>
								<TextInput labelText={item.name} value={item.keyApi} 
									onChange={(event) => {
											let list = this.state.listkeys;
											list[ index ].keyApi = event.target.value;
											this.setState( {listkeys: list}); 
											}} ></TextInput><br />
							</div>
						)
						})
					}
				
					<button class="btn btn-info btn-sm" onClick={this.updateKey}>
						<FormattedMessage id="AdminAPIKey.updateKey" defaultMessage="Update key"/>
					</button>
				</div>
				<LogEvents listEvents={this.state.listEvents} />
					
			</div>
			
			);
			// 
	}
	
	updateKey() {
		console.log("AdminAPIKey.updateKey:");
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		var param={listkeys: this.state.listkeys};
		restCallService.postJson('/api/admin/apikey/update', this, param, httpPayload =>{
			httpPayload.trace("AdminAPIKey.updateKey");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState({ "message": httpPayload.getData().message, listEvents: httpPayload.getData().listEvents }); 						
			}
		});
	
	}
}

export default AdminAPIKey;

