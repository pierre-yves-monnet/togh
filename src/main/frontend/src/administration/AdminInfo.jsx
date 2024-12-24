// -----------------------------------------------------------
//
// AdminInfo
//
// Google access
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { Loading, TextInput } from '@carbon/react';

import ToghVersion          from 'component/ToghVersion';
import FactoryService 		from 'service/FactoryService';
import LogEvents 		    from 'component/LogEvents';



// -----------------------------------------------------------
//
// AdminInfo
//
// Information on system
//
// -----------------------------------------------------------
class AdminInfo extends React.Component {
	
	
	// this.props.updateEvent()
	constructor(props) {
		super();
		this.state = { inprogress: false,
					updateIdAddress: {},
					listAdminParameters: {},
					listInfos: [],
					listIpAddresses: [],
					listIpAddressesError: []};
		this.updateIpaddress = this.updateIpaddress.bind(this);
	}
	
	componentDidMount() {
		console.log("AdminInfo.getInfo:");
		this.setState({inprogress: true });
		var restCallService = FactoryService.getInstance().getRestCallService();
		restCallService.getJson('/api/admin/info?', this, httpPayload =>{
			// httpPayload.trace("AdminInfo.callback");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ messageUpdateIpAddress:"",
				                errorUpdateIpAddress: "Server connection error" });
			}
			else {
				this.setState({ messageUpdateIpAddress: "",
				                errorUpdateIpAddress: "",
								listInfos: httpPayload.getData().listInfos,
								listIpAddresses: httpPayload.getData().listIpAddresses,
								listIpAddressesError: httpPayload.getData().listIpAddressesError,
								listAdminParameters : httpPayload.getData().listAdminParameters,
								ipAddress: httpPayload.getData().listAdminParameters.ipAddress});
			}
		});
	
	
	} 


	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		// console.log("AdminInfo.render:");
		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminInfo.Information" defaultMessage="Information" />
				</div>
				<div class="card-body">
					{this.state.inprogress && <Loading
      							description="Active loading information" withOverlay={true}
    							/>}
					{this.state.message}<br/> 

				<table class="table table-striped toghtable">
					{this.state.listInfos && this.state.listInfos.map((info,index) =>
						<tr key={index}>
							<td>{info.name}</td>
							<td style={{fontWeigh: "bold", fontSize:"smaller"}}>{info.value}</td>
						</tr>)}
                        <tr>
						    <td ><FormattedMessage id="AdminInfo.PortalVersion" defaultMessage="Portal version" /></td>
						    <td style={{fontWeigh: "bold", fontSize:"smaller"}}>
						            <ToghVersion/>
						    </td>
						</tr>
						<tr>
						    <td><FormattedMessage id="AdminInfo.IpAddress" defaultMessage="IpAddress" /></td>
						</tr>
						<tr>
						    <td colspan="2">
						    <TextInput labelText={<FormattedMessage id="AdminInfo.IpAddress" defaultMessage="Ip Address"/>}
						        value={this.state.ipAddress}
                                onChange={(event) => {
                                        this.setState( {ipAddress: event.target.value});
                                        }} ></TextInput><br />
                            </td>
                        </tr>
				</table>
				<button class="btn btn-info btn-sm" onClick={this.updateIpaddress}>
                    <FormattedMessage id="AdminInfo.updateIpAddress" defaultMessage="Update IP Address"/>
                </button>

                <LogEvents listEvents={this.state.updateIdAddress.listEvents} />

                 <br/>
                 <FormattedMessage id="AdminInfo.ListIpAddresses" defaultMessage="List Ip Addresses detected" />
                 <br/>
                 <div  style={{fontSize: "12px"}}>
                     {this.state.listIpAddresses.map((ipAddress) => {
                                                        return <span> {ipAddress},&nbsp;</span>
                                                       } )
                     }
                 </div>
				</div>	 
			</div>
			);
	}


	updateIpaddress() {
		console.log("AdminInfo.updateIpaddress:");
		this.setState({inprogress: true });
		let restCallService = FactoryService.getInstance().getRestCallService();
		let postPayload={'adminParameters':{'ipAddress': this.state.ipAddress}};
		debugger;
		restCallService.postJson('/api/admin/setadminparameter', this, postPayload, httpPayload =>{
            // httpPayload.trace("AdminInfo.updateIpaddress");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ "message": "Server connection error" });
			}
			else {
				this.setState({ updateIdAddress: httpPayload.getData()});
			}
		});
	}
	
}

export default AdminInfo;
