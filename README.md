# zfs-snapshot-trimmer
Remove older zfs snapshots.

It keeps:

* all snapshots of the current month
* the first snapshot of previous months of the current year
* the first snapshot of previous years

Everything else will be deleted.

## How to build

Use Maven (mvn package).

## How to run

java -jar zfs-snapshot-trimmer-1.0-SNAPSHOT.jar

or use the shell script: zfs-snapshot-trimmer.sh

Parameters:

* --dry-run: It will not actually delete snapshots but it will print what it would have deleted.
