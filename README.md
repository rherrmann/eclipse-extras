# Eclipse Extras [![Build Status](https://travis-ci.org/rherrmann/eclipse-extras.png)](https://travis-ci.org/rherrmann/eclipse-extras)

Small extensions for the Eclipse IDE

## Current Features

### Key Binding for the JUnit View
The key sequence <kbd>Alt+Shift+Q U</kbd> is assigned to open the JUnit view in the current perspective.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/show-junit-view.png)

### Key Binding for the Open With... Menu
The key sequence <kbd>Shift+F3</kbd> can be used to show the _Open With_ menu for the selected file. The menu provides a list of alternative editors to open the file in.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/open-with-menu.png)

### Key Binding to Delete File of Active Editor
If the currently active editor shows a file from the workspace, the key sequence <kbd>Alt+Del</kbd> can be used to delete this file and close the corresponding editor. The command invokes the regular delete operation so that the behavior is the same as if the edited file was deleted from one of the navigation views.

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/delete-editor-resource.png)

### Launch Configuration Selection Dialog
Much like the _Open Type_ or _Open Resource_ dialog, this dialog lists the available launch configurations and allows to start the selected one. Which launch mode (debug, run, profile, etc) is to be changed through the Default Launch Mode drop menu menu.

The key binding to open this dialog is <kbd>Alt+F11</kbd>

![](https://raw.githubusercontent.com/rherrmann/eclipse-extras/master/readme-images/launch-config-dialog.png)

## Installation
You can install Eclipse Extras directly from this software site: http://rherrmann.github.io/eclipse-extras/repository/

In the Eclipse main menu, click _Help > Install New Software…_. Then enter the URL above and select Eclipse Extras

## Contributing
Please see the [contributing guidelines](CONTRIBUTING.md).

## License
The code is published under the terms of the [Eclipse Public License, version 1.0](https://www.eclipse.org/legal/epl-v10.html).
