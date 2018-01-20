# BuzzStats
BuzzStats collects data from Buzz in order to extract and present statistical
information.

[![Build Status](https://travis-ci.org/ngeor/BuzzStats.svg?branch=master)](https://travis-ci.org/ngeor/BuzzStats)

## Development

### Tips

- If you don't have a MySQL database, you can start one with `docker-compose up`.
- Run the backend application. Verify it runs at http://localhost:9000/api/recentactivity
- Run the frontend application (`cd client && npm run dev`). Verify it runs at http://localhost:8080/

### First time run

- You might want to set ExportSchema to True in `app.config`, so that the database schema gets created.

### Docker Compose

Using `docker-compose up`, you can start various services the app depends on:

- mysql database
- zookeeper and kafka
- database web UI available at http://localhost:8081/

## Services

(Note: this is the future design)

The services are event driven.

| Event             | Triggered By                 | Consumed By     |
| ----------------- | ---------------------------- | --------------- |
| List Expired      | Timer                        | List Ingester   |
| Story Discovered  | List Ingester, Story Updater | Story Ingester  |
| Story Expired     | Story Updater                | Story Ingester  |
| Story Parsed      | Story Ingester               | Change Tracker  |
| Story Changed     | Change Tracker               | Web             |

* List Ingester: parses listing pages and publishes
  the stories it discovers as Story Discovered events.
* Story Ingester: parses story pages and publishes them
  as Story Parsed events.
* Story Updater: selects stories that should be rescanned for changes
  and publishes them as Story Expired events.
* Change Tracker: compares its state with incoming Story Parsed events
  and publishes activity events.

![Events Sequence](docs/events-sequence.png?raw=true "Events Sequence")

<!-- title Events Sequence

Timer->List Ingester: List Expired
List Ingester->Story Ingester: Story Discovered
Story Updater->Story Ingester: Story Expired
Story Ingester->Change Tracker: Story Parsed
Change Tracker->Web: Story Changed
 -->
