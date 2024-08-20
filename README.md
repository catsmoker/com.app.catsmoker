# Catsmoker App

Catsmoker is a simple Android app that displays a web page using a `WebView` component. The web page shown is hosted at [catsmoker.github.io](https://catsmoker.github.io). It also serves as a utility aimed at unlocking higher FPS in games by spoofing your device as a different model for specific games.

For more information, visit the [GitHub repository](https://github.com/Xposed-Modules-Repo/com.app.catsmoker).

## Features

- Spoof device information for improved gaming performance in supported games.
- Compatible with a wide variety of popular gaming apps.
- Simple installation with no configuration needed.
- Displays a web page within the app.

## Table of Contents

- [Getting Started](#getting-started)
- [Usage](#usage)
- [Supported Games](#supported-games)
- [License](#license)
- [Contributing](#contributing)

## Getting Started

### Prerequisites

- Ensure that your Android device is rooted.
- Install the [Xposed](https://github.com/ElderDrivers/EdXposed) or [Lsposed](https://github.com/LSPosed/LSPosed) framework on your device.

### Installation

1. **Download the Module**
    - Download the `catsmoker` module from the releases section of the repository.

2. **Install the APK**
    - Open [Xposed](https://github.com/ElderDrivers/EdXposed) or [Lsposed](https://github.com/LSPosed/LSPosed) Manager.
    - Go to the `Modules` section.
    - Search for `catsmoker` and enable the module.
    - The supported games are automatically added to the scope.
    - Force stop the added game manually to take effect.

## Usage

Once you've installed and activated the Catsmoker module, it will automatically spoof your device information for supported gaming applications. There's no additional configuration required. Simply force stop and open the games you want to play, and enjoy the benefits of device spoofing.

You can untick the games you don't want to spoof in the module scope. Force stop and relaunch are required.

**Note:** Device spoofing may violate the terms of service for some games or apps.

## Supported Games

- **Activision Games**
  - `com.activision.callofduty.shooter`
  - `com.activision.callofduty.warzone`
  - `com.garena.game.codm`
  - `com.tencent.tmgp.kr.codm`
  - `com.vng.codmvn`
  - `com.tencent.tmgp.cod`
- **Tencent Games**
  - `com.tencent.ig`
  - `com.pubg.imobile`
  - `com.pubg.krmobile`
  - `com.rekoo.pubgm`
  - `com.vng.pubgmobile`
  - `com.tencent.tmgp.pubgmhd`
- **Garena Games**
  - `com.dts.freefiremax`
  - `com.dts.freefireth`
- **Epic Games**
  - `com.epicgames.fortnite`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any bugs, improvements, or new features.
