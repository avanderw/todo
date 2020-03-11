# Changelog for project todo
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Entries are suggested using the [Changelog Generator](https://github.com/avanderw/changelog).

## [Unreleased]
All the changes have been set free, nothing to release here.

## Version 1.0.1-5e44193 (Maintenance release)
Released on 2020-03-11

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

## Version 1.0.0-368220f (Major release, Update recommended)
Released on 2019-09-30

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