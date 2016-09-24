#!/bin/sh
cd /opt/BuzzStats/$1/
unzip dist.zip
rm dist.zip
chown -R buzzstats:www-data .
