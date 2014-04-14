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
#define GL_GLEXT_PROTOTYPES

#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>
#include <math.h>

#include "kutils.h"
#include "utils.h"

#include "settings.h"


const QVector3D offset(0.0f, -15.0f, -35.0f);


#include "3ds.h"

t3DModel model;

#include "ecrand_win7.h"

WorldGL::WorldGL(QWidget *parent) : QGLWidget(parent)
{
}

void WorldGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
static int loaded = 0;
void WorldGL::paintGL()
{

	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	glTranslatef(0, 0, -2.5);

	if (stat)
	{
		glRotatef(-15, 0, 1, 0);
		glRotatef(15, 1, 0, 0);

		glPushMatrix();
	}

	if (!stat)
	{

#ifdef KDMODE
#else
	glScalef(-1, -1, 1);
#endif

#ifdef KDMODE
	glRotatef(90, 1, 0 , 0);
#else
	glRotatef(-90, 1, 0 , 0);
#endif

	}

	//glTranslatef(0, -1, -3);
	QMatrix4x4 mrot ;
	if (stat)
	{
		mrot = (quatToRotMatrix(rotationQuat.conjugate()));

		glMultMatrixd(mrot.constData());
	}
	
	glDisable(GL_CULL_FACE);
	
	//glEnable(GL_LIGHTING);
	
	//glColor3f(1, 1, 0);
	
	/*glBegin(GL_QUADS);
	
	glTexCoord2f(0,0);	glVertex3f(0,0,0);
	glTexCoord2f(1,0);	glVertex3f(1,0,0);
	glTexCoord2f(1,1);	glVertex3f(1,1,0);
	glTexCoord2f(0,1);	glVertex3f(0,1,0);
	glEnd();*/
	
	
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
	
	
	glEnable(GL_CULL_FACE);
	glCullFace(GL_BACK);



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

	if (stat)
		glPopMatrix();

	glPushMatrix();
	glDisable(GL_CULL_FACE);


	if (!stat)
	{
	mrot = (quatToRotMatrix(rotationQuat));
	
	glMultMatrixd(mrot.constData());
	}
	
	glColor4f(0, 0, 0, 1);
	
	glBegin(GL_LINES);
	
	pt(0);
	pt(1);
	pt(1);
	pt(2);
	pt(2);
	pt(3);
	pt(3);
	pt(0);
	
	pt(4);
	pt(7);
	pt(7);
	pt(3);
	pt(0);
	pt(4);
	
	ln(7, 6);
	ln(6, 2);
	
	ln(6, 5);
	ln(5, 1);
	ln(4, 5);
	
	glEnd();
	



	glEnable(GL_TEXTURE_2D);
	//glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, m_texture);

	float wi = 575;
	float he = 1127;
	float d = 6000;

	glPushMatrix();
	glRotatef(90, 1, 0, 0);
	glRotatef(90, 0, 1, 0);
	drawPhone(wi / d, 100 / d, he / d);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();




	glBegin(GL_QUADS);
	
	glColor4f(1, 0, 0, 0.2);
	plane(0, 1, 2, 3);
	
	glColor4f(0, 1, 0, 0.2);
	plane(4, 7, 3, 0);
	plane(7, 6, 2, 3);
	plane(6, 5, 1, 2);
	plane(4, 5, 1, 0);
	
	glColor4f(0, 0, 1, 0.2);
	plane(4, 5, 6, 7);
	
	glEnd();





	glPopMatrix();
	glEnable(GL_CULL_FACE);
	glClear(GL_DEPTH_BUFFER_BIT);
	GLUquadric *quad =  gluNewQuadric();
	glColor4f(0, 0, 0, 0.3);
	gluSphere(quad, 1, 100, 100);
	gluDeleteQuadric(quad);
}
void WorldGL::pt(int idx)
{
	glVertex3f(
	  pdaData->frustum[idx].x(),
	  pdaData->frustum[idx].y(),
	  pdaData->frustum[idx].z());
}
void WorldGL::ln(int idx, int idx2)
{
	pt(idx);
	pt(idx2);
}
void WorldGL::plane(int idx1, int idx2, int idx3, int idx4)
{
	pt(idx1);
	pt(idx2);
	pt(idx3);
	pt(idx4);
}

void WorldGL::resizeGL(int width, int height)
{
	glViewport(0, 0, width, height);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	//glTranslatef(0, 0.6, 0);
	gluPerspective(60.0f, (float)width / (float)height, 0.1f, 100.0f);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}
void WorldGL::keyPressEvent(QKeyEvent *)
{
}
