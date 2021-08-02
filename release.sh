#!/usr/bin/env bash
# Recursively find and replace in files
old='v[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9].[0-9][0-9].[0-9][0-9]'
version=v$(date +"%Y-%m-%d.%H.%M")

grep -rl "$old" * | xargs sed -i -e "s/$old/$version/g"

mvn clean
mvn package