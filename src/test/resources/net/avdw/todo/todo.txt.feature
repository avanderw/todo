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

  Scenario: Done
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I complete todo item 3
    And I list the todo items with no arguments
    Then I will get a list with 15 items
    And item 4 will be "[03] 2018-12-14 get +todo.txt summary out of github onto phone @play"
    And the file "./target/list.txt.bak" exists
    And the contents of file "./target/done.txt" starts with "x " together with today's date

  Scenario: Multi-done
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I complete todo item 3 4 5 6
    And I list the todo items with no arguments
    Then I will get a list with 12 items
    And item 5 will be "[03] 2019-01-04 get new sandles @home"
    And the file "./target/list.txt.bak" exists
    And the contents of file "./target/done.txt" starts with "x " together with today's date
    And the contents of file "./target/done.txt" contains 4 lines

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

  Scenario: List priorities
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list the priority tasks
    Then I will get a list with 1 items

  Scenario: List contexts
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list the contexts
    Then I will get a list with 3 items

  Scenario: List projects
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list the projects
    Then I will get a list with 2 items

  Scenario: List all
    Given I track the file "./src/test/resources/todo.txt/list.txt"
    When I list everything
    Then I will get a list with 50 items

  Scenario: Remove
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I remove item 2
    And I list the todo items with no arguments
    Then I will get a list with 15 items
    And item 3 will be "[02] 2018-12-04 Allow multi-desktop support for taskbar on Windows10 +HassleEUC @work"
    And the file "./target/list.txt.bak" exists

  Scenario: Multi-remove
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I remove item 3 4 5 6
    And I list the todo items with no arguments
    Then I will get a list with 12 items
    And item 5 will be "[03] 2019-01-04 get new sandles @home"
    And the file "./target/list.txt.bak" exists

  Scenario: Replace
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I replace item 3 with "Cucumber replace test"
    And I list the todo items with no arguments
    Then I will get a list with 16 items
    And item 4 will be "[03] 2018-12-14 get +todo.txt summary out of github onto phone @play"
    And item 16 will contain "Cucumber replace test"

  Scenario: Add priority
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I add priority "A" to item 5
    And I list the todo items with no arguments
    Then I will get a list with 16 items
    And item 1 will be "(A) 2018-12-28 put together a statement categorisor for bank statements @play"

  Scenario: Remove priority
    Given I copy the file "./src/test/resources/todo.txt/list.txt" to "./target/list.txt"
    And I track the file "./target/list.txt"
    When I remove priority from item 9
    And I list the todo items with no arguments
    Then I will get a list with 16 items
    And item 1 will be "[01] 2018-12-04 24h00 time format +HassleEUC @work"
