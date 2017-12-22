#!/bin/bash


docker build -t sunshower-core -f Dockerfile .   
docker run -it --rm -e MVN_REPO_USERNAME=myMavenRepo \
-e MVN_REPO_PASSWORD=lid-DOG-bin-123 \
-e MVN_REPO_URL=https://mymavenrepo.com/repo/IRgrTxMdF4OnnbNAkfnJ \
--name "sunshower-core" "sunshower-core" \


