#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo $SIGNING_KEY | base64 --decode > cd/signingkey.asc
    gpg --fast-import cd/signingkey.asc
fi