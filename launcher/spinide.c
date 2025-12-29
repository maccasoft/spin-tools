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
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <dlfcn.h>
#include <jni.h>

#include "common.h"

#ifndef PATH_MAX
#define PATH_MAX    4096
#endif

static const char * jar_files[] = {
    "org.eclipse.core.commands-3.12.400.jar",
    "org.eclipse.core.databinding-1.13.700.jar",
    "org.eclipse.core.databinding.observable-1.13.500.jar",
    "org.eclipse.core.databinding.property-1.10.500.jar",
    "org.eclipse.equinox.common-3.20.200.jar",
    "org.eclipse.jface-3.38.0.jar",
    "org.eclipse.jface.databinding-1.15.400.jar",
    "org.eclipse.osgi-3.23.200.jar",
    "org.eclipse.swt-3.132.0.jar",
#if defined(__MINGW64__) || defined(__MINGW32__)
    "org.eclipse.swt.win32.win32.x86_64-3.132.0.jar",
#elif defined(__APPLE__)
  #if defined(__aarch64__) || defined(_M_ARM64)
    "org.eclipse.swt.cocoa.macosx.aarch64-3.132.0.jar",
  #elif defined(__x86_64__) || defined(_M_X64)
    "org.eclipse.swt.cocoa.macosx.x86_64-3.132.0.jar",
  #else
    #error "Unknown architecture"
  #endif
#else
  #if defined(__aarch64__) || defined(_M_ARM64)
    "org.eclipse.swt.gtk.linux.aarch64-3.132.0.jar",
  #elif defined(__x86_64__) || defined(_M_X64)
    "org.eclipse.swt.gtk.linux.x86_64-3.132.0.jar",
  #else
    #error "Unknown architecture"
  #endif
#endif
    NULL
};

#if defined(__MINGW64__) || defined(__MINGW32__)
int  open_document(int argc, const char * argv[]);
#endif
#ifdef __linux__
void install_desktop_launcher();
void uninstall_desktop_launcher();
int  open_document(int argc, const char * argv[]);
#endif

int main(int argc, const char * argv[])
{
    int alloc_size;
    char * app_root = get_app_root(argv[0]);

#if defined(__linux__)
    if (argc > 1) {
        if (!strcmp(argv[1], "--install")) {
            install_desktop_launcher(app_root, argv[0]);
            exit(0);
        }
        if (!strcmp(argv[1], "--uninstall")) {
            uninstall_desktop_launcher();
            exit(0);
        }
    }
#endif

#if defined(__linux__) || defined(__MINGW64__) || defined(__MINGW32__)
    if (open_document(argc, argv)) {
        exit(0);
    }
#endif

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

    char * d_java_class_path = build_classpath(jar_path, jar_files);

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
    run_vm(jvm_path, 2, jvm_argv, "com/maccasoft/propeller/SpinTools", argc, argv);

    free(d_app_dir);
    free(d_java_class_path);

    free(jar_path);
    free(jvm_path);
    free(app_root);

    return 0;
}
