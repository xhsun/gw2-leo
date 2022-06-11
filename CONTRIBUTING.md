# Contributing

First off, thanks for taking the time to contribute!

The following is a set of guidelines for contributing to this project. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

## Issues

### Create a new issue

If you spot a problem or want to suggest an idea for a new feature, [search if an issue already exists](https://docs.github.com/en/search-github/searching-on-github/searching-issues-and-pull-requests#search-by-the-title-body-or-comments). If a related issue doesn't exist, you can open a new issue using a relevant [issue form](https://github.com/xhsun/gw2-leo/issues/new/choose).

### Solve an issue

Scan through [existing issues](https://github.com/xhsun/gw2-leo/issues) to find one that interests you. You can narrow down the search using labels as filters. As a general rule, I don’t assign issues to anyone. If you find an issue to work on, you are welcome to open a PR with a fix.

## Pull Requests

When you're finished with the changes, create a pull request, also known as a PR.

- Please follow all instructions in the [PR template](https://github.com/xhsun/gw2-leo/blob/main/.github/pull_request_template.md)
- Make sure your code follows the project [code style](#code-style)
- After you submit your PR, verify that all status checks are passing

    *Note: If a status check is failing, and you believe that the failure is unrelated to your change, please leave a comment on the pull request explaining why you believe the failure is unrelated so that I can open an issue to track it.*
- I may ask for changes to be made before a PR can be merged, either using [suggested changes](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/reviewing-changes-in-pull-requests/incorporating-feedback-in-your-pull-request) or pull request comments
- As you update your PR and apply changes, mark each conversation as resolved

    *Note: If you run into any merge issues, checkout this [git tutorial](https://github.com/skills/resolve-merge-conflicts) to help you resolve merge conflicts and other issues.*

## Code Style

This project follows the [Android coding standards for Kotlin](https://developer.android.com/kotlin/style-guide), with a few additions:

- Make sure to follow [SOLID principals](https://en.wikipedia.org/wiki/SOLID) and [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself)
- Interface name must be prefixed with `I` (e.g., `IStorageRefreshService`)
- Class names must have their class type as a suffix. For example, account refresh service's class name should look like `AccountRefreshService`
- A Repository class should be created as a facade for persistence that uses Collection style semantics to supply access to data/objects
    - Repositories should only contain simple CRUD methods
    - See [this documentation](https://docs.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/infrastructure-persistence-layer-design) to read more about repository
- A service class should be created to host your business logics
    - Services should not know how to access data from persistence
- `account` package contains all classes falls under the account domain
- `storage` package contains all classes falls under the storage domain
- `core` package contains cross domain functionalities and utilities
- `registry` package contains DI
