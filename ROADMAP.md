# Todo roadmap
Very basic goal setting

## ~~1.0 Functionality~~
- Addition
- Done
- Removal
- Priority
- Sorting

## ~~2.0 Configurable styling~~
- Regex rule based
- Hierarchical (overridable) configuration
- Property file driven

## ~~Lifecycle~~

- filter
- actions
- display

## ~~Category module~~
- Strategic: Creating future value
- Tactical: Refining current value
- Operational: Maintaining current value

## ~~MoSCoW module~~
- Must have: non-negotiable, mandatory
- Should have: important, not vital, significant value
- Could have: nice to have, small impact
- Won't have: not priority given time frame

## RAG status module
Rule based calculation of green, amber, red status flags

### Green
Is there anything we can learn from this project that would help other projects stay green?
Are there any risks that could move this project to Amber at the next review point and if so what can be done now to lessen those risks?

### Amber
What actions can be taken now that will move the project to Green at some point in the future.
Are there any risks that could move this project to Red at a future review-point?

### Red
Are the reasons for the Red status understood?
Are there lessons that can be learnt for other projects to avoid going Red?
Is there a recovery plan? If not: one must be constructed, with concrete actions assigned to accountable people to agreed deadlines. Future status reports will then report against a revised plan avoiding an ever-red project, which can be missed through the familiarity of it being reported as red on every successive progress reports.

## Phase module
Determine automation to trigger phase identification.

### Software phases
- Ripen, research, prototype
- Implement, do, action
- Test, verify
- Deploy, package
- Review, maintain

## Agile module
Functional area (component) breakdown into epics, features, enhancements, fixes mapped against time

## Priority module
Various variables can be used for further detail. 
The aim is to get a value score, which might reduce by effort.

### Scorecards
Essentially boils down to (value factors / cost factors) to get a score.

e.g. RICE Score: (Reach + Impact + Confidence) / Effort

#### Value factors
- Reach: 
How many clients will this feature affect
- Impact: 
How much will this impact clients
- Confidence:
How confident are we about the impact and reach scores? 
How much data do we have to back up those estimates?
- Urgency:
How much is the cost to delay? How frequent is the problem?

#### Cost factors
- Effort:
How much of a time investment will this initiative require

### Matrix
Plot value vs. effort into 4 quadrants.

- High value, low effort (Quick wins)
- Low value, low effort (Maybe later)
- High value , high effort (Big new features)
- Low value, low effort (Time sinks)

## Plugin, Addon, Mixin, Extension modules
- Plugin is a command
- Addon is a pre/post execution on a command
- Mixin is available
- Extension is available

## External modules
- Binding commands to external binaries
- Binding commands to external scripts
- Single source for configuration
- Must not use reflection as it is too slow
- Property file driven