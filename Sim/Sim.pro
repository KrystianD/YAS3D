#-------------------------------------------------
#
# Project created by QtCreator 2013-05-20T20:55:51
#
#-------------------------------------------------

QT       += core gui opengl network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = KDcopter
TEMPLATE = app
#DEFINES += KDMODE

win32 {
    LIBS += -lGLU32 -lfreeglut
}
unix {
    LIBS += -lGLU -lglut
}

SOURCES += main.cpp\
        MainWindow.cpp \
    RemoteGL.cpp \
    utils.cpp \
    ReferenceWidget.cpp \
    kutils.cpp \
    MadgwickAHRS.cpp \
    RawReadsWidget.cpp \
    WorldGL.cpp \
    objloader.cpp \
    3ds.cpp \
    ScreenGL.cpp

HEADERS  += MainWindow.h \
    RemoteGL.h \
    utils.h \
    QuadroGL.h \
    ReferenceWidget.h \
    kutils.h \
    classes.h \
    MadgwickAHRS.h \
    RawReadsWidget.h \
    WorldGL.h \
    objloader.hpp \
    3ds.h \
    settings.h \
    model.h \
    ScreenGL.h

FORMS    += MainWindow.ui \
    RawReadsWidget.ui

OTHER_FILES += \
    old.txt
