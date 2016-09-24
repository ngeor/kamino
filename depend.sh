#!/bin/bash

url='http://yuml.me/diagram/boring/class/'
isFirst=true
for i in `find . -type f -iname "*.csproj"`; do
	# get only filename
	project=`basename $i`

	# remove csproj extension
	project=${project%.csproj}

	references=`cat $i | grep '<ProjectReference' | cut -d "\"" -f 2`
	for ref in $references; do
		# keep only filename (assume Windows paths)
		ref=${ref##*\\}

		# remove csproj extension
		ref=${ref%.csproj}

		dep="[$project]->[$ref]"
		echo $dep

		if [[ "$isFirst" = true ]]; then
			isFirst=false
		else
			url="$url,"
		fi

		url="$url$dep"
	done

done

echo $url
mkdir -p dist
wget -q $url -O dist/project-dependencies.png
