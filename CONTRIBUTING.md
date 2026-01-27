# Contributing to KMPilot

Thanks for your interest in contributing.

## How to Contribute

### Reporting Bugs

Open an issue with:
- Clear description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, JDK version, Android Studio version)

### Suggesting Features

Open an issue describing:
- The problem you're trying to solve
- Your proposed solution
- Any alternatives you've considered

### Pull Requests

1. Fork the repository
2. Create a branch from `main`
3. Make your changes
4. Run `./gradlew build` to ensure everything compiles
5. Run `./gradlew ktlintFormat` to format code
6. Submit a PR with a clear description of changes

### Code Style

- Follow existing patterns in the codebase
- Use Ktlint for formatting (`./gradlew ktlintFormat`)
- Maintain Clean Architecture boundaries
- Keep feature modules isolated (no cross-feature dependencies)

### Agents & Skills

When modifying AI agents or skills in `.claude/`:
- Test changes with actual feature generation
- Update relevant documentation in the Wiki
- Ensure specs are generated correctly

## Questions?

Open an [issue](https://github.com/ThisIsSadeghi/KMPilot/issues) for questions or ideas.
