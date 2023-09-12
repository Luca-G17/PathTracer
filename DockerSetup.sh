#!/bin/sh

./mvnw package

JAVA_FX_PATH=/javafx/lib

if [ "$1" = "gui" ]
then
  START_CLASS=luca.raytracing.App
else
  START_CLASS=luca.raytracing.CommandLine
fi

mkdir Renders
java --module-path=$JAVA_FX_PATH --add-modules javafx.controls,javafx.fxml -cp ./target/PathTracer.jar $START_CLASS
