#ifndef WORLDGL_H
#define WORLDGL_H

#include <QGLWidget>
#include <QQuaternion>
#include <QTimer>

#include "classes.h"

class WorldGL : public QGLWidget
{
public:
	WorldGL (QWidget *parent = 0);
	virtual ~WorldGL () { }

	QQuaternion rotationQuat;
	TData *pdaData;

protected:
	void initializeGL ();
	void paintGL ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

private:
	QTimer tm;
};

#endif // WORLDGL_H
