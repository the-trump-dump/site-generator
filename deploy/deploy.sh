#!/usr/bin/env bash

APP_NAME=ttd-content

JOB_NAME=${APP_NAME}

cf d -f ${APP_NAME}
cf push -b staticfile_buildpack -p . ${APP_NAME}
cf set-health-check $APP_NAME none

cf restage ${APP_NAME}
