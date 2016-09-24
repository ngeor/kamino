#!/bin/bash

APPROOT=@APPROOT@
VERSION=$1

if [ -z $VERSION ]; then
	echo "Syntax: $0 version"
	exit 1
fi

VERSION_DIR=$APPROOT/$VERSION

if [ ! -d $VERSION_DIR ]; then
	echo "Directory $VERSION_DIR not found"
	exit 1
fi

CURRENT_DIR=$APPROOT/current

if [ `readlink $CURRENT_DIR` = $VERSION ]; then
	echo "Directory $VERSION already current version."
	exit 1
fi

if [[ $EUID -ne 0 ]]; then
	echo "Run as root please."
	exit 1
fi

# stop services
service BuzzStats.Crawler stop
service apache2 stop

# switch version folder
unlink $CURRENT_DIR
ln -r -s $VERSION_DIR $CURRENT_DIR

# clean temporary ASP.NET files
rm -rf /tmp/www-data-temp-aspnet-0/

# start services
service BuzzStats.Crawler start
service apache2 start
