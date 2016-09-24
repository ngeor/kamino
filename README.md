# BuzzStats
BuzzStats collects data from Buzz in order to extract and present statistical i
nformation.

## System Setup

The following software is required:

- node
- node packages: bower, grunt-cli, jscs, jscs-jsdoc, jshint, mocha, istanbul, yo
- ruby
- ruby gems: sass scss_lint compass
  tip: pass --no-format-executable to gem install if it adds version suffixes to executables
- nuget
- xdt
- igloocastle

To be able to build native ruby extensions of openSUSE:

    sudo zypper install ruby-devel
    sudo zypper install autoconf automake libtool gcc gcc-c++ make

## NuGet Sources

Add a custom nuget source:

    $ nuget sources
    Registered Sources:

      1.  https://www.nuget.org/api/v2/ [Enabled]
          https://www.nuget.org/api/v2/
      2.  priv [Enabled]
          http://192.168.1.2:8081/nexus/service/local/nuget/nuget/

    $ nuget sources Add -Name priv -Source http://192.168.1.2:8081/nexus/service/local/nuget/nuget/


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
