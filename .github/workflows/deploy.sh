#!/usr/bin/env bash

ROOT_DIR=$(cd $(dirname $0) && pwd)
APP_NAME=site-generator
PROJECT_ID=${GCLOUD_PROJECT}

## Create the Docker Image
TAG_NAME=${1:-$(date +%s)}
IMAGE_TAG="production${GITHUB_SHA:-}"
GCR_IMAGE_NAME=gcr.io/${PROJECT_ID}/twi-ttd-${APP_NAME}
mvn -f ${ROOT_DIR}/../../pom.xml -DskipTests=true \
  -e -Dspring.profiles.active=production  \
  clean \
  package \
  spring-boot:build-image
#  verify \
#  deploy \


image_id=$(docker images -q $APP_NAME)
docker tag "${image_id}" ${GCR_IMAGE_NAME}:latest
docker tag "${image_id}" ${GCR_IMAGE_NAME}:${IMAGE_TAG}
docker push ${GCR_IMAGE_NAME}:latest
docker push ${GCR_IMAGE_NAME}:${IMAGE_TAG}

## Deploy the application to Kubernetes
cd $GITHUB_WORKSPACE/deploy
SECRETS_FN=secrets.yaml
touch $SECRETS_FN
echo writing to "${SECRETS_FN}..."
cat <<EOF >${SECRETS_FN}
apiVersion: v1
kind: ConfigMap
metadata:
  name: ttd-twi-site-generator-config
data:
  SPRING_PROFILES_ACTIVE: "cloud"
  SPRING_DATASOURCE_PASSWORD: "${DB_PW}"
  SPRING_DATASOURCE_USERNAME: "${DB_USER}"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://${DB_HOST}:5432/${DB_DB}"
  GIT_PASSWORD:  "${GIT_PASSWORD}"
  GIT_USERNAME : "${GIT_USERNAME}"
EOF

ls -la

kubectl apply -n $TWI_NS -f .
rm ${SECRETS_FN}
cd ${GITHUB_WORKSPACE}
kubectl create job --from=cronjob.batch/twi-ttd-site-generator-cronjob  twi-ttd-site-generator-cronjob-${RANDOM} -n $TWI_NS
