#!/usr/bin/env bash

if [ $# -eq 1 ]
  then
    echo "Removing subcommand $1"
    rm -rf /home/vagrant/.dcos/subcommands/$1
  else
    echo "Please supply one argument: The framework name / package name."
fi
