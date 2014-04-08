#ifndef REMOTEGL_H
#define REMOTEGL_H

#include <QGLWidget>
#include <QQuaternion>
#include <QTimer>
#include <QOpenGLFunctions>
#include <QWindow>
#include <QOpenGLContext>
#include <QOpenGLPaintDevice>

class RemoteGL : public QWindow, public QOpenGLFunctions
{
public:
	RemoteGL (QOpenGLContext *ctx, QWindow *parent = 0);
	virtual ~RemoteGL () { }

	QQuaternion rotationQuat;

public:
	void initializeGL ();
	void renderNow ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

	void exposeEvent(QExposeEvent *event);
	bool event(QEvent *event);
public:
	QTimer tm;
	QOpenGLContext *m_ctx;
	QOpenGLPaintDevice *m_device;
};

#endif // REMOTEGL_H
