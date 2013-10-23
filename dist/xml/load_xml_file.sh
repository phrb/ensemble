#!/bin/bash
# -nogui
echo `java -cp ../lib/ensemble_apps.jar:../lib/ensemble.jar:../lib/NetUtil.jar:../lib/jade.jar ensemble.tools.Loader -nogui -f $1`
