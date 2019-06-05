Feature: Adding todo's

  Scenario: Add CLI
    When I type the arguments "help add"
    Then I should not get an error
    When I type the arguments "add"
    Then I should get an error
    When I type the arguments "add todo"
    Then I should not get an error

  Scenario: Add
    Given I copy the file "./src/test/resources/lists/populated-base.txt" to "./target/test-list.txt"
    And I track the list "test-list.txt"
    When I add a todo item
    And I list the todo items with no arguments
    Then I will get a list with 17 items
    And the last item will have a created date of now
    And the file "./target/list.txt.bak" exists