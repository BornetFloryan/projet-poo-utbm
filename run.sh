#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
find src/main/java -name '*.java' -print > out/sources.txt
javac --release 17 -encoding UTF-8 -d out @out/sources.txt
cp -R src/main/resources/. out/
java -cp out survivalgame.Main
