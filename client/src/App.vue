<template>
    <div id="app">
        <h1>{{ msg }}</h1>
        <RecentActivity />
        <footer>
            Copyright 2010-{{ 1900 + new Date().getYear() }} Nikolaos Georgiou
        </footer>
    </div>
</template>

<script>
import RecentActivity from './RecentActivity.vue';

export default {
    name: 'App',
    components: { RecentActivity },

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return { msg: 'Welcome to Your Vue.js App' };
    }
};
</script>

<style scoped>

</style>
