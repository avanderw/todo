Feature: Properties

  Scenario: Create the properties file
    Given there is no properties file
    When the injector is configured
    Then the properties file is created