Feature: Adding todo's

  Scenario: Add CLI
    When I type the arguments "help add"
    Then I should not get an error
    When I type the arguments "add"
    Then I should get an error
    When I type the arguments "add todo"
    Then I should not get an error