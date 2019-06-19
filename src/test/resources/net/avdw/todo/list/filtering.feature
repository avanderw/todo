Feature: Filtering

  Scenario: List tasks
    Given the task repository ""
    When I list the todo items
    Then the list will contain 6 items

  Scenario: List tasks with filter
    Given the task repository ""
    When I list the todo items with filter ""
    Then the list will contain 6 items

  Scenario: List contexts
    Given the task repository ""
    When I list the contexts
    Then the list will contain 6 items

  Scenario: List projects
    Given the task repository ""
    When I list the projects
    Then the list will contain 6 items