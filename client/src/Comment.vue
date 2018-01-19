<template>
    <tr>
        <td>{{ comment.username }}</td>
        <td :title="comment.createdAt">{{ toAgo(comment.createdAt) }}</td>
        <td>{{ comment.votesUp }}</td>
    </tr>
</template>

<script>
import { toAgo } from './date';

export default {
    name: 'Comment',
    props: {
        comment: {
            type: Object,
            default: () => {
                return {
                    username: 'user'
                };
            }
        }
    },
    methods: {
        toAgo
    }
};
</script>

<style scoped>

</style>
