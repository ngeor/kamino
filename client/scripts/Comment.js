import React from 'react';
import {toAgo} from './date';

export default class extends React.Component {
    render() {
        return <tr>
            <td>{this.props.comment.username}</td>
            <td title={this.props.comment.createdAt}>
                {toAgo(this.props.comment.createdAt)}
            </td>
            <td>{this.props.comment.votesUp}</td>
        </tr>;
    }
}
