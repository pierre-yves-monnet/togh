// -----------------------------------------------------------
//
// AdminInfo
//
// Google access
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { Loading } from 'carbon-components-react';



import FactoryService 		from 'service/FactoryService';



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
					message:"",
					listinfos:[]};
	}
	
	componentDidMount() {
		console.log("AdminInfo.getInfo:");
		this.setState({inprogress: true });
		var restCallService = FactoryService.getInstance().getRestCallService();
		restCallService.getJson('/api/admin/info?', this, httpPayload =>{
			httpPayload.trace("AdminInfo.callback");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ "message": "Server connection error" });
			}
			else {
				this.setState({ message: "",
								listinfos: httpPayload.getData() });
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
					{this.state.listinfos && this.state.listinfos.map((info,index) =>
						<tr key={index}>
							<td>{info.name}</td>
							<td>{info.value}</td>
						</tr>)}
						<tr>
						    <td>Version</td>
						    <td>Version Oct 15, 2021</td>
						</tr>
				</table>
				</div>	 
			</div>
			);
	}
	
	
}

export default AdminInfo;
