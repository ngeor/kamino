#!/bin/bash

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
    RESULT=$?
    if [[ $RESULT -ne 0 ]]; then
        echo "Could not import GPG key"
        exit $RESULT
    fi
}

function clean_gpg {
    # cleanup GPG keys
    gpg --fingerprint --with-colons ${GPG_KEY} |\
        grep "^fpr" |\
        sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
        xargs gpg --batch --yes --delete-secret-keys

    gpg --batch --yes --delete-key ${GPG_KEY}
}

# GITHUB_REF -> refs/heads/feature-branch-1
echo "github ref:"
echo $GITHUB_REF
printenv

if [[ -z "$GITHUB_REF" ]]; then

    mvn release:clean
    mvn -B release:prepare
    import_gpg
    GPG_KEY=$GPG_KEY \
        GPG_PASSPHRASE=$GPG_PASSPHRASE \
        OSSRH_USERNAME=$OSSRH_USERNAME \
        OSSRH_PASSWORD=$OSSRH_PASSWORD \
        mvn -B -s "$(dirname $0)/settings.xml" release:perform
    clean_gpg

else

    import_gpg
    GPG_KEY=$GPG_KEY \
        GPG_PASSPHRASE=$GPG_PASSPHRASE \
        OSSRH_USERNAME=$OSSRH_USERNAME \
        OSSRH_PASSWORD=$OSSRH_PASSWORD \
        mvn -B -s "$(dirname $0)/settings.xml" release:perform \
        -DdryRun=true \
        -DconnectionUrl=scm:git:https://github.com/ngeor/java.git/tags/$TAG
    clean_gpg

fi
