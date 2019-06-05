Feature: Todo list event bus
  Scenario: List updated event
    When a "ListUpdatedEvent" is posted
    Then the storage file is updated