Feature: todo-txt
  Scenario: Add

  Scenario: Do

  Scenario: List
    Given the file "./src/test/resources/todo.txt/list.txt"
    When I list the todo items with no arguments
    Then I will get a list with 16 items

  Scenario: List with filter
    Given the file "./src/test/resources/todo.txt/list.txt"
    When I list the todo items with arguments "@play"
    Then I will get a list with 9 items
    When I list the todo items with arguments "@play typ"
    Then I will get a list with 1 items

  Scenario: Remove

  Scenario: Archive
