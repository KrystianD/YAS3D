#ifndef CLASSES_H
#define CLASSES_H

#include <stdint.h>

#include <QQuaternion>

#include "utils.h"

struct TData
{
	float q0, q1, q2, q3;
	int16_t ax, ay, az, gx, gy, gz, mx, my, mz;
	float pitch, roll, yaw;
};

#endif // CLASSES_H
