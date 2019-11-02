#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
	mvn deploy -DskipTests=true -P sign,build-extras --settings cd/mvnsettings.xml
	mvn deploy -DskipTests=true -Dregistry=https://maven.pkg.github.com/blackdoor -Dtoken=$GH_TOKEN
fi
