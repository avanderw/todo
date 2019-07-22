Feature: Command line

  Scenario: todo
  Print help if repository found
  Otherwise suggest init

    Given a repository exists
    When the command `todo` is executed
    Then print help
    Given a repository does not exist
    When the command `todo` is executed
    Then suggest init

  Scenario: todo init
  Create repository at current location
  Create repository at global location
  Prevent local nesting of todo repositories
  Prevent init of existing repository
  Allow overrides with the --force argument

    Given a local repository does not exist
    When the command `todo init` is executed
    Then create the repository at the current location
    Given a global repository does not exist
    When the command `todo init --global` is executed
    Then create the repository at the global location
    Given a repository exists
    When the command `todo init` is executed
    Then prevent the repository init
    When the command `todo init --force` is executed
    Then allow the repository init

  Scenario: todo status
  Report local repository status
  Report global repository status

    Given a local repository exists
    And a global repository exist
    When the command `todo status` is executed
    Then report the locations of two repositories
    And report that the default repository is the local repository
    Given a local repository does not exist
    And a global repository does exist
    When the command `todo status` is executed
    Then report the location of 1 repository
    And report the default repository is the global repository
    Given a local repository does not exist
    And a global repository does not exist
    When the command `todo status` is executed
    Then suggest init

  Scenario: todo sort
  Sort the repository

    Given a repository that is unsorted
    When the command `todo sort` is executed
    Then the repository is sorted

  Scenario: todo clean
  Move local todo items to done.txt
  Move global todo items to done.txt


  Scenario: todo edit
  Open text file with editor

  Scenario: todo explore
  Open directory of the repository

  Scenario: todo config
  Configure the default editor
  Configure auto-sort on state changes
  Configure auto-clean on state changes

  Scenario: todo ls
  List tasks from local repository
  List tasks from global repository
  List contexts
  List project
  List multiple AND strings

  Scenario: todo add
  Add task to local repository
  Add task to global repository
  Backup task repository before alteration

  Scenario: todo rm
  Remove task from local repository
  Remove task from global repository
  Backup task repository before alteration

  Scenario: todo do
  Complete task from local repository
  Complete task from global repository
  Backup task repository before alteration

  Scenario: todo mv
  Rewrite task from local repository
  Rewrite task from global repository
  Backup task repository before alteration

  Scenario: todo pri
  Prioritise task in local repository
  Prioritise task in global repository
  Backup task repository before alteration
  Remove priority of task
  Change priority of task