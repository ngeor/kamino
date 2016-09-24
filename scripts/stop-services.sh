#!/bin/bash

if [[ -e /etc/init.d/BuzzStats.Crawler ]]; then
	service BuzzStats.Crawler stop
fi

# todo also apply username filter --user blah
# ignore error code when no mono processes exist
killall mono || true
