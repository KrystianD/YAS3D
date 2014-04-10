#-------------------------------------------------
#
# Project created by QtCreator 2013-05-20T20:55:51
#
#-------------------------------------------------

QT       += core gui opengl network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = KDcopter
TEMPLATE = app

LIBS += -lGLU -lglut

SOURCES += main.cpp\
        MainWindow.cpp \
    RemoteGL.cpp \
    utils.cpp \
    UsbRFM70.cpp \
    QuadroGL.cpp \
    ReferenceWidget.cpp \
    kutils.cpp \
    globals.cpp \
    MadgwickAHRS.cpp \
    RawReadsWidget.cpp \
    WorldGL.cpp

HEADERS  += MainWindow.h \
    RemoteGL.h \
    utils.h \
    UsbRFM70.h \
    QuadroGL.h \
    ReferenceWidget.h \
    kutils.h \
    classes.h \
    globals.h \
    MadgwickAHRS.h \
    RawReadsWidget.h \
    WorldGL.h

FORMS    += MainWindow.ui \
    RawReadsWidget.ui

OTHER_FILES += \
    old.txt
