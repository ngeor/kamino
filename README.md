# BuzzStats
BuzzStats collects data from Buzz in order to extract and present statistical
information.

[![Build Status](https://travis-ci.org/ngeor/BuzzStats.svg?branch=master)](https://travis-ci.org/ngeor/BuzzStats)
[![Coverage Status](https://coveralls.io/repos/github/ngeor/BuzzStats/badge.svg?branch=master)](https://coveralls.io/github/ngeor/BuzzStats?branch=master)

## Development

- If you don't have a MySQL database, you can start one with `docker-compose up`.
- Run the backend application. Verify it runs at http://localhost:9000/api/recentactivity
- Run the frontend application (`cd client && npm run dev`). Verify it runs at http://localhost:8080/

## First time run

- You might want to set ExportSchema to True in `app.config`, so that the database schema gets created.
