# Loklak Wok Desktop
The goal of this project is to provide a desktop version of [Loklak Wok Android](https://github.com/loklak/loklak_wok_android). The origin of the project is a [tweet by @Frank_gamefreak](https://twitter.com/Frank_gamefreak/status/706458848522080256).

![Screenshot](https://github.com/Low012/loklak_wok_desktop/blob/master/stuff/screenshot.png "Screenshot")


##How to compile and run
* import required lib by running **setup.sh**
* compile with **mvn clean install -Pexecutable-jar**
* run artifact in *target* dircetory: **java -jar wok-desktop-0.0.1-SNAPSHOT-jar-with-all-dependencies.jar**
* stop program with ESC key

##To be done
* The code has been hacked and butchered and is some kind of Frankenstein. It needs cleanup.
* Font size is hardcode. How ugly is that?
* It would be cool to have a project for code shared between Android and Desktop version.
* The only dependency which can not be resolved via Maven is [loklakj](https://github.com/loklak/loklakj_lib). Wouldn't it be cool to change that?

## LICENSE
This is licensed under LGPL 2.1. The the file
"DroidSansMono.ttf" (which is licensed by the Apache License, see https://www.google.com/fonts/attribution)
That means, this is free software!
