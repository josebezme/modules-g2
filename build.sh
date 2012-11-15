#!/bin/bash

javac -d bin -cp lib/commons-codec-1.7.jar:lib/Jama-1.0.2.jar:lib/gson-2.2.2-javadoc.jar:lib/gson-2.2.2.jar:lib/guava-13.0.1-javadoc.jar:lib/guava-13.0.1.jar:lib/jsoup-1.7.1-javadoc.jar:lib/jsoup-1.7.1-sources.jar:lib/jsoup-1.7.1.jar:lib/log4j-1.2.17.jar src/g2/*.java src/g2/api/*.java src/g2/bing/*.java src/g2/bing/json/*.java src/g2/model/*.java src/g2/testing/hierarchy/*.java src/g2/testing/querycache/*.java src/g2/util/*.java src/g2/util/cleaners/*.java
cp src/log4j.properties bin/
