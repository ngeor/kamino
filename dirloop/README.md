dirloop
=========

CLI utility that runs a command on multiple directories

[![Build Status](https://travis-ci.org/ngeor/dirloop.svg?branch=master)](https://travis-ci.org/ngeor/dirloop)
[![npm (scoped)](https://img.shields.io/npm/v/@ngeor/dirloop.svg)](https://www.npmjs.com/package/@ngeor/dirloop)
[![Coverage Status](https://coveralls.io/repos/github/ngeor/dirloop/badge.svg)](https://coveralls.io/github/ngeor/dirloop)
[![Dependencies](https://david-dm.org/ngeor/dirloop.svg)](https://david-dm.org/ngeor/dirloop)
[![devDependencies Status](https://david-dm.org/ngeor/dirloop/dev-status.svg)](https://david-dm.org/ngeor/dirloop?type=dev)

## Installation

dirloop requires nodeJS 10+.

You can install dirloop with `npm i -g @ngeor/dirloop`. In that case, you can invoke it with `dirloop`.

You can also use it without installation with `npx @ngeor/dirloop`.

## Typical use case

I want to run a series of git commands on multiple directories. They are all in
the same parent directory.

```
$ dirloop git gc
$ dirloop git fetch -p -t
$ dirloop git checkout -- .
$ dirloop git checkout master
$ dirloop git reset --hard origin/master
```

## Syntax

`dirloop [parameters] command [command arguments]`

(or `npx @ngeor/dirloop` if you don't want to install it)

## Parameters

#### `--help`

Show help about the command.

#### `--dir <dir>`

The parent directory. It contains the directories in which the command
will be run. Defaults to the current directory.

### Filtering Parameters

#### `--dir-prefix <prefix1>,<prefix2>`

An optional set of prefixes, in order to select only some of the
sub-directories (e.g. `--dir-prefix app`, `--dir-prefix src,test`).

#### `--has-file <filename>`

Only match directories that contain this filename (e.g. `--has-file package.json`).

#### `--has-json <filename;query>`

Only match directories that contain this filename. The file must be JSON and it needs to meet the given query.

**Examples**

The property `devDependencies.eslint` must exist:

```
--has-json package.json;devDependencies.eslint
```

The property `devDependencies.eslint` must be equal to `^5.10.0`:

```
--has-json package.json;devDependencies.eslint === '^5.10.0'
```

The property `devDependencies.eslint` must exist and not be equal to `^5.10.0`:

```
--has-json package.json;devDependencies.eslint !== '^5.10.0'
```

The property `nyc.reporter` must be an array containing `text-summary`:

```
--has-json package.json;nyc.reporter.indexOf('text-summary') >= 0
```

#### `--eval-js <nodejs-script>`

Only match directories in which the given nodeJS script evaluates
successfully.

Example:

```
--eval-js "var x = JSON.parse(fs.readFileSync('package.json')); process.exitCode = x.devDependencies['chai'] ? 1 : 0"
```

will match directories that do not use the `chai` dev dependency.


### Execution Parameters

#### `--dry-run`

Don't actually run the command, just see what would happen.

#### `--shell`

Run the command inside a shell process.

#### `--set-json <file;expression>`

Modify a JSON file.

**Examples**

To set the version of ESLint:

```
--set-json "package.json;j.devDependencies.eslint='^5.11.10'"
```

#### `--csv`

Captures the output of the commands and prints them together with the name of the directory.

This is useful when the command is going to generate a single line of text that needs to be combined with
the name of the directory in order to generate a CSV report.

**Example**

Print the number of commits on all repos since January 1st, 2018:

```
dirloop --csv --line-count -- git rev-list --since=2018-01-01 master
```

Will print a report like this:

```
clone-all,31
dirloop,10
```

### Output transformation parameters

#### `--line-count`

Instead of printing the output of the command, print the number of non-empty lines it produced.

Works only when combined with `--csv`.

####  Without command

If you don't specify the command to run, dirloop will print the matching directories (absolute paths).
