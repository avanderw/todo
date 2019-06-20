Feature: Command line interface

  Scenario: Initialize
    When I type the arguments "help init"
    Then I should not get an error
    When I type the arguments "init"
    Then I should not get an error
    When I type the arguments "init target/test/initialize"
    Then I should not get an error


  Scenario: Filtering
    When I type the arguments "help list"
    Then I should not get an error
    When I type the arguments "help ls"
    Then I should not get an error
    When I type the arguments "ls"
    Then I should not get an error
    When I type the arguments "ls todo"
    Then I should not get an error
    When I type the arguments "ls -c"
    Then I should not get an error
    When I type the arguments "ls -p"
    Then I should not get an error
    When I type the arguments "ls -cp"
    Then I should not get an error


  Scenario: Addition
    When I type the arguments "help add"
    Then I should not get an error
    When I type the arguments "add"
    Then I should get an error
    When I type the arguments "add todo"
    Then I should not get an error
    When I type the arguments "add todo -r target/test/initialise"
    Then I should not get an error
