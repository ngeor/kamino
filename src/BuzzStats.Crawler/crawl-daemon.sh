#!/bin/sh

case "$1" in
	start)
		daemon -n BuzzStatsCrawler /opt/BuzzStatsCrawler/crawl.sh
		;;
	stop)
		daemon -n BuzzStatsCrawler --stop /opt/BuzzStatsCrawler/crawl.sh
		;;
	*)
		echo "Usage crawl-daemon.sh {start|stop}" >&2
		exit 3
		;;
esac


exit 0

