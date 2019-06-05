Feature: Replace todo's

  Scenario: Replace CLI
    When I type the arguments "help replace"
    Then I should not get an error
    When I type the arguments "help mv"
    Then I should not get an error
    When I type the arguments "mv"
    Then I should get an error
    When I type the arguments "mv 1"
    Then I should get an error
    When I type the arguments "mv 1 replacement"
    Then I should not get an error