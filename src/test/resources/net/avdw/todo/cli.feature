Feature: Commandline

  Scenario: Tracking
    When I type the following arguments "track"
    Then I should get an error
    When I type the following arguments "track test-list"
    Then I should not get an error
