:: executes the project maven must be in the path and the registry must be already open
@echo off
start mvn exec:java -Pserver
timeout 5
start mvn exec:java -Pclient