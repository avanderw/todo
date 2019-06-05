Feature: Listing todo's

  Scenario: List CLI
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