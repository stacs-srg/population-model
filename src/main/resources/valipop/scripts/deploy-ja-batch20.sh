#!/bin/bash

TD_LOAD_AVG=`uptime | sed 's/.*load average: //' | awk -F\, '{print $1}'`
TD_NODE_IN_USE=`echo $TD_LOAD_AVG'>'0.5 | bc -l`


if [ $TD_NODE_IN_USE -eq 0 ]; then
	cd population-model
	nohup sh src/main/resources/valipop/scripts/run-factor-search.sh 14 src/main/resources/valipop/inputs/proxy-scotland-population-JA/ 40000 ja-batch20 1 0.3,0.6,1.0 0.0,0.3,0.6,1.0 /cs/tmp/tsd4/results/ > ja-batch20-`hostname`.txt
fi