#!/usr/bin/env bash

set -e

usage()
{
    echo "Usage: ./build.sh [platform]"
}

[[ $# -ne 1 ]] && (usage && exit -1)

PLATFORM=$1

OUT_DIR="./out/production/hauptwerk-midi-stops"

# --minimizejre soft

cd $OUT_DIR
jar cf midistops.jar midistops/
java -jar ~/Downloads/packr.jar --platform $PLATFORM --jdk /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/ --executable midistops --classpath midistops.jar --mainclass midistops.Main --output out-$PLATFORM --resources resources/

zip -r midistops.zip "out-$PLATFORM"
