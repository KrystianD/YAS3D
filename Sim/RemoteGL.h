#ifndef REMOTEGL_H
#define REMOTEGL_H

#include <QGLWidget>
#include <QQuaternion>
#include <QTimer>

class RemoteGL : public QGLWidget
{
public:
	RemoteGL (QWidget *parent = 0);
	virtual ~RemoteGL () { }

	QQuaternion rotationQuat;

protected:
	void initializeGL ();
	void paintGL ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

private:
	QTimer tm;
};

#endif // REMOTEGL_H
