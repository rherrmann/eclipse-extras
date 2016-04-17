# Extras for Eclipse Changelog

## Installation

See the [Installation](https://github.com/rherrmann/eclipse-extras/blob/master/README.md#installation) instructions on how to install the software.


## List of Changes
##### April 2016 
###### Launch Dialog
The launch dialog can optionally terminate all running instances of a launch configuration before relaunching it.
This setting is off by default and can be changed through the dialog's drop-down menu. [#40](https://github.com/rherrmann/eclipse-extras/issues/40)

##### March 2016 
###### JUnit Status Bar
A new context menu entry allows to change the _Activate on Failures Only_ preference of the JUnit view directly from the status bar. [#24](https://github.com/rherrmann/eclipse-extras/issues/24)

###### Launch Dialog
The _Run_ menu now has a _Start Launch Configuration..._ entry that opens the launch dialog. [#31](https://github.com/rherrmann/eclipse-extras/issues/31)

##### February 2016
###### Launch Dialog
* A context menu entry allows to terminate all running instances of the selected launch configuration(s) [#32](https://github.com/rherrmann/eclipse-extras/issues/32)
* Distinguish same launch config names among different projects. [#34](https://github.com/rherrmann/eclipse-extras/issues/34)

###### Other
The <kbd>Alt+Del</kbd> command to delete the currently edited (workspace) file is now able to also delete files from the local file system [#30](https://github.com/rherrmann/eclipse-extras/issues/30)

###### Bugfixes
* [Launch Clean up] Do not remove launch configurations that are stored in the workspace [#43](https://github.com/rherrmann/eclipse-extras/issues/43)
* [Launch Dialog] Renaming a launch configuration leads to an error [#38](https://github.com/rherrmann/eclipse-extras/issues/38)
