Feature: File repository
  Scenario: Backup before update
    When a file is updated
    Then a backup file will be created