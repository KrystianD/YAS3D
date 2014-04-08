#include "RemoteGL.h"

#include <QDebug>
#include <QTimer>
#include <QVector3D>
#include <QVector4D>
#include <QQuaternion>
#include <QTransform>
#include <QMatrix4x4>
#include <QMatrix4x3>
#include <QMatrix3x4>
#include <GL/glu.h>
#include <math.h>

#include "/home/krystiand/prog/kdlibs/kutils.h"
#include "utils.h"

const QVector3D offset(0.0f, -15.0f, -35.0f);

RemoteGL::RemoteGL(QWidget *parent) : QGLWidget(parent)
{
	// connect(&tm, SIGNAL(timeout()), this, SLOT(repaint()));
	// tm.setInterval(1000 / 30);
	// tm.start();
}

void RemoteGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
}

void RemoteGL::paintGL()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	
	QMatrix4x4 mrot = realToOGL(quatToRotMatrix(rotationQuat));
	glLoadIdentity();
	glTranslatef(offset.x(), offset.y(), offset.z());
	glMultMatrixd(mrot.transposed().constData());
	
	QColor colors[] = { Qt::red, Qt::red, Qt::black, Qt::black, Qt::green, Qt::green };
	drawBox(colors, 8.0, 1.0, 3.5, offset);
	glPushMatrix();
	glTranslatef(6, 5, -2);
	drawBox(colors, 1.0, 5.0, 1.0, offset);
	glPopMatrix();
	
	/*QColor colorsX[] = { Qt::red, Qt::red, Qt::red, Qt::red, Qt::red, Qt::red };
	QColor colorsY[] = { Qt::green, Qt::green, Qt::green, Qt::green, Qt::green, Qt::green };
	QColor colorsZ[] = { Qt::blue, Qt::blue, Qt::blue, Qt::blue, Qt::blue, Qt::blue };
	QColor colorsG[] = { Qt::black, Qt::black, Qt::black, Qt::black, Qt::black, Qt::black };*/
	/*drawLine (QVector3D (0,0,0), v1);
	drawLine (QVector3D (0,0,0), v2);
	drawLine (QVector3D (0,0,0), v3);
	drawBoxAt (colorsX, 0.5f, 0.5f, 0.5f, v1);
	drawBoxAt (colorsY, 0.5f, 0.5f, 0.5f, v2);
	drawBoxAt (colorsZ, 0.5f, 0.5f, 0.5f, v3);
	
	drawLine (QVector3D (0,0,0), w->tmp1);
	drawBoxAt (colorsZ, 0.5f, 0.5f, 0.5f, w->tmp1);*/
}
void RemoteGL::resizeGL(int width, int height)
{
	glViewport(0, 0, width, height);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glTranslatef(0, 0.6, 0);
	gluPerspective(45.0f, (float)width / (float)height, 0.1f, 100.0f);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}
void RemoteGL::keyPressEvent(QKeyEvent *)
{
}
