uap-scala
=========

[![Codecov status](https://codecov.io/gh/ua-parser/uap-scala/branch/master/graph/badge.svg)](https://codecov.io/gh/ua-parser/uap-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.uaparser/uap-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.uaparser/uap-scala_2.11)

A Scala user-agent string parser based on [ua-parser/uap-core](https://github.com/ua-parser/uap-core). It extracts browser, OS and device information.

### Checkout
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

### Build

To build and publish locally for the default Scala (currently 2.13.11):

```scala
sbt publishLocal
```

To cross-build for different Scala versions:

```scala
sbt +publishLocal
```

### Linking
Linking

You can link against this library in your program at the following coordinates:

Scala 2.10
```
groupId: org.uaparser
artifactId: uap-scala_2.10
version: 0.11.0
```

Scala 2.11
```
groupId: org.uaparser
artifactId: uap-scala_2.11
version: 0.11.0
```

Scala 2.12
```
groupId: org.uaparser
artifactId: uap-scala_2.12
version: 0.11.0
```

Scala 2.13
```
groupId: org.uaparser
artifactId: uap-scala_2.13
version: 0.11.0
```

### Usage

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
### Maintainers

* Piotr Adamski ([@mcveat](https://twitter.com/mcveat)) (Author. Based on the java implementation by Steve Jiang [@sjiang](https://twitter.com/sjiang) and using agent data from BrowserScope)
* [Ahmed Sobhi](https://github.com/humanzz) ([@humanzz](https://twitter.com/humanzz))
* [Travis Brown](https://github.com/travisbrown) ([@travisbrown](https://twitter.com/travisbrown))
* [Nguyen Hong Phuc](https://github.com/phucnh) ([@phuc89](https://twitter.com/phuc89))
