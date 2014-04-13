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
	// connect(&tm, SIGNAL(timeout()), this, SLOT(repaint()));
	// tm.setInterval(1000 / 30);
	// tm.start();
}

static int loaded = 0;
static unsigned int mTexture;
void RemoteGL::initializeGL()
{
	glShadeModel(GL_SMOOTH);
	glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	
	//if (loaded == 0)
	{
		//loaded = 1;
		// Generate a texture with the texture ID stored in the texture item
		glGenTextures(1, &mTexture);
		
		// Bind the texture to OpenGL and initalize the texture - GL_TEXTURE_2D tells it we are using a 2D texture
		glBindTexture(GL_TEXTURE_2D, mTexture);
		
		unsigned char *t = new unsigned char[gimp_image.width * gimp_image.height * 3 + 1];
		memset(t, 255, gimp_image.width * gimp_image.height * 3);
		
		gluBuild2DMipmaps(GL_TEXTURE_2D, 3, gimp_image.width, gimp_image.height, GL_RGB, GL_UNSIGNED_BYTE, gimp_image.pixel_data);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		qDebug() << "LOAD";
	}
}

void RemoteGL::paintGL()
{
	qDebug() << mTexture;
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
	
	/*QColor colors[] = { Qt::red, Qt::red, Qt::black, Qt::black, Qt::green, Qt::green };
	drawBox(colors, 8.0, 1.0, 3.5, offset);
	glPushMatrix();
	glTranslatef(6, 5, -2);
	drawBox(colors, 1.0, 5.0, 1.0, offset);*/
	
	glDisable(GL_CULL_FACE);
	glEnable(GL_TEXTURE_2D);
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mTexture);
	
	float wi = 575;
	float he = 1127;
	float d = 200;

	//glRotatef(90,0,0,1);
	glRotatef(90,0,1,0);
	
	/*glRotatef(90, 1, 0, 0);
	glRotatef(90, 0, 1, 0);*/
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
