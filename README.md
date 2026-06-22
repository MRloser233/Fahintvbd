# Fahin TV

Fahin TV is an Android sports streaming app project built around the provided `Tv channel.m3u` playlist.

## Included

- M3U playlist support with the supplied playlist bundled in `app/src/main/assets/fahin_tv_channels.m3u`
- Sports home layout: Live Now, World Cup, Football, Cricket, Bangladesh Channels, USA, International Sports
- Favorites, recently watched, continue watching, instant search
- ExoPlayer / Media3 live streaming player
- 30-second smart buffer profile, low startup buffering, automatic reconnect
- 10s and 30s seek controls, playback speed, screen lock, Picture-in-Picture
- Android TV launcher entry and responsive native UI

## Build APK

### Online build without Android Studio

Use GitHub Actions. See `GITHUB_BUILD.md`.

After uploading this project to GitHub, open `Actions > Build Fahin TV APK > Run workflow`, then download the `Fahin-TV-debug-apk` artifact.

### Android Studio build

Open this folder in Android Studio:

`C:\Users\Tuhin\Documents\Codex\2026-06-22\am\outputs\FahinTV`

Then choose:

`Build > Build Bundle(s) / APK(s) > Build APK(s)`

The APK will appear under:

`app/build/outputs/apk/debug/app-debug.apk`

## Notes

This first version runs fully from the local playlist. Cloud sync, secure accounts, remote EPG, voice search, reminders, and backend playlist auto-update are structured as next-phase integrations because they need server/API credentials.
