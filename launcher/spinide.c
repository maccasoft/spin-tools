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

#ifdef __linux__
static char exe_path[PATH_MAX];
#endif
static char app_root[PATH_MAX];
static char jvm_path[PATH_MAX];
static char lib_path[PATH_MAX];

jint JNICALL (*_DL_JNI_CreateJavaVM)(JavaVM ** pvm, void ** penv, void * args);

char * build_classpath();

#ifdef __linux__
void install_desktop_launcher();
void uninstall_desktop_launcher();
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
        fprintf(stderr, "%s\n", strerror(errno));
        exit(1);
    }

    ptr = _fullpath(app_root, argv[0], sizeof(app_root) - 1);

    ptr = strrchr(app_root, '\\');
    if (ptr != NULL) {
        *ptr = '\0';
    }

    strcpy(jvm_path, app_root);
    strcat(jvm_path, "/java/bin");
    chdir(jvm_path);
    strcat(jvm_path, "/server/jvm.dll");

    strcpy(lib_path, app_root);
    strcat(lib_path, "/lib");
#elif defined(__APPLE__)
    ptr = realpath(argv[0], app_root);
    if (ptr == NULL) {
        fprintf(stderr, "%s\n", strerror(errno));
        exit(1);
    }

    ptr = strrchr(app_root, '/');
    if (ptr != NULL) {
        *ptr = '\0';
    }
    ptr = strstr(app_root, "/Spin Tools IDE.app");
    if (ptr != NULL) {
        *ptr = '\0';
    }

    strcpy(jvm_path, app_root);
    strcat(jvm_path, "/Spin Tools IDE.app/Contents/Java/lib/libjli.dylib");

    strcpy(lib_path, jvm_path);
    strcat(lib_path, "/Spin Tools IDE.app/Contents/Resources");
#else
    ptr = realpath(argv[0], app_root);
    if (ptr == NULL) {
        fprintf(stderr, "%s\n", strerror(errno));
        exit(1);
    }
    strcpy(exe_path, ptr);

    ptr = strrchr(app_root, '/');
    if (ptr != NULL) {
        *ptr = '\0';
    }

    strcpy(jvm_path, app_root);
    strcat(jvm_path, "/java/lib/server/libjvm.so");

    strcpy(lib_path, app_root);
    strcat(lib_path, "/lib");
#endif

#ifdef __linux__
    if (argc > 1) {
        if (!strcmp(argv[1], "--install")) {
            install_desktop_launcher();
            exit(0);
        }
        if (!strcmp(argv[1], "--uninstall")) {
            uninstall_desktop_launcher();
            exit(0);
        }
    }
