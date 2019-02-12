#!/bin/sh
cd /root/device_server
./device_server.jar stop
rm device_server.jar
cd ..
mv device_server.jar device_server/device_server.jar
cd device_server
chmod +x device_server.jar
./device_server.jar start
cd ..
rm $0

