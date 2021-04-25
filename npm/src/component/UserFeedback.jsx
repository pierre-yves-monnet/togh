// -----------------------------------------------------------
//
// Display user feedback
//
// Display some feedback to the user
//
// -----------------------------------------------------------
//
import React from 'react';

import { FormattedMessage } from "react-intl";

import { InlineLoading } from 'carbon-components-react';

import LogEvents from './../component/LogEvents';

export const ERRORHTTP = 'ERRORHTTP';
export const ERRORCONTRACT = 'ERRORCONTRACT';
export const ERROR='ERROR';
export const OK='OK';


class UserFeedback extends React.Component {
	constructor(props) {
		super();
		this.state={ 
			inprogress: props.inprogress,
			label: props.label,
			status: props.status,
			result: props.result,
			listlogevents: props.listlogevents
		};
		this.timer = setTimeout(() => { if (! this.state.inprogress){ this.setState({ status:""})}; }, 4000);
	}	
	
	componentDidUpdate(prevProps) {
		if (prevProps.inprogress !== this.props.inprogress) {
			this.setState({ inprogress: this.props.inprogress });
		}
		if (prevProps.status !== this.props.status) {
			this.setState({ status: this.props.status });
		}
		if (prevProps.label !== this.props.label) {
			this.setState({ label: this.props.label });
		}
		if (prevProps.result !== this.props.result) {
			this.setState({ result: this.props.result });
		}
		if (prevProps.listlogevents !== this.props.listlogevents) {
			this.setState({ listlogevents: this.props.listlogevents });
		}
		if (this.timer)
			clearTimeout(this.timer);
		this.timer = setTimeout(() => { if (! this.state.inprogress){ this.setState({ status:""})}; }, 4000);

	}
	
	render() {
		if (this.state.status==="" && ! this.state.inprogress)
			return ( <div>&nbsp;</div>);
		if (this.state.inprogress) {
			return (<div style={{fontStyle: "italic"}}><table><tr><td><InlineLoading/></td><td> {this.state.label}</td></tr></table></div>);
		}
		
		// Display the status
		if (this.state.status === OK) {
			return (<div style={{fontStyle: "italic"}}>{this.state.label} :
						<FormattedMessage id="UserFeedback.OperationSuccess" defaultMessage="Operation successful" />
					</div> 
			)
		}
		if (this.state.status === ERRORHTTP) {
			return (<div style={{fontStyle: "italic", color:"red"}}>{this.state.label} :
						<FormattedMessage id="UserFeedback.OperationHttpError" defaultMessage="Connection to the server lost" />
					</div> 
			)
		}
		if (this.state.status === ERROR) {
			return (<div style={{fontStyle: "italic", color:"red"}}>{this.state.label} :
						<FormattedMessage id="UserFeedback.OperationFailed" defaultMessage="Operation Failed" />
						&nbsp;
						{this.state.result}
						)
						<LogEvents listEvents={this.state.listlogevents} />
				</div>) 
			
		}
		
		return (<div style={{fontStyle: "italic"}}>{this.state.label} :
						<FormattedMessage id="UserFeedback.OperationSuccessfull" defaultMessage="Success" />
						&nbsp;
						{this.state.result}						
						<LogEvents listEvents={this.state.listlogevents} />
				</div>) 
	}		
		
}

export default UserFeedback;