uap-scala
=========

[![Codecov status](https://codecov.io/gh/ua-parser/uap-scala/branch/master/graph/badge.svg)](https://codecov.io/gh/ua-parser/uap-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.uaparser/uap-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.uaparser/uap-scala_2.11)

A Scala user-agent string parser based on [ua-parser/uap-core](https://github.com/ua-parser/uap-core). It extracts browser, OS and device information.

### Usage

To use this library in your own project, add the following dependency in `build.sbt`:

```
libraryDependencies += "org.uaparser" %% "uap-scala" % "0.16.0"
```

#### Note about these examples

Instantiating Parser.default also instantiates secondary classes and reads in YAML files. This is slow.
If performance is critical or you are handling user agents in real time, be sure not to do this on the
critical path for processing requests.

#### Retrieve data on a user-agent string

```scala
import org.uaparser.scala.Parser

val ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"
val client = Parser.default.parse(ua) // you can also use CachingParser
println(client) // Client(UserAgent(Mobile Safari,Some(5),Some(1),None),OS(iOS,Some(5),Some(1),Some(1),None),Device(iPhone))
```
#### Extract partial data from user-agent string

The time costs of parsing all the data may be high.
To reduce the costs, we can just parse partial data.

```scala
import org.uaparser.scala.Parser

val raw = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"
val parser = Parser.default

val os = parser.osParser.parse(raw)
println(os) // OS(iOS,Some(5),Some(1),Some(1),None)

val userAgent = parser.userAgentParser.parse(raw)
println(userAgent) // UserAgent(Mobile Safari,Some(5),Some(1),None)

val device = parser.deviceParser.parse(raw)
println(device) // Device(iPhone,Some(Apple),Some(iPhone))
```

### Development

The code for this repository can be checked out normally. It uses a [git submodule](https://git-scm.com/docs/git-submodule) to include the files needed from [uap-core](https://github.com/ua-parser/uap-core) so care must be taken to make sure the `core` directory is properly checked out and initialized.

Checking out the repo for the first time
```
git clone --recursive https://github.com/ua-parser/uap-scala.git
```
If uap-scala was checked out and core was not properly initialized, the following can be done

```
cd uap-scala
git submodule update --init --recursive
```

#### Build

To build and publish locally for the default Scala (currently 2.13.11):

```scala
sbt publishLocal
```

To cross-build for different Scala versions:

```scala
sbt +publishLocal
```

### Details about the implementation using `regexes.yaml` file

This project use the 'regexes.yaml' file from [ua-parser/uap-core](https://github.com/ua-parser/uap-core/) repository to
perform user-agent string parsing according to the [documented specification](https://github.com/ua-parser/uap-core/blob/master/docs/specification.md).
The file is included as a git submodule in the `core` directory.

Bellow, follows a summary of that same specification.

#### Summary

This implementation (and others) works by applying three independent ordered rule lists to the same input user‑agent 
string:

- User agent parser ('user_agent_parsers' definitions): provides the "browser" name and version.
- OS parser ('os_parsers' definitions): provides operating system name and version.
- Device parser ('device_parsers'): provides device family and optional brand and model.

Each list is evaluated top‑to‑bottom. The first matching regex wins, and parsing for that list stops immediately.

#### Data file format

At a high level, 'regexes.yaml' is a YAML map with top-level keys like:

- `user_agent_parsers:`
- `os_parsers:`
- `device_parsers:`

Each value is a YAML list. Each list item is a small map that always contains a regex and may contain `*_replacement` 
fields.

##### Examples:

User agent parser example:
```yaml
user_agent_parsers:
  - regex: '(Namoroka|Shiretoko|Minefield)/(\d+)\.(\d+)\.(\d+(?:pre|))'
    family_replacement: 'Firefox ($1)'
```

OS parser example:
```yaml
os_parsers:
  - regex: 'CFNetwork/.{0,100} Darwin/22\.([0-5])\.\d+'
    os_replacement: 'iOS'
    os_v1_replacement: '16'
    os_v2_replacement: '$1'
```

Device parser example:
```yaml
device_parsers:
  - regex: '; *(PEDI)_(PLUS)_(W) Build'
    device_replacement: 'Odys $1 $2 $3'
    brand_replacement: 'Odys'
    model_replacement: '$1 $2 $3'
```

#### Capturing groups and default field mapping

The spec’s core idea is to put capturing groups `(...)` in your regex to extract parts of the UA string. If you don't 
supply replacements, fields map by group order.

#### User agent default mapping

If a user agent rule matches and it provides no replacements:
- group 1: _family_
- group 2: _major_
- group 3: _minor_
- group 4: _patch_

#### OS default mapping

Similarly, OS rules map:

- group 1: _family_
- group 2: _major_
- group 3: _minor_
- group 4: _patch_
- group 5: _patchMinor_

#### Device default mapping

Devices are slightly different: if no replacements are given, the first match defines the device family and model, 
and brand/model may be undefined depending on the rule and implementation.

In case that no matching regex is found the value for family shall be "Other". Brand and model shall not be defined. 
Leading and tailing whitespaces shall be trimmed from the result.


### Maintainers

* Piotr Adamski ([@mcveat](https://twitter.com/mcveat)) (Author. Based on the java implementation by Steve Jiang [@sjiang](https://twitter.com/sjiang) and using agent data from BrowserScope)
* [Ahmed Sobhi](https://github.com/humanzz) ([@humanzz](https://twitter.com/humanzz))
* [Travis Brown](https://github.com/travisbrown) ([@travisbrown](https://twitter.com/travisbrown))
* [Nguyen Hong Phuc](https://github.com/phucnh) ([@phuc89](https://twitter.com/phuc89))
