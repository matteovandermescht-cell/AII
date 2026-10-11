# Roblox Multi-Join (Android)

<p align="center">
  <a href="https://github.com/matteovandermescht-cell/AII/actions/workflows/android.yml">
    <img alt="Build and download APK" src="https://img.shields.io/badge/Download-Android%20APK-2ea44f?style=for-the-badge&logo=android&logoColor=white">
  </a>
</p>

**[⬇️ APK herunterladen / Build öffnen](https://github.com/matteovandermescht-cell/AII/actions/workflows/android.yml)**

Open the latest successful workflow run, scroll to **Artifacts**, and tap **Roblox-Multi-Join-APK** to download the APK. GitHub may require you to sign in to download workflow artifacts.

A small Android control-center prototype for storing up to four account labels and opening a Roblox experience link.

## Current scope
- Four local account labels (usernames only; no passwords or session tokens).
- Enter a Roblox experience URL or place link and open it through Android.
- Clear status labels: Not configured / Link opened / Unknown. The app does **not** claim to verify a live in-game session unless a reliable supported signal exists.
- No automated gameplay, credential collection, or bypassing Roblox security controls.

## Important limitation
One Android phone is not guaranteed to run two separate Roblox accounts in the same experience simultaneously. This prototype opens the chosen experience link using Android's supported app-link handling; it does not create isolated Roblox sessions.

## Build
The GitHub Actions workflow builds a debug APK on pushes to `main` and can also be run manually. Download the artifact from the workflow run.

## Privacy
Account labels and the entered link are stored locally in app preferences. Do not enter passwords or authentication cookies.
