Feature: Prioritising todo's

  Scenario: Prioritise CLI
    When I type the arguments "help priority"
    Then I should not get an error
    When I type the arguments "help pri"
    Then I should not get an error
    When I type the arguments "pri"
    Then I should get an error
    When I type the arguments "pri -r"
    Then I should get an error
    When I type the arguments "pri -r 1"
    Then I should not get an error
    When I type the arguments "pri 1"
    Then I should not get an error
    When I type the arguments "pri 1 A"
    Then I should not get an error
    When I type the arguments "pri 1 AA"
    Then I should get an error