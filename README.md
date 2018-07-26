# W Tran

A database offline translator and german dictionary.

![logo](offtrans/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

this is a very slow starting (but than fast) simple de-eng, eng-de offline translator using big
tei xml-files from [freedict.org](https://github.com/freedict/fd-dictionaries).

**This App exists in beta state!!!**

## Get the App

You can get a signed APK from here or via play store (Android 4.4+): [APK](https://raw.githubusercontent.com/no-go/offlineTranslator/master/offtrans/app/release/app-release.apk)

## Desktop Version

I try to build a Desktop version with nodejs and electron. You find executable
in my dropbox: [Download](https://www.dropbox.com/sh/u18mceddc5u0008/AAD7rxHHM9SdnO_v2iWBdroLa?dl=0)

If you want to run the code inside the `desktop/` folder with plain [node.js](https://nodejs.org)
you have to:

- install nodejs (and npm)
- switch into the `desktop/` folder
- do `npm i -D electron@latest`
- do `npm start`

If you want to build your own desktop executable:

- install [yarn](https://yarnpkg.com)
- switch into the `desktop/` folder
- do `npm install`
- do `yarn add electron-builder --dev`
- do `yarn dist`

## Feature (Android)

- initial offline in app import to a fast database
- dictionary: german
- lexicon: german-english, arabic-english, kurdi-german
- copy/paste edit window (on tap)
- search (both sides)

## Is it ready?

no.

- On Android: It will be better to have a reimport button, if initial import crashs.
- It will be better to make the tei files downloadable or import file from your phone.
- many informations of the TEI files are not displayed
- Some TEI infos in desktop version displays wrong :-(

## I need kurdî -> türkçe !

Because freedict has this dictionary, it is easy to implement this! 
Take a look on this [commit](https://github.com/no-go/offlineTranslator/commit/84e5cfcc1d189bb4c6a826c5374a053b82771a92), how to easy add other translations.

## Privacy Policy

### Personal information.

Personal information is data that can be used to uniquely identify or contact a single person. I DO NOT collect, transmit, store or use any personal information while you use this app.

### Non-Personal information.

I DO NOT collect non-personal information like user's behavior:

 -  to solve App problems
 -  to show personalized ads

The google play store collect non-personal information such as the data of install (country and equipment).

### Privacy Questions.

If you have any questions or concerns about my Privacy Policy or data processing, please contact me.


## Licenses

### License - My Android and my Node.js Code

This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or distribute this software, either in source code form or as a compiled binary, for any purpose, commercial or non-commercial, and by any means.

In jurisdictions that recognize copyright laws, the author or authors of this software dedicate any and all copyright interest in the software to the public domain. We make this dedication for the benefit of the public at large and to the detriment of our heirs and successors. We intend this dedication to be an overt act of relinquishment in perpetuity of all present and future rights to this software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to http://unlicense.org

### License - OpenThesaurus File

My Code uses a text file from [openthesaurus.de](https://www.openthesaurus.de)
with this license:

    OpenThesaurus - German Thesaurus in text format
    Automatically generated 2018-02-01 23:01
    https://www.openthesaurus.de
    Copyright (C) 2003-2017 Daniel Naber (naber at danielnaber de)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA


## Screenshots

### Win 10

![windows 10](metadata/de-DE/images/phoneScreenshots/6.jpg)

### Linux

![Linux](metadata/de-DE/images/phoneScreenshots/7.jpg)

### Mac OS X (10.10)

![Mac OS X](metadata/de-DE/images/phoneScreenshots/8.jpg)

### Android

![app image initial import](metadata/de-DE/images/phoneScreenshots/0.jpg)

![app image search kurdi](metadata/de-DE/images/phoneScreenshots/1.jpg)

![app image search english](metadata/de-DE/images/phoneScreenshots/2.jpg)

![app image DE Thesaurus](metadata/de-DE/images/phoneScreenshots/3.jpg)

![app image search arabic](metadata/de-DE/images/phoneScreenshots/4.jpg)

![tap to get copy-paste window](metadata/de-DE/images/phoneScreenshots/5.jpg)
