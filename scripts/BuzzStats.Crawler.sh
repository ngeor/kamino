#!/bin/bash

PATH=/usr/local/bin:/usr/bin:/bin
APPPATH=@APPROOT@/current/crawler
MONO=/usr/local/bin/mono
exec -a BuzzStats.Crawler $MONO $MONO_OPTIONS $APPPATH/BuzzStats.Crawler.exe crawl
