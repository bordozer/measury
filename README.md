# Measury Stopwatcher

Stopwatcher - a project to track a performance (execution time) for a separate code flows. 
There is necessary to include a special code (wrapper around a being measured code) to add report checkpoint.

The tool is suitable for collection of execution time of particular checkpoints in the scope of one thread. 
The tool is thread save but calculation several parallel threads for one checkpoint name at the same time can show weird results.  

### Prerequisites


```
- Java 8 or later
- Gradle version > 4.6
- https://github.com/bordozer/sca-parent
```

### Installing

Add to gradle build script:

```
compile group: 'com.bordozer.measury', name: 'stopwatcher', version: '2.07'
```

### Example of using:

Declare watchers with key "A KEY". 

```
private static final Stopwatcher ONE_WATCHER = StopwatchManager.getInstance("A KEY");
```
The quantity of Stopwatchers is not limited and can be declared at any place as constant or a variable.
All checkpoints of one Stopwatcher (with the same key) will be collected to a separate report.

Wrap *void* code which need to be measured (adding a checkpoint to report)

```
ONE_WATCHER.measure("Main flow", () -> someService.doSometring());
```

Wrap *not void* code which need to be measured (adding a checkpoint to report)
```
final List<String> list = ONE_WATCHER.measureAndReturn("Main flow", () -> someService());
```

The result report can be logged to log file with two precisions: milliseconds and seconds
```
ONE_WATCHER.logReportMills();
```
```
ONE_WATCHER.logReportSecs();
```

The result report can be built with two precisions: milliseconds and seconds
```
final String report = ONE_WATCHER.buildReportMills();
```
```
final String report = ONE_WATCHER.buildReportSecs();
```

### Example of generated text report:

```
Name        - flow description
Duration    - summary invocation duration
Count       - invocation count
```

```
+ ---------------------------------------------------------------------------------------------------- + ------------ + ---------- +
| Name                                                                                                 | Duration     | Count      |
+ ---------------------------------------------------------------------------------------------------- + ------------ + ---------- +
| Drools init: kie post processor [main].............................................................. | 00:00:00.002 | .........1 |
| Drools init: getting kie container [main]........................................................... | 00:03:06.382 | .........1 |
|   Drools init: loading rules [main]................................................................. | 00:00:00.770 | .........1 |
| Drools init: kie base [main]........................................................................ | 00:00:13.967 | .........1 |
| Drools init: kie session [main]..................................................................... | 00:00:00.121 | .........1 |
| Loading test data [main]............................................................................ | 00:00:00.402 | .........1 |
| Run Drools validation [main]........................................................................ | 00:00:31.959 | .........1 |
|   Validation of 150 models [main]................................................................... | 00:00:31.958 | .........1 |
|     Adding 150 objects for validation [main]........................................................ | 00:00:00.027 | .........1 |
|     Fire all for 150 models [main].................................................................. | 00:00:31.926 | .........1 |

```

All generated reports can be found in

```
build/reports/measury
``````

Report name format

```
${report_key}-yyyyMMdd_SSS.txt
```

## Authors

* **Borys Lukianov**

