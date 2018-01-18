<template>
    <div class="recent-activity">
        {{ msg }}
    </div>
</template>

<script>
export default {
    name: 'RecentActivity',

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return { msg: 'Recent Activity!' };
    }
};
</script>

<style scoped>

</style>
