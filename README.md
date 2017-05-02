# iRunSeoul Android App

Pre-requisites & Libraries
--------------

- Android SDK v25
- Firebase 
- ButterKnife
- Google Maps SDK
- Calligraphy
- Spots-dialog
- Stravazpot

You should obtain Client Key and Secret for Strava, Google Maps API Key, and Firebase credential file `google-services.json`. 
Add following values in `gradle.properties` file.

```xml
googleMapApiKey="GOOGLE_API_KEY"
StravaClientID=CLIENT_ID
StravaCientSecret="CLIENT_SECRET"
StravaRedirectURL="REDIRECT_URL"

```

Getting Started
---------------

This app uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

#### Screenshots

Marathon Events

![listactivity](screenshots/image1.png)

Detail Event

![detailactivity](screenshots/image2.png)

My Profile

![myprofile](screenshots/image3.png)

Contributors
---------------

- Sujin Lee [KoreaMarathonAPI](https://github.com/sujinleeme/KoreaMarathonAPI)
- Hassan Abid
- Jiyeon Lee (Design help)

License
-------

#### The MIT License (MIT)

Copyright (c) Hassan Abid

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


