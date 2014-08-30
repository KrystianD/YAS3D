#include "ScreenGL.h"

#include <QDebug>
#include <QTimer>
#include <QVector3D>
#include <QVector4D>
#include <QQuaternion>
#include <QTransform>
#include <QMatrix4x4>
#include <QMatrix4x3>
#include <QMatrix3x4>
#define GL_GLEXT_PROTOTYPES

#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>
#include <math.h>

#include "kutils.h"
#include "utils.h"

#include "settings.h"


const QVector3D offset(0.0f, -15.0f, -35.0f);

ScreenGL::ScreenGL(QWidget *parent) : QGLWidget(parent)
{
}

void ScreenGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);	

}
static int loaded = 0;
void ScreenGL::paintGL()
{

	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();

	QMatrix4x4 mrot ;

		mrot = (quatToRotMatrix(rotationQuat.conjugate()));

		glMultMatrixd(mrot.constData());


	for (int i = 0; i < pdaData->playerPoints.size(); i++)
	{
		QVector3D p = pdaData->playerPoints[i];
		
		glPushMatrix();
		
		glTranslatef(p.x(), p.y(), p.z());
		
		glColor4f(0, 1, 0, 1);
		glutSolidSphere(0.5f / 25.0f, 10, 10);
		
		glPopMatrix();
	}
	
	for (int i = 0; i < pdaData->foodPoints.size(); i++)
	{
		QVector3D p = pdaData->foodPoints[i];
		
		glPushMatrix();
		
		glTranslatef(p.x(), p.y(), p.z());
		
		glColor4f(1, 0, 0, 1);
		glutSolidSphere(0.5f / 25.0f, 10, 10);
		
		glPopMatrix();
	}
	
	for (int i = 0; i < pdaData->enemies.size(); i++)
	{
		Enemy &e = pdaData->enemies[i];
		
		for (int j = 0; j < e.points.size(); j++)
		{
			QVector3D p = e.points[j];
			
			glPushMatrix();
			
			glTranslatef(p.x(), p.y(), p.z());
			
			glColor4f(0, 0, 1, 1);
			glutSolidSphere(0.5f / 25.0f, 10, 10);
			
			glPopMatrix();
		}
	}

}


void ScreenGL::resizeGL(int width, int height)
{
	glViewport(0, 0, width, height);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	//glTranslatef(0, 0.6, 0);
	gluPerspective(45.0f, (float)width / (float)height, 0.1f, 1.2f);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}
void ScreenGL::keyPressEvent(QKeyEvent *)
{
}
