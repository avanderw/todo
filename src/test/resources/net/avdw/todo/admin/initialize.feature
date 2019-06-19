Feature: Initialize

  Scenario: Default
    When a default todo is initialized
    Then the target "." will contain the folder ".todo"

  Scenario: Specific
    When a the target "target/test/initialize" is initialized
    Then the target "target/test/initialize" will contain the folder ".todo"