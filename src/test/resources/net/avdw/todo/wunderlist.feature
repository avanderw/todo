Feature: Wunderlist
  Scenario: Auto-create DB
    Given there is no Wunderlist DB
    When I take an action on Wunderlist
    Then the Wunderlist will be created
    Then there is no Wunderlist DB

  Scenario: Synchronise
  Scenario: Add
  Scenario: Remove