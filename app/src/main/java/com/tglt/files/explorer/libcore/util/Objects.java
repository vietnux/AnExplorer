/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.tglt.files.explorer.libcore.util;

import com.tglt.files.explorer.libcore.io.Arrays;

public final class Objects {
    private Objects() {}

    /**
     * Returns true if two possibly-null objects are equal.
     */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }

	public static boolean equals(String a, String b) {
		 return (a == b) || (a != null && a.equals(b));
	}

	public static int hash(Object... values) {
		return Arrays.hashCode(values);
	}
}
