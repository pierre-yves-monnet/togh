/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";
import FactoryService 		from 'service/FactoryService';
import { Easel } from 'react-bootstrap-icons';
import { Modal } from 'carbon-components-react';


// -----------------------------------------------------------
//
// TakeATour
//
// Display a Take A Tour message
//
// -----------------------------------------------------------

class TakeATour extends React.Component {

	constructor(props) {
		super();

		this.state = {
			name: props.name,
			subject: props.subject,
			content: props.content,
			notes: props.notes,
			displayTour : false

		};
		this.deactivateTakeATour = this.deactivateTakeATour.bind( this );
		this.fctCallBack = props.fctCallBack;
	}

	render() {
	    const userService = FactoryService.getInstance().getUserService();
		const intl = this.props.intl;

        console.log("takeATour: Render prefUser"+userService.prefsDisplayTakeATour());

		if (userService.prefsDisplayTakeATour()) {
		    console.log("takeATour: displayTour? "+this.state.displayTour);
            if (! this.state.displayTour) {
                return (<div style={{ border: "2px solid red",
                            borderRadius: "10px",
                            textAlign: "center",
                            color: "#1f78b4",
                            fontSize: "10px",
                            fontStyle: "italic",
                            margin:"0px 5px 0px 5px"}}>
                    <button onClick={ ()=> this.setState({displayTour: true}) } style={{border:"0px", background:"transparent"}}
                            title={intl.formatMessage({id:"TakeATour.TakeATour", defaultMessage:"Take A Tour"})}>
                        <Easel width="20px" height="20px" color="#ff5500" />
                    </button>
                </div>)
            }

            // display the tour
            console.log("takeATour: show the modal: subject "+this.state.subject);
            return ( <Modal  open passiveModal
                        modalLabel={
                            <div>
                                <Easel width="20px" height="20px" color="#ff5500" style={{marginRight: "10px"}}/>
                                <FormattedMessage id="TakeATour.TakeATourLabel" defaultMessage="Take A Tour"/>
                            </div>}
                        modalHeading={this.state.subject}
                        onRequestClose={() => { this.setState({displayTour:false})}}>

                        <div style={{marginBottom: "10px"}} dangerouslySetInnerHTML={{ __html: this.state.content}} ></div>

                        <table width="100%">
                        <tr style={{backgroundColor:"#ffffcc"}}><td>
                            <div style={{marginBottom: "10px", fontSize: "12px", fontWeight: "bold", paddingLeft: "20px"}}><FormattedMessage id="TakeATour.Deactivate" defaultMessage="You can deactivate Take a tour. Enable it again in your preferences"/>}</div>
                           </td><td>
                            <button class="btn btn-info btx-sm" style={{fontSize: "10px", margin: "5px 0px 5px 0px", padding: "0px 5px 0px 5px"}}
                                onClick={this.deactivateTakeATour}>
  						        <FormattedMessage id="TakeATour.Deactivate" defaultMessage="Deactivate"/>
                            </button>
                           </td></tr>
                          </table>
                        {this.state.notes && <div style={{fontStyle: "italic", marginBottom:"5 px", borderTop:"1px solid"}}> {this.state.notes}</div>}

            		</Modal>
            		);
        }
	    return (<div/>)

    }


	deactivateTakeATour() {
	    this.setState({displayTour:false});
		const restCallService = FactoryService.getInstance().getRestCallService();
        restCallService.postJson('/api/user/takeatour?active=false', this, {name:"new event"}, httpPayload => {
            httpPayload.trace("TakeATour.createEventCallback");
            debugger;
       	    const userService = FactoryService.getInstance().getUserService();
		    userService.setPrefsDisplayTakeATour(false);
            this.fctCallBack();
        });
    }
}
export default injectIntl(TakeATour);

