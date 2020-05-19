# pj006-push-poc-adapter

Clone the repo.

cd to /src/com/solacesystems/jcsmp/samples/introsamples/intro

Change the config in the config.txt

Compile the java progarm using this command
javac -cp "../../../../../../../lib/sol-common-10.8.1.jar:../../../../../../../lib/sol-jcsmp-10.8.1.jar:../../../../../../../lib/commons-lang-2.6.jar:../../../../../../../lib/commons-logging-1.1.3.jar:../../../../../../../lib/sol-jcsmp-10.8.1-javadoc.jar" -d ../../../../../../../bin *.java

Run the adapter using this command.
java -cp "../../../../../../../bin:../../../../../../../lib/sol-common-10.8.1.jar:../../../../../../../lib/sol-jcsmp-10.8.1.jar:../../../../../../../lib/commons-lang-2.6.jar:../../../../../../../lib/commons-logging-1.1.3.jar:../../../../../../../lib/sol-jcsmp-10.8.1-javadoc.jar" com.solacesystems.jcsmp.samples.introsamples.intro.SimpleTranslatorService
