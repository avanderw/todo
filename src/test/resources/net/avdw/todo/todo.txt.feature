Feature: todo-txt
  Background:
    Given the file "./target/list.txt" does not exist
    And the file "./target/list.txt.bak" does not exist
    And the file "./target/done.txt" does not exist

  Scenario: Add
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I add a todo item
    And I list the todo items with no arguments
    Then I will get a list with 17 items
    And the last item will have a created date of now
    And the file "./target/list.txt.bak" exists

  Scenario: Do
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I complete todo item 3
    And I list the todo items with no arguments
    Then I will get a list with 15 items
    And item 3 will be "[03] 2018-12-14 get +todo.txt summary out of github onto phone @play"
    And the file "./target/list.txt.bak" exists
    And the contents of file "./target/done.txt" starts with "x " together with today's date

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
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I remove item 2
    And I list the todo items with no arguments
    Then I will get a list with 15 items
    And item 2 will be "[02] 2018-12-04 Allow multi-desktop support for taskbar on Windows10 +HassleEUC @work"
    And the file "./target/list.txt.bak" exists

  Scenario: Archive
