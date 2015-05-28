/*  SqlCipherHook Xposed Module
 * Copyright 2015 Jake Valletta (@jake_valletta)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakev.sqlcipherhook;

import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteDatabaseHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {

    private static final String TAG = "SqlCipherHook";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        /* Only hook applications that have the SqlCipher libraries */
        if (!isSqlCipherApp())
            return;

        final String packageName = lpparam.packageName;

         /* /src/net/sqlcipher/database/SQLiteDatabase.java
         * public static SQLiteDatabase openDatabase(String path, String password,
         *                                           CursorFactory factory, int flags,
         *                                           SQLiteDatabaseHook databaseHook)
         */
        findAndHookMethod("net.sqlcipher.database.SQLiteDatabase", lpparam.classLoader,
                "openDatabase", String.class, String.class, CursorFactory.class,
                int.class, SQLiteDatabaseHook.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String path = (String)param.args[0];
                        String password = (String)param.args[1];

                        Log.d(TAG, "["+packageName+"] SqlCipher openDatabase( ... ) called!");
                        Log.d(TAG, "["+packageName+"] Password Used: "+password);
                        Log.d(TAG, "["+packageName+"] DB Path: "+path);
                    }
                });

        /* /src/net/sqlcipher/database/SQLiteDatabase.java
         * public static SQLiteDatabase openDatabase(String path, char[] password,
         *                                           CursorFactory factory, int flags,
         *                                           SQLiteDatabaseHook databaseHook)
         */
        findAndHookMethod("net.sqlcipher.database.SQLiteDatabase", lpparam.classLoader,
                "openDatabase", String.class, char[].class, CursorFactory.class,
                int.class, SQLiteDatabaseHook.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String path = (String)param.args[0];
                        String password = new String((char[])param.args[1]);

                        Log.d(TAG, "["+packageName+"] SqlCipher openDatabase( ... ) called!");
                        Log.d(TAG, "["+packageName+"] Password Used: "+password);
                        Log.d(TAG, "["+packageName+"] DB Path: "+path);
                    }
                });
    } // End Hooks

    private boolean isSqlCipherApp() {

        try {
            Class.forName("net.sqlcipher.database.SQLiteDatabase");
        }
        catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }
}

