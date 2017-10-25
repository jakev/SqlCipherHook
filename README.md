# SqlCipherHook
`SqlCipherHook` is an [Xposed Framework](http://repo.xposed.info/) module that will attempt to capture crypto keys from applications using the [SQLCipher](https://www.zetetic.net/sqlcipher/) library. If it is successful, it will print the keys to the Android log buffers (viewable with `logcat`). `SqlCipherHook` is known to work up through version 3.5.7. Feel free to open a ticket if it is not working for your specific version.

## Installing & Usage
You will need to root your test device and install the Xposed Framework. Next, you can:

    $ git clone https://github.com/jakev/SqlCipherHook
    $ cd SqlCipherHook
    $ ./gradlew installDebug

Optionally, you can install the pre-built copy to avoid using Gradle:

    $ git clone https://github.com/jakev/SqlCipherHook
    $ cd SqlCipherHook
    $ adb install ./bin/com.jakev.sqlcipherhook-debug.apk

Once installed, you can interact with your test application while running the following filtered `logcat` command:

    $ adb logcat SqlCipherHook:D *:S

You should see SqlCipher interactions, including the keys used. More information is available in my blog post from [2015](http://blog.thecobraden.com/2015/05/hooking-sqlcipher-with-xposed.html).

## License
`SqlCipherHook` is released under the Apache Software License 2.0.

## Motivation
Thanks to [@MDSecLab](https://twitter.com/mdseclabs) for doing the research on which methods to hook in the SQLCipher library, and for creating a [CydiaSubstrate](http://www.cydiasubstrate.com/) tweak.
