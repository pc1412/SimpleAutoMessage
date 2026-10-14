# SimpleAutoMessage

Minecraft Paper 26.3+용 간단한 자동 메시지 플러그인입니다.

* 버전: 0.1
* 제작자: pc1412 / OSCC
* 웹사이트: https://oscc.kr
* 대상 버전: Paper 26.3+
* Java: 25+

## 주요 기능

* 여러 개의 독립적인 메시지 목록 지원
* 순차 또는 랜덤 메시지 출력
* `&` 레거시 색상 및 스타일 코드 지원
* `&#RRGGBB` RGB 색상 지원
* `\n`을 이용한 여러 줄 메시지 지원
* JSON Adventure 메시지 지원
* `/`로 시작하는 메시지를 서버 콘솔 명령어로 실행
* 한국어(`ko_KR`) 및 영어(`en_US`) 언어 파일 지원
* 플러그인 실행 중 활성화/비활성화 및 설정 다시 불러오기 지원
* 관리 명령어는 OP 또는 `simpleautomessage.admin` 권한 필요

## 명령어

일반 사용자:

* `/simpleautomessage help` (`/sam help`, `/am help`)
* `/sam version`
* `/sam 버전`

OP / `simpleautomessage.admin`:

* `/sam reload`
* `/sam list`
* `/sam send <list> [index]`
* `/sam enable`
* `/sam disable`

## 빌드

```bash
mvn clean package
```

빌드가 완료되면 `target/` 디렉터리에 JAR 파일이 생성됩니다.

## 라이선스 / 참고 프로젝트

SimpleAutoMessage는 pc1412 / OSCC가 Minecraft Paper 26.3+ 환경에 맞게 다시 작성한 플러그인입니다.

이 프로젝트는 marceloOo의 MIT 라이선스 기반 AutoMessage 프로젝트를 참고했습니다.

자세한 내용은 `LICENSE` 및 `NOTICE` 파일을 확인하세요.
