#!/bin/bash
# Script adb+
# Usage
# You can run any command adb provides on all your currently connected devices
# ./adb+ <command> is the equivalent of ./adb -s <serial number> <command>
#
# Examples
# ./adb_all adb version
# ./adb_all adb install apidemo.apk
# ./adb_all adb uninstall com.example.android.apis

adb_command=$1

$adb_command devices | while read line
do
    if [ ! "$line" = "" ] && [ `echo $line | awk '{print $2}'` = "device" ]
    then
        device=`echo $line | awk '{print $1}'`
        echo "$device ${@:2} ..."
        $adb_command -s $device ${@:2}
    fi
done