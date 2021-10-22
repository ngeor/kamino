#!/bin/bash
set -e -o pipefail
if [[ -z "$GPG_KEY" ]]; then
    echo "GPG_KEY not set"
    exit 1
fi

if [[ -z "$GPG_PASSPHRASE" ]]; then
    echo "GPG_PASSPHRASE not set"
    exit 1
fi

if [[ -z "$OSSRH_USERNAME" ]]; then
    echo "OSSRH_USERNAME not set"
    exit 1
fi

if [[ -z "$OSSRH_PASSWORD" ]]; then
    echo "OSSRH_PASSWORD not set"
    exit 1
fi

KEYS_ASC="$(dirname $0)/keys.asc"

function import_gpg {
    # Import GPG key
    gpg --batch --yes --passphrase=${GPG_PASSPHRASE} --output - $KEYS_ASC | gpg --batch --yes --import
}

function clean_gpg {
    # cleanup GPG keys
    # cleanup secret key by fingerprint
    gpg -K | grep "^  " | tr -d " " | xargs gpg --batch --yes --delete-secret-keys
    # cleanup key
    gpg --batch --yes --delete-key ${GPG_KEY}
}

# GITHUB_REF -> refs/heads/feature-branch-1
if [[ -z "$GITHUB_REF" ]]; then
    mvn release:clean
    mvn -B release:prepare
    trap "{ clean_gpg; }" EXIT
    import_gpg
    GPG_KEY=$GPG_KEY \
        GPG_PASSPHRASE=$GPG_PASSPHRASE \
        OSSRH_USERNAME=$OSSRH_USERNAME \
        OSSRH_PASSWORD=$OSSRH_PASSWORD \
        mvn -B -s "$(dirname $0)/settings.xml" release:perform
else
    trap "{ clean_gpg; }" EXIT
    import_gpg
    GPG_KEY=$GPG_KEY \
        GPG_PASSPHRASE=$GPG_PASSPHRASE \
        OSSRH_USERNAME=$OSSRH_USERNAME \
        OSSRH_PASSWORD=$OSSRH_PASSWORD \
        mvn -B -s "$(dirname $0)/settings.xml" \
        -Pgpg \
        -DskipTests=true -Dcheckstyle.skip=true -Djacoco.skip=true -Dinvoker.skip=true \
        deploy
fi
