Feature: Filtering

  Scenario: List tasks
    Given the task repository "src/test/resources/lists/filtering"
    When I list the todo items
    Then the list will contain 16 items

  Scenario: List tasks with filter
    Given the task repository "src/test/resources/lists/filtering"
    When I list the todo items with filter "allow @home"
    Then the list will contain 1 items

  Scenario: List contexts
    Given the task repository "src/test/resources/lists/filtering"
    When I list the contexts
    Then the list will be [play,work,home]

  Scenario: List projects
    Given the task repository "src/test/resources/lists/filtering"
    When I list the projects
    Then the list will be [HassleEUC,todo.txt]