#!/bin/bash
echo `java -cp ../lib/ensemble.jar:../lib/NetUtil.jar:../lib/jade.jar ensemble.tools.Loader -f $1`
