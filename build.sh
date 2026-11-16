#!/bin/sh
# Build aero.jar from source (needs a JDK 17+)
rm -rf out && mkdir out
javac -d out src/aero/*.java && cp -r res/aero out/ && jar --create --file aero.jar --main-class aero.AeroDemo -C out .
echo "built aero.jar  -  run the gallery with: java -jar aero.jar"
