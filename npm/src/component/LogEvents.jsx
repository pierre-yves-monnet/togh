/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


import React from 'react';

import { FormattedMessage } from "react-intl";

import { Tooltip } from 'carbon-components-react';


// -----------------------------------------------------------
//
// EventSectionHeader
//
// Normalize the header of a section 
//
// -----------------------------------------------------------

class LogEvent extends React.Component {
	
	// props.addItemCallback must be defined if a button PLUS is displayed
	constructor(props) {
		super();
		this.state = {
			listEvents: props.listEvents
		};
				
		console.log("LogEvent: constructor "+JSON.stringify(this.state.listEvents));

	}
	componentDidUpdate(prevProps) {
		console.log("LogEvent.componentDidUpdate listEvents=" + JSON.stringify(this.props.listEvents));
		if (prevProps.listEvents !== this.props.listEvents) {
			this.setState({ listEvents: this.props.listEvents });
		}
	}



  
	render() {
	    console.log("LogEvent: render "+JSON.stringify(this.state.listEvents));
		


		if (! this.state.listEvents || this.state.listEvents.length===0) {
			return (<div/>);
        }
        return (
        <div>
            {this.state.listEvents.map((event) => {
                event.styleLabel="black";
                if (event.eventClassName.includes("bg-success")) {
                    event.styleLabel="white";
                }
                // The Tooltip display the triggerText in black - can't change it
                return (
                    <div style={{paddingBottom: "5px"}}>
                        <label class={event.eventClassName}
                          >
                            <Tooltip  showIcon="false"
                                tabIndex={0}
                                triggerText={event.title}
                                style={{color: event.styleLabel}}
                                tooltipBodyId="tooltip-body">
                                <table width="100%">
                                    <tr style={{borderBottom: "3px solid wheat"}}>
                                        <td class={event.eventClassName} style={{fontSize: "10px",color: event.styleLabel}}>
                                            {event.key}
                                        </td>
                                    </tr>
                                    <tr><td>
                                        <div style={{fontSize: "12px", color:"white"}} >
                                            {event.title} : {event.parameters}
                                        </div>
                                        </td></tr>
                                    <tr><td style={{fontSize: "12px", color:"white"}} >
                                        {event.cause}
                                    </td></tr>
                                    {event.consequence !=null &&
                                        <tr><td style={{fontSize: "12px", color:"white"}}>
                                            <div>
                                                <span style={{fontStyle: "italic"}}><FormattedMessage id="LogEvent.Consequence" defaultMessage="Consequence" />&nbsp;</span>
                                                {event.consequence}<br/>
                                            </div>
                                        </td></tr>
                                    }
                                    {event.action !=null &&
                                        <tr><td style={{fontSize: "12px", color:"white"}}>
                                            <div>
                                                <span style={{fontStyle: "italic"}}><FormattedMessage id="LogEvent.Action" defaultMessage="Action" />:&nbsp;</span>
                                                {event.action}<br/>
                                            </div>
                                        </td></tr>
                                    }
                                    </table>
                            </Tooltip>
                        </label>
                    </div>
                )
             })}
        </div>);
	}
}
export default LogEvent