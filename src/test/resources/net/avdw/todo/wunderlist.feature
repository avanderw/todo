Feature: Wunderlist
  Scenario: Auto-create DB
    Given there is no Wunderlist
    When I take an action on Wunderlist
    Then the Wunderlist will be created
    Then there is no Wunderlist

  Scenario: Synchronise
    Given there is a Wunderlist DB which is out of sync
    When I synchronise
    Then the local DB will contain the Wunderlist DB

  Scenario: Add
    Given there is a Wunderlist DB
    When I add a task
    Then the task will be on Wunderlist

  Scenario: Remove
    Given there is a Wunderlist DB
    When I remove a task
    Then the task will not be on Wunderlist

  Scenario: Initialise
    Given there is no Wunderlist
    When I initialise the Wunderlist
    Then there will be a properties file in the user home
    And the properties file will contain a list ID
    And the Wunderlist with the list ID will exist