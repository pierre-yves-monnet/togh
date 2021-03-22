// -----------------------------------------------------------
//
// Footer
//
// Banner controler. Display the banner in top.
//  Get the user information. If nobody are connected, be back immediately ! 
//
// -----------------------------------------------------------
import React from 'react';



class Footer extends React.Component {
	
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.changeLanguage( newLanguage )

	constructor( props ) {
		super();
			this.state = { language: props.language}
	
		}



	render() {
		console.log("Footer.render");
		return(
		   <div class="toghfooter">
		   		<div class="row">
					<div class="col-xs-12" style={{background:"#e0f9d6"}} >
						<div style={{color: "black", textAlign: "right", fontSize: "small"}}>Py 2020</div>
					</div>
				</div>
		   </div>
		);
	}
	
}	
export default Footer;
