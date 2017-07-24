import React from 'react';

export default class extends React.Component {
    render() {
        return <div>{this.props.comment.user}</div>;
    }
}
