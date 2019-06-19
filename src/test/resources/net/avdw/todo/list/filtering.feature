Feature: Filtering

When I type the arguments "help list"
Then I should not get an error
When I type the arguments "help ls"
Then I should not get an error
When I type the arguments "ls"
Then I should not get an error
When I type the arguments "ls todo"
Then I should not get an error
When I type the arguments "ls -c"
Then I should not get an error
When I type the arguments "ls -p"
Then I should not get an error
When I type the arguments "ls -cp"
Then I should not get an error

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