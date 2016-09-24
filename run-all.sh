#!/bin/bash
cd src/BuzzStats.Crawler/bin/Debug && xterm -e mono BuzzStats.Crawler.exe crawl &
cd src/BuzzStats.Web && xterm -e ./xsp.sh &

