Feature: Wunderlist connect
  Background:
    Given an API Key "34be69e3313a17355d82"
    And an API Secret "869630c64d9bf7b065e48d8a059978a64e6ab12b4e13800764f3fa9b4c7e"
    And a Client Key "bcf5a937726fc583e183"

  Scenario: Connect
    When I retrieve the lists for the client
    Then there is at least one list retrieved
