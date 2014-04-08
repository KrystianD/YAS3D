#include "utils.h"

#include <GL/gl.h>

float d2r (float v) { return v * M_PI / 180.0f; }
float r2d (float v) { return v * 180.0f / M_PI; }

QMatrix4x4 quatToRotMatrix (QQuaternion q)
{
	qreal q1 = q.scalar ();
	qreal q2 = q.x ();
	qreal q3 = q.y ();
	qreal q4 = q.z ();
	return QMatrix4x4 (
				2*q1*q1 - 1 + 2*q2*q2,     2*(q2*q3 + q1*q4),     2*(q2*q4 - q1*q3), 0,
						2*(q2*q3 - q1*q4), 2*q1*q1 - 1 + 2*q3*q3,     2*(q3*q4 + q1*q2), 0,
						2*(q2*q4 + q1*q3),     2*(q3*q4 - q1*q2), 2*q1*q1 - 1 + 2*q4*q4, 0,
														0,                     0,                     0, 1);
}
QMatrix4x4 realToOGL (QMatrix4x4 m)
{
	QVector4D x = m.column (0);
	QVector4D y = m.column (1);
	QVector4D z = m.column (2);
	QVector4D w = m.column (3);

	return QMatrix4x4 (
				x.x (), z.x (), -y.x (), 0,
				x.z (), z.z (), -y.z (), 0,
				-x.y (), -z.y (), y.y (), 0,
				0,0,0,1);
}

void drawLine (QVector3D pt1, QVector3D pt2, QVector3D offset)
{
	glLoadIdentity();
	glTranslatef (offset.x (), offset.y (), offset.z ());
	glBegin(GL_LINES);
		glColor3f (0.0, 0.0, 0.0);
		glVertex3f (pt1.x (), pt1.z (), -pt1.y ());
		glVertex3f (pt2.x (), pt2.z (), -pt2.y ());
	glEnd();
}
void drawLineColor (QColor color, QVector3D pt1, QVector3D pt2, QVector3D offset)
{
	glLoadIdentity();
	glTranslatef (offset.x (), offset.y (), offset.z ());
	glBegin(GL_LINES);
		glColor3f (color.red () / 255.0f, color.green () / 255.0f, color.blue () / 255.0f);
		glVertex3f (pt1.x (), pt1.z (), -pt1.y ());
		glVertex3f (pt2.x (), pt2.z (), -pt2.y ());
	glEnd();
}

void drawBoxAt (QColor colors[], float w, float h, float d, QVector3D v, QVector3D offset)
{
	glLoadIdentity();
	glTranslatef (offset.x (), offset.y (), offset.z ());
	glTranslatef (v.x (), v.z (), -v.y ());
	drawBox (colors, w, h, d, offset);
}
void drawBox (QColor colors[], float w, float h, float d, QVector3D offset)
{
	glBegin(GL_QUADS);
		// top
		glColor4f (colors[0].redF (), colors[0].greenF (), colors[0].blueF (), colors[0].alphaF ());
		glVertex3f (-w, h, -d);
		glVertex3f (w, h, -d);
		glVertex3f (w, h, d);
		glVertex3f (-w, h, d);

		// bottom
		glColor4f (colors[1].redF (), colors[1].greenF (), colors[1].blueF (), colors[1].alphaF ());
		glVertex3f (-w, -h, -d);
		glVertex3f (w, -h, -d);
		glVertex3f (w, -h, d);
		glVertex3f (-w, -h, d);

		// left
		glColor4f (colors[2].redF (), colors[2].greenF (), colors[2].blueF (), colors[2].alphaF ());
		glVertex3f (-w, h, -d);
		glVertex3f (-w, h, d);
		glVertex3f (-w, -h, d);
		glVertex3f (-w, -h, -d);

		// right
		glColor4f (colors[3].redF (), colors[3].greenF (), colors[3].blueF (), colors[3].alphaF ());
		glVertex3f (w, h, -d);
		glVertex3f (w, h, d);
		glVertex3f (w, -h, d);
		glVertex3f (w, -h, -d);

		// front
		glColor4f (colors[4].redF (), colors[4].greenF (), colors[4].blueF (), colors[4].alphaF ());
		glVertex3f (-w, h, -d);
		glVertex3f (w, h, -d);
		glVertex3f (w, -h, -d);
		glVertex3f (-w, -h, -d);

		// back
		glColor4f (colors[5].redF (), colors[5].greenF (), colors[5].blueF (), colors[5].alphaF ());
		glVertex3f (-w, h, d);
		glVertex3f (w, h, d);
		glVertex3f (w, -h, d);
		glVertex3f (-w, -h, d);
	glEnd();
}

QQuaternion FromEuler(float pitch, float yaw, float roll)
{
	// Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll
	// and multiply those together.
	// the calculation below does the same, just shorter
	
	float p = pitch / 2.0;
	float ya = yaw / 2.0;
	float r = roll / 2.0;
	
	float sinp = sin(p);
	float siny = sin(ya);
	float sinr = sin(r);
	float cosp = cos(p);
	float cosy = cos(ya);
	float cosr = cos(r);
	
	float x = sinr * cosp * cosy - cosr * sinp * siny;
	float y = cosr * sinp * cosy + sinr * cosp * siny;
	float z = cosr * cosp * siny - sinr * sinp * cosy;
	float w = cosr * cosp * cosy + sinr * sinp * siny;
	
	return QQuaternion(w, x, y, z);
}

