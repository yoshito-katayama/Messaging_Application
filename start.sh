#!/bin/bash

# Db2の起動
colima start --arch x86_64 --vm-type vz --vz-rosetta --mount-type virtiofs
docker start db2test

# MQの起動
podman start QM1