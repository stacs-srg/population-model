#!/bin/bash

cd population-model
nohup sh src/main/resources/valipop/scripts/jobQ/start-job-q-instance.sh src/main/resources/valipop/scripts/jobQ/status-mani.txt src/main/resources/valipop/scripts/jobQ/job-q-mani.csv 37 1 20 > runs/job-run-`hostname`-$1.txt