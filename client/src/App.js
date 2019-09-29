import React from 'react';
import axios from 'axios';
import './App.css';

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      urlsFound: 0,
      errorMessage: ''
    };
  }

  render() {
    return (
      <div className="app">
        <header>BuzzStats 2.0.0-alpha</header>
        <div>
          URLs found: {this.state.urlsFound}
        </div>
        <div className="error">
          {this.state.errorMessage}
        </div>
      </div>
    );
  }

  async componentDidMount() {
    try {
      const res = await axios.get("http://localhost:8080/");
      this.setState({
          urlsFound: res.urlsFound,
          errorMessage: ''
      });
    } catch (e) {
      this.setState({
        urlsFound: 0,
        errorMessage: e.message
      });
    }
  }
}
