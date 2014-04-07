#ifndef UTILS_H
#define UTILS_H

#include <cmath>

#include <QMatrix4x4>
#include <QQuaternion>
#include <QColor>
#include <QVector3D>

float d2r (float v);
float r2d (float v);

static double bmp085GetAltitude (int32_t pressure)
{
	return 44330.0f * (1 - pow ((double)pressure / 101325.0f, 0.19029496f));
}

QMatrix4x4 quatToRotMatrix (QQuaternion q);
QMatrix4x4 realToOGL (QMatrix4x4 m);

void drawLine (QVector3D pt1, QVector3D pt2, QVector3D offset);
void drawLineColor (QColor color, QVector3D pt1, QVector3D pt2, QVector3D offset);

void drawBoxAt (QColor colors[], float w, float h, float d, QVector3D v, QVector3D offset);
void drawBox (QColor colors[], float w, float h, float d, QVector3D offset);

template<typename T>
class TChartData
{
public:
	QVector<T> m_values;
	int m_capacity;

	TChartData () : m_capacity (100) { }

	void setCapacity (int size) { m_capacity = size; }
	int getCapacity () const { return m_capacity; }

	void append (T val)
	{
		m_values.append (val);
		while (m_values.size () > m_capacity)
			m_values.remove (0);
	}
	bool isEmpty () const { return m_values.size () == 0; }
	T getLast () const { return m_values[m_values.size () - 1]; }
	int getSize () const { return m_values.size (); }
};

#endif // UTILS_H
