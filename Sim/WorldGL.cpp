#include "WorldGL.h"

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
#include <GL/glut.h>
#include <math.h>

#include "kutils.h"
#include "utils.h"

#include "settings.h"

const QVector3D offset(0.0f, -15.0f, -35.0f);

WorldGL::WorldGL(QWidget *parent) : QGLWidget(parent)
{
	// connect(&tm, SIGNAL(timeout()), this, SLOT(repaint()));
	// tm.setInterval(1000 / 30);
	// tm.start();
}

void WorldGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
}

void WorldGL::paintGL()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
	glEnable(GL_CULL_FACE);
	glCullFace(GL_BACK);
	
	
	QMatrix4x4 mrot = realToOGL(quatToRotMatrix(rotationQuat));
	glLoadIdentity();
	
#ifdef KDMODE
#else
	glScalef(-1, -1, 1);
#endif
	
	//glTranslatef(offset.x(), offset.y(), offset.z());
	glTranslatef(0, 0, -3);
	
#ifdef KDMODE
	glRotatef(90, 1, 0 , 0);
#else
	glRotatef(-90, 1, 0 , 0);
#endif
	// glMultMatrixd(mrot.transposed().constData());
	
	
	for (int i = 0; i < pdaData->playerPoints.size(); i++)
	{
		QVector3D p = pdaData->playerPoints[i];
		
		glPushMatrix();
		
		glTranslatef(p.x(), p.y(), p.z());
		
		glColor4f(0, 1, 0, 1);
		glutSolidSphere(0.5f / 15.0f, 5, 5);
		
		glPopMatrix();
	}
	
	for (int i = 0; i < pdaData->foodPoints.size(); i++)
	{
		QVector3D p = pdaData->foodPoints[i];
		
		glPushMatrix();
		
		glTranslatef(p.x(), p.y(), p.z());
		
		glColor4f(1, 0, 0, 1);
		glutSolidSphere(0.5f / 15.0f, 5, 5);
		
		glPopMatrix();
	}
	
	GLUquadric *quad =  gluNewQuadric();
	glColor4f(0, 0, 0, 0.3);
	gluSphere(quad, 1, 100, 100);
	gluDeleteQuadric(quad);
}
void WorldGL::resizeGL(int width, int height)
{
	glViewport(0, 0, width, height);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	//glTranslatef(0, 0.6, 0);
	gluPerspective(45.0f, (float)width / (float)height, 0.1f, 100.0f);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}
void WorldGL::keyPressEvent(QKeyEvent *)
{
}
