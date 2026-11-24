@echo off
REM Kompilasi dan jalankan tanpa NetBeans (Windows). Butuh JDK 11+ dan JAR MySQL Connector/J di folder lib\
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 exit /b 1
java -cp "out;lib/*" sewalapangan.Main