#endif

    char * d_java_class_path = build_classpath();

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

        jclass cls = (*env)->FindClass(env, "com/maccasoft/propeller/SpinTools");
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
                if (strstr(ent->d_name, ".jar") == NULL) {
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
                if (strstr(ent->d_name, ".jar") == NULL) {
                    continue;
                }
                //printf("  %s\n", ent->d_name);
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

#ifdef __linux__
extern unsigned char spinide16_png[];
extern unsigned int spinide16_png_len;

extern unsigned char spinide32_png[];
extern unsigned int spinide32_png_len;

extern unsigned char spinide48_png[];
extern unsigned int spinide48_png_len;

extern unsigned char spinide64_png[];
extern unsigned int spinide64_png_len;

static const char * xdg_desktop_menu = "/usr/bin/xdg-desktop-menu";
static const char * xdg_desktop_icon = "/usr/bin/xdg-desktop-icon";
static const char * xdg_icon_resource = "/usr/bin/xdg-icon-resource";

static const char * base_name = "maccasoft-spintoolside";

void install_desktop_launcher()
{
    FILE * fp;
    int rc;
    char tempdir[100], filename[200], cmd[300];

    printf("Adding desktop shortcut and menu item...");

    char * ptr = getenv("TMPDIR");
    if (ptr == NULL) {
        ptr = "/tmp";
    }
    snprintf(tempdir, sizeof(tempdir), "%s/maccasoft-spintoolside-XXXXXX", ptr);
    if (mkdtemp(tempdir) == NULL) {
        fprintf(stderr, " %s\n", strerror(errno));
        exit(1);
    }

    snprintf(filename, sizeof(filename), "%s/%s.png", tempdir, base_name);
    if ((fp = fopen(filename, "w")) == NULL) {
        fprintf(stderr, " error writing %s\n", filename);
        exit(1);
    }
    fwrite(spinide16_png, spinide16_png_len, 1, fp);
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --context apps --size 16 --novendor %s", xdg_icon_resource, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running %s\n", cmd);
        exit(1);
    }

    snprintf(filename, sizeof(filename), "%s/%s.png", tempdir, base_name);
    if ((fp = fopen(filename, "w")) == NULL) {
        fprintf(stderr, " error writing %s\n", filename);
        exit(1);
    }
    fwrite(spinide32_png, spinide32_png_len, 1, fp);
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --context apps --size 32 --novendor %s", xdg_icon_resource, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running %s\n", cmd);
        exit(1);
    }

    snprintf(filename, sizeof(filename), "%s/%s.png", tempdir, base_name);
    if ((fp = fopen(filename, "w")) == NULL) {
        fprintf(stderr, " error writing %s\n", filename);
        exit(1);
    }
    fwrite(spinide48_png, spinide48_png_len, 1, fp);
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --context apps --size 48 --novendor %s", xdg_icon_resource, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running %s\n", cmd);
        exit(1);
    }

    snprintf(filename, sizeof(filename), "%s/%s.png", tempdir, base_name);
    if ((fp = fopen(filename, "w")) == NULL) {
        fprintf(stderr, " error writing %s\n", filename);
        exit(1);
    }
    fwrite(spinide64_png, spinide64_png_len, 1, fp);
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --context apps --size 64 --novendor %s", xdg_icon_resource, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running %s\n", cmd);
        exit(1);
    }

    snprintf(filename, sizeof(filename), "%s/%s.desktop", tempdir, base_name);
    if ((fp = fopen(filename, "w")) == NULL) {
        fprintf(stderr, " error writing %s\n", filename);
        exit(1);
    }
    fprintf(fp, "[Desktop Entry]\n");
    fprintf(fp, "Type=Application\n");
    fprintf(fp, "Name=Spin Tools IDE\n");
    fprintf(fp, "GenericName=Spin Tools IDE\n");
    fprintf(fp, "Comment=Integrated development environment for Parallax Propeller microcontrollers.\n");
    fprintf(fp, "Path=%s\n", app_root);
    fprintf(fp, "Exec=%s %%f\n", exe_path);
    fprintf(fp, "Icon=%s\n", base_name);
    fprintf(fp, "Terminal=false\n");
    fprintf(fp, "Categories=Development;IDE;Electronics;\n");
    fprintf(fp, "Keywords=embedded electronics;electronics;propeller;microcontroller;\n");
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --novendor %s", xdg_desktop_menu, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running %s\n", cmd);
        exit(1);
    }

    rmdir(tempdir);

    printf(" done\n");
}

void uninstall_desktop_launcher()
{
    int rc;
    char cmd[100];

    printf("Removing desktop shortcut and menu item...");

    sprintf(cmd, "%s uninstall %s.desktop", xdg_desktop_menu, base_name);
    rc = system(cmd);
    sprintf(cmd, "%s uninstall %s.desktop", xdg_desktop_icon, base_name);
    rc = system(cmd);
    sprintf(cmd, "%s uninstall --context apps --size 16 %s.png", xdg_icon_resource, base_name);
    rc = system(cmd);
    sprintf(cmd, "%s uninstall --context apps --size 32 %s.png", xdg_icon_resource, base_name);
    rc = system(cmd);
    sprintf(cmd, "%s uninstall --context apps --size 48 %s.png", xdg_icon_resource, base_name);
    rc = system(cmd);
    sprintf(cmd, "%s uninstall --context apps --size 64 %s.png", xdg_icon_resource, base_name);
    rc = system(cmd);

    if (rc != 0) {
        // Do nothing
    }

    printf(" done\n");
}
#endif
