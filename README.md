# KeyAll Reloaded  
## The Ultimate KeyAll Plugin  
### By VELORA DEV  

---

## Key Features
- **Unlimited KeyAll commands** – No restrictions!  
- **Random timers** – Fully customizable for unique timings.  
- **Title messages** – Send KeyAll messages via titles.  
- **ActionBar messages** – Send KeyAll messages via the action bar.  
- **Sounds** – Enhance KeyAll rewarding with sound effects.  
- **PlaceholderAPI support** – Use placeholders like `%keyall_timer%`.  
- **Hex color support** – *Coming Soon!*  
- **Legacy color codes** – Fully supported for compatibility.  

---

## Optimized for Performance
- **KeyAllReloaded** is designed for **high-performance servers**.  
- Features **precise timing mechanics** to minimize resource usage and deliver **smooth performance**.  

---

## Developer Friendly
- **Clean, modular code** ensures easy integration into your setup.  
- Highly **flexible configurations** allow adaptation to any server environment.  

---

## Soft Dependencies
KeyAllReloaded supports PlaceholderAPI for placeholders like `%keyall_timer%`. Ensure PlaceholderAPI is installed for this feature.  

---

## Installation Guide
1. Download the plugin JAR file from the official source.  
2. Drag and drop the file into your server's `plugins` folder.  
3. If you plan to use `%keyall_timer%`, download and install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).  
4. Restart your server to generate the default configuration files.  
5. Customize the timer, commands, and messages in the `config.yml` file to suit your needs.  
6. Save the file and reload the plugin using `/keyall reload`.  

---

## Default Configuration Example
```yaml
# Enable debugging for testing purposes
debug: false

timer:
  duration: 60 # Timer duration in seconds
  commands:
    - "say Hello, %player%!" # Command executed when the timer ends

random-timer:
  enabled: false
  min: 1000 # Minimum delay in seconds
  max: 1000 # Maximum delay in seconds

features:
  message: "&aYou can edit this message in the KeyAll config.yml"

  actionbar:
    enabled: true
    message: ""

  title:
    enabled: true
    title: ""
    subtitle: ""
    fade-in: 2
    stay: 4
    fade-out: 2

sound:
  enabled: true
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  volume: 1.0
  pitch: 1.0
```
