#!/bin/bash

case $1 in
	"branch")
		BRANCH=`git status | grep "On branch master" | wc -l`
		UNSTAGED_CHANGES=`git status | grep "Changes not staged for commit" | wc -l`
		CHANGES_TO_BE_COMMITTED=`git status | grep "Changes to be committed" | wc -l`
		UNTRACKED_CHANGES=`git status | grep "Untracked files:" | wc -l`
		NEED_TO_PUSH=`git status | grep "use \"git push\" to publish your local commits" | wc -l`
		if [ $BRANCH != '1' ]
		then
			echo "branch: error: Unable to branch as you are Not on branch master"
		elif [ $UNSTAGED_CHANGES = '1' ]
		then
			echo "branch: error: Unable to branch as you have changes that are not staged for commit"
		elif [ $CHANGES_TO_BE_COMMITTED = '1' ]
		then
			echo "branch: error: Unable to branch as you have changes on branch master that have yet to be committed"
		elif [ $UNTRACKED_CHANGES = '1' ]
		then
			echo "branch: error: Unable to branch as you have untracked changes on branch master"
		elif [ $NEED_TO_PUSH = '1' ]
		then
			echo "branch: error: Unable to branch as you have committed changes which you need to push to the remote repository"
		else
			BRANCH_NAME=RC-`date "+%Y%m%d%H%M%S"`
			echo "branch: info: Branch name: $BRANCH_NAME"
			git branch $BRANCH_NAME

			echo "branch: info: Switching to branch: $BRANCH_NAME"
			git checkout $BRANCH_NAME
		fi

		;;
	"help"| "")
		echo "This is a CI Pipeline which is versioned as part of a project.  The intent of this script is to provide a number"
		echo "of tasks that can be called from a CI server without needing to be dependent on any one CI server."
		echo ""
		echo "Run"
		echo "   $0 tasks"
		echo "to list all of the tasks that are supported by this script."
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
		echo "to list all of the tasks that are supported by this script."
		;;
esac
