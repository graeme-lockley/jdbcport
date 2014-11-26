# jdbcport

A (very) simple ORM to support a hexagonal architecture style using a simple abstraction of database-table-row with
associated CRUD operations.

I am using this project as a prototype to create a ci-pipeline script to assist a CI server.  I have the view that the 
individual CI tasks should be included in a project's VCS so that, as the project evolves, the project knows how to 
build itself.  As this project is a library it's deployment is an upload into Nexus.  So for now the following steps are
included in the pipline:

- Phase 0: Prepare and Install in local repo
	- 0-001: `checkout-master`: Ensures that the we do not have a detached HEAD by checking out against master.
	- 0-002: `create-rc-branch`: Create a release candidate branch in the form `rc-YYYYMMdd-hhmmss`.
	- 0-003: `version-pom`: Update the project's POM to include the release candidate's version number.
	- 0-004: `maven-install`: Compile, test, package and install this project into the local repository.
- Phase 1: Release
	- 1-001: `push-rc-branch`: Push the release candidate branch with the updated POM to the remote git repository.
	- 1-002: `nexus-release`: Release this project into the public Nexus repository.
	- 1-003: `purge-old-rc-binaries`: Clean out the local repository of all of binaries from former release candidate builds that did not complete.

A couple of points:

- Although I started out thinking of using Jenkins or a build server to manage the pipeline, I have come to the conclusion that this necessarily ideal.  So I am rather going to use a collection of bash and ruby script coupled with cron to act as a sampling trigger.
- Phase 1 will need to be manually triggered.

Items still to be done:

- Phase 1's scripts still need to be written.
- Ensure that the results of the scripts are logged.
- Allow the pipeline scripts to be self describing.

