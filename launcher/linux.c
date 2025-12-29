#include <stdio.h>
#include <dlfcn.h>

#ifdef __linux__
#include <gio/gio.h>
#endif

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

void install_desktop_launcher(const char * app_root, const char * exe_file)
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
    fprintf(fp, "Exec=%s %%f\n", exe_file);
    fprintf(fp, "Icon=%s\n", base_name);
    fprintf(fp, "Terminal=false\n");
    fprintf(fp, "Categories=Development;IDE;Electronics;\n");
    fprintf(fp, "Keywords=embedded electronics;electronics;propeller;microcontroller;\n");
    fprintf(fp, "StartupWMClass=maccasoft-spintoolside\n");
    fprintf(fp, "StartupNotify=true\n");
    fprintf(fp, "MimeType=text/x-spin;text/x-spin2\n");
    fclose(fp);
    snprintf(cmd, sizeof(cmd), "%s install --novendor %s", xdg_desktop_menu, filename);
    rc = system(cmd);
    unlink(filename);
    if (rc != 0) {
        fprintf(stderr, " error running '%s'\n", cmd);
        exit(1);
    }

    rmdir(tempdir);

    strcpy(cmd, "xdg-mime install mime-info.xml");
    rc = system(cmd);
    if (rc != 0) {
        fprintf(stderr, " error running '%s'\n", cmd);
        exit(1);
    }

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

GVariantBuilder * (*_dl_g_variant_builder_new) (const GVariantType *);
void        (*_dl_g_variant_builder_add)    (GVariantBuilder *, const gchar *, const gchar *);
GVariant *  (*_dl_g_variant_new)            (const gchar *, GVariantBuilder *);
void        (*_dl_g_variant_builder_unref)  (GVariantBuilder *);
void        (*_dl_g_variant_unref)          (GVariant *);

GDBusProxy * (*_dl_g_dbus_proxy_new_for_bus_sync) (GBusType, GDBusProxyFlags, GDBusInterfaceInfo *, const gchar *,const gchar *, const gchar *, GCancellable *, GError **);
GVariant *  (*_dl_g_dbus_proxy_call_sync) (GDBusProxy *, const gchar *, GVariant *, GDBusCallFlags, gint, GCancellable *, GError **);

void        (*_dl_g_object_unref)           (gpointer);
void        (*_dl_g_error_free)             (GError *);

int open_document(int argc, const char * argv[])
{
    int rc = 0;
    GError * error = NULL;

    void * objLib = dlopen(GOBJ_LIB, RTLD_LAZY);
    void * gioLib = dlopen(GIO_LIB, RTLD_LAZY);
    void * glibLib = dlopen(GLIB_LIB, RTLD_LAZY);

    if (objLib == NULL || gioLib == NULL || glibLib == NULL) {
        if (objLib != NULL) {
            dlclose(objLib);
        }
        if (gioLib != NULL) {
            dlclose(gioLib);
        }
        if (glibLib != NULL) {
            dlclose(glibLib);
        }
        return 0;
    }

    _dl_g_dbus_proxy_new_for_bus_sync = dlsym(gioLib, "g_dbus_proxy_new_for_bus_sync");
    _dl_g_dbus_proxy_call_sync = dlsym(gioLib, "g_dbus_proxy_call_sync");

    _dl_g_variant_builder_new = dlsym(glibLib, "g_variant_builder_new");
    _dl_g_variant_builder_add = dlsym(glibLib, "g_variant_builder_add");
    _dl_g_variant_new = dlsym(glibLib, "g_variant_new");
    _dl_g_variant_builder_unref = dlsym(glibLib, "g_variant_builder_unref");
    _dl_g_variant_unref = dlsym(glibLib, "g_variant_unref");

    _dl_g_object_unref = dlsym(objLib, "g_object_unref");
    _dl_g_error_free = dlsym(objLib, "g_error_free");

    GDBusProxy * proxy = _dl_g_dbus_proxy_new_for_bus_sync(
        G_BUS_TYPE_SESSION,
        G_DBUS_PROXY_FLAGS_NONE,
        NULL,
        "org.eclipse.swt.maccasoft-spintoolside",
        "/org/eclipse/swt",
        "org.eclipse.swt",
        NULL,
        &error
    );
    if (error != NULL) {
        _dl_g_error_free(error);
    }
    if (proxy != NULL) {
        GVariantBuilder * builder = _dl_g_variant_builder_new((const GVariantType *) "as");  // as = G_VARIANT_TYPE_STRING_ARRAY;
        for (int i = 1; i < argc; i++) {
            if (argv[i][0] != '-') {
                _dl_g_variant_builder_add(builder, (const gchar *) (const GVariantType *) "s", (const gchar *) argv[i]);  // s = G_VARIANT_TYPE_STRING
            }
        }
        GVariant * parameters = _dl_g_variant_new("(as)", builder);
        _dl_g_variant_builder_unref(builder);

        GVariant * result = _dl_g_dbus_proxy_call_sync(
            proxy,
            "FileOpen",
            parameters,
            G_DBUS_CALL_FLAGS_NONE,
            -1,
            NULL,
            &error
        );
        if (error != NULL) {
            _dl_g_error_free(error);
        }
        if (result != NULL) {
            rc = 1;
            _dl_g_variant_unref(result);
        }

        _dl_g_object_unref(proxy);
    }

    dlclose(objLib);
    dlclose(gioLib);
    dlclose(glibLib);

    return rc;
}
