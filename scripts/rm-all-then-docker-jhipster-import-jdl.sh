#!/usr/bin/env bash
set -e
set -x

cd "$(dirname "$0")/.."

rm -rf $(ls -a | egrep -v '^(scripts|\.|\.\.|\.git|\.gitignore|\.idea|\.jhipster)$')

./scripts/docker-jhipster-import-jdl.sh
