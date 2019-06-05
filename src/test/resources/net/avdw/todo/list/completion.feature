Feature: Completing todo's

  Scenario: Complete CLI
    When I type the arguments "help complete"
    Then I should not get an error
    When I type the arguments "help do"
    Then I should not get an error
    When I type the arguments "do"
    Then I should get an error
    When I type the arguments "do 1"
    Then I should not get an error