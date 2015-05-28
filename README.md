#SqlCipherHook
SqlCipherHook is an [Xposed Framework](http://repo.xposed.info/) module that will attempt to capture crypto keys from applications using the [SQLCipher](https://www.zetetic.net/sqlcipher/) library.  If it is successful, it will print the keys to the Android log buffers (viewable with `logcat`).

##Usage
You will need to root your test device and install the Xposed Framework.  Next, you can download and install the SqlCipherHooks APK, [here](https://github.com/jakev/dtf/tree/master/bin/SqlCipherHook.apk). You'll need to enable the module and reboot the device after installing the APK.

Once installed, you can interact with your test application while running the following filtered `logcat` command:

    analyst$ adb logcat SqlCipherHook:D *:S

##License
SqlCipherHook is released under the Apache Software License 2.0.

##Motivation
Thanks to [@MDSecLab](https://twitter.com/mdseclabs) for doing the research on which methods to hook in the SQLCipher library, and for creating a [CydiaSubstrate](http://www.cydiasubstrate.com/) tweak.
