#!/usr/bin/env bash

RED="\e[31m"
GREEN="\e[32m"
BOLD="\e[1m"
RESET="\e[0m"
MVN="${MVN:-mvnd}"

while true; do
    # run tests
    echo -e "Running ${BOLD}tests${RESET}"
    ${MVN} -B -q test
    if [[ $? -ne 0 ]]; then
        echo -e "${RED}Tests failed!${RESET}"
        exit 1
    fi
    # any changes to commit?
    git diff --quiet
    if [[ $? -ne 0 ]]; then
        # has changes
        echo -e "${GREEN}Committing changes${RESET}"
        git cm "wip"
    fi
    # run rewrite
    echo -e "Running ${BOLD}rewrite${RESET}"
    ${MVN} -B -q -pl '!:kamino' rewrite:run
    if [[ $? -ne 0 ]]; then
        echo -e "${RED}Rewrite failed!${RESET}"
        exit 2
    fi
    # run spotless
    echo -e "Running ${BOLD}spotless${RESET}"
    ${MVN} -B -q -pl '!:kamino' spotless:apply
    if [[ $? -ne 0 ]]; then
        echo -e "${RED}Spotless failed!${RESET}"
        exit 3
    fi
    # any changes to commit on the next iteration?
    git diff --quiet
    if [[ $? -eq 0 ]]; then
        # no changes
        exit 0
    fi
done
