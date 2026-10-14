# SimpleAutoMessage

Simple automatic message plugin for Minecraft Paper 26.3+.

- Version: 0.1
- Author: pc1412 / OSCC
- Website: https://oscc.kr
- Target: Paper 26.3+
- Java: 25+

## Features

- Multiple independent message lists
- Ordered or random rotation
- `&` legacy color/style codes
- `&#RRGGBB` RGB colors
- `\n` multi-line messages
- JSON Adventure messages
- Console commands from messages starting with `/`
- Korean (`ko_KR`) and English (`en_US`) language files
- Runtime enable/disable and reload
- OP-only administration commands

## Commands

Public:
- `/simpleautomessage help` (`/sam help`, `/am help`)
- `/sam version`
- `/sam 버전`

OP / `simpleautomessage.admin`:
- `/sam reload`
- `/sam list`
- `/sam send <list> [index]`
- `/sam enable`
- `/sam disable`

## Build

```bash
mvn clean package
```

The output JAR is created in `target/`.

## License / reference

SimpleAutoMessage is a 26.3+ rewrite by pc1412 / OSCC. It references the MIT-licensed AutoMessage project by marceloOo. See `LICENSE` and `NOTICE`.
