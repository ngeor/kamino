#!/bin/sh

PATH=/usr/local/bin:/usr/bin:/bin
APPPATH=/opt/BuzzStatsCrawler/current
exec /usr/local/bin/mono $MONO_OPTIONS $APPPATH/BuzzStats.Crawler.exe crawl


