# RollingLogger
A Java library for writing logs into a fix number of files in rolling
order, in case the log files' size grows immensely.

## Download
### Maven
```xml
<dependency>
	<groupId>com.github.crazyorr</groupId>
	<artifactId>rolling-logger</artifactId>
	<version>0.1.0</version>
</dependency>
```
### Gradle
```gradle
compile 'com.github.crazyorr:rolling-logger:0.1.0'
```

## Usage
```java
RollingLogger logger = new RollingLogger(logFilePath, logFileName, logFileMaxSize, maxLogFileCount);
logger.writeLog("This is a log.");
logger.writeLogLine("This is a log line.");
```

## Credits
* NIKEDLAB's [LOG ROLLING LIBRARY][1]
[1]: http://nikedlab.com/android-log-rolling-library.html#

## License

    Copyright 2015 Lei Wang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
