import React from 'react';

export default class extends React.Component {
    render() {
        return <tr>
            <td>{this.props.comment.username}</td>
            <td>{this.props.comment.votesUp}</td>
        </tr>;
    }
}
