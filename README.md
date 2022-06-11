# Leo: Guild Wars 2 Storage Price Checker

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xhsun_gw2-leo&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xhsun_gw2-leo)
![main workflow](https://github.com/xhsun/gw2-leo/actions/workflows/main.yml/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xhsun_gw2-leo&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xhsun_gw2-leo)
![draft release workflow](https://github.com/xhsun/gw2-leo/actions/workflows/release.yml/badge.svg)

This is a companion Android application for Guild wars 2 to make the life of an altoholic slightly easier by showing them what they have across all their characters, bank, and material storage. So that they can easily find items amongst all their alts without needing to log into each and individual alts to check. This application also have builtin integration with Guild Wars 2 trading post API to allow people to check if they have any valuables hidden in their alts' inventories at ease.

| Login | Bank Storage | Item Details | Character Inventory |
| --- | --- | --- | --- |
| ![Login Screen](https://i.imgur.com/Ep4NTNV.png) | ![Bank Screen](https://i.imgur.com/5l6eHZg.png) | ![Detail Screen](https://i.imgur.com/ic3IS5P.png) | ![Inventory Screen](https://i.imgur.com/7Q0N2fP.png) |

## Prerequisites

The minimum Android version required to install and run this application is `Android Pie` (Android SDK 28). If you are not sure which version of Android you are using, please follow [check & update your Android version guide](https://support.google.com/android/answer/7680439) to find out.

You will also need to have a Guild Wars 2 API key with `account, inventories, and characters` permission granted for this application to use to access your account information. Visit [Guild Wars 2 API key wiki](https://wiki.guildwars2.com/wiki/API:API_key) to see a more detailed guide on creating an API key.

## Installation

1. Download APK for the latest version from https://github.com/xhsun/gw2-leo/releases
1. Navigate to your Download folder using a file browser app or simply begin the install by clicking on the completed download in your mobile browser
1. Android will ask you to grant permission to either the file browser or your web browser to install the app. Grant the permission and it should bounce you back to the installation screen. If not, navigate back to your Download folder after granting the permission to try again

*Note: See [user opt-in for unknown apps and sources](https://developer.android.com/studio/publish#publishing-unknown) for more details on installing unknown app.*

Each release will contain:

- `gw2-leo-<VERSION>.apk`: Android installation file
- `SHA256SUMS`: Checksum for the Android installation file
- `LICENSE`: Application's license

*Note: To verify installation file integrity, run `sha256sum -c SHA256SUMS 2>&1 | grep OK`*

## Usage

Before you are able to view your storage information, you must first supply a Guild Wars 2 API key with appropriate permission granted.

*Note: Current version of this app does not allow you to change your API key nor to change your logged in account. If you wish to change, feel free to just clear the application's data storage to reset the log in process.*

### View Different Storages

The bottom navigation allows you to switch between different storages. It also allows you to sort and filter the items.

![Bottom Navigation](https://i.imgur.com/L1TljCZ.png)

1. Display items in account bank
2. Display items in account material storage
3. Display items in each character's inventory
4. Display unit listing price (SELL) or unit purchasing price (BUY) of all items
5. Toggle to toggle between showing all items or showing only items that can be sold
6. Change sorting direction

#### Switching between character inventories

<img src="https://i.imgur.com/tcNd2ga.png" alt="Character Selector" height="200"/>

On character inventory page, you can switch between each character's inventory by swiping left or right. Or you can view a specific character's inventory by tapping on their name

### View Item Details

To view details of an item, simple tap on the item's icon. A dialog will pop up displaying the basic item information and the item's trading post information.

<img src="https://i.imgur.com/6fpz7TY.png" alt="Item Details" height="300"/>

- `Buy Price` Unit purchasing price of this item on the trading post 
- `Total Buy Price` Total coins you will get if you sold this immediately on the trading post
- `Sell Price` Unit sell price of this item on the trading post
- `Total Sell Price` Total coins you might get if you list this on the trading post

### Finer Details

Following is some interesting tidbits about this application

### Crossed out pricing

Sometimes, you will see an item with crossed out price:

<img src="https://i.imgur.com/PjIABlb.png" alt="Bounded Items" height="90"/>

What it means is that the items can be purchased or sold on the trading post. However, this particular one is bounded to your account or to a character, thus cannot be traded.

### Data refresh

This application caches your information to your Android device local storage to reduce data consumption and speed up loading time. As a side effect, data displayed might not be the most up to date information.

*Note: If you want to force a data refresh for the current page you are on, you can do so by dragging list down to trigger a refresh.*

### Automatic data refresh

This application does have the ability to update its local cache to match the latest data in the background every 2 hours. However, this process only happens when:

- You are logged in
- Android device is on wifi
- Battery is not low
- Internal storage is not low
- Android device is not currently been used

The application will also trigger a refresh the next time you open the application if an hour has passed since the application last refreshed its own data cache.

*Note: If you noticed what is shown is out dated, you can manually trigger a refresh by dragging current page down. This manual refresh process will not have the limitations the automatic ones have.*

## Notice Any Issues?

If you noticed any issues with this application, please don't hesitate to let me know by opening an issue! But before you do that, pease take a look at [contributing guideline](https://github.com/xhsun/gw2-leo/blob/main/CONTRIBUTING.md#issues).
