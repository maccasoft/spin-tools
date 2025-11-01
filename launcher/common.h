#ifndef COMMON_H_
#define COMMON_H_

void   run_vm(const char * jvm_path, int jvm_argc, const char * jvm_argv[], const char * main_class, int argc, const char * argv[]);
char * build_classpath(const char * jar_path, const char ** jar_files);

char * get_app_root(const char * exe_file);

#endif /* COMMON_H_ */
