import React from 'react';
import {parseDate, agoString} from './date';

export default class extends React.Component {
    render() {
        return <tr>
            <td>{this.props.comment.username}</td>
            <td title={this.props.comment.createdAt}>
                {agoString(new Date().getTime() - parseDate(this.props.comment.createdAt))}
            </td>
            <td>{this.props.comment.votesUp}</td>
        </tr>;
    }
}
