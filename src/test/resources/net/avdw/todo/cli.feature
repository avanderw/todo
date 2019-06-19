Feature: Command line interface

  Scenario: Initialize
    When I type the arguments "help init"
    Then I should not get an error
    When I type the arguments "init"
    Then I should not get an error
    When I type the arguments "init target/test/initialize"
    Then I should not get an error
