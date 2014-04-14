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
	bool stat;

protected:
	void initializeGL ();
	void paintGL ();
	void resizeGL (int width, int height);
	void keyPressEvent (QKeyEvent *);

private:
	QTimer tm;

	void pt(int idx);
	void ln(int idx, int idx2);
	void plane(int idx1, int idx2, int idx3, int idx4);
};

#endif // WORLDGL_H
