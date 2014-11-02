#!/bin/bash

case $1 in
	"help"| "")
		echo "This is a CI Pipeline which is versioned as part of a project.  The intent of this script is to provide a number"
		echo "of tasks that can be called from a CI server without needing to be dependent on any one CI server."
		echo ""
		echo "Run"
		echo "   $0 tasks"
		echo "to a list of all the tasks that are supported by this script."
		;;

	"tasks")
		echo "branch"
		echo "  Creates a release candidate branch using the following named format:"
		echo "    rc-yyyymmddhhmmss"
		echo "  As this is a release candidate branch it is necessary, at some point in your build pipeline, to execute the task"
		echo "    push"
		echo "  to commit any changes and update the remote repository with these changes."
		echo "help"
		echo "  Shows basic help screen."
		;;
	*)
		echo "The task \"$1\" is unknown.  Run"
		echo "   $0 tasks"
		echo "to a list of all the tasks that are supported by this script."
		;;
esac
