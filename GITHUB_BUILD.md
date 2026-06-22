# Build APK Online with GitHub

You can build Fahin TV without Android Studio by using GitHub Actions.

## Steps

1. Go to [github.com](https://github.com) and create a new repository.
2. Upload all files from this folder:

   `C:\Users\Tuhin\Documents\Codex\2026-06-22\am\outputs\FahinTV`

3. Open the repository on GitHub.
4. Go to the `Actions` tab.
5. Select `Build Fahin TV APK`.
6. Press `Run workflow`.
7. Wait until the build finishes.
8. Open the finished workflow run and download `Fahin-TV-debug-apk`.

The APK file will be inside that downloaded artifact.

## Important

The first build can take several minutes because GitHub downloads Android and Gradle dependencies.
