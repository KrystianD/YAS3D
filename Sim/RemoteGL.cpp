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

#include "kutils.h"
#include "utils.h"

#include "ecrand_win7.h"

const QVector3D offset(0.0f, -15.0f, -35.0f);

RemoteGL::RemoteGL(QWidget *parent) : QGLWidget(parent)
{
}

void RemoteGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	

	glGenTextures(1, &m_texture);
	glBindTexture(GL_TEXTURE_2D, m_texture);
	gluBuild2DMipmaps(GL_TEXTURE_2D, 3, gimp_image.width, gimp_image.height, GL_RGB, GL_UNSIGNED_BYTE, gimp_image.pixel_data);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	qDebug() << "LOAD";
}

void RemoteGL::paintGL()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	
	QMatrix4x4 mrot = realToOGL(quatToRotMatrix(rotationQuat));
	glLoadIdentity();
	glTranslatef(offset.x(), offset.y(), offset.z());

#ifdef KDMODE
	glRotatef(-90,0,1,0);
#else
	glRotatef(90,0,1,0);
#endif

	glMultMatrixd((const double*)mrot.transposed().constData());
	
	glDisable(GL_CULL_FACE);
	glEnable(GL_TEXTURE_2D);
	//glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, m_texture);
	
	float wi = 575;
	float he = 1127;
	float d = 200;

	glRotatef(90,0,1,0);

	drawPhone(wi / d, 100 / d, he / d);

	glDisable(GL_TEXTURE_2D);
	glColor3f(1,0,0);

	glPushMatrix();
	QColor colorsX[] = { Qt::red, Qt::red, Qt::red, Qt::red, Qt::red, Qt::red };
	glTranslatef(5, 0, 0);
	drawBox(colorsX, 5, 0.3, 0.3, offset);
	glPopMatrix();

	glPushMatrix();
	QColor colorsY[] = { Qt::green, Qt::green, Qt::green, Qt::green, Qt::green, Qt::green };
	glTranslatef(0, -5, 0);
	drawBox(colorsY, 0.3, 5, 0.3, offset);
	glPopMatrix();

	glPushMatrix();
	QColor colorsZ[] = { Qt::blue, Qt::blue, Qt::blue, Qt::blue, Qt::blue, Qt::blue };
	glTranslatef(0, 0, 5);
	drawBox(colorsZ, 0.3, 0.3, 5, offset);
	glPopMatrix();
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
