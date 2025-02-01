/*
 * Copyright (c) 2025 Marco Maccaferri and others.
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
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <dlfcn.h>
#include <jni.h>

#ifndef PATH_MAX
#define PATH_MAX    4096
#endif

static const char * path_separator = "/";
#if defined(__MINGW64__) ||  defined(__MINGW32__)
static const char * classpath_separator = ";";
#else
static const char * classpath_separator = ":";
#endif
static const char * lib_folder = "lib";

static char exe_path[PATH_MAX];
static char app_root[PATH_MAX];
static char jvm_path[PATH_MAX];
static char lib_path[PATH_MAX];

jint JNICALL (*_DL_JNI_CreateJavaVM)(JavaVM ** pvm, void ** penv, void * args);

char * build_classpath();

#ifdef __linux__
void install_desktop_launcher();
void uninstall_desktop_launcher();
void mkdir_p(const char * path);
#endif

int main(int argc, char * argv[])
{
    void * jni_handle;
    JavaVM * jvm;
    JNIEnv * env;
    JavaVMInitArgs vm_args;
    char * error, * ptr;

#if defined(__MINGW64__) ||  defined(__MINGW32__)
    char * cwd_path = getcwd(NULL, 0);
    if (cwd_path == NULL) {
        printf("%s\n", strerror(errno));
        exit(1);
    }

    ptr = _fullpath(app_root, argv[0], sizeof(app_root) - 1);
    strcpy(exe_path, ptr);
    ptr = strrchr(app_root, '\\');
    if (ptr != NULL) {
        *(ptr + 1) = '\0';
    }
#else
    ptr = realpath(argv[0], app_root);
    if (ptr == NULL) {
        printf("%s\n", strerror(errno));
        exit(1);
    }
    strcpy(exe_path, ptr);
#endif
    ptr = strrchr(app_root, '/');
    if (ptr != NULL) {
        *(ptr + 1) = '\0';
    }
    //printf("app_root = %s\n", app_root);

    strcpy(jvm_path, app_root);
#if defined(__MINGW64__) ||  defined(__MINGW32__)
    strcat(jvm_path, "java/bin");
    chdir(jvm_path);
    strcat(jvm_path, "/server/jvm.dll");
#elif defined(__APPLE__)
    ptr = strstr(jvm_path, "/MacOS");
    if (ptr != NULL) {
        *(ptr + 1) = '\0';
    }
    else {
        strcat(jvm_path, "Spin Tools IDE.app/Contents/");
    }
    strcat(jvm_path, "Java/lib/libjli.dylib");
#else
    strcat(jvm_path, "java/lib/server/libjvm.so");
#endif
    //printf("jvm_path = %s\n", jvm_path);

    strcpy(lib_path, app_root);
    strcat(lib_path, lib_folder);
    //printf("lib_path = %s\n", lib_path);

    char * d_java_class_path = build_classpath();

#if defined(__APPLE__)
    ptr = strstr(app_root, ".app");
    if (ptr != NULL) {
        *ptr = '\0';
        ptr = strrchr(app_root, '/');
        if (ptr != NULL) {
            *ptr = '\0';
        }
    }
#endif
    int size = strlen("-DAPP_DIR=") + strlen(app_root) + 1;
    char * d_app_dir = (char *) malloc(size);
    if (d_app_dir == NULL) {
        fprintf(stderr, "can't allocate %d bytes for APP_DIR\n", size);
        exit(1);
    }
    strcpy(d_app_dir, "-DAPP_DIR=");
    strcat(d_app_dir, app_root);

    jni_handle = dlopen(jvm_path, RTLD_LAZY);
    if (jni_handle == NULL) {
        fprintf(stderr, "%s\n", dlerror());
        exit(1);
    }
    dlerror();

    _DL_JNI_CreateJavaVM = dlsym(jni_handle, "JNI_CreateJavaVM");

#if defined(__MINGW64__) ||  defined(__MINGW32__)
    chdir(cwd_path);
#endif

    if ((error = dlerror()) == NULL) {
        vm_args.version = JNI_VERSION_10;
        vm_args.nOptions = 0;
        vm_args.ignoreUnrecognized = 0;

        size = sizeof(JavaVMOption) * 2;
        vm_args.options = (JavaVMOption *) malloc(size);
        if (vm_args.options == NULL) {
            fprintf(stderr, "can't allocate %d bytes for JavaVMOption\n", size);
            exit(1);
        }
        vm_args.options[vm_args.nOptions].optionString = (char *) d_java_class_path;
        vm_args.options[vm_args.nOptions].extraInfo = NULL;
        vm_args.nOptions++;

        vm_args.options[vm_args.nOptions].optionString = (char *) d_app_dir;
        vm_args.options[vm_args.nOptions].extraInfo = NULL;
        vm_args.nOptions++;

        _DL_JNI_CreateJavaVM(&jvm, (void **) &env, &vm_args);

        jclass cls = (*env)->FindClass(env, "com/maccasoft/propeller/SpinCompiler");
        if ((*env)->ExceptionOccurred(env) == NULL) {
            jmethodID mid = (*env)->GetStaticMethodID(env, cls, "main", "([Ljava/lang/String;)V");
            if ((*env)->ExceptionOccurred(env) == NULL) {
                jobjectArray args = (*env)->NewObjectArray(env, argc - 1, (*env)->FindClass(env, "java/lang/String"), 0);
                for (int i = 1, idx = 0; i < argc; i++, idx++) {
                    jstring str = (*env)->NewStringUTF(env, argv[i]);
                    (*env)->SetObjectArrayElement(env, args, idx, str);
                }
                (*env)->CallStaticVoidMethod(env, cls, mid, args);
            }
        }
        if ((*env)->ExceptionOccurred(env) != NULL) {
            (*env)->ExceptionDescribe(env);
        }

        (*jvm)->DestroyJavaVM(jvm);

        free(vm_args.options);
    }

    dlclose(jni_handle);

    free(d_app_dir);
    free(d_java_class_path);

    return 0;
}

char * build_classpath()
{
    DIR * dir;
    struct dirent * ent;
    static const char * prefix = "-Djava.class.path=";

    int size = strlen(prefix) + 1;
    if ((dir = opendir(lib_path)) != NULL) {
        if ((ent = readdir(dir)) != NULL) {
            do {
                if (strcmp(ent->d_name, ".") == 0 || strcmp(ent->d_name, "..") == 0) {
                    continue;
                }
                size += strlen(classpath_separator) + strlen(lib_path) + strlen(path_separator) + strlen(ent->d_name);
            } while ((ent = readdir(dir)) != NULL);
        }
        closedir(dir);
    }

    char * result = (char *)malloc(size);
    if (result == NULL) {
        fprintf(stderr, "can't allocate %d bytes for classpath\n", size);
        exit(1);
    }

    strcpy(result, prefix);
    if ((dir = opendir(lib_path)) != NULL) {
        if ((ent = readdir(dir)) != NULL) {
            do {
                if (strcmp(ent->d_name, ".") == 0 || strcmp(ent->d_name, "..") == 0) {
                    continue;
                }
                strcat(result, classpath_separator);
                strcat(result, lib_path);
                strcat(result, path_separator);
                strcat(result, ent->d_name);
            } while ((ent = readdir(dir)) != NULL);
        }
        closedir(dir);
    }
    //printf("d_java_class_path = (%d / %zu) %s\n", size, strlen(result), result);

    return result;
}
