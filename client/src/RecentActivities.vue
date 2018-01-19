<template>
    <ol class="recent-activity">
        <li
            v-for="recentActivity in recentActivities"
            :key="recentActivity.id">
            {{ recentActivityToText(recentActivity) }}
        </li>
    </ol>
</template>

<script>
import ajax from './ajax';
import { toAgo } from './date';

/**
 * Converts a recent activity to a text.
 * @param {object} recentActivity - The recent activity object.
 * @returns {string} The text representation.
 */
function recentActivityToText(recentActivity) {
    if (!recentActivity) {
        return 'N/A';
    }

    if (recentActivity.storyVoteUsername) {
        return `Story ${recentActivity.title} voted by user ${recentActivity.storyVoteUsername}`;
    }

    if (recentActivity.commentUsername) {
        return `New comment on story ${recentActivity.title}` +
            ` by user ${recentActivity.commentUsername}` +
            ` (${toAgo(recentActivity.createdAt)})`;
    }

    return `New story ${recentActivity.title}` +
        ` by user ${recentActivity.storyUsername}` +
        ` (${toAgo(recentActivity.createdAt)})`;
}

export default {
    name: 'RecentActivities',

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return {
            recentActivities: []
        };
    },

    /**
     * Initializes the component.
     */
    created() {
        ajax('http://localhost:9000/api/recentactivity').then(data => {
            this.recentActivities = data;
        }).catch(reason => alert(reason));
    },

    methods: {
        recentActivityToText
    }
};
</script>

<style scoped>

</style>
