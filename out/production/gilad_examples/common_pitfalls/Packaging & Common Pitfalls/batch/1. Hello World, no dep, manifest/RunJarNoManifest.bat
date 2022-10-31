
java -cp "./HelloWorldNoManifest.jar" test.HelloWorld

rem this doesn't work since the class path wasn't supplied explicitly
rem java -jar HelloWorldNoManifest.jar test.HelloWorld

rem if the jar was in lib folder from where we stand it would have been
rem java -cp "./lib/HelloWorldNoManifest.jar" test.HelloWorld
