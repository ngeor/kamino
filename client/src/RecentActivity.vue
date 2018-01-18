<template>
    <div class="recent-activity">
        {{ msg }}
        <ol>
            <li
                v-for="recentActivity in recentActivities"
                :key="recentActivity.id">
                {{ recentActivity }}
            </li>
        </ol>
    </div>
</template>

<script>
import ajax from './ajax';

export default {
    name: 'RecentActivity',

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return {
            msg: 'Recent Activity!',
            recentActivities: [

            ]
        };
    },

    /**
     * Initializes the component.
     */
    created() {
        ajax('http://localhost:9000/api/recentactivity').then(data => {
            this.recentActivities = data;
        }).catch(reason => alert(reason));
    }
};
</script>

<style scoped>

</style>
