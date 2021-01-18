// -----------------------------------------------------------
//
// Banner
//
// Banner controler. Display the banner in top.
//  Get the user information. If nobody are connected, be back immediately ! 
//
// -----------------------------------------------------------


class Banner extends React.Component {
	constructor() {
		super();
		}


	render() {
		return ( <div> Welcome the banner</div>)
	}
	
}	

console.log("Banner Render id=" + document.getElementById('reactBanner'));

ReactDOM.render(<Banner />, document.getElementById('reactBanner'));
