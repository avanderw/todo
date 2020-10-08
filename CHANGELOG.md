# Changelog for project todo
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Entries are suggested using the [Changelog Generator](https://github.com/avanderw/changelog).

## [v2.0.1] (Maintenance release)
*Released on 2020-10-08*

### Fixed
- Fix resolving global directory when the local directory does not exist

## [v2.0.0] (Major release)
*Released on 2020-10-07*

### Added
- Add inclusion of done items for stats tracking
- Add 'todo stats' to display lead, cycle and reaction time
- Add 'todo changelog' command
- Add tests for functional code
- Add maven model dependency to parse pom.xml for version
- Add version provider to picocli to provide pom version
- Add a new styling option which is configured with a property file
- Add 'todo archive' command
- Add 'todo ls --done' command
- Add 'todo ls --parked' command
- Add 'todo ls --removed' command
- Add 'todo park' command
- Add todo top x command
- Add todo ls --greater-than functionality
- Add todo ls --clean option to remove meta data
- Add bar chart functionality
- Add edit function
- Add removed item to removed.txt file
- Add sort by meta key list
- Add not, and, or to ls
- Add basic commandline usage

### Changed
- Update todo styling to a production setting
- Refactor code to pass quality rules
- Refactor common testing tasks
- Update versions for plugins and dependencies
- Update chart to handle context and projects
- Update --greater-than to handle complete dates
- Update rm to default to removed list with no arguments
- Update park to show parked items without parameters
- Refactor default action to list todo items
- Enhance remove to handle multiple indexs
- Refactor templates
- Refactor backup
- Refactor syling for various commands
- Refactor styling of status to completely use the theme
- Refactor renderers
- Refactor renderers
- Refactor progress bar to use theme
- Refactor TodoItem
- Refactor styling on ls
- Change empty 'pri' to display priority items
- Refactor for code rules
- Enhance status to show context breakdown
- Update status to show progress bars per path
- Refactor to remove tracing module, profiling module suffices
- Refactor logging to have a release mode
- Refactor Console to use Logger
- Refactor rename to replace
- Refactor TodoItem creation to use Guice
- Enhance context / project list to show completion
- Refactor 'todo ls' command
- Enhance sort to list todo's after sorting

### Deprecated
- Deprecate TodoList in favour of ListCli
- Deprecate TodoEdit in favour of EditCli
- Deprecate TodoSort in favour of SortCli
- Deprecate TodoExplore in favour of ExploreCli
- Deprecate TodoBackup in favour of BackupCli
- Deprecate functionality not to be used in v2.0.0
- Deprecate TodoPriority for PriorityCli
- Deprecate TodoArchive for ArchiveCli
- Deprecate TodoDone in favour of DoneCli
- Deprecate TodoRemove in favour of RemoveCli
- Deprecate TodoPark in favour of ParkCli
- Deprecate TodoInit in favour of InitCli
- Deprecate older java versions and TodoAdd in favour of AddCli
- Deprecate old theme classes to hint at using new style classes

### Removed
- Remove checkstyle plugin until a fix is found to add it again
- Remove cli options

### Fixed
- Fix styling of priority addition date bug
- Fix string escaping bug when displaying todos
- Fix bundle reference
- Fix priority to not escape strings
- Fix ls to work with lowercase
- Fix conflict with findbugs conflict
- Fix 'todo repeat' functionality
- Fix inconsistency between 'ls' and 'add'
- Fix inconsistency between 'ls' and 'pri'
- Fix inconsistency between 'ls' and 'rm'
- Fix inconsistencies between 'todo ls' and 'todo do'
- Fix remove functionality not working with LF vs CRLF

## [1.0.1] (Maintenance release)
*Released on 2020-03-11*

### Changed
- Refactor templates
- Refactor backup
- Refactor styling for various commands
- Refactor styling of status to completely use the theme
- Refactor renderers
- Refactor progress bar to use theme
- Refactor TodoItem
- Refactor styling on ls
- Change empty 'pri' to display priority items
- Refactor for code rules
- Enhance status to show context breakdown
- Update status to show progress bars per path
- Refactor to remove tracing module, profiling module suffices
- Refactor logging to have a release mode
- Refactor Console to use Logger
- Refactor rename to replace
- Refactor TodoItem creation to use Guice
- Enhance context / project list to show completion
- Refactor 'todo ls' command
- Enhance sort to list todo's after sorting

### Bug fixes
- Fix 'todo repeat' functionality
- Fix inconsistency between 'ls' and 'add'
- Fix inconsistency between 'ls' and 'pri'
- Fix inconsistency between 'ls' and 'rm'
- Fix inconsistencies between 'todo ls' and 'todo do'
- Fix remove functionality not working with LF vs CRLF

## [1.0.0] (Major release)
*Released on 2019-09-30*

### Added
- Add 'todo pri --shift-[up|down]' feature
- Add 'todo pri --optmize' feature
- Add editor property config
- Add properties to manage log level
- Add 'todo repeat' command

### Changed
- Enhance todo highlighting to make due dates more informative
- Refactor todo with priority
- Enhance list to contain counts for context and project
- Update 'todo start' to set a priority if none is present
- Update empty priority assignment to choose next avialable priority
- Update README.md

### Bug fixes
- Fix auto-date property not being located correctly
- Fix a bug when viewing the status of an invalid known path