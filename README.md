# Overworld-Agent

Overworld-Agent is a Minecraft plugin to create and define agent behavior. 

_**Requires Java 11+**_

---

## Building
Build the source with Maven:
```
$ mvn install
```

---

## Configuration
The config file can be found under `/Overworld-Agent/src/main/resources/config.yml`.

### Skins
| Key | Type | Description |
|---|---|---|
|`skins.<skin name>`|`string`|Unique name for the skin can be made up by the researcher|
|`skins.<skin name>.signature`|`string`|The texture signature of the custom skin from https://mineskin.org|
|`skins.<skin name>.data`|`string`|The texture value of the custom skin from https://mineskin.org|


#### Example
```yaml
skins:
  <skin name>:
    signature: <texture signature of custom skin>
    data: <texture value of custom skin>
```

---

## Commands
| Command                                                                                | Description                                                                                                                          |
|----------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `/novicespawn <player name> <skin name> <agent name>`                                  | Spawn an agent with the specified skin and name that guides the specified student to a destination then prompts for an observation.  |
| `/expertspawn <player name> <skin name> <agent name>`                                  | Spawn an agent with the specified skin and name that follows the specified student and when right clicked prompts for an observation.|
