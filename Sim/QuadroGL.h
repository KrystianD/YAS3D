#ifndef QUADROGL_H
#define QUADROGL_H

#include <QGLWidget>
#include <QQuaternion>
#include <QTimer>

class QuadroGL : public QGLWidget
{
public:
	QuadroGL (QWidget *parent = 0);
	virtual ~QuadroGL () { }

	QQuaternion rotationQuat, destRotationQuat;
	QVector3D v1;

protected:
	void initializeGL ();
	void paintGL ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

private:
	QTimer tm;
};

#endif // GL_H
