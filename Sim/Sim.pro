#-------------------------------------------------
#
# Project created by QtCreator 2013-05-20T20:55:51
#
#-------------------------------------------------

QT       += core gui opengl network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = KDcopter
TEMPLATE = app

LIBS += -lusb-1.0 -lGLU

SOURCES += main.cpp\
        MainWindow.cpp \
    RemoteGL.cpp \
    utils.cpp \
    UsbRFM70.cpp \
    QuadroGL.cpp \
    ReferenceWidget.cpp \
    kutils.cpp \
    Widgets/RawReadsVisWidget.cpp \
    globals.cpp \
    MadgwickAHRS.cpp \
    RawReadsWidget.cpp

HEADERS  += MainWindow.h \
    RemoteGL.h \
    utils.h \
    UsbRFM70.h \
    QuadroGL.h \
    ReferenceWidget.h \
    kutils.h \
    classes.h \
    Widgets/RawReadsVisWidget.h \
    globals.h \
    MadgwickAHRS.h \
    RawReadsWidget.h

FORMS    += MainWindow.ui \
    RawReadsWidget.ui

OTHER_FILES += \
    old.txt
