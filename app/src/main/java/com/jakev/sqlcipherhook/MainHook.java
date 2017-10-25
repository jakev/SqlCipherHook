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

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class MainHook implements IXposedHookLoadPackage {

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

        /* We can simply hook all calls to "openDatabase(...)", and handle the slight differences */
        Class<?> clazz = findClass(SQLCIPHER_CLASS_NAME, classLoader);
        XC_MethodHook hook =  new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                /* Param 1 will always be a string. */
                String path = (String)param.args[0];

                /* Param 2 can be either a char[] or a String. */
                String password = "ERROR";
                Object inPassword = param.args[1];

                if (inPassword instanceof String) {
                    password = (String)inPassword;
                } else if (inPassword instanceof char[]) {
                    password = String.valueOf((char[])inPassword);
                }

                Log.d(TAG, "[" + currentPackageName + "] SqlCipher openDatabase( ... ) called!");
                Log.d(TAG, "[" + currentPackageName + "] Password Used: " + password);
                Log.d(TAG, "[" + currentPackageName + "] DB Path: " + path);
            }
        };

        /* Perform the hook */
        XposedBridge.hookAllMethods(clazz, "openDatabase", hook);
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

