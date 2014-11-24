# jdbcport

A (very) simple ORM to support a hexagonal architecture style using a simple abstraction of database-table-row with
associated CRUD operations.

I am using this project as a prototype to create a ci-pipeline script to assist a CI server.  I have the view that the 
individual CI tasks should be included in a project's VCS so that, as the project evolves, the project knows how to 
build itself.  As this project is a library it's deployment is an upload into Nexus.  So for now the following steps are
included in the pipline:

- Phase 0: Release Candidate Branch
	- 0-001: `checkout-master`: Ensures that the we do not have a detached HEAD by checking out against master.
	- 0-002: `create-rc-branch`: Create a release candidate branch in the form `RC-YYYYMMdd-hhmmss`.
	- 0-003: `version-pom`: Update the project's POM to include the release candidate's version number.
- Phase 1: Compile, Test and Package Project
	- 1-001: `maven-install`: Compile, test, package and install this project into the local repository.
- Phase 2: Release
	- 2-001: `push-rc-branch`: Push the release candidate branch with the updated POM to the remote git repository.
	- 2-002: `nexus-release`: Release this project into the public Nexus repository.
	- 2-003: `purge-old-rc-binaries`: Clean out the local repository of all of binaries from former release candidate builds that did not complete.

A couple of points:

- I still need to build the crontab trigger that will kick off this pipeline.
- I still need to build Phase 2.
- Phase 0 and Phase 1 are automatically triggered in Jenkins following a VCS update.
- Phase 2 will be manually triggered 
