# RollingLogger
A Java library for rolling log to external storage.

## Usage
```java
RollingLogger logger = new RollingLogger(logFilePath, logFileName, maxLogFileSize, maxLogFileCount);
logger.writeLog("This is a log.");
logger.writeLogLine("This is a log line.");
```

## Credits
* NIKEDLAB's [LOG ROLLING LIBRARY][1]

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
    
   [1]: http://nikedlab.com/android-log-rolling-library.html#
