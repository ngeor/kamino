#!/bin/bash
# Last updated: 2015-08-23

case "$1" in
	start)
		daemon -n BuzzStats.Crawler --respawn /opt/BuzzStats/scripts/BuzzStats.Crawler.sh
		;;
	stop)
		daemon -n BuzzStats.Crawler --stop /opt/BuzzStats/scripts/BuzzStats.Crawler.sh
		;;
	*)
		echo "Usage $0 start|stop"
		exit 3
		;;
esac

exit 0
