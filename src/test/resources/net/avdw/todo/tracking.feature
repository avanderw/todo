Feature: Tracking lists

  Scenario: Tracking CLI
    When I type the arguments "track"
    Then I should get an error
    When I type the arguments "track test-list"
    Then I should not get an error