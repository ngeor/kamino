# BuzzStats
BuzzStats collects data from Buzz in order to extract and present statistical
information.

[![Build Status](https://travis-ci.org/ngeor/BuzzStats.svg?branch=master)](https://travis-ci.org/ngeor/BuzzStats)
[![Coverage Status](https://coveralls.io/repos/github/ngeor/BuzzStats/badge.svg?branch=master)](https://coveralls.io/github/ngeor/BuzzStats?branch=master)

## System Setup

The following software is required:

- node
- node packages: bower, grunt-cli, jscs, jscs-jsdoc, jshint, mocha, istanbul, yo
- ruby
- ruby gems: sass scss_lint compass
  tip: pass --no-format-executable to gem install if it adds version suffixes to executables
- nuget

## Building

Install dependencies:

- npm install
- bower install
- nuget restore (in src folder)

Build using grunt:

	grunt ci build --projectConfiguration=Release

Ports:

	8420 web
	8421 crawler

Environment Variables during the build:

- `SEMANTIC_VERSION=1.5.16`
- `GIT_BRANCH=origin/project-downsize`
- `VERSION=1.5.16.114`
- `FOLDER=1.5.16.114-project-downsize`
