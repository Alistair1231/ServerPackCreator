# Contributing

## Important info regarding pull requests and GitHub!

I currently do not accept contributions due to the fact that I moved my repositories over to my own GitLab instance.
The GitHub repository is a mirror from my GitLab server to GitHub. Meaning, any changes I make, I push to my GitLab repository,
which then mirrors these changes to GitHub. If I were to accept a PR on GitHub, my GitLab could then no longer mirror from itself
to GitHub, as the destination repository has changes which the source doesn't have.

If you want to contribute anyway, talk to me on my Discord server at https://discord.griefed.de. If you manage to convince
me that your contribution is valuable, that you can be trusted and that you are generally a nice person to deal with, I
*may* give you limited access to an external account to my GitLab server, which would *then* allow you to fork ServerPackCreator
on my GitLab server, where you can *then* push your changes to, create merge request and get your changes into
ServerPackCreator. I don't see that happening though. It is tough to convince a total stranger that you are completely
trustworthy. It's just the nature of the internet.

# Code

- **The Main-class:** The Main-class of ServerPackCreator only passes the arguments from execution to the Handler-class.

- **Config file:** If you want to contribute to SPC, please make sure you do not change the `serverpackcreator.conf`-file. Ideally, any version of SPC will work with any config file, as they all have the same content. Changing what's inside the `serverpackcreator.conf`-file would make versions incompatible to each other, but I want users to be able to simply download the newest version **without** having to migrate their config file or even worrying about such a thing.
Therefore, I ask that you do not touch the `serverpackcreator.conf`-file.
  
- **Variable names:** Please keep variable names verbose i.e. `thisStoresSomething` or `checkForStuff` or some such. Variables like `a` and `tmpA` make code harder to read. We're not aiming for best performance or whatever, so we can have longer variable names if we want to.

- **Access modifiers:** Because the files generated by this program are supposed to be distributed to multiple people, I try to keep methods and classes as closed-off as possible. It's not much, but it's something. Therefore, unless your method or class *absolutely* must be public, keep it package private or make it private completely. I reserve the right to refactor any such classes or methods.  

- **Translating:** If you wish to contribute to translating ServerPackCreator, have a look at the [resource bundles](https://github.com/Griefed/ServerPackCreator/tree/main/src/main/resources/de/griefed/resources/lang). Should you want to **add** a language to ServerPackCreator, add your locale in [LocalizationManager](https://github.com/Griefed/ServerPackCreator/blob/main/src/main/java/de/griefed/serverpackcreator/i18n/LocalizationManager.java), copy the [lang_en_us.properties](https://github.com/Griefed/ServerPackCreator/blob/main/src/main/resources/de/griefed/resources/lang/lang_en_us.properties) to lang_your_locale.properties and start translating!  

# Commits

If you want to contribute to SPC, please make sure your commits follow the conventional commit layout and use the types/categories/flags described here: [actions/auto-changelog](https://github.com/marketplace/actions/auto-changelog)

For completeness' sake:

```
type(category): description [flag]
```

The `type` must be one of the followings:

* `breaking` (Breaking Changes)
* `build` (Build System / Dependencies)
* `ci` (Continuous Integration)
* `chore` (Chores)
* `docs` (Documentation Changes)
* `feat` (New Features)
* `fix` (Bug Fixes)
* `other` (Other Changes)
* `perf` (Performance Improvements)
* `refactor` (Refactors)
* `revert` (Reverts)
* `style` (Code Style Changes)
* `test` (Tests)

> If the `type` is not found in the list, it'll be considered as `other`.

The `category` is optional and can be anything of your choice.

The `flag` is optional (if provided, it must be surrounded in square brackets) and can be one of the followings:

* `ignore` (Omits the commit from the changelog)

> If `flag` is not found in the list, it'll be ignored.