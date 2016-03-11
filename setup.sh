#!/usr/bin/env sh

mvn install:install-file -Dfile="libs/loklakj_lib.jar" -DgroupId=org.loklak -DartifactId=loklakj_lib -Dversion=0.1 -Dpackaging=jar
