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

import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteDatabaseHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {

    private static final String TAG = "SqlCipherHook";
    private static final String SQLCIPHER_CLASS_NAME = "net.sqlcipher.database.SQLiteDatabase";

    private String currentPackageName = "";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        currentPackageName = lpparam.packageName;

        findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                Context context = (Context) param.args[0];
                ClassLoader cl = context.getClassLoader();

                if (hasSqlCipher(cl)) {
                    hookSqlCipher(cl);
                }
            }
        });
    }

    /* Perform Hooks */
    private void hookSqlCipher(ClassLoader classLoader) {


        Log.d(TAG, "Hooking SqlCipher libraries for: " + currentPackageName);

         /* /src/net/sqlcipher/database/SQLiteDatabase.java
         * public static SQLiteDatabase openDatabase(String path, String password,
         *                                           CursorFactory factory, int flags,
         *                                           SQLiteDatabaseHook databaseHook)
         */
        findAndHookMethod("net.sqlcipher.database.SQLiteDatabase", classLoader,
                "openDatabase", String.class, String.class, CursorFactory.class,
                int.class, SQLiteDatabaseHook.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String path = (String)param.args[0];
                        String password = (String)param.args[1];

                        Log.d(TAG, "["+currentPackageName+"] SqlCipher openDatabase( ... ) called!");
                        Log.d(TAG, "["+currentPackageName+"] Password Used: "+password);
                        Log.d(TAG, "["+currentPackageName+"] DB Path: "+path);
                    }
                });

        /* /src/net/sqlcipher/database/SQLiteDatabase.java
         * public static SQLiteDatabase openDatabase(String path, char[] password,
         *                                           CursorFactory factory, int flags,
         *                                           SQLiteDatabaseHook databaseHook)
         */
        findAndHookMethod("net.sqlcipher.database.SQLiteDatabase", classLoader,
                "openDatabase", String.class, char[].class, CursorFactory.class,
                int.class, SQLiteDatabaseHook.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String path = (String)param.args[0];
                        String password = new String((char[])param.args[1]);

                        Log.d(TAG, "["+currentPackageName+"] SqlCipher openDatabase( ... ) called!");
                        Log.d(TAG, "["+currentPackageName+"] Password Used: "+password);
                        Log.d(TAG, "["+currentPackageName+"] DB Path: "+path);
                    }
                });
    } // End Hooks

    private boolean hasSqlCipher(ClassLoader classLoader) {

        try {
            classLoader.loadClass(SQLCIPHER_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}

