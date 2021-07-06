#!/bin/bash
set -ex

# edit the script before running anything
exit 1

OLD_REPO=kamino
NEW_REPO=python

for p in bclean chm-helper delete-old-tweets git-analyze kddbot version-ci-bot wpbot
do

    pushd $OLD_REPO
    git branch -D temp || true
    git subtree split -P $p/ -b temp
    popd

    pushd $NEW_REPO
    git subtree add -P apps/$p ../$OLD_REPO/ temp
    popd

    pushd $OLD_REPO
    git branch -D temp || true
    git rm -rf $p/
    git commit -m "Migrated $p to new repo"
    popd

done
