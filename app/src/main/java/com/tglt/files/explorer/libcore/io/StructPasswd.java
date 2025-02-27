/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.tglt.files.explorer.libcore.io;

/**
 * Information returned by getpwnam(3) and getpwuid(3). Corresponds to C's
 * {@code struct passwd} from
 * <a href="http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/pwd.h.html">&lt;pwd.h&gt;</a>
 */
public final class StructPasswd {
    public String pw_name;
    public int pw_uid; /* uid_t */
    public int pw_gid; /* gid_t */
    public String pw_dir;
    public String pw_shell;

    public StructPasswd(String pw_name, int pw_uid, int pw_gid, String pw_dir, String pw_shell) {
        this.pw_name = pw_name;
        this.pw_uid = pw_uid;
        this.pw_gid = pw_gid;
        this.pw_dir = pw_dir;
        this.pw_shell = pw_shell;
    }
}
