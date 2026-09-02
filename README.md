# EA Connect (demo Android client)

A UI-only Kotlin / Jetpack Compose clone of EA Connect, built for the event-driven
Devin demo. Every screen is static demo data except one call: **Party Up → Send party
invite**, which hits the `eaconnect` vertical in `COG-GTM/event-driven-devin`
(`POST /api/eaconnect/party/invite`) and deterministically fails.

Screens: Friends (online/offline, status notes), Chat (inbox + conversation with quick
replies), Find Friends (search + add), Activity, and the Party Up invite flow.

## Build

```sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # any JDK 17
./gradlew assembleDebug                               # backend defaults to http://10.0.2.2:3000
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.ea.connect.demo/com.ea.connect.MainActivity
```

`10.0.2.2` is the emulator's alias for the host, so the default build talks to a backend
run locally with `PORT=3000 node app/server.js`.

| Gradle property | Env var | Default | Purpose |
| --- | --- | --- | --- |
| `eaconnectBaseUrl` | `EACONNECT_BASE_URL` | `http://10.0.2.2:3000` | Backend base URL |
| `eaconnectDemoToken` | `EACONNECT_DEMO_TOKEN` | *(empty)* | Presenter token — see below |
| `devinOrgId` | `DEVIN_ORG_ID` | *(empty)* | Devin org for the triggered session |

## Alerting is off unless you opt in

The invite always fails, but the backend raises a Slack alert and a Devin session **only**
when the request carries `X-EAConnect-Demo-Token`, which this app sends only when built
with `-PeaconnectDemoToken=<token>`. A default build — including Android Studio's Run
button — pages nobody. Do not add the token to `gradle.properties`.

Presenter build:

```sh
./gradlew assembleDebug -PeaconnectBaseUrl=<deployed-url> -PeaconnectDemoToken=<token>
```
