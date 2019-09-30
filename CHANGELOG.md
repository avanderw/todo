# Changelog for project todo
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Entries are suggested using the [Changelog Generator](https://github.com/avanderw/changelog).

## [Unreleased]
All the changes have been set free, nothing to release here.

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