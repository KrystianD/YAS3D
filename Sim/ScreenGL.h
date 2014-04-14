#ifndef SCREENGL_H
#define Screen

#include <QGLWidget>
#include <QQuaternion>
#include <QTimer>

#include "classes.h"

class ScreenGL : public QGLWidget
{
public:
	ScreenGL (QWidget *parent = 0);
	virtual ~ScreenGL () { }

	QQuaternion rotationQuat;
	TData *pdaData;
	bool stat;
	unsigned int m_texture;

protected:
	void initializeGL ();
	void paintGL ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

private:
	QTimer tm;
};

#endif // WORLDGL_H
