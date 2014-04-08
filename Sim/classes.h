#ifndef CLASSES_H
#define CLASSES_H

#include <stdint.h>

#include <QQuaternion>

#include "utils.h"

struct TData
{
	float q0, q1, q2, q3;
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float pitch, roll, yaw;
};

#define TYPE_SENSORS 0
#define TYPE_OBJ     1

#pragma pack(1)
struct TUdpDataSENSORS
{
	uint8_t type;
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float q0, q1, q2, q3;
	uint64_t ticks;
};
#pragma pack()

#endif // CLASSES_H
