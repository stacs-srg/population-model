#!/bin/bash

cd population-model
nohup sh src/main/resources/valipop/scripts/jobQ/start-job-q-instance.sh src/main/resources/valipop/scripts/jobQ/status-sif.txt src/main/resources/valipop/scripts/jobQ/job-q-clusters.csv 8 1 5.0 > runs/job-run-`hostname`-A.txt