Releasing
=========

1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
2. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version).
3. `./gradlew clean install bintrayUpload`.
4. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version).
5. Update the `gradle.properties` to the next SNAPSHOT version.
6. `git commit -am "Prepare next development version."`
7. `git push && git push --tags`
8. Update the sample modules to point to the newly released version.

If step 3 fails, fix the problem, commit, and start again at step 3.
