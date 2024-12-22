// -----------------------------------------------------------
//
// IntermittentMessage
//
// Display a message, based on a 3 states
//    0 : do not shaw any message.
//    1 : in progress (display a Wait message)
//    2 : operation done, display a message, delete it after x seconds
//    3 : operation in error, display a message in red, delete it after x+10 seconds

// At the end of the timer, the message will diseapear.
// If the parent want to display it again, it has
//  - to change the state (to 0, or 1) and then set it again to 2 (or3)
//  - to change the message.
//  then the message is visible again for the delay
// -----------------------------------------------------------
//

import React from 'react';

import { InlineLoading } from '@carbon/react';





class IntermittentMessage extends React.Component {

	constructor(props) {
		super();
		this.state = {
			state: props.state,
			message: props.message,
			showMessage: true
		};
        this.armTimer = this.armTimer.bind(this);
	}


	componentDidUpdate(prevProps) {
	    let redoTimer=false;
		if (this.props.state !== this.state.state) {
			// console.log("IntermittentMessage.componentDidUpdate propsstate=" + this.props.state+",state="+this.state.state+" message=["+this.props.message+"]" );

		    this.setState({ state: this.props.state, showMessage:true });
		    redoTimer=true;
		}
		if (this.props.message !== this.state.message) {
			// console.log("IntermittentMessage.componentDidUpdate Message=" + this.props.state+" message=["+this.props.message+"]" );

            this.setState({ message: this.props.message, showMessage:true });
            redoTimer=true;
        }
        if (redoTimer) {
            this.armTimer();
        }

	}


	render() {
	    if (this.state.state === 0)
	        return (<span></span>);
	    if (this.state.state === 1)
	        return (<InlineLoading/>);
	    if (this.state.state === 2) {
	        return (<span style={{fontWeight: "bold", paddingLeft: "5px", paddingTop:"10px", fontSize:"10px",color:"blue"}}>{this.getMessage()}</span>);

	    }
	    if (this.state.state === 3)
	        return (<span style={{fontWeight: "bold", paddingLeft: "5px", paddingTop:"10px", fontSize:"10px",color:"red"}}>{this.getMessage()}</span>);

        return (<span>No State acceptable{this.state.state}</span>);
	}


    armTimer() {
        let delay=3000;

        if (this.props.state === 3) {
            delay=10000;
        }

        var thisTimer = this;
        console.log("IntermitentMessage: setTimer");
    	var thisInterval=setInterval(function () { console.log("IntermitentMessage: endOfTimer");
                clearInterval( thisInterval);
                thisTimer.setState( {showMessage:false}) }
    	    ,delay);
    }

	getMessage() {
        if (this.state.showMessage)
            return this.state.message;
        return "";
    }

}
export default IntermittentMessage;
