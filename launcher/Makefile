
SPINIDE_OBJS = spinide.o common.o
SPINC_OBJS = spinc.o common.o

CXX = gcc
XXD = xxd
STRIP = strip
WINDRES = windres
RM = rm

CXXFLAGS = -O2 -Wall -fmessage-length=0 -I "$(JAVA_HOME)/include"

ifeq ($(OS),Windows_NT)
	CXXFLAGS += -I "$(JAVA_HOME)/include/win32"
	SPINIDE_OBJS += windows.o spinide.res
	SPINIDE_LIBS = -mwindows -static-libgcc -Wl,-static -ldl
	SPINC_LIBS = -static-libgcc -Wl,-static -ldl
	EXT = .exe
else
	UNAME_S := $(shell uname -s)
	UNAME_P := $(shell uname -p)
	ifeq ($(UNAME_S),Linux)
		CXXFLAGS += -I "$(JAVA_HOME)/include/linux" -I "/usr/include/glib-2.0" -I "/usr/lib/$(UNAME_P)-linux-gnu/glib-2.0/include"
		CXXFLAGS += -DGOBJ_LIB="\"libgobject-2.0.so.0\"" -DGIO_LIB="\"libgio-2.0.so.0\"" -DGLIB_LIB="\"libglib-2.0.so.0\""
		SPINIDE_OBJS += linux.o spinide16.o spinide32.o spinide48.o spinide64.o
	endif
	ifeq ($(UNAME_S),Darwin)
		ifeq ($(UNAME_P),x86_64)
			CXXFLAGS += -mmacosx-version-min=10.15
		else
			CXXFLAGS += -mmacosx-version-min=11.0
		endif
		CXXFLAGS += -I "$(JAVA_HOME)/include/darwin"
	endif
endif

all: spinide$(EXT) spinc$(EXT)

spinide$(EXT): Makefile $(SPINIDE_OBJS)
	$(CXX) -o $@ $(SPINIDE_OBJS) $(SPINIDE_LIBS)
	$(STRIP) $@

spinc$(EXT): Makefile $(SPINC_OBJS)
	$(CXX) -o $@ $(SPINC_OBJS) $(SPINC_LIBS)
	$(STRIP) $@

%.o: %.c
	$(CXX) $(CXXFLAGS) -o $@ -c $<

%.res: %.rc
	$(WINDRES) -O coff -o $@ -i $<

%.c : %.png
	$(XXD) -i $< $@

clean:
	$(RM) -f spinide$(EXT) $(SPINIDE_OBJS) spinc$(EXT) $(SPINC_OBJS)
