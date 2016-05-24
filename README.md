# UA Parser Scala Library

This is the Scala implementation of [ua-parser](https://github.com/tobie/ua-parser).
The implementation uses the shared regex patterns and overrides from regexes.yaml.
This is a fork of [the original uap-scala](https://github.com/ua-parser/uap-scala), and independently maintained from the original as it seems not to be maintained currently.

[![wercker status](https://app.wercker.com/status/ecaa3d98ba2b475f475cad290458a661/s "wercker status")](https://app.wercker.com/project/bykey/ecaa3d98ba2b475f475cad290458a661)

## Quick start

This fork of uap-scala is published to [Maven Central](http://search.maven.org/), currently only for Scala 2.11 (cross building is in my plan). So add the following to your build.sbt.

```
libraryDependencies += "com.github.yanana" %% "uap-scala" % "0.1.0"
```

That's it! It's time to use uap-scala. 

```scala
import ua.parser.Parser

  val ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"
  val client = Parser.get.parse(ua) // you can also use CachingParser
  println(client) // Client(UserAgent(Mobile Safari,Some(5),Some(1),None),OS(iOS,Some(5),Some(1),Some(1),None),Device(iPhone))
}
```

Author:
-------
  * Shun Yanaura ([@ya7_](https://twitter.com/ya7_))

  Originary forked from the official UA Parser Scala binding by Piotr Adamski [@mcveat](https://twitter.com/mcveat).
