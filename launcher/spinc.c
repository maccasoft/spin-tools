/*
 * Copyright (c) 2021-2025 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>

#include "common.h"

int main(int argc, const char * argv[])
{
    int alloc_size;
    char * app_root = get_app_root(argv[0]);

    alloc_size = strlen(app_root) + PATH_MAX;
    char * jvm_path = (char *) malloc(alloc_size);
    if (jvm_path == NULL) {
        fprintf(stderr, "can't allocate %d bytes for jvm_path\n", alloc_size);
        exit(1);
    }
    char * jar_path = (char *) malloc(alloc_size);
    if (jar_path == NULL) {
        fprintf(stderr, "can't allocate %d bytes for jar_path\n", alloc_size);
        exit(1);
    }

    strcpy(jvm_path, app_root);
    strcpy(jar_path, app_root);

#if defined(__MINGW64__) ||  defined(__MINGW32__)
    strcat(jvm_path, "/java/bin/server/jvm.dll");
    strcat(jar_path, "/lib");
#elif defined(__APPLE__)
    strcat(jvm_path, "/Spin Tools IDE.app/Contents/Java/lib/libjli.dylib");
    strcat(jar_path, "/Spin Tools IDE.app/Contents/Resources");
#else
    strcat(jvm_path, "/java/lib/server/libjvm.so");
    strcat(jar_path, "/lib");
#endif

    //printf("app_root = %s\n", app_root);
    //printf("jvm_path = %s\n", jvm_path);
    //printf("jar_path = %s\n", jar_path);

    char * d_java_class_path = build_classpath(jar_path, NULL);

    alloc_size = strlen("-DAPP_DIR=") + strlen(app_root) + 1;
    char * d_app_dir = (char *) malloc(alloc_size);
    if (d_app_dir == NULL) {
        fprintf(stderr, "can't allocate %d bytes for APP_DIR\n", alloc_size);
        exit(1);
    }
    strcpy(d_app_dir, "-DAPP_DIR=");
    strcat(d_app_dir, app_root);

    const char * jvm_argv[] = {
        d_java_class_path, d_app_dir
    };
    run_vm(jvm_path, 2, jvm_argv, "com/maccasoft/propeller/SpinCompiler", argc, argv);

    free(d_app_dir);
    free(d_java_class_path);

    free(jar_path);
    free(jvm_path);
    free(app_root);

    return 0;
}
