#!/bin/bash
source $HOME/.passphrases
set -e

if [ ! -e pom.xml ] ; then
	echo "This does not appear to be a Maven project."
	exit 1
fi

SNAPSHOT=0
CMD=deploy

while [ -n "$1" ] ; do
	case $1 in
		-s|--snapshot) SNAPSHOT=1 ;;
		--dry-run) CMD=package ;;
		*)
			echo "Unknown option: $1"
			exit 1
			;;
	esac
	shift
done

if [ $SNAPSHOT -eq 0 ] ; then
	#$HOME/bin/pom
	#if [ $? -eq 1 ] ; then exit 1 ; fi
	isrelease || {
		echo "This is not a release version."
		exit 1
	}
	validate-pom || {
		echo "Please fix the POM before deploying a release version."
		exit 1
	}
else
	#$HOME/bin/issnapshot
	#if [ $? -eq 1 ] ; then exit 1 ; fi
	issnapshot ||  {
		echo "This is not a snapshot version."
		exit 1
	}
fi

V=`version`
JAR=all-$V-sources.jar
mvn clean 
mvn package dependency:sources
cd target/sources
jar cvf $JAR META-INF org
mv $JAR ../
cd ../../
mvn javadoc:jar $CMD -Dgpg.passphrase="$PGP_PASSPHRASE"
#mvn javadoc:jar source:jar gpg:sign deploy -Dgpg.passphrase="$PGP_PASSPHRASE"
#mvn site
