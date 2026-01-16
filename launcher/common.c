#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

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

static const char * common_jar_files[] = {
    "spin-tools-0.52.1.jar",
    "commons-cli-1.9.0.jar",
    "commons-codec-1.17.1.jar",
    "commons-collections4-4.4.jar",
    "commons-compress-1.27.1.jar",
    "commons-io-2.16.1.jar",
    "commons-lang3-3.18.0.jar",
    "jackson-annotations-2.17.2.jar",
    "jackson-core-2.17.2.jar",
    "jackson-databind-2.17.2.jar",
    "jssc-2.9.6.jar",
    "lz4-java-1.10.1.jar",
    "native-lib-loader-2.5.0.jar",
    "slf4j-api-2.0.16.jar",
    "slf4j-nop-2.0.16.jar",
    NULL
};

jint JNICALL (*_DL_JNI_CreateJavaVM)(JavaVM ** pvm, void ** penv, void * args);

void run_vm(const char * jvm_path, int jvm_argc, char * const jvm_argv[], const char * main_class, int argc, const char * argv[])
{
    char * error;
    JavaVM * jvm;
    JNIEnv * env;
    JavaVMInitArgs vm_args;

    void * jni_handle = dlopen(jvm_path, RTLD_LAZY);
    if (jni_handle == NULL) {
        fprintf(stderr, "%s\n", dlerror());
        exit(1);
    }
    dlerror();

    _DL_JNI_CreateJavaVM = dlsym(jni_handle, "JNI_CreateJavaVM");

    if ((error = dlerror()) == NULL) {
        vm_args.version = JNI_VERSION_10;
        vm_args.nOptions = 0;
        vm_args.ignoreUnrecognized = 0;

        vm_args.nOptions = jvm_argc;
        vm_args.options = (JavaVMOption *) malloc(sizeof(JavaVMOption) * jvm_argc);
        if (vm_args.options == NULL) {
            fprintf(stderr, "can't allocate %d bytes for JavaVMOption\n", (int)(sizeof(JavaVMOption) * jvm_argc));
            exit(1);
        }
        for (int i = 0; i < vm_args.nOptions ; i++) {
            vm_args.options[i].optionString = jvm_argv[i];
            vm_args.options[i].extraInfo = NULL;
        }

        _DL_JNI_CreateJavaVM(&jvm, (void **) &env, &vm_args);

        jclass cls = (*env)->FindClass(env, main_class);
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
}

char * build_classpath(const char * jar_path, const char ** jar_files)
{
    const char * prefix = "-Djava.class.path=";
    const int fixed_path_length = strlen(classpath_separator) + strlen(jar_path) + strlen(path_separator);

    int size = strlen(prefix) + 1;
    for (int i = 0; common_jar_files[i] != NULL; i++) {
        size += fixed_path_length + strlen(common_jar_files[i]);
    }
    if (jar_files != NULL) {
        for (int i = 0; jar_files[i] != NULL; i++) {
            size += fixed_path_length + strlen(jar_files[i]);
        }
    }

    char * result = (char *)malloc(size);
    if (result == NULL) {
        fprintf(stderr, "can't allocate %d bytes for classpath\n", size);
        exit(1);
    }

    strcpy(result, prefix);
    for (int i = 0; common_jar_files[i] != NULL; i++) {
        strcat(result, classpath_separator);
        strcat(result, jar_path);
        strcat(result, path_separator);
        strcat(result, common_jar_files[i]);
    }
    if (jar_files != NULL) {
        for (int i = 0; jar_files[i] != NULL; i++) {
            strcat(result, classpath_separator);
            strcat(result, jar_path);
            strcat(result, path_separator);
            strcat(result, jar_files[i]);
        }
    }
    //printf("d_java_class_path = (%d / %zu) %s\n", size, strlen(result), result);

    return result;
}

char * get_app_root(const char * exe_file)
{
    char * ptr;

    char * result = (char *) malloc(PATH_MAX);
    if (result == NULL) {
        fprintf(stderr, "can't allocate %d bytes for app_root\n", PATH_MAX);
        exit(1);
    }

#if defined(__MINGW64__) ||  defined(__MINGW32__)
    ptr = _fullpath(result, exe_file, PATH_MAX - 1);

    ptr = strrchr(result, '\\');
    if (ptr != NULL) {
        *ptr = '\0';
    }
#else
    if (realpath(exe_file, result) == NULL) {
        fprintf(stderr, "%s\n", strerror(errno));
        exit(1);
    }
    ptr = strrchr(result, '/');
    if (ptr != NULL) {
        *ptr = '\0';
    }
#endif

#if defined(__APPLE__)
    ptr = strstr(result, "/Spin Tools IDE.app");
    if (ptr != NULL) {
        *ptr = '\0';
    }
#endif

    return result;
}
