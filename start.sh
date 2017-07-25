#!/bin/bash

rm logs.txt
#Set permissions to +rwx before starting:

export TF_CPP_MIN_LOG_LEVEL=3
export FLASK_APP=server.py
#Add all your configurations here

#------HERE---------------#

#in debug mode
flask run -h $1
