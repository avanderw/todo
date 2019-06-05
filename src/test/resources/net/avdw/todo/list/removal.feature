Feature: Removing todo's

  Scenario: Remove CLI
    When I type the arguments "help remove"
    Then I should not get an error
    When I type the arguments "help del"
    Then I should not get an error
    When I type the arguments "help rm"
    Then I should not get an error
    When I type the arguments "rm"
    Then I should get an error
    When I type the arguments "rm 1"
    Then I should not get an error