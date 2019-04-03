Feature: todo-txt
  Background:
    Given the file "./target/list.txt" does not exist

  Scenario: Add
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I add a todo item
    And I list the todo items with no arguments
    Then I will get a list with 17 items
    And the last item will have a created date of now

  Scenario: Do

  Scenario: List
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list the todo items with no arguments
    Then I will get a list with 16 items

  Scenario: List with filter
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list the todo items with arguments "@play"
    Then I will get a list with 9 items
    When I list the todo items with arguments "@play typ"
    Then I will get a list with 1 items

  Scenario: Remove

  Scenario: Archive
