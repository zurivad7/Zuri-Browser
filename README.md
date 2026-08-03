# Zuri Browser

A lightweight, fast, privacy-respecting Android web browser with a built-in VPN
client and a universal video downloader.

> **Distribution:** Zuri is built for sideloading / F-Droid / GitHub APK
> releases, not the Google Play Store. Some features (e.g. downloading from
> YouTube) violate Play policy, which is why apps in this category ship outside
> the Play Store.

## Features (planned)

| Area        | What it does                                                             | Status |
|-------------|--------------------------------------------------------------------------|--------|
| Browser     | GeckoView (Firefox engine), tracking protection, lean Compose UI         | **M1 — in progress** |
| Downloader  | NewPipeExtractor + network sniffer, remux everything to **MP4** via ffmpeg | Planned (M2) |
| VPN         | WireGuard client — import your own `.conf` (bring-your-own server)         | Planned (M3) |
| Polish      | Tabs, dark theme, settings, split-tunnel, signed release APKs             | Planned (M4) |

### Video downloads → always MP4
The downloader **never** hands the user a raw `.m3u8`/HLS playlist. Segments are
downloaded and **remuxed into a single MP4** (lossless when the source is
H.264/AAC, which is the common case). Default quality is *highest MP4-native*
(fast, no re-encode); forcing max resolution where only VP9/AV1 exists will
transcode and is opt-in.

## Architecture

Currently a single `:app` module; it will be split into `:browser`,
`:downloader`, `:vpn`, and `:core` modules as later milestones land.

```
com.zuri.browser
├── ZuriApplication      # creates the shared GeckoRuntime
├── MainActivity         # Compose host
├── browser/
│   ├── GeckoEngine      # process-wide GeckoRuntime + tracking protection
│   ├── BrowserViewModel # one GeckoSession -> observable BrowserState
│   └── UrlUtils         # address-bar text -> URL/search
└── ui/                  # Compose screen + theme
```

## Building

Requires JDK 17 and the Android SDK (platform 35, build-tools 35.0.0).

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

CI builds a debug APK on every push and uploads it as the `zuri-browser-debug`
artifact (see the Actions tab).

## License

GPL-3.0-or-later. GeckoView (via Mozilla) and the planned NewPipeExtractor /
WireGuard components make GPLv3 the natural fit and keep the app F-Droid-eligible.
See [`LICENSE`](LICENSE).

## Legal note

A video downloader is a general-purpose tool. Downloading content may be
restricted by a site's Terms of Service and by copyright law depending on the
content and how it's used. Respect the rights of content owners; use Zuri for
content you have the right to download (your own uploads, offline viewing where
permitted, public-domain / openly-licensed media, etc.).
