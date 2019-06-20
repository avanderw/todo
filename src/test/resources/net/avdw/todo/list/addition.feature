Feature: Addition

  Scenario: Task addition
    When a task is added
    Then the list added to will contain an additional task

  Scenario: Generate creation date
    When a task is added
    Then the added task's creation date will be today