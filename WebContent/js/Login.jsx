
class Login extends React.Component {
	constructor () {
	    super();
	}
	 render() {
    return (
      	<div className="App">
			Email adress<p/>
			<input type="string"></input><p/>
			Password<p/>
			<input type="password"></input><p/>
        	<button class="btn btn-info"  onClick={this.connect}>Connection</button>
      	</div>
    )
  }

	connect() {
		console.log("Click !");
	}
}

// export default Login
//
//					<button class="btn btn-info"  onClick={this.connect}>Connection</button>
//			</div>



console.log("Login Render id="+document.getElementById('login'));

ReactDOM.render(<Login />, document.getElementById('login'));
