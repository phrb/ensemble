#!/bin/bash
# -nogui
echo `java -cp lib/libpd.jar:lib/ensemble_apps.jar:ensemble.jar:lib/NetUtil.jar:lib/jade.jar ensemble.tools.Loader --patch $1`
