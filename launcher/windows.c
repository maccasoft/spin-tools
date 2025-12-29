#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <process.h>

int open_document(int argc, const char * argv[])
{
    int rc = 0;

    HWND window = FindWindow(NULL, "SWT_Window_maccasoft-spintoolside");
    if (window != NULL) {
        int size = 0;
        for (int i = 1; i < argc; i++) {
            if (argv[i][0] != '-') {
                int length = strlen(argv[i]);
                if (length > size) {
                    size = length;
                }
            }
        }
        if (size != 0) {
            HANDLE mapHandle = CreateFileMapping(INVALID_HANDLE_VALUE, NULL, PAGE_READWRITE, 0, size, NULL);
            if (mapHandle != NULL) {
                UINT msg = RegisterWindowMessage("SWT_OPENDOC");
                DWORD processID = GetCurrentProcessId();

                for (int i = 1; i < argc; i++) {
                    if (argv[i][0] != '-') {
                        HANDLE processMapHandle = NULL;

                        HANDLE processHandle = OpenProcess(PROCESS_ALL_ACCESS, FALSE, processID);
                        if (processHandle != NULL) {
                            DuplicateHandle(processHandle, mapHandle, GetCurrentProcess(), &processMapHandle, DUPLICATE_SAME_ACCESS, FALSE, DUPLICATE_SAME_ACCESS);
                            CloseHandle(processHandle);
                        }

                        if (processMapHandle != NULL) {
                            wchar_t * sharedData = MapViewOfFile(processMapHandle, FILE_MAP_WRITE, 0, 0, 0);
                            if (sharedData != NULL) {
                                mbstowcs(sharedData, argv[i], strlen(argv[i]) + 1);
                                UnmapViewOfFile(sharedData);
                            }
                            CloseHandle(processMapHandle);
                        }

                        SendMessage(window, msg, (WPARAM) processID, (LPARAM) mapHandle);
                        rc = 1;
                    }
                }

                CloseHandle(mapHandle);
            }
        }
    }

    return rc;
}
