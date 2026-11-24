#!/bin/sh
# Kompilasi dan jalankan tanpa NetBeans (Linux/macOS). Butuh JDK 11+ dan JAR MySQL Connector/J di folder lib/
set -e
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp "out:lib/*" sewalapangan.Main
