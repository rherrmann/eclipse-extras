# Extras for Eclipse 
[![Build Status](https://img.shields.io/codeship/6a994910-8fa7-0132-ebb3-32b8c1ae92e1/master.svg)](https://codeship.com/projects/61325)
[![Version](https://img.shields.io/badge/version-1.0-lightgrey.svg)](http://rherrmann.github.io/eclipse-extras/repository/)
[![Requirements](https://img.shields.io/badge/requirements-JRE%201.8%20%26%20Luna%20or%20later-2C2255.svg)](https://eclipse.org/luna/)
[![EPL licensed](https://img.shields.io/badge/license-EPL-blue.svg)](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/LICENSE)

Small extensions for the Eclipse IDE

Extras for Eclipse include a launch dialog, a JUnit status bar, a launch configuration housekeeper, and little helpers to accomplish recurring tasks with keyboard shortcuts.

## Installation

Extras for Eclipse is  available from the [Eclipse Marketplace](https://marketplace.eclipse.org/content/extras-eclipse). Just drag the icon to your running Eclipse workspace:

<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2710118" class="drag" title="Drag to your running Eclipse workspace to install Extras for Eclipse"><img class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/_themes/solstice_marketplace/public/images/btn-install.png" alt="Drag to your running Eclipse workspace to install Extras for Eclipse" /></a>

If you prefer, you can also install from this software site: 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;http://rherrmann.github.io/eclipse-extras/repository/

In the Eclipse main menu, click _Help > Install New Software…_, then enter the URL above and select _Extras for the Eclipse IDE_. Expand the item to select only certain features for installation.

Please note that a JRE 8 or later and Eclipse Luna (4.4) or later are required to run this software.


## Features at a Glance

### JUnit Status Bar
A small contribution to the status bar shows the status of the current or most recent JUnit run. The progress bar is colored green or red depending on how the test results are. The button to the left of the bar opens the [JUnit view](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fviews%2Fref-view-junit.htm). 

Use the _Java &gt; JUnit &gt; JUnit Status Bar_ preference page to show or hide the JUnit Status Bar. Note that in Eclipse 4.5 (Mars) and later you [need to resize the workbench window afterwards](https://bugs.eclipse.org/bugs/show_bug.cgi?id=459904) to make the change appear (sigh).

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/junit-status-bar-starting.png)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/junit-status-bar-green.png)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/junit-status-bar-red.png)

### Key Binding for the JUnit View
The key sequence <kbd>Alt+Shift+Q U</kbd> is assigned to open the JUnit view in the current perspective.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/show-junit-view.png)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/junit-view.png)

### Launch Configuration Selection Dialog
Much like the _Open Type_ or _Open Resource_ dialog, the <kbd>Alt+F11</kbd> key binding opens a dialog that lists all launch configurations. The selected launch configuration(s) can be started or edited prior to starting. The launch mode (debug, run, profile, etc) to be used can be changed through the _Default Launch Mode_ drop menu menu.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/launch-config-dialog.png)

Launch configurations that are currently running are decorated with a _running_ ![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/running.png) symbol.

### Key Binding for the Open With... Menu
The key sequence <kbd>Shift+F3</kbd> can be used to show the _Open With_ menu for the selected file. The menu provides a list of alternative editors to open the file in.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/open-with-menu.png)

### Key Binding to Delete File of Active Editor
If the currently active editor shows a file from the workspace, the key sequence <kbd>Alt+Del</kbd> can be used to delete this file and close the corresponding editor. The command invokes the regular delete operation so that the behavior is the same as if the edited file was deleted from one of the navigation views.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/delete-editor-resource.png)

### Clean Up Generated Lanch Configurations
A new preference page (_Run/Debug > Launching > Clean Up_) allows to clean up launch configurations generated by launch shortcuts. If this option is enabled, all _generated_ launch configurations will be deleted after they have been launched and terminated. As _generated_ are considered all launch configurations that are created by commands outside of the launch configuration dialog, for example with _Run As > JUnit Test_.

This option helps keeping the launch configuration dialog tidy in that only the relevant launch configurations are shown.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/launch-cleanup.png)

### Key Binding to Close the Active View
The key binding <kbd>Ctrl+Shift+Q</kbd> closes the currently active view. Just like <kbd>Ctrl+W</kbd> closes the active editor, there is now also a key binding to close the active view. 

### Dynamic Project Working Set
A new type of working set allows to group projects by naming patterns. Rather than manually adding and removing resources, the working set maintains its contents dynamically. When created it is given a regular expression pattern and all projects whose name match that pattern are contained. The content is updated whenever matching projects are created, deleted or change their name.

Dynamic Project Working Sets can be created with the _New Wizard_ (_File &gt; New &gt; Other &gt; General &gt; Dynamic Project Working Set_) or with the working set configuration dialogs of the respective navigation views like _Package Explorer_ or _Project Explorer_.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/dynamic-working-sets.png)

## Requirements
Works with Eclipse 4.4 (Luna) or later and Java 1.8 or later.

## Contributing
Please see the [contributing guidelines](CONTRIBUTING.md).

## License
The code is published under the terms of the [Eclipse Public License, version 1.0](https://www.eclipse.org/legal/epl-v10.html).
