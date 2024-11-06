#!/bin/bash

# Db2の停止
docker stop db2test
colima stop

# MQの停止
podman stop QM1