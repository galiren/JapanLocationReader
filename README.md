# JapanLocationReader

Convert [国土交通省国土地理院地名集日本](https://www.gsi.go.jp/kihonjohochousa/gazetteer.html) file into
struct yaml file. It's a side project of a Japanese location name learning application.

For more information, see my blog post: [『日本地名勉強アプリ作り』](https://blog.galiren.me/japan-location-name-app) (日本語) (WIP).

## Build

- JDK version `21`
### UNIX
```shell
./gradlew build
```

### Windows

```shell
.\gradlew.bat build
```

The output jar file will be found in `build/libs`.

## Run

Should have JRE `21` or newer installed.
## Usage

Refer `java -jar <jar file> --help`.
