#!/bin/sh

find @APPROOT@/ -mindepth 1 -maxdepth 1 -type d -not -name scripts -not -name log -not -name "`readlink current`" | xargs rm -rf
