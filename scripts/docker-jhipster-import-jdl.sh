#!/usr/bin/env bash
set -e
set -x

cd "$(dirname "$0")/.."

./scripts/docker-jhipster.sh jhipster import-jdl ./scripts/swingifts.jdl --force
