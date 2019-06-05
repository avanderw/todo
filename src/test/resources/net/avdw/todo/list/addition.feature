Feature: Addition

  Scenario: Command line interface
    When I type the arguments "help add"
    Then I should not get an error
    When I type the arguments "add"
    Then I should get an error
    When I type the arguments "add todo"
    Then I should not get an error

  Scenario: Task addition
    When a task is added
    Then the list added to will contain an additional task

  Scenario: Generate creation date
    When a task is added
    Then the added task's creation date will be today